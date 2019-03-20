package ticktrack.frontend.controller;

import common.enums.UserRole;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;
import ticktrack.frontend.attributes.User;


@Controller
public class ErrorController {

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String displayErrorPage(ModelMap model, @SessionAttribute User user) {
        if (user.getRole().equals(UserRole.Admin)) {
            model.put("admin", true);
        }
        return "error";
    }
}
