package ticktrack.frontend;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ticktrack.frontend.interceptors.CheckActiveSessionInterceptor;
import ticktrack.frontend.interceptors.CheckAdminRoleInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final CheckActiveSessionInterceptor sessionInterceptor;
    private final CheckAdminRoleInterceptor adminRoleInterceptor;

    @Autowired
    public WebMvcConfig(CheckActiveSessionInterceptor sessionInterceptor, CheckAdminRoleInterceptor adminRoleInterceptor) {
        this.sessionInterceptor = sessionInterceptor;
        this.adminRoleInterceptor = adminRoleInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/fromLogin/**", "/changePassword/**")
                .order(Ordered.HIGHEST_PRECEDENCE);

        registry.addInterceptor(adminRoleInterceptor)
                .addPathPatterns("/admin/**")
                .order(Ordered.LOWEST_PRECEDENCE);
    }
}
