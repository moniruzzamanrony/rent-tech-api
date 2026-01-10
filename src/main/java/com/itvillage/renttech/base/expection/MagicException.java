package com.itvillage.renttech.base.expection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class MagicException {

    @Getter
    @RequiredArgsConstructor
    private static class ServiceException extends RuntimeException {
        private final int code;
        private final String message;
    }

    public static class AuthenticationException extends ServiceException {
        public AuthenticationException(String message) {
            super(HttpStatus.UNAUTHORIZED.value(), message);
        }
    }

    public static class AlreadyExistsException extends ServiceException {

        public AlreadyExistsException() {
            super(HttpStatus.CONFLICT.value(), "Sorry, the same information already exists in the system !!");
        }

        public AlreadyExistsException(String message) {
            super(HttpStatus.CONFLICT.value(), message);
        }
    }

    public static class NotFoundException extends ServiceException {

        public NotFoundException() {
            super(HttpStatus.NOT_FOUND.value(), "Sorry, the information you requested could not be found !!");
        }

        public NotFoundException(String message) {
            super(HttpStatus.NOT_FOUND.value(), message);
        }
    }


    public static class BadRequestException extends ServiceException {
        public BadRequestException() {
            super(HttpStatus.BAD_REQUEST.value(), "Sorry, your request cannot be processed !!");
        }

        public BadRequestException(String message) {
            super(HttpStatus.BAD_REQUEST.value(), message);
        }
    }

    public static class NotPermittedException extends ServiceException {
        public NotPermittedException(String message) {
            super(HttpStatus.FORBIDDEN.value(), message);
        }
    }
    public static class InternalServerError extends ServiceException {
        public InternalServerError(String message) {
            super(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
        }
    }

    public static class DownloadException extends ServiceException {
        public DownloadException(String message) {
            super(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
        }
    }

  public static class ForbiddenException extends ServiceException {
      public ForbiddenException(String message) {
          super(HttpStatus.FORBIDDEN.value(), message);
      }
  }
}
