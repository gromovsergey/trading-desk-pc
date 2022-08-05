package com.foros.session.account.yandex;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.foros.model.IdNameEntity;

import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Logger;

public abstract class TnsParser<T extends IdNameEntity> {

    private TnsHandler<T> handler;

    public TnsParser(TnsHandler hanler) {
        this.handler = hanler;
    }

    public void parse(InputStream content) {
        ObjectMapper mapper = new ObjectMapper();
        try (JsonParser parser = mapper.getFactory().createParser(content)) {
            ObjectNode node = mapper.readTree(parser);
            JsonNode itemsNode = node.findValue("items");
            if (itemsNode == null || !itemsNode.isArray()) {
                throw new RuntimeException("Not array object");
            }
            Iterator<JsonNode> itemsIterator = itemsNode.iterator();
            while (itemsIterator.hasNext()) {
                JsonNode itemNode = itemsIterator.next();
                T tns = mapper.treeToValue(itemNode, getClazz());
                handler.handle(tns);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Class<T> getClazz();

    public abstract static class TnsHandler<T extends IdNameEntity> {
        private static final Logger logger = Logger.getLogger(TnsHandler.class.getName());

        public void handle(T tns) {
            if (tns.getId() == null) {
                logger.severe("Id must be not null");
                return;
            }

            if (tns.getName() == null) {
                logger.severe("Name must be not null");
                return;
            }
            mergeObject(tns);
        }

        protected abstract void mergeObject(T tns);
    }
}

