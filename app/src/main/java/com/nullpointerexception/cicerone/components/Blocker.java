package com.nullpointerexception.cicerone.components;

import android.os.Handler;

/**
 * Blocker
 *
 * Allow to control if the button is already been pressed in the specific time
 */
public class Blocker {
    private static final int DEFAULT_BLOCK_TIME = 1000;
    private boolean mIsBlockClick;

    /**
     * Block any event occurs in x milliseconds to prevent spam action
     * @param blockInMillis: time to control
     * @return false if not in blocks state, otherwise return true.
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