package com.foros.model.channel;

public enum ChannelVisibility {
    PRI(false),
    PUB(true),
    CMP(true);

    private boolean publicAvailable;

    ChannelVisibility(boolean publicAvailable) {
        this.publicAvailable = publicAvailable;
    }

    public boolean isPublicAvailable() {
        return publicAvailable;
    }

    public String getName() {
        return name();
    }
}
