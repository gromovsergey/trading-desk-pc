package app.programmatic.ui.creative.service;

import com.foros.rs.client.model.advertising.template.CreativeSize;
import com.foros.rs.client.model.advertising.template.LivePreviewResult;
import com.foros.rs.client.model.advertising.template.Option;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.creative.dao.model.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface CreativeService {

    Creative find(Long id);

    Long create(Creative creative);

    Long update(Creative creative);

    List<Long> uploadCreatives(CreativeUpload creativeUpload);

    List<Long> uploadCreatives(CreativeUploadHtml creativeUploadHtml);

    CreativeTemplate findTemplate(Long accountId, String templateName);

    List<CreativeSizeStat> getDisplaySizes(Long accountId, Long templateId);

    CreativeSize findDisplaySize(Long id);

    List<CreativeTemplateStat> getAccountDisplayTemplates(Long accountId);

    CreativeTemplate findDisplayTemplate(Long id);

    CreativeCategories getCreativeCategories();

    List<CreativeCategory> getLinkedContentCategories(Long accountId);

    List<CreativeStat> getDisplayCreatives(Long accountId, int limit);

    List<CreativeStat> getDisplayCreativesByIds(List<Long> ids, int limit);

    LivePreviewResult preview(Long creativeId);

    LivePreviewResult livePreview(Creative creative);

    MajorDisplayStatus changeStatus(Long creativeId, StatusOperation operation);

    FileUrl upload(MultipartFile file, Long accountId);

    List<CreativeImage> uploadZip(MultipartFile file, Long accountId);

    List<CreativeImage> uploadZipHtml(MultipartFile file, Long accountId);

    Boolean checkFileExist(MultipartFile file, Long accountId);

    FileUrl getFileBaseUrl(Long accountId);
}
