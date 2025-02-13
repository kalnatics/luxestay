package model;

public class Booking {
    private int reservationID, customerID, roomID;
    private String name, roomNo, roomType, bookingDate, checkIn, checkOut, status;
    private double totalPrice;

    public Booking() {}

    public Booking(int reservationID, int customerID, String name, String roomNo, 
                String roomType, String bookingDate, String checkIn, 
                String checkOut, double totalPrice, String status) {
     this.reservationID = reservationID;
     this.customerID = customerID;
     this.name = name;
     this.roomNo = roomNo;
     this.roomType = roomType;
     this.bookingDate = bookingDate;
     this.checkIn = checkIn;
     this.checkOut = checkOut;
     this.totalPrice = totalPrice;
     this.status = status;
 }


    public int getReservationID() { return reservationID; }
    public void setReservationID(int reservationID) { this.reservationID = reservationID; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public String getCustomerName() { return name; }
    public void setCustomerName(String name) { this.name = name; }

    public int getRoomID() { return roomID; }
    public void setRoomID(int roomID) { this.roomID = roomID; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
