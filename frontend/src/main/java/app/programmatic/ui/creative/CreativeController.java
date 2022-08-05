package app.programmatic.ui.creative;

import static app.programmatic.ui.common.config.ApplicationConstants.MAX_RESULTS_SIZE;

import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.creative.tool.CreativeCopy;
import com.foros.rs.client.model.advertising.template.CreativeSize;
import com.foros.rs.client.model.advertising.template.LivePreviewResult;
import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.creative.dao.model.*;
import app.programmatic.ui.creative.service.CreativeService;
import app.programmatic.ui.creative.view.TemplateSizeView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class CreativeController {

    @Autowired
    private CreativeService creativeService;

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/stat", produces = "application/json")
    public List<CreativeStat> getAdvertiserCreatives(@RequestParam(value = "accountId") Long accountId) {
        return creativeService.getDisplayCreatives(accountId, MAX_RESULTS_SIZE);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/category/stat", produces = "application/json")
    public CreativeCategories getCreativeCategories() {
        return creativeService.getCreativeCategories();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/size/stat", produces = "application/json")
    public List<CreativeSizeStat> getDisplaySizes(@RequestParam(value = "accountId") Long accountId,
                                                  @RequestParam(value = "templateId") Long templateId) {
        return creativeService.getDisplaySizes(accountId, templateId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/template/stat", produces = "application/json")
    public List<CreativeTemplateStat> getDisplayTemplates(@RequestParam(value = "accountId") Long accountId) {
        return creativeService.getAccountDisplayTemplates(accountId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/size", produces = "application/json")
    public CreativeSize getDisplaySize(@RequestParam(value = "sizeId") Long sizeId) {
        return creativeService.findDisplaySize(sizeId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/template", produces = "application/json")
    public CreativeTemplate getDisplayTemplate(@RequestParam(value = "templateId") Long templateId) {
        return creativeService.findDisplayTemplate(templateId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/imageTemplate", produces = "application/json")
    public CreativeTemplate getImageTemplate(@RequestParam(value = "accountId") Long accountId) {
        CreativeTemplate result = creativeService.findTemplate(accountId, CreativeTemplateNames.IMAGE.getName());
        return result == null ? new CreativeTemplate() : result;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/htmlTemplate", produces = "application/json")
    public CreativeTemplate getHtmlTemplate(@RequestParam(value = "accountId") Long accountId) {
        CreativeTemplate result = creativeService.findTemplate(accountId, CreativeTemplateNames.HTML.getName());
        return result == null ? new CreativeTemplate() : result;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/templatesize", produces = "application/json")
    public TemplateSizeView getDisplayTemplateAndSize(@RequestParam(value = "templateId") Long templateId,
                                                      @RequestParam(value = "sizeId") Long sizeId,
                                                      @RequestParam(value = "accountId") Long accountId) {
        TemplateSizeView result = new TemplateSizeView();
        result.setTemplate(creativeService.findDisplayTemplate(templateId));
        result.setSize(creativeService.findDisplaySize(sizeId));
        result.setAccountContentCategories(creativeService.getLinkedContentCategories(accountId));

        return result;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/contentCategories", produces = "application/json")
    public List<CreativeCategory> getAccountContentCategories(@RequestParam(value = "accountId") Long accountId) {
        return creativeService.getLinkedContentCategories(accountId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative", produces = "application/json")
    public Creative getCreative(@RequestParam(value = "creativeId") Long creativeId) {
        return creativeService.find(creativeId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/creative", produces = "application/json")
    public Long create(@RequestBody Creative creative) {
        return creativeService.create(creative);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/creative", produces = "application/json")
    public Long update(@RequestBody Creative creative) {
        return creativeService.update(creative);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/creative/copy", produces = "application/json")
    public Long copy(@RequestParam(value = "creativeId") Long creativeId) {
        Creative creative = creativeService.find(creativeId);
        creative.setId(null);
        creative.setName(CreativeCopy.getNewName(creative.getName()));
        creative.setDisplayStatus(MajorDisplayStatus.INACTIVE);
        return creativeService.create(creative);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/preview", produces = "application/json")
    public LivePreviewResult preview(@RequestParam(value = "creativeId") Long creativeId) {
        return creativeService.preview(creativeId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/creative/livePreview", produces = "application/json")
    public LivePreviewResult livePreview(@RequestBody Creative creative) {
        return creativeService.livePreview(creative);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/creative/operation", produces = "application/json")
    public Object creativeOperation(@RequestParam(value = "name") StatusOperation operation,
                                    @RequestParam(value = "creativeId") Long creativeId) {
        return creativeService.changeStatus(creativeId, operation);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/creative/fileUpload", produces = "application/json")
    public FileUrl handleFileUpload(@RequestParam(value = "accountId") Long accountId,
                                    @RequestParam("file") MultipartFile file) {
        return creativeService.upload(file, accountId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/creative/zipUpload", produces = "application/json")
    public List<CreativeImage> handleZipUpload(@RequestParam(value = "accountId") Long accountId,
                                               @RequestParam("file") MultipartFile file) {
        return creativeService.uploadZip(file, accountId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/creative/zipUploadHtml", produces = "application/json")
    public List<CreativeImage> handleZipHtmlUpload(@RequestParam(value = "accountId") Long accountId,
                                               @RequestParam("file") MultipartFile file) {
        return creativeService.uploadZipHtml(file, accountId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/creative/checkFileExist", produces = "application/json")
    public Boolean checkFileExist(@RequestParam(value = "accountId") Long accountId,
                                  @RequestParam("file") MultipartFile file) {
        return creativeService.checkFileExist(file, accountId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/creative/upload", produces = "application/json")
    public List<Long> uploadCreatives(@RequestBody CreativeUpload creativeUpload) {
        return creativeService.uploadCreatives(creativeUpload);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/creative/uploadHtml", produces = "application/json")
    public List<Long> uploadCreatives(@RequestBody CreativeUploadHtml creativeUploadHtml) {
        return creativeService.uploadCreatives(creativeUploadHtml);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/creative/fileBaseUrl", produces = "application/json")
    public FileUrl getFileBaseUrl(@RequestParam(value = "accountId") Long accountId) {
        return creativeService.getFileBaseUrl(accountId);
    }
}
