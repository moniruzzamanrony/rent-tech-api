package com.itvillage.renttech.base.expection;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class MagicGlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }
    @ExceptionHandler(MagicException.AuthenticationException.class)
    public ResponseEntity<?> handleNotPermittedException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }
    @ExceptionHandler(MagicException.NotPermittedException.class)
    public ResponseEntity<?> handleNotPermittedException(MagicException.NotPermittedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }

    @ExceptionHandler(MagicException.InternalServerError.class)
    public ResponseEntity<?> handleInternalServerErrorException(MagicException.InternalServerError e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler(MagicException.AlreadyExistsException.class)
    public ResponseEntity<?> handleBadException(MagicException.AlreadyExistsException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }


    @ExceptionHandler(MagicException.BadRequestException.class)
    public ResponseEntity<?> handleBadException(MagicException.BadRequestException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ExceptionHandler(MagicException.NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(MagicException.NotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(MagicException.DownloadException.class)
    public ResponseEntity<?> handleDownloadException(MagicException.DownloadException ex) {
        log.error("Download error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

}

