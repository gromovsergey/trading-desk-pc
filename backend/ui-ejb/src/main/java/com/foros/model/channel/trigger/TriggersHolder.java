package com.foros.model.channel.trigger;

import com.foros.model.channel.TriggersChannel;
import com.foros.util.StringUtil;
import com.foros.util.changes.ChangesSupportList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {
        "positiveStrings",
        "negativeStrings"
})
public abstract class TriggersHolder<T extends TriggerBase> implements Serializable {

    private static Comparator<TriggerBase> TRIGGERS_COMPARATOR = new Comparator<TriggerBase>() {
        @Override
        public int compare(TriggerBase o1, TriggerBase o2) {
            return o1.getOriginal().compareTo(o2.getOriginal());
        }
    };

    protected TriggersChannel owner;
    private TriggerCreator<T> creator;
    private String propertyName;

    private List<T> positive;
    private List<T> negative;

    public TriggersHolder() {
        // JAXB wants it
    }

    protected TriggersHolder(TriggersChannel owner, TriggerCreator<T> creator, String propertyName) {
        this.owner = owner;
        this.creator = creator;
        this.propertyName = propertyName;
    }

    @XmlTransient
    public List<T> getPositive() {
        if (positive == null) {
            positive = buildCollection(false);
        }
        return new ChangesSupportList<T>(owner, propertyName, positive);
    }

    @XmlElementWrapper(name = "positive")
    @XmlElement(name = "t")
    public String[] getPositiveStrings() {
        return toStrings(getPositive());
    }

    public void setPositiveStrings(String[] triggers) {
        setPositive(triggers);
    }

    @XmlTransient
    public String getPositiveString() {
        return StringUtil.join(Arrays.asList(getPositiveStrings()));
    }

    public void setPositiveString(String triggers) {
        setPositiveStrings(StringUtil.splitAndTrim(triggers));
    }

    @XmlElementWrapper(name = "negative")
    @XmlElement(name = "t")
    public String[] getNegativeStrings() {
        return toStrings(getNegative());
    }

    public void setNegativeStrings(String[] triggers) {
        setNegative(triggers);
    }

    @XmlTransient
    public String getNegativeString() {
        return StringUtil.join(Arrays.asList(getNegativeStrings()));
    }

    public void setNegativeString(String triggers) {
        setNegativeStrings(StringUtil.splitAndTrim(triggers));
    }

    private String[] toStrings(List<T> p) {
        String[] res = new String[p.size()];

        for (int i = 0; i < p.size(); i++) {
            T t = p.get(i);
            res[i] = t == null ? null : t.getOriginal();
        }
        return res;
    }

    public void setPositive(List<T> triggers) {
        positive = new ArrayList<T>(triggers);
        registerChange();
    }

    public void setPositive(String... triggers) {
        setPositive(asTriggers(triggers, false));
    }

    @XmlTransient
    public List<T> getNegative() {
        if (negative == null) {
            negative = buildCollection(true);
        }
        return new ChangesSupportList<T>(owner, propertyName, negative);
    }

    public void setNegative(List<T> negative) {
        this.negative = new ArrayList<T>(negative);
        registerChange();
    }

    public void setNegative(String... triggers) {
        setNegative(asTriggers(triggers, true));
    }

    /** UI-normalize triggers before storing them in the database (OUI-26059, OUI-25036) */
    public Collection<T> normalizeAndDeduplicate() {
        Set<T> removed = new HashSet<>();
        normalizeAndDeduplicate(getPositive(), removed);
        normalizeAndDeduplicate(getNegative(), removed);
        return removed;
    }

