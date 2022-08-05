package com.foros.session.channel;

import com.foros.model.Status;
import com.foros.session.EntityTO;
import com.foros.util.tree.TreeNode;

import java.util.LinkedList;
import java.util.List;

public class ChannelTreeNode implements TreeNode<EntityTO> {
    private ChannelTreeNode parentChannel;
    private List<ChannelTreeNode> children = new LinkedList<ChannelTreeNode>();
    private EntityTO element;
    private int level;

    public ChannelTreeNode(EntityTO element, ChannelTreeNode parentChannel, int level) {
        this.parentChannel = parentChannel;
        this.element = element;
        this.level = level;
    }

    public ChannelTreeNode getParent() {
        return parentChannel;
    }

    public List<ChannelTreeNode> getChildren() {
        return children;
    }

    public EntityTO getElement() {
        return element;
    }

    public int getLevel() {
        return level;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public Status getInheritedStatus() {
        if (this.parentChannel == null || this.element.getStatus() == Status.DELETED){
            return this.element.getStatus();
        } else {
            if (this.parentChannel.getElement().getStatus() == Status.DELETED) {
                return Status.DELETED;
            }
            return this.parentChannel.getInheritedStatus();
        }
    }
}
