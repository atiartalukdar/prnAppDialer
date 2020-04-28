package model;

public class NumberModel {
    public String websiteID;
    public String number;
    public String createdBy;
    public String createdAt;

    public NumberModel() {
    }

    public NumberModel(String websiteID, String number, String createdBy, String createdAt) {
        this.websiteID = websiteID;
        this.number = number;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getWebsiteID() {
        return websiteID;
    }

    public String getNumber() {
        return number;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
