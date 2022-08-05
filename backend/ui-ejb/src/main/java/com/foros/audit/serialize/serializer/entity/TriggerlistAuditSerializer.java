package com.foros.audit.serialize.serializer.entity;

import com.foros.audit.serialize.serializer.AuditSerializer;
import com.foros.changes.inspection.ChangeNode;

import javax.xml.stream.XMLStreamWriter;

public class TriggerlistAuditSerializer implements AuditSerializer {
    private static final int MAX_NODES_COUNT = 50;

    @Override
    public void startSerialize(XMLStreamWriter writer, ChangeNode changeNode) {
    }

    @Override
    public void endSerialize(XMLStreamWriter writer) {
    }

    public void serialize(XMLStreamWriter writer, ChangeNode node) {
//        Change theChange = node.getChange();
//
//        Element elem = parent.getParent().getParent().getParent();
//        String className = elem.attributeValue("class");
//        boolean useCsvReader = DiscoverChannelList.class.getName().equals(className);
//
//        Set<String> newLines = createSet(theChange.getNewValue(), useCsvReader);
//        Set<String> oldLines = createSet(theChange.getOldValue(), useCsvReader);
//
//        removeDuplicates(newLines, oldLines);
//
//        // create alternate tree for serializing
//        ChangeNode triggerList = createCollectionNode(node.getChange().getPropertyName());
//        addRemoved(oldLines, triggerList);
//        addInserted(newLines, triggerList);
//
//        AuditSerializerFactory.create(triggerList).serialize(parent, triggerList);
    }

//    private void addInserted(Collection<String> newLines, ChangeNode triggerList) {
//        Collection<String> subCollection = getSubCollection(newLines, MAX_NODES_COUNT);
//
//        for (String newLine : subCollection) {
//            triggerList.addChildNode(createChangeNode(ChangeType.ADD, newLine, null));
//        }
//
//        if (newLines.size() > MAX_NODES_COUNT) {
//            triggerList.addChildNode(createChangeNode(ChangeType.ADD, "... and " + (newLines.size() - MAX_NODES_COUNT) + " triggers more", null));
//        }
//    }
//
//    private void addRemoved(Collection<String> oldLines, ChangeNode triggerList) {
//        Collection<String> subCollection = getSubCollection(oldLines, MAX_NODES_COUNT);
//        for (String oldLine : subCollection) {
//            triggerList.addChildNode(createChangeNode(ChangeType.REMOVE, null, oldLine));
//        }
//
//        if (oldLines.size() > MAX_NODES_COUNT) {
//            triggerList.addChildNode(createChangeNode(ChangeType.REMOVE, null, "... and " + (oldLines.size() - MAX_NODES_COUNT) + " triggers more"));
//        }
//    }
//
//    private Collection<String> getSubCollection(Collection<String> collection, int count) {
//        Collection<String> result = new ArrayList<String>(count);
//        Iterator<String> iterator = collection.iterator();
//        int i = 0;
//        while (iterator.hasNext() && i++<count) {
//            result.add(iterator.next());
//        }
//        return result;
//    }
//
//    private ChangeNode createChangeNode(ChangeType changeType, String newLine, String oldLine) {
//        Change change = new Change(newLine, oldLine, null);
//        return new ChangeNode(change, ChangeObjectType.PRIMITIVE, changeType, ContainmentType.COLLECTION_ITEM, null);
//    }
//
//    private void removeDuplicates(Set<String> newLines, Set<String> oldLines) {
//        Iterator<String> iterator = oldLines.iterator();
//        while (iterator.hasNext()) {
//            String line = iterator.next();
//            if(newLines.contains(line)) {
//                iterator.remove();
//                newLines.remove(line);
//            }
//        }
//    }
//
//    private ChangeNode createCollectionNode(String propertyName) {
//        Change change = new Change(null, null, propertyName);
//        return new ChangeNode(change, ChangeObjectType.COLLECTION, ChangeType.UNCHANGED, ContainmentType.NOT_CONTAINED, null);
//    }
//
//    private Set<String> createSet(Object value, boolean useCsvReader) {
//        if (value != null) {
//            String string = new String((byte[]) value);
//
//            if (!useCsvReader) {
//                return new HashSet<String>(Arrays.asList(StringUtil.splitByLines(string)));
//            } else {
//                try {
//                    DiscoverChannelUtils.ChannelCsvReader csvReader = new DiscoverChannelUtils.ChannelCsvReader(string);
//                    Set<String> lines = new HashSet<String>();
//
//                    while (csvReader.readRecord()) {
//                        lines.add(csvReader.getRawRecord());
//                    }
//
//                    return lines;
//                } catch (Exception e) {
//                    return new HashSet<String>(Arrays.asList(StringUtil.splitByLines(string)));
//                }
//            }
//        }
//
//        return new HashSet<String>();
//    }
//
}
