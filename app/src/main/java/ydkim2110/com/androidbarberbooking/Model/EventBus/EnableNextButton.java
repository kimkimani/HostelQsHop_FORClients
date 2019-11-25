package ydkim2110.com.androidbarberbooking.Model.EventBus;

import ydkim2110.com.androidbarberbooking.Model.Hostel;
import ydkim2110.com.androidbarberbooking.Model.hostelArea;

public class EnableNextButton {

    private int step;
    private Hostel hostel;
    private hostelArea hostelArea;
    private int timeSlot;

    public EnableNextButton(int step, Hostel hostel) {
        this.step = step;
        this.hostel = hostel;
    }

    public EnableNextButton(int step, hostelArea hostelArea) {
        this.step = step;
        this.hostelArea = hostelArea;
    }

    public EnableNextButton(int step, int timeSlot) {
        this.step = step;
        this.timeSlot = timeSlot;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Hostel getHostel() {
        return hostel;
    }

    public void setHostel(Hostel hostel) {
        this.hostel = hostel;
    }

    public hostelArea getHostelArea() {
        return hostelArea;
    }

    public void setHostelArea(hostelArea hostelArea) {
        this.hostelArea = hostelArea;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }
}