    private void normalizeAndDeduplicate(List<T> triggers, Set<T> removed) {
        Map<String, T> uiNormalized = new HashMap<>(triggers.size());
        boolean changed = false;
        for (T t : triggers) {
            String uiNormalizedLower = t.getUINormalized().toLowerCase();
            if (uiNormalized.containsKey(uiNormalizedLower)) {
                removed.add(t);
                changed = true;
                continue;
            }
            T processed = toUINormalized(t);
            changed = changed || processed != t;
            uiNormalized.put(uiNormalizedLower, processed);
        }
        if (changed) {
            triggers.clear();
            triggers.addAll(uiNormalized.values());
        }
    }

    private T toUINormalized(T t) {
        String uiNormalized = t.getUINormalized();
        if (uiNormalized.equals(t.getOriginal())) {
            return t;
        } else {
            return creator.create(uiNormalized, t.isNegative);
        }
    }

    private List<T> buildCollection(boolean isNegative) {
        Set<ChannelTrigger> triggers;
        if (owner != null && owner.isTriggersInitialized()) {
            triggers = owner.getTriggers();
        } else {
            triggers = Collections.emptySet();
        }
        return filterTriggers(triggers, isNegative);
    }

    public boolean isEmpty() {
        return getPositive().isEmpty() && getNegative().isEmpty();
    }

    public List<T> getAll() {
        List<T> p = getPositive();
        List<T> n = getNegative();
        List<T> res = new ArrayList<T>(p.size() + n.size());
        res.addAll(p);
        res.addAll(n);
        return res;
    }

    public void set(TriggersHolder<T> holder) {
        if (holder != null) {
            setPositive(holder.getPositive());
            setNegative(holder.getNegative());
        } else {
            setNull();
        }
    }

    public void setNull() {
        positive = null;
        negative = null;
        registerChange();
    }

    public void clear() {
        setPositive(new ArrayList<T>());
        setNegative(new ArrayList<T>());
    }

    public int size() {
        return getPositive().size() + getNegative().size();
    }

    public void setAll(Collection<T> triggers) {
        List<T> p = new ArrayList<T>();
        List<T> n = new ArrayList<T>();
        for (T trigger : triggers) {
            if (trigger.isNegative()) {
                n.add(trigger);
            } else {
                p.add(trigger);
            }
        }
        setPositive(p);
        setNegative(n);
    }

    public static PageKeywordsHolder pageKeywords(TriggersChannel owner) {
        return new PageKeywordsHolder(owner);
    }

    public static SearchKeywordsHolder searchKeywords(TriggersChannel owner) {
        return new SearchKeywordsHolder(owner);
    }

    public static UrlsHolder urls(TriggersChannel owner) {
        return new UrlsHolder(owner);
    }

    public static UrlKeywordsHolder urlKeywords(TriggersChannel owner) {
        return new UrlKeywordsHolder(owner);
    }

    @Override
    public String toString() {
        return String.format("TriggersHolder [positive=%s, negative=%s]", toString(positive), toString(negative));
    }

    private String toString(List<T> triggers) {
        return triggers == null ? "null" : String.valueOf(triggers.size());
    }

    private void registerChange() {
        if (owner != null) {
            owner.registerChange(propertyName);
        }
    }

    private List<T> asTriggers(String[] triggers, boolean isNegative) {
        List<T> res = new ArrayList<T>(triggers.length);
        for (String trigger : triggers) {
            res.add(creator.create(trigger, isNegative));
        }
        return res;
    }

    private List<T> filterTriggers(Collection<ChannelTrigger> triggers, boolean isNegative) {
        List<T> res = new ArrayList<T>();
        Character typeLetter = creator.getType().getLetter();
        for (ChannelTrigger trigger : triggers) {
            if (typeLetter.equals(trigger.getTriggerType()) && trigger.isNegative() == isNegative) {
                res.add(creator.create(trigger.getOriginalTrigger(), trigger.isNegative()));
            }
        }
        return res;
    }

    public interface TriggerCreator<T> extends Serializable {
        public T create(String originalTrigger, boolean isNegative);

        public TriggerType getType();
    }

    private static abstract class KeywordTriggerCreatorSupport<T> implements TriggerCreator<T> {
        private final TriggersChannel owner;

