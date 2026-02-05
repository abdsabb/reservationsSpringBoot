package be.iccbxl.pid.reservationsspringboot.controller;

import be.iccbxl.pid.reservationsspringboot.model.User;
import be.iccbxl.pid.reservationsspringboot.service.EmailService;
import be.iccbxl.pid.reservationsspringboot.service.PasswordResetTokenService;
import be.iccbxl.pid.reservationsspringboot.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final PasswordResetTokenService tokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) final Boolean loginRequired,
            @RequestParam(required = false) final Boolean loginError,
            @RequestParam(required = false) final Boolean logoutSuccess,
            final Model model
    ) {

        if (loginRequired == Boolean.TRUE) {
            model.addAttribute("errorMessage", "Vous devez vous connecter pour avoir accès.");
        }

        if (loginError == Boolean.TRUE) {
            model.addAttribute("errorMessage", "Échec de la connexion !");
        }

        if (logoutSuccess == Boolean.TRUE) {
            model.addAttribute("successMessage", "Vous êtes déconnecté avec succès.");
        }

        return "authentication/login";
    }

    // Page "mot de passe oublié"
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "authentication/forgot-password";
    }

    // Soumission email : on ne révèle pas si l'email existe (anti user-enumeration)
    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("email") @NotBlank @Email String email,
            Model model
    ) {
        User user = userService.findByEmail(email);

        if (user != null) {
            var token = tokenService.createTokenForUser(user);
            emailService.sendPasswordResetMail(user.getEmail(), token.getToken());
        }

        model.addAttribute("successMessage",
                "Si un compte existe pour cet e-mail, tu recevras un lien de réinitialisation.");
        return "authentication/forgot-password";
    }

    // Form reset (token dans la query string)
    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        var user = tokenService.validatePasswordResetToken(token);

        if (user == null) {
            model.addAttribute("errorMessage", "Lien invalide ou expiré.");
            // token absent => ton template peut afficher un message + lien forgot-password
            return "authentication/reset-password";
        }

        model.addAttribute("token", token);
        return "authentication/reset-password";
    }

    // Soumission nouveau mot de passe
    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") @NotBlank String password,
            @RequestParam("confirmPassword") @NotBlank String confirmPassword,
            Model model
    ) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("errorMessage", "Les mots de passe ne correspondent pas.");
            return "authentication/reset-password";
        }

        var user = tokenService.validatePasswordResetToken(token);
        if (user == null) {
            model.addAttribute("errorMessage", "Lien invalide ou expiré.");
            return "authentication/reset-password";
        }

        user.setPassword(passwordEncoder.encode(password));
        userService.updateUser(user.getId(), user);

        tokenService.deleteToken(token);

        return "authentication/reset-success";
    }
}
