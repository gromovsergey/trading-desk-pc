package com.foros.session.fileman;

import group.Unit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(Unit.class)
public class FileUtilsTest {
    @Test
    public void trimPathName() throws Exception {
        Assert.assertTrue(FileUtils.trimPathName(null).equals(""));
        Assert.assertTrue(FileUtils.trimPathName("").equals(""));
        Assert.assertTrue(FileUtils.trimPathName("  \t\n").equals(""));

        Assert.assertTrue(FileUtils.trimPathName("com\\foros").equals("com/foros/"));
        Assert.assertTrue(FileUtils.trimPathName("com/foros").equals("com/foros/"));
        Assert.assertTrue(FileUtils.trimPathName("\\com\\foros").equals("com/foros/"));
        Assert.assertTrue(FileUtils.trimPathName("/com/foros").equals("com/foros/"));
        Assert.assertTrue(FileUtils.trimPathName("com\\foros\\").equals("com/foros/"));
        Assert.assertTrue(FileUtils.trimPathName("com/foros/").equals("com/foros/"));
        Assert.assertTrue(FileUtils.trimPathName("\\com\\foros\\").equals("com/foros/"));
        Assert.assertTrue(FileUtils.trimPathName("/com/foros/").equals("com/foros/"));

        Assert.assertTrue(FileUtils.trimPathName("/com/////foros/").equals("com/foros/"));
        Assert.assertTrue(FileUtils.trimPathName("///////com/foros/").equals("com/foros/"));
        Assert.assertTrue(FileUtils.trimPathName("/com/foros///////").equals("com/foros/"));
        Assert.assertTrue(FileUtils.trimPathName("//").equals("/"));
    }

    @Test
    public void isNamespaceRestrictionsApply() throws Exception {
        assertFalse(FileUtils.isNamespaceRestrictionsApply(null));
        assertFalse(FileUtils.isNamespaceRestrictionsApply(""));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("-"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("-abc"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("~"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("~abc"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply(" "));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("\\"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("/"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("*"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply(":"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("?"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("\""));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("<"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply(">"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply("|"));
        assertFalse(FileUtils.isNamespaceRestrictionsApply(new StringBuffer(254).toString()));
        assertTrue(FileUtils.isNamespaceRestrictionsApply("a"));
        assertTrue(FileUtils.isNamespaceRestrictionsApply("abv"));
        assertTrue(FileUtils.isNamespaceRestrictionsApply("abv"));
        assertTrue(FileUtils.isNamespaceRestrictionsApply("абв"));
    }

    @Test
    public void generatePathById() {
        assertEquals("0000/0000/0055", FileUtils.generatePathById(Long.valueOf(55)));
        assertEquals("0000/0000/0155", FileUtils.generatePathById(Long.valueOf(155)));
        assertEquals("0002/0000/0043", FileUtils.generatePathById(Long.valueOf(200000043L)));
        assertEquals("0020/0000/0043", FileUtils.generatePathById(Long.valueOf(2000000043L)));
        assertEquals("2000/0000/0043", FileUtils.generatePathById(Long.valueOf(200000000043L)));
    }
}
