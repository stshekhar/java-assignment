package com.fulfilment.application.monolith.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ApiExceptionMapperTest {

  @Inject ApiExceptionMapper exceptionMapper;

  @Test
  void doesNotExposeInternalExceptionDetailsInServerErrorResponse() {
    Response response =
        exceptionMapper.toResponse(new IllegalStateException("database password leaked"));

    assertEquals(500, response.getStatus());
    ObjectNode body = (ObjectNode) response.getEntity();
    assertEquals("INTERNAL_SERVER_ERROR", body.get("errorType").asText());
    assertEquals(500, body.get("code").asInt());
    assertEquals("An unexpected server error occurred.", body.get("error").asText());
    assertFalse(body.toString().contains("database password leaked"));
  }
}
