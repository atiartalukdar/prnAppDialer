package model;

public class UserModel {
    String FullName;
    String Email;
    long   createdAt;

    public UserModel(){};
    public UserModel(String FullName, String email, long createdAt){
        this.FullName=FullName;
        this.Email=email;
        this.createdAt=createdAt;
    }

    public String getFullName() {
        return FullName;
    }

    public String getEmail() {
        return Email;
    }

    public long getCreatedAt() {
        return createdAt;
    }

}
