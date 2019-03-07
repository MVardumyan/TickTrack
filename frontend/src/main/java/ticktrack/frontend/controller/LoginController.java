package ticktrack.frontend.controller;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@ComponentScan()
 class LoginController {
   @RequestMapping(value = {"/login", "/"}, method = RequestMethod.GET)
   public String showLoginPage() {
      return "login";
   }

   @RequestMapping(value = "/login", method = RequestMethod.POST)
   public String showWelcomePage(ModelMap model, @RequestParam String name, @RequestParam String password) {
//      model.put("name", name);
//      model.put("password", password);

      return "regularUserMain";
   }

   @RequestMapping(value = "/notFound", method = RequestMethod.GET)
    String notFound() {
       return "notFound";
   }

}

