package com.foros.session.creative;

import com.foros.model.Status;
import com.foros.model.creative.CreativeSize;
import com.foros.session.DisplayStatusEntityTO;
import com.foros.util.i18n.LocalizableNameProvider;

public class CreativeSizeTO extends DisplayStatusEntityTO {
    private String protocolName;
    private Long width;
    private Long height;

    public CreativeSizeTO() {
    }

    public CreativeSizeTO(CreativeSize cs) {
        this(cs.getId(), cs.getDefaultName(), cs.getStatus().getLetter(), cs.getProtocolName(), cs.getWidth(), cs.getHeight());
    }

    public CreativeSizeTO(Long id, String name, char status, Long width, Long height) {
        this(id, name, status, null, width, height);
    }

    public CreativeSizeTO(Long id, String name, char status, String protocolName, Long width, Long height) {
        super(id, name, status, CreativeSize.getDisplayStatus(Status.valueOf(status)));
        this.protocolName = protocolName;
        this.width = width;
        this.height = height;
    }

    @Override
    protected String getProvidedResKey() {
        return LocalizableNameProvider.CREATIVE_SIZE.getResourceKey(getId());
    }

    public String getProtocolName() {
        return protocolName;
    }

    public Long getWidth() {
        return width;
    }

    public Long getHeight() {
        return height;
    }
}
