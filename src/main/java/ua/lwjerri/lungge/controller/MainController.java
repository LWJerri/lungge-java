package ua.lwjerri.lungge.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Main", description = "Main API")
public class MainController {

    @Operation(summary = "Home page", description = "Returns the home page")
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @Operation(summary = "Token page", description = "Returns the token page with user information")
    @GetMapping("/token")
    public String token(@AuthenticationPrincipal OidcUser user, Model model) {
        if (user != null) {
            model.addAttribute("username", user.getPreferredUsername());

            String accessToken = user.getIdToken().getTokenValue();
            model.addAttribute("accessToken", accessToken);
        } else {
            return "redirect:/";
        }
        return "token";
    }
}