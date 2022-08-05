package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.GeoType;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.channel.geo.GeoChannelService;
import com.foros.util.AccountUtil;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;

@Validations(
    customValidators = {
            @CustomValidator(type = "convtransform", key = "errors.field.number", fieldName = "radius",
                    parameters = {@ValidationParameter(name = "fieldMask", value = "geoChannels\\[.+\\]\\.radius")})
    }
)
public class SaveGeoTargetAction extends BaseActionSupport implements RequestContextsAware, ModelDriven<CampaignCreativeGroup>  {
    @EJB
    private AccountService accountService;

    @EJB
    private CampaignCreativeGroupService creativeGroupService;

    @EJB
    private GeoChannelService geoChannelService;

    @EJB
    private CampaignCreativeGroupService groupService;

    private CampaignCreativeGroup group = new CampaignCreativeGroup();
    private List<GeoChannel> geoChannels = new ArrayList<>();

    private AdvertisingAccountBase existingAccount;

    private Collection<GeoChannel> states;
    private Collection<GeoChannel> cities;

    public  List<GeoChannel> getGeoChannels() {
        return geoChannels;
    }

    public void setGeoChannels(List<GeoChannel> geoChannels) {
        this.geoChannels = geoChannels;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getExistingAccount());
    }

    public AdvertisingAccountBase getExistingAccount() {
        if (existingAccount == null) {
            existingAccount = (AdvertisingAccountBase)accountService.find(group.getAccount().getId());
        }
        return existingAccount;
    }

    @Override
    public CampaignCreativeGroup getModel() {
        return group;
    }

    public SaveGeoTargetAction() {
        super();
        existingAccount = new AgencyAccount();
    }

    private void processAddressChannels() {
        List<GeoChannel> addressChannels = new ArrayList<>();

        for (int i = geoChannels.size() - 1; i >= 0; i--) {
            if (geoChannels.get(i) == null) {
                geoChannels.remove(i);
            } else if (geoChannels.get(i).getGeoType() == GeoType.ADDRESS) {
                geoChannels.get(i).setCountry(group.getCountry());
                GeoChannel address = geoChannelService.findOrCreateAddressChannel(geoChannels.get(i));

                addressChannels.add(address);
                geoChannels.remove(i);
            }
        }

        geoChannels.addAll(addressChannels);
    }

    public String update() {
        processAddressChannels();
        existingAccount = (AdvertisingAccountBase) AccountUtil.extractAccount(existingAccount.getId());
        creativeGroupService.updateGeoTarget(getModel(), geoChannels);
        return SUCCESS;
    }

    public Collection<GeoChannel> getStates() {
        if (states == null) {
            states = geoChannelService.getStates(group.getCountry());
        }
        return states;
    }

    public Collection<GeoChannel> getCities() {
        if (cities == null) {
            cities = geoChannelService.getOrphanCities(group.getCountry());
        }
        return cities;
    }

}
