package com.fulfilment.application.monolith.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Exception> {

  private static final Logger LOGGER = Logger.getLogger(ApiExceptionMapper.class);

  @Inject ObjectMapper objectMapper;

  @Override
  public Response toResponse(Exception exception) {
    int status =
        exception instanceof WebApplicationException webException
            ? webException.getResponse().getStatus()
            : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    ApiErrorType errorType = ApiErrorType.fromStatus(status);

    if (status >= 500) {
      LOGGER.errorf(exception, "Unhandled API error: type=%s", errorType);
    } else {
      LOGGER.warnf("Request rejected: type=%s status=%d", errorType, status);
    }

    ObjectNode error = objectMapper.createObjectNode();
    error.put("errorType", errorType.name());
    error.put("code", status);

    if (status < 500 && exception.getMessage() != null) {
      error.put("error", exception.getMessage());
    } else if (status >= 500) {
      error.put("error", "An unexpected server error occurred.");
    }

    return Response.status(status).entity(error).build();
  }
}
