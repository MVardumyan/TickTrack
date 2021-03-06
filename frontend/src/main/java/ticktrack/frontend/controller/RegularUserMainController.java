package ticktrack.frontend.controller;

import common.enums.UserRole;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ticktrack.frontend.attributes.User;

import javax.servlet.http.HttpSession;

/**
 * This controller is responsible for displaying regular and business users home page.
 * */

@Controller
//@SessionAttributes("name")
public class RegularUserMainController {
    private final OkHttpClient httpClient;

    @Autowired
    public RegularUserMainController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/regUserMain", method = RequestMethod.GET)
    public String displayHomePage(ModelMap model, @SessionAttribute("user") User user) {
        if (!user.getRole().equals(UserRole.RegularUser)) {
            model.put("notRegular", true);
        }
        if (user.getRole().equals(UserRole.Admin)) {
            model.put("admin", true);
        }
        model.put("name", user.getUsername());
        return "regularUserMain";
    }
}
