package com.fulfilment.application.monolith.errors;

public enum ApiErrorType {
  INVALID_REQUEST,
  NOT_FOUND,
  CONFLICT,
  UNAUTHORIZED,
  FORBIDDEN,
  INTERNAL_SERVER_ERROR,
  SERVICE_UNAVAILABLE,
  HTTP_ERROR;

  public static ApiErrorType fromStatus(int status) {
    return switch (status) {
      case 400, 422 -> INVALID_REQUEST;
      case 401 -> UNAUTHORIZED;
      case 403 -> FORBIDDEN;
      case 404 -> NOT_FOUND;
      case 409 -> CONFLICT;
      case 500 -> INTERNAL_SERVER_ERROR;
      case 503 -> SERVICE_UNAVAILABLE;
      default -> HTTP_ERROR;
    };
  }
}
