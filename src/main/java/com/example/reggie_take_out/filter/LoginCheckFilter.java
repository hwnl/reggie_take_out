package com.example.reggie_take_out.filter;

import com.alibaba.fastjson.JSON;
import com.example.reggie_take_out.common.BaseContext;
import com.example.reggie_take_out.common.R;
import org.springframework.http.HttpRequest;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.LogRecord;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
// 不需要拦截的页面
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/employee/page",
                "/user/sendMsg",
                "/user/login"
        };
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for(int i=0;i<urls.length;i++){
            boolean match = antPathMatcher.match(urls[i], requestURI);
            if(match){
                filterChain.doFilter(request,response);
                return;
            }
        }
        // 判断登录
        if(request.getSession().getAttribute("employee") != null){
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }
        // 判断移动端登录
        // 判断登录
        if(request.getSession().getAttribute("user") != null){
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId((userId));
            filterChain.doFilter(request,response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
}
