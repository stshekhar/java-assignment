package com.fulfilment.application.monolith.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ApiErrorTypeTest {

  @Test
  void mapsCommonHttpStatusesToMeaningfulErrorTypes() {
    assertEquals(ApiErrorType.INVALID_REQUEST, ApiErrorType.fromStatus(400));
    assertEquals(ApiErrorType.INVALID_REQUEST, ApiErrorType.fromStatus(422));
    assertEquals(ApiErrorType.UNAUTHORIZED, ApiErrorType.fromStatus(401));
    assertEquals(ApiErrorType.FORBIDDEN, ApiErrorType.fromStatus(403));
    assertEquals(ApiErrorType.NOT_FOUND, ApiErrorType.fromStatus(404));
    assertEquals(ApiErrorType.CONFLICT, ApiErrorType.fromStatus(409));
    assertEquals(ApiErrorType.INTERNAL_SERVER_ERROR, ApiErrorType.fromStatus(500));
    assertEquals(ApiErrorType.SERVICE_UNAVAILABLE, ApiErrorType.fromStatus(503));
  }

  @Test
  void mapsUnlistedStatusToGenericHttpError() {
    assertEquals(ApiErrorType.HTTP_ERROR, ApiErrorType.fromStatus(418));
  }
}
