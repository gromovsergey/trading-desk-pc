package com.foros.action.site;

import static com.foros.util.StringUtil.isPropertyNotEmpty;

import com.foros.model.site.PassbackType;
import com.foros.model.site.Tag;
import com.foros.session.ServiceLocator;
import com.foros.session.site.TagsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;

public class TagHelper {
    private static boolean arePassbacksEqual(String pb1, String pb2) {
        if (pb1 != null && pb2 != null) {
            try {
                BufferedReader reader1 = new BufferedReader(new StringReader(pb1));
                BufferedReader reader2 = new BufferedReader(new StringReader(pb2));
                String line1, line2;
                while ((line1 = reader1.readLine()) != null) {
                    line2 = reader2.readLine();
                    if (!line1.equals(line2)) {
                        return false;
                    }
                }
                if (reader2.readLine() != null) {
                    return false;
                }
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return pb1 == null && pb2 == null;
        }
    }

    public static void preparePassback(Tag tag) {
        TagsService tagsService = ServiceLocator.getInstance().lookup(TagsService.class);
        if (tag.getId() != null) {
            Tag existingTag = tagsService.find(tag.getId());

            if (isPropertyNotEmpty(existingTag.getPassback())
                    && existingTag.getPassbackType() != PassbackType.HTML_URL) {
                try {
                    if (!arePassbacksEqual(tag.getPassbackHtml(), tagsService.getPassbackHtml(existingTag))) {
                        tag.registerChange("passbackHtml");
                    }
                } catch (IOException e) {
                    tag.registerChange("passbackHtml");
                }
            } else {
                if (!StringUtils.equals(tag.getPassback(), existingTag.getPassback())) {
                    tag.registerChange("passback");
                }
                tag.registerChange("passbackHtml");
            }
        } else {
            if (isPropertyNotEmpty(tag.getPassback())) {
                tag.registerChange("passback");
            } else if (isPropertyNotEmpty(tag.getPassbackHtml())) {
                tag.registerChange("passbackHtml");
            }
        }
    }

}
