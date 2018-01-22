package cn.cerc.summer.android.basis;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/7.
 */

public class LiteDatabase {
    private static SQLiteDatabase db;
    private AppCompatActivity view;

    public LiteDatabase(AppCompatActivity view) {
        this.view = view;
        if (db == null) {
            db = view.openOrCreateDatabase("test", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS product (code varchar(10), name varchar(30), desc varchar(30), unit varchar(30))");
        }
    }

    public SQLiteDatabase getDB() {
        return db;
    }

    public LiteQuery getQuery(String sql) {
        LiteQuery query = new LiteQuery(db);
        query.setCommandText(sql);
        return query;
    }

    //插入一条记录
    public void insert(String table, ContentValues record) {
        db.insert(table, null, record);
    }

    public String getText(int resourceId) {
        EditText edt = (EditText) view.findViewById(resourceId);
        return edt.getText().toString();
    }

    public void delete(String table, String whereClause) {
        db.delete(table, whereClause, null);
    }
}
