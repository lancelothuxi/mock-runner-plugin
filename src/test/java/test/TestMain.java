package com.test;

import com.student.Student;
import java.util.List;

public class TestMain {
    public static void main(String[] args) {
        System.out.println("Testing MockAgent with generic types...");
        
        TestService service = new TestService();
        
        // Test List<Student> - this should not throw ClassCastException anymore
        try {
            List<Student> students = service.getStudents();
            System.out.println("getStudents() returned: " + students);
            
            if (students != null && !students.isEmpty()) {
                Student firstStudent = students.get(0);
                System.out.println("First student: " + firstStudent);
                System.out.println("First student name: " + firstStudent.getName());
            }
        } catch (Exception e) {
            System.err.println("Error with getStudents(): " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test single Student object
        try {
            Student student = service.getStudent("John");
            System.out.println("getStudent() returned: " + student);
            
            if (student != null) {
                System.out.println("Student name: " + student.getName());
            }
        } catch (Exception e) {
            System.err.println("Error with getStudent(): " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Test completed!");
    }
}