package com.nehirozsari.smartpantry.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nehirozsari.smartpantry.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private HttpServletRequest request;

    @Test
    void handleNotFound_returnsStructuredErrorResponse() {
        when(request.getRequestURI()).thenReturn("/api/v1/test");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new ResourceNotFoundException("User not found"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("User not found");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/test");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    void errorResponse_serializesToExpectedJsonShape() throws Exception {
        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.NOT_FOUND)
                .message("User not found")
                .path("/api/v1/test")
                .status(404)
                .error("Not Found")
                .build();

        String json = objectMapper.writeValueAsString(error);

        assertThat(json).contains("\"code\":\"NOT_FOUND\"");
        assertThat(json).contains("\"message\":\"User not found\"");
        assertThat(json).doesNotContain("errors");
    }
}
