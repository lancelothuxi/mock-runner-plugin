import com.example.plugin.agent.MockAgent;

public class TestParseDirect {
    public static void main(String[] args) {
        String value = "[{\"name\":\"John Doe\",\"age\":25,\"email\":\"john@example.com\"},{\"name\":\"Jane Smith\",\"age\":23,\"email\":\"jane@example.com\"}]";
        String type = "List<com.student.Student>";
        
        System.out.println("Testing parseMockValue directly:");
        System.out.println("Value: " + value);
        System.out.println("Type: " + type);
        
        Object result = MockAgent.Interceptor.parseMockValue(value, type);
        System.out.println("Result: " + result);
        System.out.println("Result class: " + (result != null ? result.getClass().getName() : "null"));
        
        if (result instanceof java.util.List) {
            java.util.List<?> list = (java.util.List<?>) result;
            if (!list.isEmpty()) {
                System.out.println("First element class: " + list.get(0).getClass().getName());
            }
        }
    }
}
