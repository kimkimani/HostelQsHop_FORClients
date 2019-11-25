package ydkim2110.com.androidbarberbooking.Model.EventBus;

import java.util.List;

import ydkim2110.com.androidbarberbooking.Model.Hostel;

public class BarberDoneEvent {

    private List<Hostel> mHostelList;

    public BarberDoneEvent(List<Hostel> hostelList) {
        mHostelList = hostelList;
    }

    public List<Hostel> getBarberList() {
        return mHostelList;
    }

    public void setBarberList(List<Hostel> hostelList) {
        mHostelList = hostelList;
    }
}
