package com.foros.rs.resources.campaign;

import com.foros.config.ConfigService;
import com.foros.model.Status;
import com.foros.model.creative.Creative;
import com.foros.session.account.AccountService;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.CreativeSelector;
import com.foros.session.creative.CreativePreviewService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.creative.LivePreviewException;
import com.foros.session.creative.LivePreviewHelper;
import com.foros.session.creative.LivePreviewResult;
import com.foros.session.creative.PreviewInfoTO;
import com.foros.session.template.OptionService;
import com.foros.session.template.TemplateService;
import com.foros.validation.constraint.violation.parsing.ParseErrorsSupport;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
@Path("/creatives/")
public class DisplayCreativeResource {
    private static final Logger logger = Logger.getLogger(DisplayCreativeResource.class.getName());

    @EJB
    private DisplayCreativeService displayCreativeService;

    @EJB
    private CreativePreviewService previewService;

    @EJB
    private AccountService accountService;

    @EJB
    private OptionService optionService;

    @EJB
    private CreativeSizeService creativeSizeService;

    @EJB
    private TemplateService templateService;

    @EJB
    private ConfigService configService;

    @GET
    public Result<Creative> get(
            @QueryParam("advertiser.ids") List<Long> accountIds,
            @QueryParam("creative.ids") List<Long> creativeIds,
            @QueryParam("creative.statuses") List<Status> creativeStatuses,
            @QueryParam("size.ids") List<Long> sizeIds,
            @QueryParam("size.ids.excluded") List<Long> excludedSizeIds,
            @QueryParam("template.ids") List<Long> templateIds,
            @QueryParam("template.ids.excluded") List<Long> excludedTemplateIds,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        CreativeSelector selector = new CreativeSelector();

        selector.setAdvertiserIds(accountIds);
        selector.setCreatives(creativeIds);
        selector.setSizes(sizeIds);
        selector.setExcludedSizes(excludedSizeIds);
        selector.setTemplates(templateIds);
        selector.setExcludedTemplates(excludedTemplateIds);
        selector.setStatuses(creativeStatuses);

        if (pagingFirst != null || pagingCount != null) {
            selector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        return displayCreativeService.get(selector);
    }

    @GET
    @Path("preview")
    public LivePreviewResult preview(@QueryParam("creative.id") Long creativeId) {
        PreviewInfoTO to = previewService.generateCreativePreviewInfo(creativeId);
        if (to.getErrors() != null && !to.getErrors().isEmpty()) {
            return null;
        }
        return new LivePreviewResult(to.getHeight(), to.getWidth(), to.getPath());
    }

    @POST
    public OperationsResult perform(Operations<Creative> operations) {
        ParseErrorsSupport.throwIfAnyErrorsPresent(operations);
        return displayCreativeService.perform(operations);
    }

    @POST
    @Path("livePreview")
    public LivePreviewResult livePreview(Creative creative) {
        try {
            if (LivePreviewHelper.isPreviewPossible(creative)) {
                return LivePreviewHelper.prepareCreative(creative,
                        configService, accountService, optionService, creativeSizeService, templateService, previewService);
            }
        } catch (LivePreviewException e) {
            logger.log(Level.WARNING, "Can't generate live preview", e.getCause());
            return e.getPreviewResult();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Can't generate live preview", e);
        }

        return null;
    }
}
