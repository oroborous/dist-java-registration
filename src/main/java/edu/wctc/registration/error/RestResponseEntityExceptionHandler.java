package edu.wctc.registration.error;

import edu.wctc.registration.dto.GenericResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        logger.error("400 Status Code", ex);
        BindingResult result = ex.getBindingResult();
        GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(),
                "Invalid " + result.getObjectName());
        return handleExceptionInternal(ex,
                bodyOfResponse,
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    // 409
    @ExceptionHandler({UserAlreadyExistsException.class})
    public ResponseEntity<Object> handleUserAlreadyExist(final RuntimeException ex, final WebRequest request) {
        logger.error("409 Status Code", ex);
        final GenericResponse bodyOfResponse = new GenericResponse(
                "An account with that username/email already exists",
                "UserAlreadyExists");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
