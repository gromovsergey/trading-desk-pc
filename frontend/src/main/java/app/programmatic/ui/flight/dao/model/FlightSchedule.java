package app.programmatic.ui.flight.dao.model;

import app.programmatic.ui.common.model.EntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;


@Entity
public class FlightSchedule extends EntityBase<Long> {
    @SequenceGenerator(name = "FlightScheduleGen", sequenceName = "flightschedule_schedule_id_seq", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FlightScheduleGen")
    @Column(name = "schedule_id", nullable = false)
    private Long id;

    @JoinColumn(name = "flight_id", referencedColumnName = "flight_id")
    @ManyToOne
    private FlightBase flight;

    @Column(name = "time_from", nullable = false)
    private Long timeFrom;

    @Column(name = "time_to", nullable = false)
    private Long timeTo;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FlightBase getFlight() {
        return flight;
    }

    public void setFlight(FlightBase flight) {
        this.flight = flight;
    }

    public Long getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Long timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Long getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Long timeTo) {
        this.timeTo = timeTo;
    }

    public void copyBusinessProperties(FlightSchedule from) {
        if (from == null) {
            return;
        }

        setTimeFrom(from.getTimeFrom());
        setTimeTo(from.getTimeTo());
    }

    public static FlightSchedule cloneBusinessProperties(FlightSchedule from) {
        FlightSchedule result = new FlightSchedule();
        result.copyBusinessProperties(from);
        return result;
    }
}
