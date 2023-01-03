package com.mt.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.mt.reggie.common.BaseContext;
import com.mt.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//web过滤器注解,filterName=>指定过滤器的name属性,等价于<filter-name>,urlPatterns=>指定过滤器的
//URL的匹配模式,等价于<url-pattern>,如果是一个/则表示全部拦截,/*表示/后跟有属性的就拦截
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //Spring中提供的路径匹配器，支持通配符?、*、**
    /**
     * ?表示匹配一个字符
     * *表示匹配0个或者多个字符
     * **表示匹配0个或者多个目录/字符
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        //类型转换
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取当前的请求路径
        String requestURI = request.getRequestURI();
        //输入日志
        log.info("拦截到请求:{}",requestURI);
        //定义不需要拦截的请求路径,
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean flag = check(urls, requestURI);
        //为true，则直接放行
        if(flag){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        String userAgent = request.getHeader("UserAgent");
        //assert 宏的原型定义在 assert.h 中，其作用是如果它的条件返回错误，则终止程序执行。
        //他可以计算给定的计算表达式,如果其值为假（即为0），那么它先向 stderr 打印一条出错信息,然后通过调用 abort 来终止程序运行。
        //某种意义上可以替代if,缺点是频繁的调用会极大的影响程序的性能，增加额外的开销。
        //在调试结束后，可以通过在包含 #include 的语句之前插入 #define NDEBUG 来禁用 assert 调用
        //assert userAgent!=null;
        if("backend".equals(userAgent)){
            //还要判断登录状态,如果已经登录,全放行
            if(request.getSession().getAttribute("employee")!=null){
                long userId = (long) request.getSession().getAttribute("employee");
                log.info("用户已登录,用户id为:{}", userId);
                BaseContext.setCurrentId(userId);
                filterChain.doFilter(request,response);
            }else {
               isLogin(response);
            }
        }else if ("front".equals(userAgent)) {
            //移动端判断登录状态
            if(request.getSession().getAttribute("user")!=null){
                long userId = (long) request.getSession().getAttribute("user");
                log.info("用户已登录,用户id为:{}", userId);
                BaseContext.setCurrentId(userId);
                filterChain.doFilter(request,response);
            }else {
                isLogin(response);
            }
        }else {
            isLogin(response);
        }
    }

    //自定义检查方法
    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            if(PATH_MATCHER.match(url,requestURI)){
                return true;
            }
        }
        return false;
    }
    //自定义未登录的检查方法
    private void isLogin(HttpServletResponse response) throws IOException {
        log.info("用户未登录");
        //如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

}
