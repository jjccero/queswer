package com.gzu.queswer.util;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "qaFilter",urlPatterns = "/filter/*")
public class UserFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        request.getSession(false);
        DecodeWrapper wrapper=new DecodeWrapper(request);
        filterChain.doFilter(wrapper,servletResponse);
    }
}
