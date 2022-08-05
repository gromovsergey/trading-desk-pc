package com.foros.migration;

import com.foros.migration.util.PreviewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class UpdateImagePreviews implements Migration.Executor {
    @Autowired
    private static Logger logger;

    @Autowired
    @Qualifier("postgresJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Value("${oui_creatives_folder}")
    private String creativesFolderName;

    @Value("${oui_textad_images_folder}")
    private String textAdImagesFolderName;

    public static void main(String[] args) {
        Migration.perform(UpdateImagePreviews.class);
    }

    @Override
    public void run() throws Exception {
        createImagePreviews();
        updateOptionValues();
    }

    private void createImagePreviews() {
        if (creativesFolderName == null) {
            throw new RuntimeException("Creatives folder is not properly set");
        }
        if (textAdImagesFolderName == null) {
            throw new RuntimeException("Texd Ad images folder is not properly set");
        }

        PreviewGenerator generator = new PreviewGenerator();

        File creativesFolder = new File(creativesFolderName);
        if (!creativesFolder.isDirectory()) {
            return;
        }
        for (File file1 : creativesFolder.listFiles()) {
            if (file1.isDirectory() && file1.getName().matches("\\d+")) {
                for (File file2 : file1.listFiles()) {
                    if (file2.isDirectory()) {
                        if (file2.getName().matches("\\d+")) {
                            for (File file3 : file2.listFiles()) {
                                if (file3.getName().equals(textAdImagesFolderName)) {
                                    processDir(generator, file3);
                                    break;
                                }
                            }
                        } else if (file2.getName().equals(textAdImagesFolderName)) {
                            processDir(generator, file2);
                        }
                    }
                }
            }
        }
    }

    private void processDir(PreviewGenerator generator, File dir) {
        try {
            generator.walk(dir);
        } catch (IOException e) {
            logger.severe("Unable to process folder: " + dir.getPath() + ". " + e.getMessage());
        }
    }

    private void updateOptionValues() {
        jdbcTemplate.update(
                "update " +
                        "  CreativeOptionValue cov " +
                        "set " +
                        "  cov.value = substr(cov.value, 0, length(cov.value) - 4) || '.png' " +
                        "where " +
                        "  cov.option_id = ( " +
                        "    select o.option_id " +
                        "    from " +
                        "      Options o " +
                        "      inner join OptionGroup og on (og.option_group_id = o.option_group_id) " +
                        "      inner join template t on (og.template_id = t.template_id) " +
                        "    where " +
                        "      t.template_type='CREATIVE' and t.name='Text' " +
                        "      and o.token='IMAGE_FILE' " +
                        "  ) " +
                        "  and cov.value is not null " +
                        "  and cov.value like '%.jpg'"
        );
    }
}
