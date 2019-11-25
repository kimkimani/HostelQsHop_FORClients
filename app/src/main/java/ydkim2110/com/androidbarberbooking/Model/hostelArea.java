package ydkim2110.com.androidbarberbooking.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class hostelArea implements Parcelable {
//salon
    private String name, address, website, phone, openHours, HoatelAreaId;

    public hostelArea() {
    }

    protected hostelArea(Parcel in) {
        name = in.readString();
        address = in.readString();
        website = in.readString();
        phone = in.readString();
        openHours = in.readString();
        HoatelAreaId = in.readString();
    }

    public static final Creator<hostelArea> CREATOR = new Creator<hostelArea>() {
        @Override
        public hostelArea createFromParcel(Parcel in) {
            return new hostelArea(in);
        }

        @Override
        public hostelArea[] newArray(int size) {
            return new hostelArea[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getHoatelAreaId() {
        return HoatelAreaId;
    }

    public void setHoatelAreaId(String hoatelAreaId) {
        this.HoatelAreaId = hoatelAreaId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(website);
        dest.writeString(phone);
        dest.writeString(openHours);
        dest.writeString(HoatelAreaId);
    }
}
