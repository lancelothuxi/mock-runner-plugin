public class Main {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        
        int result1 = calc.add(5, 3);
        System.out.println("5 + 3 = " + result1);
        
        int result2 = calc.multiply(4, 7);
        System.out.println("4 * 7 = " + result2);
        
        String message = calc.getMessage();
        System.out.println("Message: " + message);
    }
}

class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public int multiply(int a, int b) {
        return a * b;
    }
    
    public String getMessage() {
        return "Hello from Calculator";
    }
}
