package ddwucom.mobile.finalproject.ma02_20200981;

import java.io.Serializable;

public class Memo implements Serializable {
    long _id;
    String title;
    String info;

    public Memo(String title, String info) {
        this.title = title;
        this.info = info;
    }

    public Memo(long _id, String title, String info) {
        this._id = _id;
        this.title = title;
        this.info = info;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append("\t\t\t\t[");
        sb.append(info);
        sb.append("]");
        return sb.toString();
    }
}
