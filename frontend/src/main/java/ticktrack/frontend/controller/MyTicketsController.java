package ticktrack.frontend.controller;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MyTicketsController {
    private final OkHttpClient httpClient;

    @Autowired
    public MyTicketsController(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @RequestMapping(value = "/myTickets", method = RequestMethod.GET)
    public String displaySearchPage(ModelMap model) {

        return "myTickets";
    }
}
