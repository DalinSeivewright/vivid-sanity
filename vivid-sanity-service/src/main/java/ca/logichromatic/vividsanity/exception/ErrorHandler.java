package ca.logichromatic.vividsanity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ ImageNotFoundException.class })
    @ResponseBody
    ResponseEntity<Object> handleControllerException(HttpServletRequest req, Throwable ex) {

        if(ex instanceof ImageNotFoundException) {
            return new ResponseEntity<>("Oh no!", HttpStatus.NOT_FOUND) ;
        } else {
            return new ResponseEntity<>("weee", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
