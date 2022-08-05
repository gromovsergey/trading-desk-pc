package com.foros.rs.client.service;

import com.foros.rs.client.AbstractUnitTest;
import com.foros.rs.client.data.ContentSource;
import com.foros.rs.client.model.file.RootLocation;
import com.foros.rs.client.result.RsConstraintViolationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class FilesServiceTest extends AbstractUnitTest {

    private Long advertiserId;
    private FilesService filesService;

    @Before
    public void prepare() {
        advertiserId = longProperty("foros.test.advertiser.id");
        filesService = foros.getFilesService();
    }

    @Test
    public void testUploadBadArchive() throws IOException {
        invalidUpload("com/foros/rs/client/invalidZip.zip", "Root", "/1/2/3/OUI-20484.patch");
    }

    private void invalidUpload(String zipName, String folderName, String expectedValue) throws IOException {
        try (InputStream is = new ClassPathResource(zipName).getInputStream()) {
            filesService.upload(advertiserId, null, folderName, RootLocation.CREATIVES,
                new ContentSource() {
                    @Override
                    public void writeTo(OutputStream os) throws IOException {
                        IOUtils.copy(is, os);
                    }
                });
            fail();
        } catch (RsConstraintViolationException e) {
            String value = e.getConstraintViolations().iterator().next().getValue();
            assertEquals(expectedValue, value.replace('\\', '/'));
        }
    }

    @Test
    public void testUploadInvalidMaxDimension() throws IOException {
        invalidUpload("com/foros/rs/client/invalidMaxDimension.zip", "TextAdImages", "/1/2/invalidDimension.jpg");
    }

    @Test
    public void testUploadInvalidName() throws IOException {
        invalidUpload("com/foros/rs/client/invalidFileName.zip", "Root", "1/2/3/--.jpg");
    }

    @Test()
    public void testUpload() throws IOException {
        uploadTestZip("Root");
        uploadTestZip("");
        uploadTestZip(null);
    }


    private void uploadTestZip(String root) throws IOException {
        try (InputStream is = new ClassPathResource("com/foros/rs/client/test.zip").getInputStream()) {
            filesService.upload(advertiserId, null, root, RootLocation.CREATIVES,
                new ContentSource() {
                    @Override
                    public void writeTo(OutputStream os) throws IOException {
                        IOUtils.copy(is, os);
                    }
                });
        }
    }
}
