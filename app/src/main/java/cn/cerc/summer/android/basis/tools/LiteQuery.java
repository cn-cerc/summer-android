package cn.cerc.summer.android.basis.tools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/7.
 */

public class LiteQuery {
    private SQLiteDatabase db;
    private Cursor cursor;
    private int size;

    private int position = -1;
    private String commandText;

    private boolean active;

    public LiteQuery(SQLiteDatabase db) {
        this.db = db;
    }

    public boolean open() {
        if (this.commandText == null)
            throw new RuntimeException("commandText is null");
        cursor = db.rawQuery(this.commandText, null);
        size = cursor.getCount();
        this.active = true;
        return cursor.moveToFirst();
    }

    public boolean isActive() {
        return active;
    }

    public boolean fetch() {
        if (size == 0)
            return false;
        if (position < (size - 1)) {
            position++;
            if (position == 0)
                cursor.moveToFirst();
            else
                cursor.moveToNext();
            return true;
        } else
            return false;
    }

    public String getString(String fieldCode) {
        return cursor.getString(cursor.getColumnIndex(fieldCode));
    }

    public String getCommandText() {
        return commandText;
    }

    public void setCommandText(String commandText) {
        this.commandText = commandText;
    }

    public int getPosition() {
        return position;
    }
}
