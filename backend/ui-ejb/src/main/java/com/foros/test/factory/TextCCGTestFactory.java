package com.foros.test.factory;

import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.action.Action;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CCGSchedule;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.DeliveryPacing;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.model.creative.Creative;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.status.DisplayStatusService;
import com.foros.util.DateUtil;
import com.foros.util.RandomUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.LinkedHashSet;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class TextCCGTestFactory extends CCGTestFactory {

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private TextCampaignTestFactory campaignTF;

    @EJB
    private TextCreativeLinkTestFactory textCreativeLinkTF;

    @EJB
    private TextCreativeTestFactory textCreativeTF;

    @EJB
    private DisplayStatusService displayStatusService;

    public void populate(CampaignCreativeGroup campaignCreativeGroup) {
        campaignCreativeGroup.setName(getTestEntityRandomName());
        campaignCreativeGroup.setCountry(campaignCreativeGroup.getCampaign().getAccount().getCountry());
        campaignCreativeGroup.setCcgType(CCGType.TEXT);
        campaignCreativeGroup.setTgtType(TGTType.KEYWORD);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (60 * 1000)-10);
        DateUtil.resetFields(calendar, Calendar.SECOND, Calendar.MILLISECOND);
        campaignCreativeGroup.setDateStart(calendar.getTime());

        campaignCreativeGroup.setCampaignCreatives(new LinkedHashSet<CampaignCreative>());
        campaignCreativeGroup.setCcgKeywords(new LinkedHashSet<CCGKeyword>());
        campaignCreativeGroup.setActions(new LinkedHashSet<Action>());

        campaignCreativeGroup.setBudget(BigDecimal.valueOf(10000));
        campaignCreativeGroup.setDeliveryPacing(DeliveryPacing.FIXED);
        campaignCreativeGroup.setMinUidAge(0L);
        campaignCreativeGroup.setDailyBudget(BigDecimal.valueOf(10));

        createCcgRate(campaignCreativeGroup, RateType.CPC, RandomUtil.getRandomBigDecimal().setScale(2, RoundingMode.UP));
        campaignCreativeGroup.setStatus(Status.ACTIVE);
        campaignCreativeGroup.setCcgSchedules(new LinkedHashSet<CCGSchedule>());
    }

    @Override
    public CampaignCreativeGroup create() {
        Campaign campaign = campaignTF.createPersistent();
        return create(campaign);
    }

    public CampaignCreativeGroup create(Campaign campaign) {
        checkCampaign(campaign);

        CampaignCreativeGroup group = new CampaignCreativeGroup();
        group.setCampaign(campaign);
        populate(group);

        group.setCountry(campaign.getAccount().getCountry());
        group.setDateStart(campaign.getDateStart());
        group.setLinkedToCampaignEndDateFlag(true);
        return group;
    }

    @Override
    public void persist(CampaignCreativeGroup group) {
        campaignCreativeGroupService.create(group);
        entityManager.flush();
    }

    public void update(CampaignCreativeGroup group) {
        campaignCreativeGroupService.update(group);
        entityManager.flush();
    }

    @Override
    public CampaignCreativeGroup createPersistent() {
        Campaign campaign = campaignTF.createPersistent();
        return createPersistent(campaign);
    }

    public CampaignCreativeGroup createPersistent(Campaign campaign) {
        checkCampaign(campaign);

        CampaignCreativeGroup group = create(campaign);
        persist(group);
        return group;
    }

    private void checkCampaign(Campaign campaign) {
        if (campaign.getCampaignType() != CampaignType.TEXT) {
            throw new IllegalArgumentException("A Text CCG must be in a Text Campaign");
        }
    }

    public CCGKeyword createCcgKeyword(String keyword) {
        return createCcgKeyword(keyword, KeywordTriggerType.SEARCH_KEYWORD);
    }

    public CCGKeyword createCcgKeyword(String keyword, KeywordTriggerType triggerType) {
        CCGKeyword ccgKeyword = new CCGKeyword();
        ccgKeyword.setTriggerType(triggerType);
        ccgKeyword.setOriginalKeyword(keyword);
        ccgKeyword.setClickURL("http://" + keyword + ".com");
        ccgKeyword.setMaxCpcBid(BigDecimal.valueOf(keyword.length()));
        return ccgKeyword;
    }

    public CampaignCreativeGroup createGreenCCG(Campaign campaign) {
        CampaignCreativeGroup group = create(campaign);
        group.setTgtType(TGTType.CHANNEL);
        group.setStatus(Status.ACTIVE);
        group.setChannelTarget(ChannelTarget.UNTARGETED);
        group.setCcgSchedules(new LinkedHashSet<CCGSchedule>());
        persist(group);

        Creative textCreative = textCreativeTF.createPersistent(group.getAccount());
        CampaignCreative cc = textCreativeLinkTF.create(group, textCreative);
        textCreativeLinkTF.persist(cc);

        cc.setStatus(Status.ACTIVE);
        cc.getCreative().setQaStatus(ApproveStatus.APPROVED);
        group.setStatus(Status.ACTIVE);
        group.setQaStatus(ApproveStatus.APPROVED);
        entityManager.flush();

        displayStatusService.update(cc.getCreative());
        displayStatusService.update(cc);
        displayStatusService.update(group);
        entityManager.flush();
        group = refresh(group);

        if (group.getDisplayStatus() != CampaignCreativeGroup.LIVE)
            throw new RuntimeException("Failed to create Green CCG, actual = " + group.getDisplayStatus());

        return group;
    }

    public CampaignCreativeGroup createRedCCG(Campaign campaign) {
        CampaignCreativeGroup group = create(campaign);
        group.setTgtType(TGTType.CHANNEL);
        group.setChannelTarget(ChannelTarget.UNTARGETED);
        group.setStatus(Status.ACTIVE);
        group.setCcgSchedules(new LinkedHashSet<CCGSchedule>());
        persist(group);

        group.setStatus(Status.ACTIVE);
        group.setQaStatus(ApproveStatus.APPROVED);
        entityManager.flush();

        displayStatusService.update(group);
        entityManager.flush();
        entityManager.refresh(group);

        if (group.getDisplayStatus() != CampaignCreativeGroup.NOT_LIVE_LINKED_CREATIVE_NEED_ATT)
            throw new RuntimeException("Failed to create Red CCG, actual = " + group.getDisplayStatus());

        return group;
    }

    public CampaignCreativeGroup createGreyCCG(Campaign campaign) {
        CampaignCreativeGroup group = create(campaign);
        group.setTgtType(TGTType.CHANNEL);
        group.setChannelTarget(ChannelTarget.UNTARGETED);
        group.setStatus(Status.INACTIVE);
        group.setCcgSchedules(new LinkedHashSet<CCGSchedule>());
        persist(group);
        entityManager.refresh(group);

        if (group.getDisplayStatus() != CampaignCreativeGroup.INACTIVE)
            throw new RuntimeException("Failed to create Grey CCG, actual = " + group.getDisplayStatus());

        return group;
    }
}
