package singletonPattern;

public class Clipboard {

    private static Clipboard clipboard = null;

    private String value;

    private Clipboard() {}

    public static Clipboard getInstance() {
        if (clipboard == null){
            clipboard = new Clipboard();
        }
        return clipboard;
    }

    public void copy(String value){
        this.value = value;
    }

    public String paste(){
        return this.value;
    }
}
