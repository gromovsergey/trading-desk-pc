package app.programmatic.ui.flight.restriction;

import static app.programmatic.ui.campaign.dao.model.CampaignDisplayStatus.NO_ACTIVE_GROUPS;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.CREATE;
import static app.programmatic.ui.common.permission.dao.model.PermissionAction.EDIT;
import static app.programmatic.ui.common.permission.dao.model.PermissionType.ADVERTISER_ENTITY;

import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;
import app.programmatic.ui.common.restriction.service.EntityRestrictions;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.service.FlightService;
import app.programmatic.ui.flight.service.LineItemService;
import app.programmatic.ui.flight.tool.FlightLineItemsInfo;
import app.programmatic.ui.common.permission.service.PermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Restrictions
public class FlightRestrictions {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private EntityRestrictions entityRestrictions;

    @Autowired
    private FlightService flightService;

    @Autowired
    private LineItemService lineItemService;

    @Autowired
    private AccountService accountService;

    @Restriction("flight.update")
    public boolean canUpdate(Long flightId) {
        Flight flight = flightService.find(flightId);

        if (!permissionService.isGranted(ADVERTISER_ENTITY, EDIT)) {
            return false;
        }

        return !entityRestrictions.isDeleted(flight) &&
                entityRestrictions.isEntityAcessable(flight.getOpportunity().getAccountId());
    }

    @Restriction("flight.create")
    public boolean canCreate(Long accountId) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);

        if (!permissionService.isGranted(ADVERTISER_ENTITY, CREATE)) {
            return false;
        }

        return entityRestrictions.canViewEdit(account);
    }

    @Restriction("flight.updateLineItem")
    public boolean canUpdateLineItem(Long lineItemId) {
        LineItem lineItem = lineItemService.find(lineItemId);

        if (!permissionService.isGranted(ADVERTISER_ENTITY, EDIT)) {
            return false;
        }

        return !entityRestrictions.isDeleted(lineItem) &&
                entityRestrictions.isEntityAcessable(lineItem.getAccountId());
    }

    @Restriction("flight.createLineItem")
    public boolean canCreateLineItem(Long flightId) {
        Flight flight = flightService.find(flightId);

        if (!permissionService.isGranted(ADVERTISER_ENTITY, CREATE)) {
            return false;
        }

        return !entityRestrictions.isDeleted(flight) &&
                entityRestrictions.isEntityAcessable(flight.getOpportunity().getAccountId());
    }

    @Restriction("flight.changeStatus")
    public boolean canChangeStatus(Long flightId) {
        Flight flight = flightService.find(flightId);

        // Check permissions
        if (!permissionService.isGranted(ADVERTISER_ENTITY, EDIT)) {
            return false;
        }

        // Check entity access
        if(entityRestrictions.isDeleted(flight)) {
            return false;
        }

        // Check entity access
        if(!entityRestrictions.isEntityAcessable(flight.getOpportunity().getAccountId())) {
            return false;
        }

        // Check if status is 'No Active Groups' (we can not change status in this case)
        if (hasNoActiveLineItems(flight)) {
            return false;
        }

        return true;
    }

    private boolean hasNoActiveLineItems(Flight flight) {
        if (flight.getDisplayStatus() == NO_ACTIVE_GROUPS) {
            // Default Line Item status is changed automatically
            FlightLineItemsInfo lineItemsInfo = new FlightLineItemsInfo(flight.getId(), lineItemService);
            return !lineItemsInfo.isDefaultLineItemExist();
        }
        return false;
    }

    @Restriction("flight.updateFlightCreatives")
    public boolean canUpdateFlightCreatives(Long id) {
        if (!permissionService.isGranted(ADVERTISER_ENTITY, EDIT)) {
            return false;
        }

        Flight flight = flightService.find(id);
        return !entityRestrictions.isDeleted(flight) &&
                entityRestrictions.isEntityAcessable(flight.getOpportunity().getAccountId());
    }

    @Restriction("flight.updateLineItemCreatives")
    public boolean canUpdateLineItemCreatives(Long id) {
        if (!permissionService.isGranted(ADVERTISER_ENTITY, EDIT)) {
            return false;
        }

        LineItem lineItem = lineItemService.find(id);
        return !entityRestrictions.isDeleted(lineItem) &&
                entityRestrictions.isEntityAcessable(lineItem.getAccountId());
    }
}
