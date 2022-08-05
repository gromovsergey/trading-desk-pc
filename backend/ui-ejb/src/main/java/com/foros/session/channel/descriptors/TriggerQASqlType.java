package com.foros.session.channel.descriptors;

import com.foros.util.HashUtil;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

class TriggerQASqlType implements SQLData {

    public static final String TABLE_TYPE_NAME = "TRIGGERQA_TBL";
    public static final String TYPE_NAME = "TRIGGERQA_REC";

    private char triggerType;
    private String normalizedTrigger;
    private Character wildCard;
    private char qaStatus;
    private char channelType;
    private String countryCode;
    private Long triggerId;

    public TriggerQASqlType() {
    }

    TriggerQASqlType(char triggerType, String normalizedTrigger, Character wildCard, char qaStatus, char channelType, String countryCode, Long triggerId) {
        this.triggerType = triggerType;
        this.normalizedTrigger = normalizedTrigger;
        this.wildCard = wildCard;
        this.qaStatus = qaStatus;
        this.channelType = channelType;
        this.countryCode = countryCode;
        this.triggerId = triggerId;
    }

    public String getSQLTypeName() throws SQLException {
        return TYPE_NAME;
    }

    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        this.triggerType = (char) stream.readInt();
        this.normalizedTrigger = stream.readString();
        this.wildCard = (char) stream.readInt();
        this.qaStatus = (char) stream.readInt();
        this.channelType = (char)stream.readInt();
        this.countryCode = stream.readString();
        this.triggerId = stream.readLong();
    }

    public void writeSQL(SQLOutput stream) throws SQLException {
        stream.writeString(String.valueOf(this.triggerType));
        stream.writeString(this.normalizedTrigger);
        stream.writeString(String.valueOf(this.wildCard != null ? this.wildCard : 'N'));
        stream.writeString(String.valueOf(this.qaStatus));
        stream.writeString(String.valueOf(this.channelType));
        stream.writeString(this.countryCode);
        stream.writeLong(triggerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TriggerQASqlType)) {
            return false;
        }

        TriggerQASqlType that = (TriggerQASqlType) o;

        if (triggerId.equals(that.triggerId) || triggerType != that.triggerType || !wildCard.equals(that.wildCard)
                || qaStatus != that.qaStatus
                || channelType != that.channelType || countryCode.equals(that.countryCode)) {
            return false;
        }

        if (normalizedTrigger != null ? !normalizedTrigger.equals(that.normalizedTrigger) : that.normalizedTrigger != null) {
            return false;
        }


        return true;
    }

    @Override
    public int hashCode() {
        return HashUtil.calculateHash(triggerId, triggerType, normalizedTrigger, wildCard, qaStatus, channelType, countryCode);
    }
}
