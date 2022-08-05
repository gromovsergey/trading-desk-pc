package com.foros.tools;

import com.foros.model.channel.trigger.UrlTrigger;
import com.foros.session.channel.descriptors.BulkUpdateChannelTriggersStoredProcedureDescriptor;
import com.foros.session.channel.descriptors.ChannelTriggersContainer;
import com.foros.session.security.descriptiors.AuditUserSqlType;
import com.foros.util.SQLUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class Updater {
    private static final Logger logger = Logger.getLogger(Updater.class.getName());

    private Collection<ChannelTriggersContainer> triggersToAdd;
    private List<ChannelTriggersContainer> channels;
    private JdbcTemplate template;

    public Updater(JdbcTemplate template) {
        this.template = template;
    }

    public void setTriggersToAdd(Collection<ChannelTriggersContainer> triggersToAdd) {
        this.triggersToAdd = triggersToAdd;
    }

    public void prepareChannels() {
        logger.info("Querying for channels...");

        Set<Long> channelIds = new HashSet<>();
        for (ChannelTriggersContainer ctc : triggersToAdd) {
            channelIds.add(ctc.getChannel().getId());
        }

        String sql = "select " +
                " c.country_code, " +
                " c.channel_id, " +
                " ct.trigger_type, " +
                " ct.original_trigger, " +
                " ct.negative " +
                " from channeltrigger ct " +
                " join channel c on c.channel_id=ct.channel_id " +
                " where " + SQLUtil.formatINClause("ct.channel_id", channelIds);

        final TriggersCollector triggersCollector = new TriggersCollector();

        template.query(sql, new Object[0], new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                long channelId = rs.getLong("channel_id");
                String countryCode = rs.getString("country_code");
                String triggerType = rs.getString("trigger_type");
                String originalTrigger = rs.getString("original_trigger");
                String negative = rs.getString("negative");

                triggersCollector.add(
                        channelId,
                        countryCode,
                        triggerType,
                        originalTrigger,
                        "Y".equals(negative)
                );
            }
        });

        logger.info(String.format("%d channels was found", triggersCollector.getTriggers().size()));

        triggersCollector.addAll(triggersToAdd);

        channels = triggersCollector.getTriggers();
        for (ChannelTriggersContainer channel : channels) {
            UrlTrigger.calcMasked(channel.getUrlTriggers());
        }
    }

    public void doUpdate() {
        AuditUserSqlType auditUser = new AuditUserSqlType(null, "127.0.0.1");

        for (int from = 0; from < channels.size(); from = from + 10) {
            int to = Math.min(from + 10, channels.size());
            logger.info(String.format("Updating channels %s to %s", from + 1, to));
            List<ChannelTriggersContainer> chunk = channels.subList(from, to);


            final BulkUpdateChannelTriggersStoredProcedureDescriptor descriptor = new BulkUpdateChannelTriggersStoredProcedureDescriptor(chunk, auditUser);

            template.execute(
                    new CallableStatementCreator() {
                        @Override
                        public CallableStatement createCallableStatement(Connection con) throws SQLException {
                            con.getTypeMap().putAll(descriptor.getRegisteredTypes());
                            CallableStatement cs = con.prepareCall(descriptor.getCommand());
                            return cs;
                        }
                    },
                    new CallableStatementCallback<Object>() {
                        @Override
                        public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                            descriptor.prepareStatement(cs);
                            cs.execute();
                            return null;
                        }
                    }
            );
        }
    }
}

