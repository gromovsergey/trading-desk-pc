package com.foros.test.factory;

import com.foros.model.ApproveStatus;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
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
import com.foros.model.channel.Channel;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.User;
import com.foros.model.template.CreativeTemplate;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.util.DateUtil;
import com.foros.util.PersistenceUtils;
import com.foros.util.RandomUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.LinkedHashSet;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.springframework.beans.factory.annotation.Autowired;

@Stateless
@LocalBean
public class DisplayCCGTestFactory extends CCGTestFactory {
    @Autowired
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @Autowired
    private DisplayCampaignTestFactory campaignTF;

    @Autowired
    private DisplayCreativeLinkTestFactory creativeLinkTF;

    @Autowired
    private DisplayCreativeTestFactory creativeTF;

    @Autowired
    private DisplayCreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private CountryTestFactory countryTF;

    public void populate(CampaignCreativeGroup group) {
        group.setStatus(Status.ACTIVE);
        group.setQaStatus(ApproveStatus.APPROVED);
        group.setDisplayStatus(CampaignCreativeGroup.LIVE);

        group.setName(getTestEntityRandomName());
        group.setCcgType(CCGType.DISPLAY);
        group.setTgtType(TGTType.CHANNEL);

        Calendar calendar = Calendar.getInstance();
        DateUtil.resetFields(calendar, Calendar.SECOND, Calendar.MILLISECOND);
        group.setDateStart(calendar.getTime());
        group.setCampaignCreatives(new LinkedHashSet<CampaignCreative>());
        group.setActions(new LinkedHashSet<Action>());
        group.setDailyImpressions(0L);
        group.setBudget(BigDecimal.valueOf(10000));
        group.setChannelTarget(ChannelTarget.UNTARGETED);
        group.setDeliveryPacing(DeliveryPacing.UNRESTRICTED);
        group.setMinUidAge(0L);

        group.setCountry(countryTF.findOrCreatePersistent("US"));
        createCcgRate(group, RateType.CPC, RandomUtil.getRandomBigDecimal().setScale(2, RoundingMode.DOWN));
        group.setCcgSchedules(new LinkedHashSet<CCGSchedule>());
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
        return group;
    }

    @Override
    public void persist(CampaignCreativeGroup group) {
        campaignCreativeGroupService.create(group);
    }

    public void update(CampaignCreativeGroup group) {
        campaignCreativeGroupService.update(group);
    }

    @Override
    public CampaignCreativeGroup createPersistent() {
        Campaign campaign = campaignTF.createPersistent();
        return createPersistent(campaign);
    }

    public CampaignCreativeGroup createPersistent(Campaign campaign) {
        CampaignCreativeGroup group = create(campaign);
        persist(group);
        return group;
    }

    private void checkCampaign(Campaign campaign) {
        if (campaign.getCampaignType() != CampaignType.DISPLAY) {
            throw new IllegalArgumentException("A Display CCG must be in a Display Campaign");
        }
    }

    public CampaignCreativeGroup createPersistentCCGWithChannelTarget(Channel behavioralChannel, AdvertiserAccount account){
        behavioralChannel.setDisplayStatus(Channel.LIVE);
        User user = account.getUsers().iterator().next();
        Campaign campaign = campaignTF.createLiveCampaign(account, user);

        CreativeSize creativeSize = creativeSizeTF.createPersistent();
        CreativeTemplate creativeTemplate = creativeTemplateTF.createPersistent();
        creativeTemplate.setStatus(Status.ACTIVE);

        Creative creative = creativeTF.prepareLiveCreative(account, creativeTemplate, creativeSize);

        CampaignCreative campaignCreative = creativeLinkTF.create(creative);

        CampaignCreativeGroup campaignCreativeGroup = prepareLiveDisplayCampaignCreativeGroup(account, campaign);
        campaignCreativeGroup.getCampaignCreatives().add(campaignCreative);
        campaignCreativeGroup.setChannelTarget(ChannelTarget.TARGETED);
        campaignCreativeGroup.setChannel(behavioralChannel);
        campaignCreativeGroup.setCountry(account.getCountry());
        campaignCreative.setCreativeGroup(campaignCreativeGroup);

        entityManager.persist(campaign);
        entityManager.persist(creative);
        entityManager.persist(campaignCreativeGroup);
        entityManager.flush();

        return campaignCreativeGroup;
    }

    public CampaignCreativeGroup prepareLiveDisplayCampaignCreativeGroup(AdvertiserAccount advertiserAccount, Campaign campaign){
        CampaignCreativeGroup campaignCreativeGroup = create(campaign);

        campaignCreativeGroup.setCcgRate(null);
        campaignCreativeGroup.setDailyImpressions(1L);
        campaignCreativeGroup.setCountry(advertiserAccount.getCountry());
        return campaignCreativeGroup;
    }

    @Override
    public CampaignCreativeGroup refresh(CampaignCreativeGroup group) {
        group = super.refresh(group);

        PersistenceUtils.initialize(group.getDeviceChannels());

        return group;
    }
}
