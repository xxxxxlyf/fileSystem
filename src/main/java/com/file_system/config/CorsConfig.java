package com.file_system.config;

import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 过滤器解决跨域
 * @author lyf
 */
//@Component
public class CorsConfig implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,systemname");
        //response.setHeader("Access-Control-Allow-Headers", "systemname");
        // response.setHeader("Access-Control-Expose-Headers", "Access-Control-Allow-Origin,Access-Control-Allow-Credentials");
        // 如果要把Cookie发到服务器，需要指定Access-Control-Allow-Credentials字段为true
        // response.setHeader("Access-Control-Allow-Credentials", "true");
        // response.setHeader("Access-Control-Expose-Headers", "*");




//        // 設定允許多個域名請求
//        String[] allowDomains = {"http://10.167.214.11:8020","http://localhost:8080","http://10.167.214.11:8011"};
//        Set allowOrigins = new HashSet(Arrays.asList(allowDomains));
//        String originHeads = req.getHeader("Origin");
//        if(allowOrigins.contains(originHeads)){
//            // 這裡填寫你允許進行跨域的主機ip（正式上線時可以動態配置具體允許的域名和IP）
//            rep.setHeader("Access-Control-Allow-Origin", originHeads);
//        }
        if (((HttpServletRequest) servletRequest).getMethod().equals("OPTIONS")) {
            servletResponse.getWriter().println("ok");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
