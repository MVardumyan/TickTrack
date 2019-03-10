package ticktrack.frontend.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ticktrack.frontend.attributes.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static common.enums.UserRole.*;

@Component
public class CheckActiveSessionInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if(path.equals("/login") || path.equals("/register")){
            if(session == null || session.getAttribute("user") == null) {
                return true;
            } else {
                User user = (User) session.getAttribute("user");
                if(Admin.equals(user.getRole())) {
                    response.sendRedirect(request.getContextPath() + "/adminMain");
                } else {
                    response.sendRedirect(request.getContextPath() + "/regUserMain");
                }
                return false;
            }
        } else if(session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        } else {
            return true;
        }
    }
}
