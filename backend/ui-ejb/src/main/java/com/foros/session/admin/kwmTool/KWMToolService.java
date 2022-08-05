package com.foros.session.admin.kwmTool;

import java.util.List;
import javax.ejb.Local;

@Local
public interface KWMToolService {
    static final int URL = 0;
    static final int TEXT = 1;
    static final int HTML = 2;

    KWMToolResult runKWMTool(int sourceFormat, String url, int maxSize, int loops, String xmlConfig, String inputText, String userAgent) throws KWMToolException;
}
