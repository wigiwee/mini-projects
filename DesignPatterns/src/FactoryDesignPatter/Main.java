package FactoryDesignPatter;

import java.util.Scanner;

/* Imagine you’re building a program that simulates a simple console based calculator.
 * You have different types of operations like addition, subtraction, multiplication, division etc.
 * Each operation has its own unique behavior. Now, you want to create these operation objects in
 * your program based on customer choice.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {

            System.out.println("Enter first num");
            double firstNum = scanner.nextInt();

            System.out.println("Enter operation");
            String operation = scanner.next();

            System.out.println("Enter second num");
            double secondNum = scanner.nextInt();

            OperationFactory operationFactory = new OperationFactoryImpl();
            Operation operation1 =  operationFactory.getInstance(operation);

            System.out.println("The Result is : " + operation1.calculate(firstNum, secondNum));
        }catch (InvalidOperation e ){
            System.out.println(e.getMessage());
        }
    }
}
