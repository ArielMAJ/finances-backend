package tech.artadevs.finances.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import tech.artadevs.finances.dtos.ApiErrorDto;

@Controller
public class CustomErrorController implements ErrorController {
    public CustomErrorController() {
    }

    @RequestMapping("/error")
    public ResponseEntity<ApiErrorDto> handleError(WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.valueOf(404))
                .body(new ApiErrorDto("Access /docs to see the API documentation."));
    }
}
