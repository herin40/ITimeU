package com.itti7.itimeu.data;

import android.provider.BaseColumns;

/**
 * Created by Admin on 2017-08-16.
 */

public final class TimerContract {
    private TimerContract() {}
    public static class TimerEntry implements BaseColumns {
        public static final String TABLE_NAME = "Timer";
        public static final String COLUMN_REPEAT = "repeat";
    }
    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
}
