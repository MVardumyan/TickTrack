package ticktrack.frontend.service;

import org.springframework.stereotype.Service;

@Service
public class LoginService {

   public boolean validateUser(String userid, String password) {
      return userid.equalsIgnoreCase("user") && password.equalsIgnoreCase("user");
   }

}
