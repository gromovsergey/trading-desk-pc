package com.foros.session.site;

import com.foros.model.site.Tag;

import java.io.ByteArrayOutputStream;
import javax.ejb.Local;

@Local
public interface TagsPreviewService {

    ByteArrayOutputStream getLivePreview(Tag tag);

}
