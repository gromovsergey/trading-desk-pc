package com.foros.session.template;

import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.model.template.OptionValueUtils;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProviderService;
import com.foros.tx.TransactionCallback;
import com.foros.tx.TransactionSupportService;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.apache.commons.lang.ArrayUtils;

public abstract class HtmlOptionFileHelper {
    public static final String VERSION_HTML_FORMAT = "yyyyMMdd-HHmmssSSS";

    abstract protected TransactionSupportService getTransactionSupportService();

    abstract protected PathProviderService getPathProviderService();

    public void updateFilesOnCommit(Creative creative) {

        final Map<String, CreativeOptionValue> currentFiles = new HashMap<>();
        for (CreativeOptionValue optionValue : creative.getOptions()) {
            if (optionValue.getOption().getType() == OptionType.HTML) {
                String fileName = createFileName(optionValue);
                currentFiles.put(fileName, optionValue);
            }
        }

        String path = OptionValueUtils.getHtmlRoot(creative);
        final FileSystem creativeFS = getCreativeFS(path);
        final List<String> oldFiles = Arrays.asList(creativeFS.list(null));
        if (oldFiles.equals(currentFiles.keySet())) {
            return;
        }

        final Set<String> createdFiles = new HashSet<>();

        getTransactionSupportService().onTransaction(new TransactionCallback() {
            @Override
            public void onBeforeCommit() {
                for (Map.Entry<String, CreativeOptionValue> entry : currentFiles.entrySet()) {
                    String file = entry.getKey();
                    CreativeOptionValue optionValue = entry.getValue();

                    if (creativeFS.checkExist(file)) {
                        continue;
                    }

                    createdFiles.add(file);
                    try (Writer writer = new OutputStreamWriter(creativeFS.openFile(file))) {
                        writer.write(optionValue.getValue());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCommit() {
                for (String oldFile : oldFiles) {
                    if (!currentFiles.containsKey(oldFile)) {
                        creativeFS.delete(oldFile);
                    }
                }
            }

            @Override
            public void onRollback() {
                for (String file : createdFiles) {
                    creativeFS.delete(file);
                }
            }
        });
    }

    public static String createFileName(CreativeOptionValue optionValue) {
        DateFormat dateFormat = new SimpleDateFormat(VERSION_HTML_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return optionValue.getOption().getId() + "-" + dateFormat.format(optionValue.getVersion()) + ".html";
    }

    private FileSystem getCreativeFS(String path) {
        return getPathProviderService().getCreatives().getNested(path, OnNoProviderRoot.AutoCreate).createFileSystem();
    }

    public void removeFilesOnCommit(final Creative creative, final Set<String> optionIds, final boolean retainGiven) {
        final String path = OptionValueUtils.getHtmlRoot(creative);
        getTransactionSupportService().onTransaction(new TransactionCallback() {
            @Override
            public void onBeforeCommit() {
            }

            @Override
            public void onCommit() {
                FileSystem creativeFileSystem = getCreativeFS(path);
                String[] fileNames = creativeFileSystem.list(null);
                if (!ArrayUtils.isEmpty(fileNames)) {
                    for (String fileName : fileNames) {
                        String optionId = fileName.substring(0, fileName.indexOf("-"));
                        if (retainGiven && !optionIds.contains(optionId) || !retainGiven && optionIds.contains(optionId)) {
                            creativeFileSystem.delete(fileName);
                        }
                    }
                }
            }

            @Override
            public void onRollback() {
            }
        });
    }

    public void removeOptionFilesOnCommit(Option htmlOption) {
        Collection<Creative> creatives = findCreatives(htmlOption.getOptionGroup());
        for (Creative creative : creatives) {
            removeFilesOnCommit(creative, new HashSet<>(Arrays.asList(htmlOption.getId().toString())), false);
        }
    }

    public void removeOptionGroupFilesOnCommit(OptionGroup optionGroup) {
        Set<String> optionIds = new HashSet<>();
        for (Option option : optionGroup.getOptions()) {
            if (option.getType() == OptionType.HTML) {
                optionIds.add(option.getId().toString());
            }
        }

        if (optionIds.isEmpty()) {
            return;
        }

        Collection<Creative> creatives = findCreatives(optionGroup);
        if (creatives.isEmpty()) {
            return;
        }

        for (Creative creative : creatives) {
            removeFilesOnCommit(creative, optionIds, false);
        }
    }

    private Collection<Creative> findCreatives(OptionGroup optionGroup) {
        if (optionGroup.getCreativeSize() != null) {
            return optionGroup.getCreativeSize().getCreatives();
        } else if (optionGroup.getTemplate() instanceof CreativeTemplate) {
            return ((CreativeTemplate) optionGroup.getTemplate()).getCreatives();
        }
        return Collections.emptyList();
    }
}
