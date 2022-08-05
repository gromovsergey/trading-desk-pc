package com.foros.util.tree;

import java.util.List;

/**
 * @author Vladimir
 */
public interface TreeNode<T> {
    public TreeNode<? extends T> getParent();
    public List<? extends TreeNode<T>> getChildren();
    public T getElement();
    public int getLevel();
    public boolean isLeaf();
}
