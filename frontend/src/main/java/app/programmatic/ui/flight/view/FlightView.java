package app.programmatic.ui.flight.view;

import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;
import app.programmatic.ui.common.tool.javabean.JavaBeanAccessor;
import app.programmatic.ui.common.tool.javabean.JavaBeanUtils;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.Opportunity;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class FlightView extends FlightBaseView {
    private static final int MAX_IO_NUMBER_LENGTH = 100;

    private Long accountId;
    private Long ioId;
    private String name;
    private BigDecimal budget;
    private Long version2;

    public FlightView() {
    }

    public FlightView(Flight flight, List<String> whiteList, List<String> blackList) {
        super(flight, whiteList, blackList);
        this.ioId = flight.getOpportunity().getId();
        this.accountId = flight.getOpportunity().getAccountId();
        this.name = flight.getOpportunity().getName();
        this.budget = flight.getOpportunity().getAmount();
        this.version2 = XmlDateTimeConverter.convertToEpochTime(flight.getOpportunity().getVersion());

        JavaBeanAccessor<Flight> flightAccessor = JavaBeanUtils.createJavaEntityBeanAccessor(Flight.class);
        setEmptyProps(flightAccessor.getPropertyNames().stream()
                .filter( name -> isEmpty(flightAccessor.get(flight, name)) )
                .collect(Collectors.toList()));
    }

    public Flight buildFlight() {
        Flight flight = new Flight();
        buildFlightBase(flight);

        Opportunity opportunity = new Opportunity();
        opportunity.setFlight(flight);
        opportunity.setName(name);
        opportunity.setId(ioId);
        opportunity.setAccountId(accountId);
        opportunity.setVersion(XmlDateTimeConverter.convertEpochToTimestamp(version2));
        if (name != null) {
            opportunity.setIoNumber(name.substring(0, Integer.min(name.length(), MAX_IO_NUMBER_LENGTH)));
        }
        flight.setOpportunity(opportunity);
        flight.setBudget(budget);
        flight.setAccountId(accountId);

        return flight;
    }

    public Long getIoId() {
        return ioId;
    }

    public void setIoId(Long ioId) {
        this.ioId = ioId;
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

    private boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        return (obj instanceof Collection) ? ((Collection) obj).isEmpty() : false;
    }
}
