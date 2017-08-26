package cn.cerc.summer.android.basis.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/13.
 */

public class DataSet {
    private List<Record> records = new ArrayList<>();
    private int position = -1;

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public void append() {
        Record record = new Record();
        records.add(record);
        position = records.size() - 1;
    }

    public void setField(String fieldCode, Object fieldValue) {
        getCurrent().setField(fieldCode, fieldValue);
    }

    public Record getCurrent() {
        return records.get(position);
    }

    public Record get(int index) {
        return records.get(index);
    }

    public void remove(int index) {
        records.remove(index);
    }

    public void clear() {
        records.clear();
    }

    public void insert(int index) {
        Record record = new Record();
        records.add(index, record);
        this.position = index;
    }

    public int getInt(String num) {
        return getCurrent().getInt(num);
    }

    public boolean locate(String fieldCode, String fieldValue) {
        position = -1;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getString(fieldCode).equals(fieldValue)) {
                position = i;
                return true;
            }
        }
        return false;
    }
}
