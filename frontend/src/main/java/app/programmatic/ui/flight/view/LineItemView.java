package app.programmatic.ui.flight.view;

import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.tool.EffectiveLineItemTool;

import java.math.BigDecimal;
import java.util.List;


public class LineItemView extends FlightBaseView {
    private Long flightId;
    private Long accountId;
    private String name;
    private BigDecimal budget;
    private Long ccgId;
    private Long ccgChannelId;
    private Long specialChannelId;
    private Long version2;
    private List<String> propsWithFlightValues;
    private List<String> resetAwareProps;

    public LineItemView() {
    }

    public LineItemView(Flight owner, LineItem lineItem, List<String> whiteList, List<String> blackList, List<String> viewResetAwareFields) {
        super(lineItem, whiteList, blackList);
        this.flightId = lineItem.getFlightId();
        this.accountId = lineItem.getAccountId();
        this.name = lineItem.getName();
        this.budget = lineItem.getBudget();
        this.ccgId = lineItem.getCcgId();
        this.ccgChannelId = lineItem.getCcgChannelId();
        this.version2 = XmlDateTimeConverter.convertToEpochTime(lineItem.getCcgVersion());
        this.specialChannelId = lineItem.getSpecialChannelId();

        EffectiveLineItemTool.LineItemPropsInfo lineItemPropsInfo = EffectiveLineItemTool.getLineItemPropsInfo(owner, lineItem);
        this.propsWithFlightValues = lineItemPropsInfo.getPropsWithFlightValues();
        setEmptyProps(lineItemPropsInfo.getEmptyProps());
        this.resetAwareProps = lineItemPropsInfo.getResetAwareProps();
        this.resetAwareProps.addAll(viewResetAwareFields);
    }

    public LineItem buildLineItem() {
        LineItem lineItem = new LineItem();
        buildFlightBase(lineItem);

        lineItem.setFlightId(flightId);
        lineItem.setAccountId(accountId);
        lineItem.setName(name);
        lineItem.setBudget(budget);
        lineItem.setCcgId(ccgId);
        lineItem.setCcgChannelId(ccgChannelId);
        lineItem.setCcgVersion(XmlDateTimeConverter.convertEpochToTimestamp(version2));
        lineItem.setSpecialChannelId(specialChannelId);
        //lineItem.setPropertiesSource(EffectiveLineItemTool.buildPropsSource(getPropsWithFlightValues())); // ToDo: currently we do not sync with flight

        return lineItem;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    public Long getCcgChannelId() {
        return ccgChannelId;
    }

    public void setCcgChannelId(Long ccgChannelId) {
        this.ccgChannelId = ccgChannelId;
    }

    public Long getSpecialChannelId() {
        return specialChannelId;
    }

    public void setSpecialChannelId(Long specialChannelId) {
        this.specialChannelId = specialChannelId;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public Long getVersion2() {
        return version2;
    }

    public void setVersion2(Long version2) {
        this.version2 = version2;
    }

    public List<String> getPropsWithFlightValues() {
        return nullToEmpty(propsWithFlightValues);
    }

    public void setPropsWithFlightValues(List<String> propsWithFlightValues) {
        this.propsWithFlightValues = propsWithFlightValues;
    }

    public List<String> getResetAwareProps() {
        return nullToEmpty(resetAwareProps);
    }

    public void setResetAwareProps(List<String> resetAwareProps) {
        this.resetAwareProps = resetAwareProps;
    }

    @Override
    public Long getWhiteListId() {
        return getBlackWhiteId(super.getWhiteListId());
    }

    @Override
    public Long getBlackListId() {
        return getBlackWhiteId(super.getBlackListId());
    }

    private Long getBlackWhiteId(Long id) {
        // Currently, we do not allow to derive values from parent. The only exception is
        // Default Line Item, which is inaccessible directly
        return getPropsWithFlightValues().isEmpty() ? id : null;
    }
}
