import com.example.plugin.mock.MockConfig;
import com.google.gson.Gson;
import java.io.FileReader;

public class DebugConfig {
    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            MockConfig config = gson.fromJson(new FileReader("test-mock-config.json"), MockConfig.class);
            
            System.out.println("Mock methods:");
            for (var method : config.getMockMethods()) {
                System.out.println("  - " + method.getClassName() + "." + method.getMethodName());
                System.out.println("    returnType: " + method.getReturnType());
                System.out.println("    returnValue: " + method.getReturnValue());
            }
            
            config.rebuildMockRules();
            
            System.out.println("\nMock rules:");
            for (var entry : config.getAllRules().entrySet()) {
                System.out.println("  - " + entry.getKey());
                System.out.println("    returnType: " + entry.getValue().getReturnType());
                System.out.println("    returnValue: " + entry.getValue().getReturnValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}