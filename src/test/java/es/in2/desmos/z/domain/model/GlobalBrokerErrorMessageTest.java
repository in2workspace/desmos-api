//package es.in2.desmos.todo.domain.model;
//
//import es.in2.desmos.domain.models.GlobalErrorMessage;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class GlobalBrokerErrorMessageTest {
//
//    @Test
//    void testBuilderAndAccessors() {
//        // Arrange
//        String title = "Error Title";
//        String message = "Error Message";
//        String path = "/error/path";
//        // Act
//        GlobalErrorMessage globalErrorMessage = GlobalErrorMessage.builder()
//                .title(title)
//                .message(message)
//                .path(path)
//                .build();
//        // Assert
//        assertEquals(title, globalErrorMessage.title());
//        assertEquals(message, globalErrorMessage.message());
//        assertEquals(path, globalErrorMessage.path());
//    }
//
//    @Test
//    void testGlobalErrorMessageBuilderToString() {
//        // Arrange
//        String title = "Error Title";
//        String message = "Error Message";
//        String path = "/error/path";
//
//        String expectedToString = "GlobalErrorMessage.GlobalErrorMessageBuilder(title=" + title
//                + ", message=" + message
//                + ", path=" + path + ")";
//
//        // Act
//        GlobalErrorMessage.GlobalErrorMessageBuilder globalErrorMessageBuilder = GlobalErrorMessage.builder()
//                .title(title)
//                .message(message)
//                .path(path);
//
//        // Assert
//        assertEquals(expectedToString, globalErrorMessageBuilder.toString());
//    }
//
//}
