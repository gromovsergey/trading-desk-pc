package com.foros.web.taglib;

import com.foros.util.tree.TreeHolder;
import com.foros.util.tree.TreeNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author Vladimir
 */
public class TreeTag extends BodyTagSupport {

    private TreeHolder tree;
    private Iterator<TreeNode> nodeIterator;
    private String itemId;
    private TreeNode item;

    private Collection<TreeNode> openNodes;

    private int lastLevel;

    @Override
    public int doStartTag() throws JspException {
        lastLevel = -1;

        nodeIterator = tree.iterator();

        item = nodeIterator.next();

        return super.doStartTag();
    }

    @Override
    public void release() {
        nodeIterator = null;
        tree = null;
        item = null;
        itemId = null;
        lastLevel = -1;
    }

    @Override
    public int doAfterBody() throws JspException {
        BodyContent body = getBodyContent();
        JspWriter out = body.getEnclosingWriter();

        if (openNodes == null) {
            openNodes = Collections.emptyList();
        }

        try {
            if (item.getElement() != null) {
                if (item.getLevel() > lastLevel) {
                    out.write("<ul class=\"treeClickable\">");
                } else if (item.getLevel() < lastLevel) {
                    int levelDiff = lastLevel - item.getLevel();

                    while (levelDiff-- > 1) {
                        out.write("</ul></li>");
                    }

                    out.write("</ul>");

                    if (hasNext()) {
                        out.write("</li>");
                    }
                }

                if (item.isLeaf()) {
                    out.write("<li class=\"treeBullet\"><div class=\"expand\"></div>");
                    out.write(body.getString());
                    out.write("</li>");
                } else {
                    out.write("<li class=\"" + (openNodes.contains(item)? "treeOpen": "treeClosed") + "\"><div class=\"expand\"></div>");
                    out.write(body.getString());
                }
            }

            body.clear();

            if (hasNext()) {
                lastLevel = item.getLevel();
                next();
                pageContext.setAttribute(itemId, item);
                return EVAL_BODY_AGAIN;
            } else {
                out.write("</ul>");
                return EVAL_BODY_INCLUDE;
            }

        } catch (IOException e) {
            throw new JspTagException(e);
        }
    }

    public TreeNode next() {
        return item = nodeIterator.next();
    }

    public boolean hasNext() {
        return nodeIterator.hasNext();
    }

    public void setItems(Object tree) {
        if (tree instanceof TreeHolder) {
            this.tree = (TreeHolder) tree;
        } else if (tree instanceof TreeNode) {
            final TreeNode treeNode = (TreeNode) tree;
            //noinspection unchecked
            this.tree = new TreeHolder(new TreeNode() {
                @Override
                public TreeNode getParent() {
                    return null;
                }

                @Override
                public List<? extends TreeNode> getChildren() {
                    return Arrays.asList(treeNode);
                }

                @Override
                public Object getElement() {
                    return null;
                }

                @Override
                public int getLevel() {
                    return treeNode.getLevel() - 1;
                }

                @Override
                public boolean isLeaf() {
                    return false;
                }
            });
        }
    }

    public Object getItems() {
        return tree;
    }

    public String getVar() {
        return itemId;
    }

    public void setVar(String var) {
        this.itemId = var;
    }

    public Collection<TreeNode> getOpenNodes() {
        return openNodes;
    }

    public void setOpenNodes(Collection<TreeNode> openNodes) {
        this.openNodes = openNodes;
    }
}
