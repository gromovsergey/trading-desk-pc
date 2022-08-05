package com.foros.session.textad;

import com.foros.config.Config;
import com.foros.config.ConfigService;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.template.OptionValueUtils;
import com.foros.session.fileman.FileUtils;
import com.foros.util.StringUtil;

public class TextAdImageUtil {
    public static String getResizedFilePath(Config config, AdvertiserAccount account, String srcFileName) {
        if (StringUtil.isPropertyEmpty(srcFileName)) {
            return null;
        }

        String textAdImagesResizedRoot = OptionValueUtils.getTextAdImagesResizedRoot(config, account);
        return srcFileName.startsWith(textAdImagesResizedRoot) ? srcFileName : textAdImagesResizedRoot + getResizedFileName(srcFileName);
    }

    public static String getSourceFilePath(ConfigService config, AdvertiserAccount account, String fileName) {
        if (StringUtil.isPropertyEmpty(fileName)) {
            return null;
        }

        String textAdImagesResizedRoot = OptionValueUtils.getTextAdImagesResizedRoot(config, account);
        if (fileName.startsWith(textAdImagesResizedRoot)) {
            return FileUtils.withoutExtension(fileName.substring(textAdImagesResizedRoot.length()));
        }
        return fileName;
    }

    public static String getResizedFileName(String srcFileName) {
        return srcFileName + ".png";
    }
}
