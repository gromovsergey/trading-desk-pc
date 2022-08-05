package com.foros.migration;

import com.foros.model.creative.TextCreativeOption;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.CreativeToken;
import com.foros.model.template.Option;
import com.foros.util.SQLUtil;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ObjectUtils;

@Configuration
public class CreativeHash implements Migration.Executor {

    private static final int LIST_SIZE = 1000000;
    private static java.util.regex.Pattern NAME_PATTERN = java.util.regex.Pattern.compile("<|>");
    private static final int OPTIONS_LENGTH = TextCreativeOption.values().length;

    private static final int BATCH_SIZE = 1000;
    private static final int PRINT_SIZE = 1000;

    @Autowired
    private Logger logger;

    @Autowired
    @Qualifier("postgresJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private Long textTemplateId;

    private Map<Long, Option> textOptions = new HashMap<>();
    private Map<String, Option> textOptionsByToken = new HashMap<>();
    private Long hashOptionId;

    public static void main(String[] args) {
        Migration.perform(CreativeHash.class);
    }

    @Override
    public void run() throws Exception {
        prepare();

        changeTextOptionsToRequired();
        addToolTipToTextOptions();

        initHashOption();

        ArrayList<Creative> creatives = extractCreativeWithOptions();
        MultiValueMap creativesByHash = calculateMD5(creatives);
        updateCreatives(creativesByHash);

    }

    private void updateCreatives(MultiValueMap creativesByHash) {
        Map<Creative, String> hashCreatives = new HashMap<Creative, String>(LIST_SIZE);
        Map<Creative, Creative> creativesToUpdate = new HashMap<Creative, Creative>(LIST_SIZE);
        for (Object object : creativesByHash.entrySet()) {
            Entry<MultiKey, LinkedList<Creative>> entry = (Entry<MultiKey, LinkedList<Creative>>) object;
            LinkedList<Creative> creatives = entry.getValue();
            for (Creative creative : creatives) {
                hashCreatives.put(creative, (String) entry.getKey().getKey(0));
                if (creatives.size() > 1 && !creative.equals(creatives.peekFirst())) {
                    creativesToUpdate.put(creative, creatives.peekFirst());
                }
            }
        }

        final Stat stat = new Stat(hashCreatives.entrySet().size(), PRINT_SIZE);
        logger.info("Start insertHashOptionValueToCreative");
        try {
            String insertHashOptionValueToCreative = "insert into creativeoptionvalue  (option_id, creative_id, value, version ) values (?, ?, ?, Current_Timestamp)";
            jdbcTemplate.batchUpdate(insertHashOptionValueToCreative, hashCreatives.entrySet(), 1000, new ParameterizedPreparedStatementSetter<Entry<Creative, String>>() {
                @Override
                public void setValues(PreparedStatement ps, Entry<Creative, String> argument) throws SQLException {
                    stat.log();
                    ps.setLong(1, hashOptionId);
                    ps.setLong(2, argument.getKey().getId());
                    ps.setString(3, argument.getValue());
                }
            });
        } catch (RuntimeException e) {
            logger.severe("Error during migration: " + e.getMessage());
        }

        logger.info("End insertHashOptionValueToCreative");

        logger.info("Start delete creatives");
        try {
            Collection<Creative> creativesToDelete = creativesToUpdate.keySet();
            final Stat deleteStat = new Stat(creativesToDelete.size(), PRINT_SIZE);
            String deleteCreativeSql = "update creative set status = 'D' , display_status_id = 6, name = ? where creative_id = ?";

            jdbcTemplate.batchUpdate(deleteCreativeSql, creativesToDelete, BATCH_SIZE, new ParameterizedPreparedStatementSetter<Creative>() {
                @Override
                public void setValues(PreparedStatement ps, Creative argument) throws SQLException {
                    deleteStat.log();
                    ps.setLong(2, argument.getId());
                    ps.setString(1, argument.getName());
                }
            });
        } catch (RuntimeException e) {
            logger.severe("Error during migration: " + e.getMessage());
        }
        logger.info("End delete creatives");

        logger.info("Start update name");
        try {
            final Stat updateStat = new Stat(hashCreatives.keySet().size(), PRINT_SIZE);
            String updateName = "update creative set name = ? where creative_id = ?";
            jdbcTemplate.batchUpdate(updateName, hashCreatives.keySet(), BATCH_SIZE, new ParameterizedPreparedStatementSetter<Creative>() {
                @Override
                public void setValues(PreparedStatement ps, Creative argument) throws SQLException {
                    updateStat.log();
                    ps.setLong(2, argument.getId());
                    ps.setString(1, argument.getName());
                }
            });
        } catch (RuntimeException e) {
            logger.severe("Error during migration: " + e.getMessage());
        }
        logger.info("End update name");

        logger.info("Start link campaign creative");
        try {
            final Stat linkStat = new Stat(creativesToUpdate.entrySet().size(), PRINT_SIZE);
            String linkCampaignCreativesToNewCreaive = "update campaigncreative set creative_id = ? where creative_id = ?";
            jdbcTemplate.batchUpdate(linkCampaignCreativesToNewCreaive, creativesToUpdate.entrySet(), BATCH_SIZE, new ParameterizedPreparedStatementSetter<Entry<Creative, Creative>>() {
                @Override
                public void setValues(PreparedStatement ps, Entry<Creative, Creative> argument) throws SQLException {
                    linkStat.log();
                    ps.setLong(1, argument.getValue().getId());
                    ps.setLong(2, argument.getKey().getId());
                }
            });
        } catch (RuntimeException e) {
            logger.severe("Error during migration: " + e.getMessage());
        }
        logger.info("End link campaign creative");
    }

    private MultiValueMap calculateMD5(List<Creative> creatives) {

        MultiValueMap creativesByHash = (MultiValueMap) MapUtils.multiValueMap(new MultiKeyMap(), LinkedList.class);
        for (int i = 0; i < creatives.size(); i++) {
            Creative creative = creatives.get(i);
            if (i % PRINT_SIZE == 0) {
                logger.info("Calculated  hash for " + i + " creatives");
            }
            creativesByHash.put(new MultiKey(calculateHash(creative), creative.getAccount(), creative.getDisplayStatusId()), creative);
        }
        return creativesByHash;
    }

    private Object calculateHash(Creative creative) {
        StringBuilder source = new StringBuilder();
        for (TextCreativeOption textCreativeOption : TextCreativeOption.values()) {
            Option option = textOptionsByToken.get(textCreativeOption.getToken());
            CreativeOptionValue optionValue = creative.getOptionValue(textCreativeOption);
            if (optionValue != null && !ObjectUtils.nullSafeEquals(optionValue.getValue(), option.getDefaultValue())) {
                source.append(option.getToken()).append(":").append(optionValue.getValue());
            }
        }
        return DigestUtils.md5Hex(source.toString());
    }

    private ArrayList<Creative> extractCreativeWithOptions() {
        logger.info("Start extractCreativeWithOptions");
        final ArrayList<Creative> creatives = new ArrayList<>(LIST_SIZE);
        String sql = "select cr.creative_id, cr.name,  opt.option_id, opt.value, cr.account_id, cr.display_status_id from creative cr "
                + " left join creativeoptionvalue opt on cr.creative_id = opt.creative_id  and " + SQLUtil.formatINClause("opt.option_id", textOptions.keySet())
                + " where  cr.template_id = " + textTemplateId
                + " order by cr.account_id, cr.status, cr.version, cr.creative_id, opt.option_id ";
        jdbcTemplate.query(sql, new RowMapper<CreativeOptionValue>() {
            Creative last;
            @Override
            public CreativeOptionValue mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (rowNum % PRINT_SIZE == 0) {
                    logger.info("Extracted " + rowNum + " options");
                }
                long creativeId = rs.getLong(1);
                Creative creative;
                if (last == null || last.getId() != creativeId) {
                    creative = createCreative(rs, creativeId);
                    creatives.add(creative);
                    if (last != null) {
                        checkName(last);
                    }
                    last = creative;
                } else {
                    creative = last;
                }

                CreativeOptionValue optionValue = new CreativeOptionValue();
                if (rs.getObject(3) != null) {
                    String value = rs.getString(4);
                    Option option = textOptions.get(rs.getLong(3));
                    optionValue.setValue(value);
                    creative.setOptionValue(TextCreativeOption.byToken(option.getToken()), optionValue);
                }
                return optionValue;
            }

            private Creative createCreative(ResultSet rs, long creativeId) throws SQLException {
                Creative creative;
                creative = new Creative(creativeId);
                creative.setName(rs.getString(2));
                creative.setAccount(rs.getLong(5));
                creative.setDisplayStatusId(rs.getLong(6));
                return creative;
            }
        });

        checkName(creatives.get(creatives.size() - 1));

        logger.info("End extractCreativeWithOptions");
        return creatives;
    }

