package model;

public class WebsitesModel{
    String website;
    String websiteID;
    String createdBy;
    String createdAt;

    public WebsitesModel() {
    }

    public WebsitesModel(String website, String websiteID, String createdBy, String createdAt) {
        this.website = website;
        this.websiteID = websiteID;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getWebsite() {
        return website;
    }

    public String getWebsiteID() {
        return websiteID;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
