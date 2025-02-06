package model;

public class IDCardType {
    private int idCardTypeID;
    private String idCardType;

    // Default constructor
    public IDCardType() {}

    // Parameterized constructor
    public IDCardType(int idCardTypeID, String idCardType) {
        this.idCardTypeID = idCardTypeID;
        this.idCardType = idCardType;
    }

    // Getters and setters
    public int getIdCardTypeID() {
        return idCardTypeID;
    }

    public void setIdCardTypeID(int idCardTypeID) {
        this.idCardTypeID = idCardTypeID;
    }

    public String getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(String idCardType) {
        this.idCardType = idCardType;
    }
}
