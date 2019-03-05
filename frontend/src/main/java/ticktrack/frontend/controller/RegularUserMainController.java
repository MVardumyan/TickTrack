package ticktrack.frontend.controller;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RegularUserMainController {
    private final OkHttpClient httpClient;

    @Autowired
    public RegularUserMainController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/regUserMain", method = RequestMethod.GET)
    public String displaySearchPage(ModelMap model) {

        return "regularUserMain";
    }
}
