package be.iccbxl.pid.reservationsspringboot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String index(@RequestParam(required = false) String denied, Model model) {

        if ("true".equals(denied)) {
            model.addAttribute("errorMessage", "Accès refusé.");
        }

        return "home";
    }
}
