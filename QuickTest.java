public class QuickTest {
    public static void main(String[] args) {
        String value = "[{\"name\":\"John Doe\",\"age\":25}]";
        String type = "List<com.student.Student>";
        
        System.out.println("Calling parseMockValue with type: " + type);
        Object result = com.example.plugin.agent.MockAgent.Interceptor.parseMockValue(value, type);
        System.out.println("Result: " + result);
        System.out.println("Result class: " + (result != null ? result.getClass().getName() : "null"));
    }
}
