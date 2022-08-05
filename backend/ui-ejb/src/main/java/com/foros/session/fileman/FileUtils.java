package com.foros.session.fileman;

import com.foros.changes.inspection.ChangeType;
import com.foros.model.template.OptionFileType;
import com.foros.session.fileman.audit.FileSystemAudit;
import com.foros.session.fileman.audit.NullFileSystemAudit;
import com.foros.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class FileUtils {
    private static final Parser PARSER = new AutoDetectParser();
    private static final MimeTypes MIME_TYPES = TikaConfig.getDefaultConfig().getMimeRepository();
    private static final Map<String, String> MIMETYPE_KNOWN_ALIASES = new HashMap<String, String>() {{
        put("video/x-m4v", "video/mp4");
    }};


    static {
        try {
            MIME_TYPES.addPattern(MIME_TYPES.getMimeType(".html"), ".*\\.rptdesign$", true);
        } catch (Exception ignored) {}
    }

    /**
     * Check if namespace restrictions apply for filename:
     * - [ ] File and folder name must not be empty
     * - [ ] Maximum 100 characters in file and folder name
     * - [ ] File and folder name must be trimmed
     * - [ ] File and folder name can contain all characters except the following: \ / : * ? " < > |
     * - [ ] File and folder name can not begin with - (dash)
     * - [ ] File and folder name can not begin with ~ (tilde)
     */
    public static boolean isNamespaceRestrictionsApply(String filename) {
        if (filename == null || filename.length() == 0) {
            return false;
        }

        if (filename.length() > 100) {
            return false;
        }

        if (!StringUtil.trimProperty(filename).equals(filename)) {
            return false;
        }

        if (!filename.matches("[^\\\\/:\\*\\?\"<>|]+")) {
            return false;
        }

        if (filename.charAt(0) == '~' || filename.charAt(0) == '-') {
            return false;
        }

        return true;
    }

    /**
     * Build a well-formed path name:
     *   1) replace all symbol '\' to a '/'
     *   2) if isDeleteLeadingSeparator is true then remove leading symbol '/';
     *   3) add at last the symbol '/'
     * On a MacOS the isDeleteLeadingSeparator param must be false becouse "Users/admin/"
     * is a wrong path and the leading symbol '/' must be preserved (e.g. "/Users/admin/")
     */
    public static String trimPathName(String path, boolean isDeleteLeadingSeparator) {
        if (StringUtil.isPropertyEmpty(path)) {
            return "";
        }

        StringBuilder sb = new StringBuilder(path.replace('\\', '/'));
        // Remove duplicating slashes.
        if (sb.length() > 1) {
            int i = 0;
            while (i < sb.length() - 1) {
                if (sb.charAt(i) == '/' && sb.charAt(i+1) == '/') {
                    sb.deleteCharAt(i+1);
                } else {
                    i += 2;
                }
            }
        }

        if (isDeleteLeadingSeparator && sb.charAt(0) == '/') {
            sb.deleteCharAt(0);
        }

        if (sb.length() == 0 || sb.charAt(sb.length() - 1) != '/') {
            sb.append('/');
        }

        return sb.toString();
    }

    /**
     * Removes leading and ending slashes
     * @param pathName specified path name
     * @return name of folder itself
     */
    public static String extractPathName(String pathName) {
        pathName = trimPathName(pathName);
        if (StringUtil.isPropertyNotEmpty(pathName) && pathName.charAt(pathName.length() - 1) == '/') {
            return pathName.substring(0, pathName.length() - 1);
        } else {
            return pathName;
        }
    }

    public static String extractPathToFile(String pathName) {
        if (StringUtil.isPropertyNotEmpty(pathName)) {
            int pos = pathName.lastIndexOf('/');
            if (pos > 0) {
                return pathName.substring(0, pos);
            }
        }
        return "";
    }

    /**
     * Build a well-formed path name
     */
    public static String trimPathName(String path) {
        return trimPathName(path, true);
    }

    public static boolean deleteFile(File file) {
        return deleteFile(file, NullFileSystemAudit.INSTANCE);
    }

    public static boolean deleteFile(File file, FileSystemAudit audit) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (!deleteFile(f, audit)) {
                    return false;
                }
            }
        }


        boolean res = file.delete();
        if (res) {
            audit.add(ChangeType.REMOVE, file);
        }
        return res;
    }

    public static String generatePathById(Long id) {
        assert (id != null);
        NumberFormat formatter = new DecimalFormat("000000000000");
        String formattedId = formatter.format(id);
        return formattedId.substring(0, 4) + "/" + formattedId.substring(4, 8)+ "/" + formattedId.substring(8, 12);
    }

    public static String generateDirectoryPartById(Long id) {
        assert (id != null);
        NumberFormat formatter = new DecimalFormat("000000000000");
        String formattedId = formatter.format(id);
        return formattedId.substring(0, 4) + "/" + formattedId.substring(4, 8);
    }

    public static String generateFilenamePartById(Long id) {
        assert (id != null);
        NumberFormat formatter = new DecimalFormat("000000000000");
        String formattedId = formatter.format(id);
        return formattedId.substring(8, 12);
    }

    static final String UNKNOWN = "unknown";

    public static String getMimeTypeByMagic(File file) {
        try {
            String mimeType = "text/html";
            if (!detectHtmlSniffing(file)) {
                Metadata metadata = new Metadata();
                metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
                ParseContext context = new ParseContext();
                context.set(Parser.class, PARSER);

                try (FileInputStream is = new FileInputStream(file)) {
                    PARSER.parse(is, new BodyContentHandler(), metadata, context);
                }
                mimeType = metadata.get(Metadata.CONTENT_TYPE);
            }

            return replaceAlias(mimeType);
        } catch (SAXException e) {
            return UNKNOWN;
        } catch (TikaException e) {
            return UNKNOWN;
        } catch (IOException e) {
            return UNKNOWN;
        }
    }

    private static String replaceAlias(String mimeType) {
        String baseType = MIMETYPE_KNOWN_ALIASES.get(mimeType);
        return baseType == null ? mimeType : baseType;
    }

    /**
     *  Html content sniffing algorithm described in http://www.adambarth.com/papers/2009/barth-caballero-song.pdf
     *
     * @param file file to scan
     *
     * @return true if content looks like HTML
     */
    private static boolean detectHtmlSniffing(File file) {
        final int SCAN_SIZE = 128;

        byte[] data = new byte[SCAN_SIZE];

        int byteCount = 0;
        try (InputStream is = new FileInputStream(file)) {
            while (byteCount < SCAN_SIZE) {
                int curCount = is.read(data, byteCount, SCAN_SIZE - byteCount);
                if (curCount == -1) {
                    break;
                }

                byteCount += curCount;
            }
        } catch (IOException e) {
            //ignore?
        }

        if (byteCount == 0) {
            return false;
        }

        int nonSpacePos = 0;
        String content = new String(data).toUpperCase();
        for (int n = 0; n < content.length(); n++) {
            if (Character.isSpaceChar(content.charAt(n))) {
                nonSpacePos++;
            } else {
                break;
            }
        }

        if (nonSpacePos == content.length()) {
            return false;
        }

        if (containsHtmlTag(content)) {
            return true;
        }

        // SVG file is an XML file, that means it starts with "<?xml ..."
        if ("svg".equalsIgnoreCase(getExtension(file.getName()))) {
            if (content.startsWith("<?XML", nonSpacePos)) {
                return false; // this is a valid SVG file
            }
        }

        return startsWithHtmlTag(content, nonSpacePos);
    }

    private static boolean containsHtmlTag(String content) {
        boolean result = content.contains("<HTML") ||
                content.contains("<SCRIPT") ||
                content.contains("<TITLE") ||
                content.contains("<BODY") ||
                content.contains("<HEAD") ||
                content.contains("<PLAINTEXT") ||
                content.contains("<TABLE") ||
                content.contains("<IMG") ||
                content.contains("<PRE") ||
                content.contains("text/html");

        if (!result) {
            String singleCharTag = "<A";
            int position = content.indexOf(singleCharTag);
            if (isWhitespaceFollow(content, singleCharTag, position)) {
                return true;
            }
        }

        return result;
    }

    private static boolean startsWithHtmlTag(String content, int position) {
        boolean result = content.startsWith("<!", position) ||
            content.startsWith("<?", position) ||
            content.startsWith("<FRAMESET", position) ||
            content.startsWith("<IFRAME", position) ||
            content.startsWith("<LINK", position) ||
            content.startsWith("<BASE", position) ||
            content.startsWith("<STYLE", position) ||
            content.startsWith("<DIV", position) ||
            content.startsWith("<FONT", position) ||
            content.startsWith("<APPLET", position) ||
            content.startsWith("<META", position) ||
            content.startsWith("<CENTER", position) ||
            content.startsWith("<FORM", position) ||
            content.startsWith("<ISINDEX", position) ||
            content.startsWith("<H1", position) ||
            content.startsWith("<H2", position) ||
            content.startsWith("<H3", position) ||
            content.startsWith("<H4", position) ||
            content.startsWith("<H5", position) ||
            content.startsWith("<H6", position) ||
            content.startsWith("<BR", position);

        if (result) {
            return true;
        }

        String[] singleCharTags = { "<P", "<B" };
        for (String tag : singleCharTags) {
            if (content.startsWith(tag) && isWhitespaceFollow(content, tag, position)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isWhitespaceFollow(String content, String tag, int position) {
        int tagLength = tag.length();
        if (position < 0 || content.length() <= position + tagLength) {
            return false;
        }

        return Character.isWhitespace(content.charAt(position + tagLength));
    }

    public static String withoutExtension(String filename) {
        int dotPos = filename.lastIndexOf('.');
        if (dotPos == -1) {
            return filename;
        } else {
            return filename.substring(0, dotPos);
        }
    }

    public static String getExtension(String filename) {
        int dotPos = filename.lastIndexOf('.');
        if (dotPos == -1 || dotPos == filename.length() - 1) {
            return "";
        } else {
            return filename.substring(dotPos + 1);
        }
    }

    public static String getMimeTypeByExtension(String filename) {
        if (!filename.contains(".")) {
            filename = "." + filename;
        }

        MimeType mimeType = MIME_TYPES.getMimeType(filename);
        return mimeType.getName();
    }

    public static List<String> fileTypesToMimeTypes(List<String> fileTypes) {
        List<String> mimeTypes = new ArrayList<String>();
        if (fileTypes != null) {
            for (String fileType : fileTypes) {
                mimeTypes.add(getMimeTypeByExtension(fileType));
            }
        }

        return mimeTypes;
    }

    public static boolean isFileAllowed(String fileName, List<OptionFileType> fileTypes) {
        if (fileTypes == null || fileTypes.isEmpty()) {
            return true;
        }

        String mimeType = FileUtils.getMimeTypeByExtension(fileName);
        for (OptionFileType optionFileType : fileTypes) {
            if (mimeType.equals(FileUtils.getMimeTypeByExtension("." + optionFileType.getFileType()))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllowedFileExtension(String fileName, List<String> fileTypes) {
        if (fileTypes == null || fileTypes.isEmpty()) {
            return true;
        }

        return fileTypes.contains(FileUtils.getMimeTypeByExtension(fileName));
    }
}
