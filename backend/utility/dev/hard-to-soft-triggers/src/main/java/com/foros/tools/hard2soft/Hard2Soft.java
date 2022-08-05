package com.foros.tools.hard2soft;

import com.foros.rs.client.Foros;
import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.model.TriggersType;
import com.foros.rs.client.model.advertising.channel.BehavioralChannel;
import com.foros.rs.client.model.advertising.channel.Channel;
import com.foros.rs.client.model.advertising.channel.ChannelSelector;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.OperationType;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.Result;
import com.foros.rs.client.rsclient.RsClientConfigurator;
import com.foros.rs.client.result.RsConstraintViolationException;
import com.foros.rs.client.service.AdvertisingChannelService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Hard2Soft {
    private static Foros foros;

    public static void main(String[] args) throws Exception {

        String settingsFile = args[0];
        String idsFile = args[1];

        Properties properties = readProperties(settingsFile);
        String forosBase = properties.getProperty("forosBase");
        String userToken = properties.getProperty("userToken");
        String key = properties.getProperty("key");
        int batchSize = Integer.parseInt(properties.getProperty("batchSize"));

        Properties idsProperties = readProperties(idsFile);
        String ids = idsProperties.getProperty("ids");

        RsClientConfigurator configurator = RsClientConfigurator.configure(forosBase)
                .userToken(userToken)
                .key(key);

        foros = new Foros(configurator);

        List<Long> allIds = parse(ids);
        for(int fromIndex = 0; fromIndex < allIds.size(); fromIndex += batchSize) {
            int toIndex = Math.min(fromIndex + batchSize, allIds.size());
            List<Long> batch = allIds.subList(fromIndex, toIndex);
            System.out.println("INFO: Processing: " + fromIndex + "-" + toIndex + " ids: " + batch);
            process(batch);
        }
    }

    private static Properties readProperties(String file) throws Exception {
        Properties properties = new Properties();
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            properties.load(is);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    private static List<Long> parse(String ids) {
        String[] idsArr = ids.split(",");
        ArrayList<Long> res = new ArrayList<Long>(idsArr.length);
        for (String id : idsArr) {
            res.add(Long.parseLong(id));
        }
        return res;
    }

    private static void process(List<Long> ids) {
        AdvertisingChannelService channelService = foros.getAdvertisingChannelService();

        ChannelSelector channelSelector = new ChannelSelector();
        channelSelector.setChannelIds(ids);
        Result<Channel> channelResult = channelService.get(channelSelector);

        if (channelResult.getEntities().size() != ids.size()) {
            System.out.println("WARNING: some ids are not found:" + ids);
        }

        Operations<BehavioralChannel> operations = new Operations<BehavioralChannel>();

        for (Channel channel : channelResult.getEntities()) {
            if (!(channel instanceof BehavioralChannel)) {
                System.out.println("WARNING: wrong channel type : " + channel.getId());
                continue;
            }

            BehavioralChannel original = (BehavioralChannel) channel;

            Operation<BehavioralChannel> operation = new Operation<BehavioralChannel>();

            BehavioralChannel toUpdate = new BehavioralChannel();
            TriggersType pageKeywords = processKeywords(original.getPageKeywords());
            TriggersType searchKeywords = processKeywords(original.getSearchKeywords());

            if (equals(pageKeywords, original.getPageKeywords()) && equals(searchKeywords, original.getSearchKeywords())) {
                System.out.println("INFO: nothing to update : " + channel.getId());
                continue;
            }

            toUpdate.setId(original.getId());
            toUpdate.setName(original.getName()); //TODO: workaround of OUI-22089
            toUpdate.setPageKeywords(pageKeywords);
            toUpdate.setSearchKeywords(searchKeywords);
            toUpdate.setUrls(original.getUrls());
            toUpdate.setUpdated(original.getUpdated());

            operation.setType(OperationType.UPDATE);
            operation.setEntity(toUpdate);
            operations.getOperations().add(operation);
        }

        try {
            channelService.perform(operations);
        } catch (RsConstraintViolationException e) {
            for (ConstraintViolation violation : e.getConstraintViolations()) {
                System.out.println("code=" + violation.getCode());
                System.out.println("path=" + violation.getPath());
                System.out.println("message=" + violation.getMessage());
            }
        }
    }

    private static boolean equals(TriggersType o1, TriggersType o2) {
        return o1.getPositive().equals(o2.getPositive()) && o1.getNegative().equals(o2.getNegative());
    }

    private static TriggersType processKeywords(TriggersType keywords) {
        TriggersType res = new TriggersType();
        res.setPositive(new ArrayList<String>());
        res.setNegative(keywords.getNegative());
        for (String keyword : keywords.getPositive()) {
            if (keyword.length() > 2 && keyword.charAt(0) == '"' && keyword.charAt(keyword.length() - 1) == '"') {
                res.getPositive().add(keyword.substring(1, keyword.length() - 1));
            } else {
                res.getPositive().add(keyword);
            }
        }
        return res;
    }
}
