package tech.artadevs.finances.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import tech.artadevs.finances.exception.ApiError;

@Controller
public class CustomErrorController implements ErrorController {
    public CustomErrorController() {
    }

    @RequestMapping("/error")
    public ResponseEntity<ApiError> handleError(WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.valueOf(404))
                .body(new ApiError("Access /docs to see the API documentation."));
    }
}
