package com.foros.session.fileman;

public class UnpackOptions {
    public static final UnpackOptions DEFAULT = new UnpackOptions(true, MergeMode.ADD_UPDATE);

    private final boolean transactional;
    private final MergeMode mergeMode;

    public UnpackOptions(boolean transactional, MergeMode mergeMode) {
        this.transactional = transactional;
        this.mergeMode = mergeMode;
    }

    public enum MergeMode {
        // add new files and update existing files
        ADD_UPDATE
//        // add new files, existing files will not be updated
//        ADD,
//        // all files will be deleted and new ones will be added
//        REPLACE
    }

    public boolean isTransactional() {
        return transactional;
    }

    public MergeMode getMergeMode() {
        return mergeMode;
    }
}
