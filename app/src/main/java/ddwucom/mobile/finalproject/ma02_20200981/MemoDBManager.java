package ddwucom.mobile.finalproject.ma02_20200981;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MemoDBManager {
    MemoDBHelper memoDBHelper = null;
    Cursor cursor = null;

    public MemoDBManager(Context context) { memoDBHelper = new MemoDBHelper(context); }
    public ArrayList<Memo> getAllMemo() {
        ArrayList memoList = new ArrayList();
        SQLiteDatabase db = memoDBHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + MemoDBHelper.TABLE_NAME, null);

        while(cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_TITLE));
            String info = cursor.getString(cursor.getColumnIndexOrThrow(MemoDBHelper.COL_INFO));
            memoList.add(new Memo (id, title, info));
        }

        cursor.close();
        memoDBHelper.close();
        return memoList;
    }

    // DB에 새로운 메모 추가
    public boolean addNewMemo(Memo newMemo) {
        SQLiteDatabase db = memoDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(MemoDBHelper.COL_TITLE, newMemo.getTitle());
        value.put(MemoDBHelper.COL_INFO, newMemo.getInfo());

        // 데이터 삽입이 정상적으로 이루어질 경우 1, 이상 시 0
        long count = db.insert(MemoDBHelper.TABLE_NAME, null, value);
        if (count > 0) return true;
        return false;
    }

    // _id를 기준으로 메모의 title과 info 변경
    public boolean modifyMemo(Memo memo) {
        SQLiteDatabase sqLiteDatabase = memoDBHelper.getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put(MemoDBHelper.COL_TITLE, memo.getTitle());
        row.put(MemoDBHelper.COL_INFO, memo.getInfo());

        String whereClause = MemoDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(memo.get_id())};

        int result = sqLiteDatabase.update(MemoDBHelper.TABLE_NAME, row, whereClause, whereArgs);
        memoDBHelper.close();
        if (result > 0)
            return true;
        return false;
    }

    // _id를 기준으로 DB에서 메모 삭제
    public boolean removeMemo(long id) {
        SQLiteDatabase sqLiteDatabase = memoDBHelper.getWritableDatabase();
        String whereClause = MemoDBHelper.COL_ID + "=?";
        //listView의 position 위치에 해당하는 메모 객체를 가지고 와서 그 안에 있는 식별자인 id값을 알아내 문자 변환
        String[] whereArgs = new String[] { String.valueOf(id) };
        int result = sqLiteDatabase.delete(MemoDBHelper.TABLE_NAME, whereClause, whereArgs);
        memoDBHelper.close();
        if (result > 0)
            return true;
        return false;
    }

    // DB 검색

    // close 수행
    public void close() {
        if (memoDBHelper != null) memoDBHelper.close();
        if (cursor != null) cursor.close();
    }
}
