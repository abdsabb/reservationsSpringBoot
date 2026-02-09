package be.iccbxl.pid.reservationsspringboot.controller;

import be.iccbxl.pid.reservationsspringboot.dto.UserRegistrationDto;
import be.iccbxl.pid.reservationsspringboot.service.UserService;
import jakarta.validation.Valid;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model, HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // pas connecté -> redirection login
        if (auth == null || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login?loginRequired=true";
        }

        // connecté mais pas admin -> redirection home + message
        if (!request.isUserInRole("ADMIN")) {
            return "redirect:/?denied=true";
        }

        model.addAttribute("user", new UserRegistrationDto());
        return "authentication/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationDto dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirAttrs,
            HttpServletRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // pas connecté -> redirection login
        if (auth == null || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login?loginRequired=true";
        }

        // connecté mais pas admin -> redirection home + message
        if (!request.isUserInRole("ADMIN")) {
            return "redirect:/?denied=true";
        }

        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Erreurs de validation !");
            return "authentication/register";
        }

        if (!userService.isLoginAndEmailAvailable(dto.getLogin(), dto.getEmail())) {
            model.addAttribute("errorMessage", "Email ou login déjà utilisé !");
            return "authentication/register";
        }

        userService.registerFromDto(dto);
        redirAttrs.addFlashAttribute("successMessage", "Inscription réussie !");
        return "redirect:/login";
    }
}
