package model;

public class NumberModel {
    public String websiteID;
    public String number;
    public String callDuration;
    public String createdBy;
    public String createdAt;

    public NumberModel() {
    }

    public NumberModel(String websiteID, String number, String callDuration, String createdBy, String createdAt) {
        this.websiteID = websiteID;
        this.callDuration = callDuration;
        this.createdBy = createdBy;
        this.createdAt = createdAt;

        Character charAt = number.charAt(0);
        if (charAt.toString().equals("+")){
            this.number = number;
        }else {
            this.number = "+"+number;
        }
    }

    public String getWebsiteID() {
        return websiteID;
    }

    public void setWebsiteID(String websiteID) {
        this.websiteID = websiteID;
    }

    public String getNumber() {
        Character charAt = number.charAt(0);
        if (charAt.toString().equals("+")){
            return number;
        }else {
            return "+"+number;
        }
    }

    public void setNumber(String number) {
        Character charAt = number.charAt(0);
        if (charAt.toString().equals("+")){
            this.number = number;
        }else {
            this.number = "+"+number;
        }    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
