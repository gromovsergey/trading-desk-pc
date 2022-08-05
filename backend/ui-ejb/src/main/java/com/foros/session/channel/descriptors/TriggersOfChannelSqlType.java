package com.foros.session.channel.descriptors;

import com.foros.util.EqualsUtil;
import com.foros.util.HashUtil;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

public class TriggersOfChannelSqlType implements SQLData {

    public static final String TYPE_NAME = "TRIGGERSOFCHANNEL_REC";
    public static final String TABLE_TYPE_NAME = "TRIGGERSOFCHANNEL_TBL";

    private long channelId;
    private String channelType;
    private String countryCode;
    private String triggerType;
    private String originalTrigger;
    private String normalizedTrigger;
    private String triggerGroup;
    private Boolean masked;
    private Boolean negative;

    public TriggersOfChannelSqlType(long channelId, String channelType, String countryCode,
                                    String triggerType, String originalTrigger,
                                    String normalizedTrigger, String triggerGroup, Boolean masked,
                                    Boolean negative) {
        this.channelId = channelId;
        this.channelType = channelType;
        this.countryCode = countryCode;
        this.triggerType = triggerType;
        this.originalTrigger = originalTrigger;
        this.normalizedTrigger = normalizedTrigger;
        this.triggerGroup = triggerGroup;
        this.masked = masked;
        this.negative = negative;
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return TYPE_NAME;
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        stream.writeLong(channelId);
        stream.writeString(channelType);
        stream.writeString(countryCode);
        stream.writeString(triggerType);
        stream.writeString(originalTrigger);
        stream.writeString(normalizedTrigger);
        stream.writeString(triggerGroup);
        stream.writeString(masked == null ? null : masked ? "Y" : "N");
        stream.writeString(negative == null ? null : negative ? "Y" : "N");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TriggersOfChannelSqlType)) {
            return false;
        }

        TriggersOfChannelSqlType that = (TriggersOfChannelSqlType) o;

        return EqualsUtil.equals(channelId, that.channelId, channelType, that.channelType,
                countryCode, that.countryCode, triggerType, that.triggerType,
                originalTrigger, that.originalTrigger, normalizedTrigger, that.normalizedTrigger,
                triggerGroup, that.triggerGroup, masked, that.masked, negative, that.negative);
    }

    @Override
    public int hashCode() {
        return HashUtil.calculateHash(channelId, channelType, countryCode, triggerType, originalTrigger,
                normalizedTrigger, triggerGroup, masked, negative);
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
    }
}
