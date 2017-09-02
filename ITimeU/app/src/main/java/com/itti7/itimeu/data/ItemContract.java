package com.itti7.itimeu.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 계약 클래스는 URI, 테이블 및 컬럼의 이름을 정의하는 상수를 유지하는 컨테이너입니다.
 * 계약 클래스를 사용하면 동일한 패키지 내 모든 클래스에 동일한 상수를 사용할 수 있습니다.
 * 즉, 어느 한 장소에서 컬럼 이름을 변경하면 코드 전체에 변경 사항이 반영됩니다.
 */
/**
 * API Contract for the I Time U app.
 */
public final class ItemContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ItemContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.itti7.itimeu";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.itti7.itimeu/itimeu/ is a valid path for
     * looking at item data. content://com.itti7.itimeu/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_ITIMEU = "itimeu";

    /**
     * Inner class that defines constant values for the items database table.
     * Each entry in the table represents a single item.
     */

    public static final class ItemEntry implements BaseColumns {

        /** The content URI to access the item data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITIMEU);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITIMEU;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITIMEU;

        /** Name of database table for items */
        public final static String TABLE_NAME = "list";

        /**
         * Unique ID number for the item (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the item.
         *
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_NAME = "name";

        /**
         * Detail of the item.
         *
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_DETAIL = "detail";

        /**
         * Created date of the item.
         *
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_DATE = "date";

        /**
         * Total unit number of the item.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_TOTAL_UNIT = "totalUnit";

        /**
         * Unit number of the item.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_UNIT = "unit";

        /**
         * Status of the item.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_STATUS = "status";

        /**
         * Possible values for the status of the item
         */
        public final static int STATUS_TODO = 0;
        public final static int STATUS_DO = 1;
        public final static int STATUS_DONE = 2;

        /**
         * sReturns whether or not the given status is
         * {@link ItemEntry#STATUS_TODO},
         * {@link ItemEntry#STATUS_DO},
         * {@link ItemEntry#STATUS_DONE}.
         */
        public static boolean isValiedStatus(int status) {
            if (status == STATUS_TODO || status == STATUS_DO || status == STATUS_DONE) {
                return true;
            }
            return false;
        }
    }
}
