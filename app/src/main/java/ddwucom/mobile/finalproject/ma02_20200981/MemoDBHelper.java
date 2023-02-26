package ddwucom.mobile.finalproject.ma02_20200981;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MemoDBHelper extends SQLiteOpenHelper {
    final static String TAG = "MemoDBHelper";

    final static String DB_NAME = "memos.db";
    public final static String TABLE_NAME = "memo_table";

    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "title";
    public final static String COL_INFO = "info";

    public MemoDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_TITLE + " TEXT, " + COL_INFO + " TEXT)";
        Log.d(TAG, sql);
        db.execSQL(sql);

        insertSample(db);       // 샘플이 필요할 경우 추가
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    private void insertSample(SQLiteDatabase db) {
        db.execSQL("insert into " + TABLE_NAME + " values (null, '국립중앙박물관', '가보고 싶어요');");
        db.execSQL("insert into " + TABLE_NAME + " values (null, '해양 박물관', '관심 있어요');");
        db.execSQL("insert into " + TABLE_NAME + " values (null, '민속 박물관', '매주 월요일 휴관이래요');");
    }
}
