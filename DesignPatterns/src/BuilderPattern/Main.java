package BuilderPattern;

/*
 * In the User Object the name and email fields are must have but the city and phoneNo Fields are optional
 * such Class is Managed by Builder pattern
 */
public class Main {

    public static void main(String[] args) {

        User user1 = User.builder("Aayush", "sitaram@test.com")
                .city("Pune")
                .build();

        System.out.println(user1);

        User user2 = User.builder("Pramod", "pramod@test.com")
                .city("Mumbai")
                .phoneNo("333")
                .build();

        System.out.println(user2);
    }
}
