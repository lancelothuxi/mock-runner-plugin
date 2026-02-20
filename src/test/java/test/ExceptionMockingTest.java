package test;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;

import io.github.lancelothuxi.idea.plugin.mock.mock.MockConfig;

/**
 * Simple test to verify exception mocking works correctly
 */
public class ExceptionMockingTest {

    private MockConfig mockConfig;
    private TestService testService;

    @BeforeMethod
    public void setUp() throws Exception {
        // Create mock config with exception
        mockConfig = new MockConfig();

        MockConfig.MockRule exceptionRule = new MockConfig.MockRule("", "void");
        exceptionRule.setEnabled(true);
        exceptionRule.setThrowException(true);
        exceptionRule.setExceptionType("java.lang.IllegalArgumentException");
        exceptionRule.setExceptionMessage("This is a mocked exception!");

        mockConfig.addMockRule(TestService.class.getName(), "throwException", exceptionRule);

        // Create service with mocked exception
        testService = new TestService(mockConfig);
    }

    @Test
    public void testExceptionThrowing() {
        try {
            testService.throwException();
            throw new AssertionError("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            // Expected exception
            assert e.getMessage().equals("This is a mocked exception!") :
                "Exception message mismatch: " + e.getMessage();
        }
    }

    @Test
    public void testReturnValueWhenNotThrowingException() throws Exception {
        // Create a new config that returns a value
        MockConfig valueConfig = new MockConfig();
        MockConfig.MockRule valueRule = new MockConfig.MockRule("42", "int");
        valueRule.setEnabled(true);
        valueRule.setThrowException(false);
        valueConfig.addMockRule(TestService.class.getName(), "getValue", valueRule);

        TestService valueService = new TestService(valueConfig);

        int result = valueService.getValue();
        assert result == 42 : "Expected value 42, got: " + result;
    }

    /**
     * Simple test service for testing
     */
    static class TestService {
        private final MockConfig mockConfig;

        public TestService(MockConfig mockConfig) {
            this.mockConfig = mockConfig;
        }

        public void throwException() {
            MockConfig.MockRule rule = mockConfig.getMockRule(
                TestService.class.getName(), "throwException");

            if (rule != null && rule.isEnabled()) {
                if (rule.isThrowException()) {
                    try {
                        Class<?> exceptionClass = Class.forName(rule.getExceptionType());
                        if (Exception.class.isAssignableFrom(exceptionClass)) {
                            throw (Exception) exceptionClass
                                .getConstructor(String.class)
                                .newInstance(rule.getExceptionMessage());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(
                            "Failed to create exception: " + rule.getExceptionType(), e);
                    }
                }
            } else {
                // Normal behavior - no exception
            }
        }

        public int getValue() {
            MockConfig.MockRule rule = mockConfig.getMockRule(
                TestService.class.getName(), "getValue");

            if (rule != null && rule.isEnabled()) {
                if (rule.isThrowException()) {
                    // Should not reach here for this test
                    throw new RuntimeException("Unexpected exception mode");
                }
                return Integer.parseInt(rule.getReturnValue());
            }
            return 0;
        }
    }
}