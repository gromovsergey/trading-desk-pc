package com.foros.model.channel.trigger;

import com.foros.util.url.TriggerNormalization;
import com.foros.util.url.TriggerQANormalization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UrlTrigger extends TriggerBase {

    private String normalized;
    private String qaNormalized;
    private boolean wildcard = true;
    private String plain;
    private String group;
    private boolean masked;

    public UrlTrigger(){}
    
    public UrlTrigger(String original, boolean isNegative) {
        super(original, isNegative);
    }

    private void init() {
        if (original == null || normalized != null) {
            return;
        }
        normalized = TriggerNormalization.normalizeURL(original);
        qaNormalized =  TriggerQANormalization.normalizeURL(original);
        plain = qaNormalized;
        if (plain.startsWith("\"") && plain.endsWith("\"")) {
            wildcard = false;
            plain = plain.length() > 1 ? plain.substring(1, plain.length() - 1) : "";
        }
        int index = plain.indexOf('/');
        group = index > 0 ? plain.substring(0, index) : plain;
    }

    public boolean maskedBy(UrlTrigger trigger) {
        init();
        trigger.init();
        if (isNegative && !trigger.isNegative) {
            return false;
        }
        if (trigger.wildcard) {
            if (plain.startsWith(trigger.plain)) {
                return true;
            }
        } else if (plain.equals(trigger.plain)) {
            return true;
        }
        return false;
    }

    public static Collection<UrlTrigger> unique(Collection<UrlTrigger> triggers) {
        Set<String> positives = new HashSet<String>();
        Set<String> negatives = new HashSet<String>();
        List<UrlTrigger> unique = new ArrayList<UrlTrigger>(triggers.size());
        for (UrlTrigger trigger : triggers) {
            String name = trigger.getOriginal();
            if (trigger.isNegative()) {
                if (negatives.add(name)) {
                    unique.add(trigger);
                }
            } else {
                if (positives.add(name)) {
                    unique.add(trigger);
                }
            }
        }
        return unique;
    }

    public static void calcMasked(Collection<UrlTrigger> urlTriggers) {
        UrlTrigger[] triggers = urlTriggers.toArray(new UrlTrigger[urlTriggers.size()]);
        for (int i = 0; i < triggers.length; i++) {
            if (!triggers[i].isMasked()) {
                for (int j = 0; j < triggers.length; j++) {
                    if (i != j) {
                        if (triggers[i].maskedBy(triggers[j])) {
                            if (!triggers[j].maskedBy(triggers[i]) || i > j) {
                                triggers[i].setMasked(true);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getNormalized() {
        init();
        return normalized;
    }

    @Override
    public String getUINormalized() {
        return getNormalized();
    }

    @Override
    public String getQANormalized() {
        init();
        return (isNegative ? "-" : "") + qaNormalized;
    }

    public boolean isWildcard() {
        init();
        return wildcard;
    }

    public String getPlain() {
        init();
        return plain;
    }

    public String getGroup() {
        init();
        return group;
    }

    public boolean isMasked() {
        return masked;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }
}