    private void checkName(Creative last) {
        CreativeOptionValue headLineOptionValue = last.getOptionValue(TextCreativeOption.HEADLINE);
        if (headLineOptionValue == null) {
            String defaultValue = textOptionsByToken.get(TextCreativeOption.HEADLINE.getToken()).getDefaultValue();
            if (defaultValue != null && !last.getName().equals(defaultValue)) {
                last.setName(NAME_PATTERN.matcher(defaultValue).replaceAll("_"));
            }
        } else {
            String headLine = headLineOptionValue.getValue();
            if (headLine != null && !last.getName().equals(headLine)) {
                last.setName(NAME_PATTERN.matcher(headLine).replaceAll("_"));
            }
        }
    }

    private void initHashOption() {
        hashOptionId = jdbcTemplate.queryForObject(" select opt.option_id  from options opt join template tmpl on tmpl.template_id = opt.template_id "
                + "where opt.token = '" + CreativeToken.CREATIVE_HASH + "' and tmpl.name = 'Text'", Long.class);
    }

    private void addToolTipToTextOptions() {
        try {
            MultiKeyMap toolTips = ToolTip.getToolTips();
            MultiKeyMap optionsToUpdate = new MultiKeyMap();
            for (String lang : ToolTip.LANGS) {
                for (Option option : textOptions.values()) {
                    Integer count = jdbcTemplate.queryForObject("select count(*) from dynamicresources where key = 'Option-label." + option.getId() + "' and lang = '" + lang + "'", Integer.class);
                    if (count == 0) {
                        optionsToUpdate.put(option.getId(), lang, toolTips.get(TextCreativeOption.byToken(option.getToken()), lang));
                    }
                }
            }

            jdbcTemplate.batchUpdate("insert into dynamicresources (key, lang, value) VALUES (?, ?, ?)", optionsToUpdate.entrySet(), optionsToUpdate.values().size(), new ParameterizedPreparedStatementSetter<Map.Entry<MultiKey, String>>() {
                @Override
                public void setValues(PreparedStatement ps, Map.Entry<MultiKey, String> argument) throws SQLException {
                    ps.setString(1, "Option-label." + argument.getKey().getKey(0).toString());
                    ps.setString(2, argument.getKey().getKey(1).toString());
                    ps.setString(3, argument.getValue());
                }
            });

        } catch (IOException e) {
            logger.severe("ToolTip migration fail");
        }
    }

