package com.foros.util.tree;

import java.util.*;

/**
 * @author Vladimir
 */
public class TreeHolder<T> extends AbstractCollection<TreeNode<T>> {
    private TreeNode<T> root;
    private Integer size = null;

    public TreeHolder(TreeNode<T> root) {
        this.root = root;
    }

    public Iterator<TreeNode<T>> iterator() {
        return new TreeIterator();
    }

    public int size() {
        if (size == null) {
            size = calculateSize(root); 
        }

        return size;
    }

    public void sort(Comparator<TreeNode<T>> comparator) {
        internalSort(root, comparator);
    }

    private void internalSort(TreeNode<T> node, Comparator<TreeNode<T>> comparator) {
        if (!node.isLeaf()) {
            Collections.sort(node.getChildren(), comparator);
            for (TreeNode<T> child : node.getChildren()) {
                internalSort(child, comparator);
            }
        }
    }

    private int calculateSize(TreeNode<T> node) {
        int currSize = 1;

        for (TreeNode<T> child : node.getChildren()) {
            currSize += calculateSize(child);
        }

        return currSize;
    }

    private class TreeIterator implements Iterator<TreeNode<T>> {
        private Iterator<TreeNode<T>> iterator;

        private TreeIterator() {
            List<TreeNode<T>> serializedTree = new ArrayList<TreeNode<T>>(size());
            serialize(serializedTree, root);
            iterator = serializedTree.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public TreeNode<T> next() {
            return iterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void serialize(List<TreeNode<T>> serializedTree, TreeNode<T> node) {
            serializedTree.add(node);

            for (TreeNode<T> child : node.getChildren()) {
                serialize(serializedTree, child);
            }
        }
    }
}
