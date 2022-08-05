package com.foros.audit.serialize.serializer;

import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.dom4j.CharacterData;
import org.dom4j.Node;

public class TriggersAuditSerializerHelper {
    public static final String ADDED_XPATH = "item[@changeType='ADD']/text()";
    public static final String REMOVED_XPATH = "item[@changeType='REMOVE']/text()";

    public static String fetchAddedTriggers(Node triggersNode) {
        return fetchTriggers(triggersNode, ADDED_XPATH);
    }

    public static String fetchRemovedTriggers(Node triggersNode) {
        return fetchTriggers(triggersNode, REMOVED_XPATH);
    }

    private static String fetchTriggers(Node triggersNode, String itemsFilter) {
        List nodes = triggersNode.selectNodes(itemsFilter);
        List<String> strings = new ArrayList<>(nodes.size());

        CollectionUtils.collect(nodes, new Transformer() {
            @Override
            public Object transform(Object input) {
                return ((CharacterData) input).getText();
            }
        }, strings);

        Collections.sort(strings, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return StringUtil.compareToIgnoreCase(s1, s2);
            }
        });

        return StringUtil.join(strings);
    }
}
