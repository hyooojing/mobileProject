package ddwucom.mobile.finalproject.ma02_20200981;

import java.io.Serializable;

public class MuseumDTO implements Serializable {
    private int _id;
    private String name;
    private String type;
    private String address;
    private String lat;
    private String lng;
    private String num;
    private String open;
    private String close;
    private String restDay;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getRestDay() {
        return restDay;
    }

    public void setRestDay(String restDay) {
        this.restDay = restDay;
    }

    @Override
    public String toString() {
        return  "박물관 이름: " + name + '\'' +
                "분류: " + type + '\'' +
                "주소: " + address + '\'' +
                "전화번호: " + num + '\'' +
                "오픈: " + open + '\'' +
                "마감: " + close + '\'' +
                "휴일: " + restDay + '\'' +
                '}';
    }
}
