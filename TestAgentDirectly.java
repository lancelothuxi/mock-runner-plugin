import com.example.plugin.agent.MockAgent;

public class TestAgentDirectly {
    public static void main(String[] args) {
        try {
            // Test the parseMockValue method directly
            String jsonValue = "[{\"name\":\"John Doe\",\"age\":25,\"email\":\"john@example.com\"}]";
            String returnType = "List<com.student.Student>";
            
            System.out.println("Testing parseMockValue directly:");
            System.out.println("Value: " + jsonValue);
            System.out.println("Type: " + returnType);
            
            Object result = MockAgent.Interceptor.parseMockValue(jsonValue, returnType);
            System.out.println("Result: " + result);
            System.out.println("Result class: " + (result != null ? result.getClass().getName() : "null"));
            
            if (result instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) result;
                System.out.println("List size: " + list.size());
                if (!list.isEmpty()) {
                    System.out.println("First element: " + list.get(0));
                    System.out.println("First element class: " + list.get(0).getClass().getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}