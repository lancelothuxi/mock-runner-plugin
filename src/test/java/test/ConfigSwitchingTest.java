package test;

import io.github.lancelothuxi.idea.plugin.mock.mock.MockConfig;
import io.github.lancelothuxi.idea.plugin.mock.mock.MockMethodConfig;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Test to verify configuration switching between exception and return value works instantly
 */
public class ConfigSwitchingTest {

    @Test
    public void testSwitchFromExceptionToReturnValue() {
        MockConfig config = new MockConfig();

        // First, add an exception rule
        MockMethodConfig exceptionConfig = new MockMethodConfig();
        exceptionConfig.setClassName("test.Service");
        exceptionConfig.setMethodName("testMethod");
        exceptionConfig.setReturnValue("original value");
        exceptionConfig.setReturnType("java.lang.String");
        exceptionConfig.setEnabled(true);
        exceptionConfig.setThrowException(true);
        exceptionConfig.setExceptionType("java.lang.IllegalArgumentException");
        exceptionConfig.setExceptionMessage("Original exception");

        config.addMockMethod(exceptionConfig);

        // Verify exception rule is set
        MockConfig.MockRule rule = config.getMockRule("test.Service", "testMethod");
        assertNotNull(rule, "Rule should exist");
        assertTrue(rule.isThrowException(), "Should be in exception mode");
        assertEquals("java.lang.IllegalArgumentException", rule.getExceptionType());
        assertEquals("Original exception", rule.getExceptionMessage());
        assertEquals("original value", rule.getReturnValue());

        // Now switch to return value mode
        MockMethodConfig valueConfig = new MockMethodConfig();
        valueConfig.setClassName("test.Service");
        valueConfig.setMethodName("testMethod");
        valueConfig.setReturnValue("new value");
        valueConfig.setReturnType("java.lang.String");
        valueConfig.setEnabled(true);
        valueConfig.setThrowException(false); // Switch to return value mode
        valueConfig.setExceptionType("java.lang.RuntimeException");
        valueConfig.setExceptionMessage("This shouldn't be used");

        config.addMockMethod(valueConfig);

        // Verify the rule was updated instantly
        MockConfig.MockRule updatedRule = config.getMockRule("test.Service", "testMethod");
        assertNotNull(updatedRule, "Rule should still exist");
        assertFalse(updatedRule.isThrowException(), "Should NOT be in exception mode anymore");
        assertEquals("new value", updatedRule.getReturnValue(), "Return value should be updated");
        System.out.println("✓ Configuration switched successfully from exception to return value!");
    }

    @Test
    public void testSwitchFromReturnValueToException() {
        MockConfig config = new MockConfig();

        // First, add a return value rule
        MockMethodConfig valueConfig = new MockMethodConfig();
        valueConfig.setClassName("test.Service");
        valueConfig.setMethodName("testMethod");
        valueConfig.setReturnValue("original value");
        valueConfig.setReturnType("java.lang.String");
        valueConfig.setEnabled(true);
        valueConfig.setThrowException(false);

        config.addMockMethod(valueConfig);

        // Verify return value rule is set
        MockConfig.MockRule rule = config.getMockRule("test.Service", "testMethod");
        assertNotNull(rule, "Rule should exist");
        assertFalse(rule.isThrowException(), "Should NOT be in exception mode");
        assertEquals("original value", rule.getReturnValue());

        // Now switch to exception mode
        MockMethodConfig exceptionConfig = new MockMethodConfig();
        exceptionConfig.setClassName("test.Service");
        exceptionConfig.setMethodName("testMethod");
        exceptionConfig.setReturnValue("new exception value");
        exceptionConfig.setReturnType("java.lang.String");
        exceptionConfig.setEnabled(true);
        exceptionConfig.setThrowException(true); // Switch to exception mode
        exceptionConfig.setExceptionType("java.lang.NullPointerException");
        exceptionConfig.setExceptionMessage("New exception message");

        config.addMockMethod(exceptionConfig);

        // Verify the rule was updated instantly
        MockConfig.MockRule updatedRule = config.getMockRule("test.Service", "testMethod");
        assertNotNull(updatedRule, "Rule should still exist");
        assertTrue(updatedRule.isThrowException(), "Should be in exception mode now");
        assertEquals("java.lang.NullPointerException", updatedRule.getExceptionType());
        assertEquals("New exception message", updatedRule.getExceptionMessage());
        System.out.println("✓ Configuration switched successfully from return value to exception!");
    }

    @Test
    public void testUpdateSameMethodMultipleTimes() {
        MockConfig config = new MockConfig();

        // Add initial exception rule
        MockMethodConfig exceptionConfig = new MockMethodConfig();
        exceptionConfig.setClassName("test.Service");
        exceptionConfig.setMethodName("testMethod");
        exceptionConfig.setReturnValue("value1");
        exceptionConfig.setReturnType("java.lang.String");
        exceptionConfig.setEnabled(true);
        exceptionConfig.setThrowException(true);
        exceptionConfig.setExceptionType("java.lang.IllegalArgumentException");
        exceptionConfig.setExceptionMessage("Exception 1");

        config.addMockMethod(exceptionConfig);

        MockConfig.MockRule rule1 = config.getMockRule("test.Service", "testMethod");
        assertTrue(rule1.isThrowException());
        assertEquals("Exception 1", rule1.getExceptionMessage());

        // Update to return value
        MockMethodConfig valueConfig = new MockMethodConfig();
        valueConfig.setClassName("test.Service");
        valueConfig.setMethodName("testMethod");
        valueConfig.setReturnValue("value2");
        valueConfig.setReturnType("java.lang.String");
        valueConfig.setEnabled(true);
        valueConfig.setThrowException(false);

        config.addMockMethod(valueConfig);

        MockConfig.MockRule rule2 = config.getMockRule("test.Service", "testMethod");
        assertFalse(rule2.isThrowException());

        // Update back to exception with different settings
        MockMethodConfig exceptionConfig2 = new MockMethodConfig();
        exceptionConfig2.setClassName("test.Service");
        exceptionConfig2.setMethodName("testMethod");
        exceptionConfig2.setReturnValue("value3");
        exceptionConfig2.setReturnType("java.lang.String");
        exceptionConfig2.setEnabled(true);
        exceptionConfig2.setThrowException(true);
        exceptionConfig2.setExceptionType("java.lang.RuntimeException");
        exceptionConfig2.setExceptionMessage("Exception 2");

        config.addMockMethod(exceptionConfig2);

        MockConfig.MockRule rule3 = config.getMockRule("test.Service", "testMethod");
        assertTrue(rule3.isThrowException());
        assertEquals("java.lang.RuntimeException", rule3.getExceptionType());
        assertEquals("Exception 2", rule3.getExceptionMessage());

        System.out.println("✓ Multiple updates work correctly!");
    }
}