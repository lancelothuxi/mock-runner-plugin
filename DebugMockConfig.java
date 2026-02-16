import com.example.plugin.mock.MockConfig;
import com.google.gson.Gson;
import java.io.FileReader;

public class DebugMockConfig {
    public static void main(String[] args) {
        try {
            System.out.println("Loading config from: test-mock-config.json");
            Gson gson = new Gson();
            MockConfig config = gson.fromJson(new FileReader("test-mock-config.json"), MockConfig.class);
            
            System.out.println("Config loaded successfully");
            System.out.println("mockRules size: " + config.getAllRules().size());
            System.out.println("mockMethods size: " + config.getMockMethods().size());
            
            if (config.getAllRules().isEmpty() && !config.getMockMethods().isEmpty()) {
                System.out.println("mockRules is empty, rebuilding from mockMethods...");
                config.rebuildMockRules();
                System.out.println("After rebuild, mockRules size: " + config.getAllRules().size());
            }
            
            System.out.println("\nMock methods:");
            for (var method : config.getMockMethods()) {
                System.out.println("  - " + method.getClassName() + "." + method.getMethodName() + 
                    " -> " + method.getReturnValue() + " (type: " + method.getReturnType() + ")");
            }
            
            System.out.println("\nMock rules:");
            for (var entry : config.getAllRules().entrySet()) {
                System.out.println("  - " + entry.getKey() + 
                    " -> " + entry.getValue().getReturnValue() + " (type: " + entry.getValue().getReturnType() + ")");
            }
            
            // Test rule lookup
            System.out.println("\nTesting rule lookup:");
            var rule1 = config.getMockRule("com.test.TestService", "getStudents");
            System.out.println("getStudents rule: " + (rule1 != null ? rule1.getReturnType() : "null"));
            
            var rule2 = config.getMockRule("com.test.TestService", "getStudent");
            System.out.println("getStudent rule: " + (rule2 != null ? rule2.getReturnType() : "null"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}