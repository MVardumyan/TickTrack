package ticktrack.frontend.controller;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ticktrack.frontend.service.LoginService;

@Controller
@SessionAttributes("name")
 class LoginController {
   @Autowired
   Logger logger;

   @Autowired
   LoginService service;

   @RequestMapping(value = "/login", method = RequestMethod.GET)
   public String showLoginPage(ModelMap model) {
      return "login";
   }

   @RequestMapping(value = "/login", method = RequestMethod.POST)
   public String showWelcomePage(ModelMap model, @RequestParam String name, @RequestParam String password) {

      boolean isValidUser = service.validateUser(name, password);

      if (!isValidUser) {
         model.put("errorMessage", "Invalid Credentials");
         return "login";
      }

      model.put("name", name);
      model.put("password", password);

      return "welcome";
   }

}
