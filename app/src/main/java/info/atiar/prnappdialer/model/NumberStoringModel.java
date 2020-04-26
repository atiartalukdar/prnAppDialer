package info.atiar.prnappdialer.model;

public class NumberStoringModel {
    public String website;
    public String number;
    public String createdAt;

    public NumberStoringModel() {
    }

    public NumberStoringModel(String website, String number, String createdAt) {
        this.website = website;
        this.number = number;
        this.createdAt = createdAt;
    }
}
