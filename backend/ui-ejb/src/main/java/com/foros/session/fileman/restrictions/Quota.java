package com.foros.session.fileman.restrictions;

public class Quota {
    public static final long NO_LIMIT = -1;

    private long fileSizeAvailable = NO_LIMIT;

    /**
     * @return max file size available user allowed to create.
     */
    public long getFileSizesAvailable() {
        return fileSizeAvailable;
    }

    public void setFileSizeAvailable(long fileSizeAvailable) {
        this.fileSizeAvailable = fileSizeAvailable;
    }
}
