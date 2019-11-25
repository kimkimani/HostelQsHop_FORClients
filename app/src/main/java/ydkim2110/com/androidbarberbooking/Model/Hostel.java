package ydkim2110.com.androidbarberbooking.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Hostel implements Parcelable {
//berber

    private String name, username, password, hostelId;
    private Double rating;
    private Long ratingTimes;

    public Hostel() {
    }

    protected Hostel(Parcel in) {
        name = in.readString();
        username = in.readString();
        password = in.readString();
        hostelId = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
            ratingTimes = in.readLong();
        }
    }

    public static final Creator<Hostel> CREATOR = new Creator<Hostel>() {
        @Override
        public Hostel createFromParcel(Parcel in) {
            return new Hostel(in);
        }

        @Override
        public Hostel[] newArray(int size) {
            return new Hostel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Long getRatingTimes() {
        return ratingTimes;
    }

    public void setRatingTimes(Long ratingTimes) {
        this.ratingTimes = ratingTimes;
    }

    public String getHostelId() {
        return hostelId;
    }

    public void setHostelId(String hostelId) {
        this.hostelId = hostelId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(hostelId);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rating);
            dest.writeLong(ratingTimes);
        }
    }
}
