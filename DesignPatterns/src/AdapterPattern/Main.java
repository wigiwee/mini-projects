package AdapterPattern;

//client side
//converting NokiaCellPhone instance to CellPhone instance
public class Main {
    public static void main(String[] args) {
        NokiaCellPhone adaptee = new NokiaCellPhone();
        CellPhone adapter = new CellphoneAdapter(adaptee);
        adapter.call();
    }
}

//Target Class
interface CellPhone{
    void call();
}

//Adaptee ( the class to be adapted)
class NokiaCellPhone {
    public void ring(){
        System.out.println("Ringing");
    }
}

//Adapter class implementing client interface
class CellphoneAdapter implements CellPhone{

    private NokiaCellPhone nokiaCellPhone;

    public CellphoneAdapter(NokiaCellPhone nokiaCellPhone){
        this.nokiaCellPhone = nokiaCellPhone;
    }

    @Override
    public void call() {
        nokiaCellPhone.ring();
    }
}

