package com.foros.action.channel.behavioral;

import com.foros.action.IdNameVersionForm;
import com.foros.model.channel.BehavioralParametersUnits;
import com.foros.util.StringUtil;
import com.foros.action.IdNameForm;

public class BehavioralParametersForm extends IdNameForm<String> {
    private boolean enabled;
    private String from;
    private String minimumVisits;
    private String to;
    private BehavioralParametersUnits units = BehavioralParametersUnits.MINUTES;
    private String triggerType;
    private String weight = "1";
    private TriggerListForm triggerList = new TriggerListForm();

    public static class TriggerListForm extends IdNameVersionForm {
        private String triggerList;

        public TriggerListForm() {

        }

        public String getTriggerList() {
            return triggerList;
        }

        public void setTriggerList(String triggerList) {
            this.triggerList = triggerList;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BehavioralParametersUnits getUnits() {
        return units;
    }

    public void setUnits(BehavioralParametersUnits units) {
        this.units = units;
    }

    public String getUnitsName() {
        return units.getName();
    }

    public void setUnitsName(String name) {
        this.units = BehavioralParametersUnits.byName(name);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTimeFrom(Long timeFrom) {
        if (timeFrom != null && timeFrom > 0) {
            if (timeFrom % (60 * 60 * 24) == 0) {
                from = "" + timeFrom / (60 * 60 * 24);
                units = BehavioralParametersUnits.DAYS;
            } else {
                if (timeFrom % (60 * 60) == 0) {
                    from = "" + timeFrom / (60 * 60);
                    units = BehavioralParametersUnits.HOURS;
                } else {
                    from = "" + timeFrom / 60;
                    units = BehavioralParametersUnits.MINUTES;
                }
            }
        } else {
            from = (timeFrom == null) ? null : "0";
        }
    }

    public void setTimeTo(Long timeTo) {
        if (timeTo != null && timeTo > 0) {
            if (timeTo % (60 * 60 * 24) == 0 && timeTo / (60 * 60 * 24) > 0) {
                to = "" + timeTo / (60 * 60 * 24);
                units = BehavioralParametersUnits.DAYS;
            } else {
                if (timeTo % (60 * 60) == 0 && timeTo / (60 * 60) > 0) {
                    to = "" + timeTo / (60 * 60);
                    units = BehavioralParametersUnits.HOURS;
                } else {
                    to = "" + timeTo / 60;
                    units = BehavioralParametersUnits.MINUTES;
                }
            }
        } else {
            to = (timeTo == null) ? null : "0";
        }
    }

    public Long getTimeFrom() {
        if (StringUtil.isPropertyNotEmpty(from)) {
            if (units != null && units.equals(BehavioralParametersUnits.MINUTES)) {
                return Long.parseLong(from) * 60;
            } else {
                if (units != null && units.equals(BehavioralParametersUnits.HOURS)) {
                    return Long.parseLong(from) * 60 * 60;
                } else {
                    if (units != null && units.equals(BehavioralParametersUnits.DAYS)) {
                        return Long.parseLong(from) * 60 * 60 * 24;
                    }
                }
            }
        }
        
        return null;
    }

    public Long getTimeTo() {
        if (StringUtil.isPropertyNotEmpty(to)) {
            if (units != null && units.equals(BehavioralParametersUnits.MINUTES)) {
                return Long.parseLong(to) * 60;
            } else {
                if (units != null && units.equals(BehavioralParametersUnits.HOURS)) {
                    return Long.parseLong(to) * 60 * 60;
                } else {
                    if (units != null && units.equals(BehavioralParametersUnits.DAYS)) {
                        return Long.parseLong(to) * 60 * 60 * 24;
                    }
                }
            }
        }
        
        return null;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public TriggerListForm getTriggerList() {
        return triggerList;
    }

    public void setTriggerList(TriggerListForm triggerList) {
        this.triggerList = triggerList;
    }

    public String getMinimumVisits() {
        return minimumVisits;
    }

    public void setMinimumVisits(String minimumVisits) {
        this.minimumVisits = minimumVisits;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
