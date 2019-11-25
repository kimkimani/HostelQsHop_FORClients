package ydkim2110.com.androidbarberbooking.Model;

import com.google.firebase.Timestamp;

import org.w3c.dom.Text;

import java.util.List;

import ydkim2110.com.androidbarberbooking.Database.CartItem;

public class BookingInformation {
    private String cityBook, customerName, customerPhone, time, hostelId, HostelName, HoatelAreaId, HoatelAreaName, HoatelAreaAddress;
    private Long slot;
    private Timestamp timestamp;
    private double Price;
    private boolean done;
    private List<CartItem> cartItemList;



    public BookingInformation() {
    }

    public BookingInformation(String cityBook, String customerName,
                              String customerPhone, String time,
                              String barbarId, String HostelName,
                              String HoatelAreaId, String HoatelAreaName,
                              String HoatelAreaAddress, Long slot, Timestamp timestamp, Boolean done,double Price) {
        this.cityBook = cityBook;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.time = time;
        this.hostelId = barbarId;
        this.HostelName = HostelName;
        this.HoatelAreaId = HoatelAreaId;
        this.HoatelAreaName = HoatelAreaName;
        this.HoatelAreaAddress = HoatelAreaAddress;
        this.slot = slot;
        this.timestamp = timestamp;
        this.done = done;
        this.Price=Price;
    }

    public double getPrice() {
        return Price;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setPrice(double price) {
        this.Price=price;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHostelId() {
        return hostelId;
    }

    public void setHostelId(String hostelId) {
        this.hostelId = hostelId;
    }

    public String getHostelName() {
        return HostelName;
    }

    public void setHostelName(String hostelName) {
        this.HostelName = hostelName;
    }

    public String getHoatelAreaId() {
        return HoatelAreaId;
    }

    public void setHoatelAreaId(String hoatelAreaId) {
        this.HoatelAreaId = hoatelAreaId;
    }

    public String getHoatelAreaName() {
        return HoatelAreaName;
    }

    public void setHoatelAreaName(String hoatelAreaName) {
        this.HoatelAreaName = hoatelAreaName;
    }

    public String getHoatelAreaAddress() {
        return HoatelAreaAddress;
    }

    public void setHoatelAreaAddress(String hoatelAreaAddress) {
        this.HoatelAreaAddress = hoatelAreaAddress;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getCityBook() {
        return cityBook;
    }

    public void setCityBook(String cityBook) {
        this.cityBook = cityBook;
    }

//    public  List<CartItem> getCartItemList() {
//        return cartItemList;
//    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }
    public void getCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }
}
