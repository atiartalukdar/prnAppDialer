package info.atiar.prnappdialer;

public class User {
    String FullName;
    String Email;
    long   createdAt;

    public User (){};
    public User(String FullName,String email,long createdAt){
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
