package main.java;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class loginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();
    private final ArrayList<String> adminAllowURIs = new ArrayList<>();

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }


        if (httpRequest.getSession().getAttribute("email") == null) {
            httpResponse.sendRedirect("/cs122b-spring20-project1/login.html");
        } else {
            if(this.isUrlAllowedadminLogin(httpRequest.getRequestURI()) && !httpRequest.getSession().getAttribute("status").equals("administor")){
                ((HttpServletRequest) request).getSession().setAttribute("status", null);
                ((HttpServletRequest) request).getSession().setAttribute("email", null);
                ((HttpServletRequest) request).getSession().setAttribute("robot-check", null);
                httpResponse.sendRedirect("login.html");
            }
            chain.doFilter(request, response);
        }

    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {

        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    private boolean isUrlAllowedadminLogin(String requestURI) {

        return adminAllowURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("postpage.css");
        allowedURIs.add("background.png");
        allowedURIs.add("table.css");
        adminAllowURIs.add("_dashboard.html");
        adminAllowURIs.add("dashboard.js");
        adminAllowURIs.add("api/dashboard");
        adminAllowURIs.add("api/dashboard_addmovie");
    }

    public void destroy() {
        // ignored.
    }

}