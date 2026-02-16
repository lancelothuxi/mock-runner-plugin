import com.example.plugin.agent.MockAgent;
import com.google.gson.Gson;

public class TestMockAgentDirect {
    public static void main(String[] args) {
        // 测试 parseMockValue 方法
        String jsonValue = "[{\"id\": \"00\",\"name\": \"test\",\"age\": 20,\"gender\": \"M\",\"major\": \"CS\",\"className\": \"A\"}]";
        String type = "List<com.student.Student>";
        
        System.out.println("Testing parseMockValue with:");
        System.out.println("  value: " + jsonValue);
        System.out.println("  type: " + type);
        
        Object result = MockAgent.Interceptor.parseMockValue(jsonValue, type);
        
        System.out.println("\nResult: " + result);
        System.out.println("Result class: " + (result != null ? result.getClass().getName() : "null"));
        
        if (result instanceof java.util.List) {
            java.util.List<?> list = (java.util.List<?>) result;
            System.out.println("List size: " + list.size());
            if (!list.isEmpty()) {
                Object first = list.get(0);
                System.out.println("First element class: " + first.getClass().getName());
            }
        }
    }
}
