package model;

public class Room {
    private int roomID, roomTypeID, maxPerson, price;
    private String roomNo, status, roomTypeName;

    // Constructor default
    public Room() {}

    // Constructor lengkap
    public Room(int roomID, String roomNo, int roomTypeID, String roomTypeName, int maxPerson, String status) {
        this.roomID = roomID;
        this.roomNo = roomNo;
        this.roomTypeID = roomTypeID;
        this.roomTypeName = roomTypeName;
        this.maxPerson = maxPerson;
        this.status = status;
    }

    // Getter dan Setter
    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public int getRoomTypeID() {
        return roomTypeID;
    }

    public void setRoomTypeID(int roomTypeID) {
        this.roomTypeID = roomTypeID;
    }

    public int getMaxPerson() {
        return maxPerson;
    }

    public void setMaxPerson(int maxPerson) {
        this.maxPerson = maxPerson;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }
    
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
