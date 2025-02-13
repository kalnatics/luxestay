package model;

public class Room {

    private Integer roomID;
    private Integer roomTypeID;
    private String roomNo;
    private Integer status;
    private String roomTypeName;
    private Integer price;
    private Integer maxPerson;

    // Constructor kosong
    public Room() {
    }

    // Constructor lengkap
    public Room(Integer roomID, String roomNo, Integer roomTypeID, String roomTypeName,
            Integer status, Integer price, Integer maxPerson) {
        this.roomID = roomID;
        this.roomNo = roomNo;
        this.roomTypeID = roomTypeID;
        this.roomTypeName = roomTypeName;
        this.status = status;
        this.price = price;
        this.maxPerson = maxPerson;
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

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getStatus() {
        return status != null ? status : "0";  // default value
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

    public int getMaxPerson() {
        return maxPerson;
    }

    public void setMaxPerson(int maxPerson) {
        this.maxPerson = maxPerson;
    }

}
