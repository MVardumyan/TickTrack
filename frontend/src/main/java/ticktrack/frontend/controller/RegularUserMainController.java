package ticktrack.frontend.controller;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ticktrack.frontend.attributes.User;

import javax.servlet.http.HttpSession;

@Controller
//@SessionAttributes("name")
public class RegularUserMainController {
    private final OkHttpClient httpClient;

    @Autowired
    public RegularUserMainController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/regUserMain", method = RequestMethod.GET)
    public String displaySearchPage(ModelMap model, @SessionAttribute("user") User user) {
        model.put("name", user.getUsername());
        return "regularUserMain";
    }
}
