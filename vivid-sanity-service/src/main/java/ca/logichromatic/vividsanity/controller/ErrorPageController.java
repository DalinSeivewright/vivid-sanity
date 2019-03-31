package ca.logichromatic.vividsanity.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ErrorPageController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @RequestMapping(value = ERROR_PATH)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String error() {
        return "";
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
