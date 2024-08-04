package singletonPattern;

//singleton design pattern is used to make sure that no more than one instance of a class is created

public class Main {
    public static void main(String[] args) {

        Clipboard clipboard1 = Clipboard.getInstance();
        Clipboard clipboard2 = Clipboard.getInstance();

        clipboard1.copy("first value");
        clipboard2.copy("second value");

        System.out.println(clipboard1.paste());
        System.out.println(clipboard2.paste());
    }

}