        protected KeywordTriggerCreatorSupport(TriggersChannel owner) {
            this.owner = owner;
        }

        protected String getCountryCode() {
            return owner == null || owner.getCountry() == null ? null : owner.getCountry().getCountryCode();
        }
    }

    protected static final class PageKeywordTriggerCreator extends KeywordTriggerCreatorSupport<PageKeywordTrigger> {
        protected PageKeywordTriggerCreator(TriggersChannel owner) {
            super(owner);
        }

        @Override
        public PageKeywordTrigger create(String originalTrigger, boolean isNegative) {
            return new PageKeywordTrigger(getCountryCode(), originalTrigger, isNegative);
        }

        @Override
        public TriggerType getType() {
            return TriggerType.PAGE_KEYWORD;
        }
    }

    protected static final class SearchKeywordTriggerCreator extends KeywordTriggerCreatorSupport<SearchKeywordTrigger> {
        protected SearchKeywordTriggerCreator(TriggersChannel owner) {
            super(owner);
        }

        @Override
        public SearchKeywordTrigger create(String originalTrigger, boolean isNegative) {
            return new SearchKeywordTrigger(getCountryCode(), originalTrigger, isNegative);
        }

        @Override
        public TriggerType getType() {
            return TriggerType.SEARCH_KEYWORD;
        }
    }

    protected static final class UrlTriggerCreator implements TriggerCreator<UrlTrigger> {
        @Override
        public UrlTrigger create(String originalTrigger, boolean isNegative) {
            return new UrlTrigger(originalTrigger, isNegative);
        }

        @Override
        public TriggerType getType() {
            return TriggerType.URL;
        }
    }

    protected static final class UrlKeywordTriggerCreator extends KeywordTriggerCreatorSupport<UrlKeywordTrigger> {
        protected UrlKeywordTriggerCreator(TriggersChannel owner) {
            super(owner);
        }

        @Override
        public UrlKeywordTrigger create(String originalTrigger, boolean isNegative) {
            return new UrlKeywordTrigger(getCountryCode(), originalTrigger, isNegative);
        }

        @Override
        public TriggerType getType() {
            return TriggerType.URL_KEYWORD;
        }
    }

    public static <T extends TriggerBase> boolean equals(TriggersHolder<T> list1, TriggersHolder<T> list2) {
        return equals(list1.getPositive(), list2.getPositive()) && equals(list1.getNegative(), list2.getNegative());
    }

    private static <T extends TriggerBase> boolean equals(List<T> list1, List<T> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        TriggerBase[] a1 = new TriggerBase[list1.size()];
        Arrays.sort(list1.toArray(a1), TRIGGERS_COMPARATOR);
        TriggerBase[] a2 = new TriggerBase[list2.size()];
        Arrays.sort(list2.toArray(a2), TRIGGERS_COMPARATOR);
        return Arrays.equals(a1, a2);
    }

    public static boolean copyChangedTriggers(TriggersChannel channel, TriggersChannel existing) {
        boolean res = false;

        if (channel.isChanged("pageKeywords") && !TriggersHolder.equals(existing.getPageKeywords(), channel.getPageKeywords())) {
            existing.setPageKeywords(channel.getPageKeywords());
            res = true;
        }

        if (channel.isChanged("searchKeywords") && !TriggersHolder.equals(existing.getSearchKeywords(), channel.getSearchKeywords())) {
            existing.setSearchKeywords(channel.getSearchKeywords());
            res = true;
        }

        if (channel.isChanged("urls") && !TriggersHolder.equals(existing.getUrls(), channel.getUrls())) {
            existing.setUrls(channel.getUrls());
            res = true;
        }

        if (channel.isChanged("urlKeywords") && !TriggersHolder.equals(existing.getUrlKeywords(), channel.getUrlKeywords())) {
            existing.setUrlKeywords(channel.getUrlKeywords());
            res = true;
        }
        return res;
    }
}
