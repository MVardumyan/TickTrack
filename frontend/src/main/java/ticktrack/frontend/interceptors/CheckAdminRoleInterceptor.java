package ticktrack.frontend.interceptors;

import common.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ticktrack.frontend.attributes.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class CheckAdminRoleInterceptor extends HandlerInterceptorAdapter {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("user") == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        } else {
            User user = (User) session.getAttribute("user");
            if(user.getRole().equals(UserRole.Admin)) {
                return true;
            } else {
                response.sendRedirect(request.getContextPath() + "/error");
                return false;
            }
        }
    }
}
