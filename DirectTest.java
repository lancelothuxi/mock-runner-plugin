import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class DirectTest {
    public static void main(String[] args) {
        String jsonValue = "[{\"name\":\"John Doe\",\"age\":25,\"email\":\"john@example.com\"},{\"name\":\"Jane Smith\",\"age\":23,\"email\":\"jane@example.com\"}]";
        String returnType = "List<com.student.Student>";
        
        System.out.println("Testing direct Gson parsing:");
        System.out.println("Value: " + jsonValue);
        System.out.println("Type: " + returnType);
        
        try {
            Gson gson = new Gson();
            
            // Test 1: Parse as generic List (this will create LinkedTreeMap)
            List<?> genericList = gson.fromJson(jsonValue, List.class);
            System.out.println("\nGeneric List result: " + genericList);
            System.out.println("First element class: " + genericList.get(0).getClass().getName());
            
            // Test 2: Parse with TypeToken (this should create proper Student objects)
            Type listType = TypeToken.getParameterized(List.class, com.student.Student.class).getType();
            List<com.student.Student> typedList = gson.fromJson(jsonValue, listType);
            System.out.println("\nTyped List result: " + typedList);
            System.out.println("First element class: " + typedList.get(0).getClass().getName());
            System.out.println("First student name: " + typedList.get(0).getName());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}