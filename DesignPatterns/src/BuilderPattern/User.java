package BuilderPattern;

public class User {

    public String name;             //required field
    public String email;            //required field
    public String phoneNo;          //optional field
    public String city;             //optional field

    public User(UserBuilder userBuilder) {
        this.name = userBuilder.name;
        this.email = userBuilder.email;
        this.phoneNo = userBuilder.phoneNo;
        this.city = userBuilder.city;
    }

    public static UserBuilder builder(String name, String email){
        return new UserBuilder(name, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    public static class UserBuilder{
        private String name;
        private String email;
        private String phoneNo="Unknown";
        private String city="Unknown";

        public UserBuilder(String name, String email){
            this.name = name;
            this.email = email;
        }

        public UserBuilder name(String name){
            this.name = name;
            return this;
        }

        public UserBuilder email(String email){
            this.email = email;
            return this;
        }

        public UserBuilder city(String city){
            this.city = city;
            return this;
        }

        public UserBuilder phoneNo(String phoneNo){
            this.phoneNo = phoneNo;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }

}
