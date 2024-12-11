package tech.artadevs.finances.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RootController implements ErrorController {
    @GetMapping("/")
    public RedirectView redirectToDocs() {
        return new RedirectView("/docs");
    }
}
