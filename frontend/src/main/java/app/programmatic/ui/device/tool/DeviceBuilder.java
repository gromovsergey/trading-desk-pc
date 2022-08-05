package app.programmatic.ui.device.tool;

import com.foros.rs.client.model.device.DeviceChannel;
import com.foros.rs.client.model.entity.Status;
import app.programmatic.ui.device.dao.model.DeviceNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;


public class DeviceBuilder {

    public static Collection<DeviceNode> buildTree(Collection<DeviceChannel> channels) {
        NodesLinker nodesLinker = new NodesLinker(channels.size());
        channels.stream()
                .filter( c -> c.getStatus() != Status.DELETED )
                .forEach( c -> nodesLinker.addNode(c) );
        return nodesLinker.getRootNodes().stream()
                // Root node with name = null is a "gap", i.e. it is unchecked on AccountType level (is not production case)
                .filter( node -> node.getName() != null )
                .collect(Collectors.toList());
    }

    private static class DeviceNodesMap {
        private final HashMap<Long, DeviceNode> deviceNodesMap;

        DeviceNodesMap(int size) {
            deviceNodesMap = new HashMap<>(size);
        }

        public DeviceNode getNode(Long id) {
            DeviceNode node = deviceNodesMap.get(id);
            if (node == null) {
                node = new DeviceNode(id);
                deviceNodesMap.put(id, node);
            }

            return node;
        }

        public HashMap<Long, DeviceNode> getNodes() {
            return deviceNodesMap;
        }
    }

    private static class NodesLinker {
        private final DeviceNodesMap nodes;

        public NodesLinker(int nodesSize) {
            this.nodes = new DeviceNodesMap(nodesSize);
        }

        public void addNode(DeviceChannel channel) {
            DeviceNode node = nodes.getNode(channel.getId());
            node.setName(channel.getName());

            if (hasParent(channel)) {
                DeviceNode parent = nodes.getNode(getParentId(channel));
                parent.addChild(node);
            }
        }

        public Collection<DeviceNode> getRootNodes() {
            return nodes.getNodes().values().stream()
                    .filter( node -> node.getParentId() == null )
                    .collect(Collectors.toList());
        }

        public static boolean hasParent(DeviceChannel channel) {
            return channel.getParentChannel() != null && channel.getParentChannel().getId() != null;
        }

        public static Long getParentId(DeviceChannel channel) {
            return channel.getParentChannel().getId();
        }
    }
}