    private void prepare() {
        textTemplateId = jdbcTemplate.queryForObject("select template_id from Template where name = '" + CreativeTemplate.TEXT_TEMPLATE + "'", Number.class).longValue();

        jdbcTemplate.query("select option_id, token, required, default_value from options opt join optiongroup gr on opt.option_group_id = gr.option_group_id where  opt.template_id = " + textTemplateId + " and gr.type = 'Advertiser'",
            new RowMapper<Option>() {
                @Override
                public Option mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Option option = new Option();
                    option.setId(rs.getLong(1));
                    option.setToken(rs.getString(2));
                    option.setRequired("Y".equals(rs.getString(3)));
                    option.setDefaultValue(rs.getString(4));
                    if (TextCreativeOption.byTokenOptional(option.getToken()) != null) {
                        textOptions.put(option.getId(), option);
                        textOptionsByToken.put(option.getToken(), option);
                    }
                    return option;
                }
            });
    }

    private void changeTextOptionsToRequired() {
        Collection<Option> optionsToUpdate = new ArrayList<Option>();

        for (Option option : textOptions.values()) {
            switch (TextCreativeOption.byToken(option.getToken())) {
            case HEADLINE:
            case DESCRIPTION_LINE_1:
            case DESCRIPTION_LINE_2:
            case DISPLAY_URL:
            case CLICK_URL:
                optionsToUpdate.add(option);
                break;
            default:
            }
        }

        jdbcTemplate.batchUpdate("update options set required = 'Y' where option_id = ?", optionsToUpdate, optionsToUpdate.size(), new ParameterizedPreparedStatementSetter<Option>() {
            @Override
            public void setValues(PreparedStatement ps, Option argument) throws SQLException {
                argument.setRequired(true);
                ps.setLong(1, argument.getId());
            }
        });
    }

    class Stat {
        private int size;
        private int printSize;
        private Integer count = 0;

        public Stat(int size, int printSize) {
            this.size = size;
            this.printSize = printSize;
        }

        public void log() {
            if (count++ % printSize == 0 || count.equals(size)) {
                logger.info(String.format(" %1$s entities of %2$s was updated", count, size));
            }
        }

    }

    static class Creative {
        private long id;
        private String name;
        private long account;
        private long displayStatusId;
        private CreativeOptionValue[] optionValues = new CreativeOptionValue[OPTIONS_LENGTH];

        public Creative(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public long getAccount() {
            return account;
        }

        public void setAccount(long account) {
            this.account = account;
        }

        public long getDisplayStatusId() {
            return displayStatusId;
        }

        public void setDisplayStatusId(long displayStatusId) {
            this.displayStatusId = displayStatusId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public CreativeOptionValue getOptionValue(TextCreativeOption option) {
            return optionValues[option.ordinal()];
        }

        public void setOptionValue(TextCreativeOption option, CreativeOptionValue optionValue) {
            optionValues[option.ordinal()] = optionValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Creative creative = (Creative) o;

            return id == creative.id;

        }

        @Override
        public int hashCode() {
            return (int) (id ^ (id >>> 32));
        }
    }

    private static class CreativeOptionValue {

        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
