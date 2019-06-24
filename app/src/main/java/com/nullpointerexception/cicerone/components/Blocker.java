package com.nullpointerexception.cicerone.components;

import android.os.Handler;

public class Blocker {
    private static final int DEFAULT_BLOCK_TIME = 1000;
    private boolean mIsBlockClick;

    /**
     * Block any event occurs in x millisecond to prevent spam action
     * @return false if not in block state, otherwise return true.
     */
    public boolean block(int blockInMillis) {
        if (!mIsBlockClick) {
            mIsBlockClick = true;
            new Handler().postDelayed(() -> mIsBlockClick = false, blockInMillis);
            return false;
        }
        return true;
    }

    public boolean block() {
        return block(DEFAULT_BLOCK_TIME);
    }
}