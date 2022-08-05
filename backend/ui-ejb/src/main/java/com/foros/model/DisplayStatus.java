package com.foros.model;

import com.foros.security.principal.SecurityContext;
import com.foros.util.CollectionUtils;
import com.foros.util.mapper.Converter;

import java.util.Collection;

public class DisplayStatus {
    private Long id;
    private Major major;
    private String description;
    private String externalDescription;

    public enum Major {
        LIVE('L', "displaystatus.major.live"), // green
        LIVE_NEED_ATT('A', "displaystatus.major.live_na"), // amber
        NOT_LIVE('N', "displaystatus.major.not_live"), // red
        INACTIVE('I', "displaystatus.major.inactive"), // grey
        DELETED('D', "displaystatus.major.deleted"); // grey

        private final char letter;
        private final String description;

        private Major(char letter, String description) {
            this.letter = letter;
            this.description = description;
        }

        public char getLetter() {
            return letter;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return this.name();
        }
    }

    /**
     * This class is immutable. And must be initialized only through constructor.
     *
     * @param id - Id of DisplayStatus in corresponding table. Id is not unique for the different types of entities.
     * It may have the same value for Creative and Campaign, but indicates different display status.
     * @param major - major part of display status. (Live, Not Live and etc.)
     * @param description - resource key for status description.
     */
    public DisplayStatus(Long id, Major major, String description) {
        this.id = id;
        this.major = major;
        this.description = description;
    }
    
    public DisplayStatus(Long id, Major dispStatus, String description, String externalDescription) {
        this(id, dispStatus, description);
        this.externalDescription = externalDescription;
    }

    public Long getId() {
        return id;
    }

    public Major getMajor() {
        return major;
    }

    public String getDescription() {
        if (externalDescription != null && !SecurityContext.isInternal()) {
            return externalDescription;
        }
        
        return description;        
    }

    @Override
    public String toString() {
        return "DisplayStatus: " + id + ", major: "+ major + ", desc: " + description;
    }

    public static Collection<Long> getIds(Collection<DisplayStatus> statuses) {
        if (statuses == null) {
            return null;
        }
        return CollectionUtils.convert(statuses, new Converter<DisplayStatus, Long>() {
            @Override
            public Long item(DisplayStatus value) {
                return value.getId();
            }
        });
    }
}
