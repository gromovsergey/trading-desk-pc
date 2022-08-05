package com.foros.action.creative.display;

import com.foros.action.IdNameBean;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.Status;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.EntityTO;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.CreativeSortType;
import com.foros.session.creative.CreativeTO;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.StringUtil;
import com.foros.util.context.RequestContexts;
import com.foros.util.mapper.DisplayStatusMapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

public class ListCreativesAction extends SearchCreativesBaseAction implements RequestContextsAware {

    @EJB
    private AccountTypeService accountTypeService;

    @EJB
    private CreativeSizeService sizeService;

    @EJB
    private CampaignService campaignService;

    private Long displayStatusId;

    private Collection<CreativeTO> creatives;

    private String emptyMessage;

    public List<EntityTO> getCampaignList() {
        List<EntityTO> campaigns = campaignService.getCampaignsByAccount(getAdvertiserId());
        Iterator<EntityTO> iter = campaigns.iterator();
        while (iter.hasNext()) {
            EntityTO entity = iter.next();
            if (entity.getStatus() == Status.DELETED) {
                iter.remove();
            }
        }
        Collections.sort(campaigns, new Comparator<EntityTO>() {
            @Override
            public int compare(EntityTO o1, EntityTO o2) {
                if (o1.getStatus() == Status.INACTIVE && o2.getStatus() != Status.INACTIVE) {
                    return -1;
                } else if (o1.getStatus() != Status.INACTIVE && o2.getStatus() == Status.INACTIVE) {
                    return 1;
                } else {
                    return StringUtil.compareToIgnoreCase(o1.getName(), o2.getName());
                }
            }
        });
        return campaigns;
    }

    public Collection<CreativeTO> getCreatives() {
        return creatives;
    }

    public Map<Long, String> getCreativeStatuses() {
        if (userService.getMyUser().isDeletedObjectsVisible()) {
            return CollectionUtils
                    .map(-1L, StringUtil.getLocalizedString("searchParams.status.allbutdeleted"))
                    .map(0L, StringUtil.getLocalizedString("searchParams.status.all"))
                    .items(new DisplayStatusMapper(), Arrays.asList(
                            Creative.LIVE,
                            Creative.DECLINED,
                            Creative.PENDING_FOROS,
                            Creative.PENDING_USER,
                            Creative.INACTIVE,
                            Creative.DELETED
                    )).build();
        } else {
            return CollectionUtils.map(0L, StringUtil.getLocalizedString("searchParams.status.all"))
                    .items(new DisplayStatusMapper(), Arrays.asList(
                            Creative.LIVE,
                            Creative.DECLINED,
                            Creative.PENDING_FOROS,
                            Creative.PENDING_USER,
                            Creative.INACTIVE
                    )).build();
        }
    }

    public Long getDisplayStatusId() {
        return displayStatusId;
    }

    public String getEmptyMessage() {
        return emptyMessage;
    }

    public Map<String, String> getOrderBy() {
        return CollectionUtils
                .localizeMap(CreativeSortType.LASTREVIEWED.name(), CreativeSortType.LASTREVIEWED.getOptionNameTranslation())
                .map(CreativeSortType.FIRSTREVIEWED.name(), CreativeSortType.FIRSTREVIEWED.getOptionNameTranslation())
                .map(CreativeSortType.ATOZ.name(), CreativeSortType.ATOZ.getOptionNameTranslation())
                .map(CreativeSortType.ZTOA.name(), CreativeSortType.ZTOA.getOptionNameTranslation())
                .build();
    }

    public Long getPage() {
        return getSearchParams().getPage();
    }

    public List<IdNameBean> getSizes() {
        List<IdNameBean> sizes = new LinkedList<IdNameBean>();
        List<CreativeSize> creativeSizes = sizeService.findByAccountType(getAccount().getAccountType());
        for (CreativeSize creativeSize : creativeSizes) {
            IdNameBean size = new IdNameBean();
            size.setId(creativeSize.getId().toString());
            size.setName(localizeSizeName(creativeSize));
            sizes.add(size);
        }

        CreativeSize textSize = sizeService.findTextSize();
        sizes.add(new IdNameBean(textSize.getId().toString(), localizeSizeName(textSize)));

        Collections.sort(sizes, new Comparator<IdNameBean>() {
            @Override
            public int compare(IdNameBean o1, IdNameBean o2) {
                return StringUtil.compareToIgnoreCase(o1.getName(), o2.getName());
            }
        });
        return sizes;
    }

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "#target.account")
    public String list() {
        getSearchParams().setDisplayStatusId(displayStatusId);
        return SUCCESS;
    }

    private String localizeSizeName(CreativeSize creativeSize) {
        String name = LocalizableNameUtil.getLocalizedValue(creativeSize.getName());
        return EntityUtils.appendStatusSuffix(name, creativeSize.getStatus());
    }

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "#target.account")
    public String search() {
        getSearchParams().setPageSize(100);
        List<CreativeTO> creatives = searchCreatives();
        if (creatives == null || creatives.isEmpty()) {
            emptyMessage = "creative.nothing.found.to.display";
        } else {
            setCreatives(creatives);
        }
        return SUCCESS;
    }

    public void setCreatives(Collection<CreativeTO> creatives) {
        this.creatives = creatives;
    }

    public void setDisplayStatusId(Long displayStatusId) {
        this.displayStatusId = displayStatusId;
    }

    public void setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
    }

    public void setPage(Long page) {
        getSearchParams().setPage(page);
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getAdvertiserId());
    }
}
