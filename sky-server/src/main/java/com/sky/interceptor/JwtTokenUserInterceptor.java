package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

///**
// * jwt令牌校验的拦截器
// */
//@Component
//@Slf4j
//public class JwtTokenUserInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    private JwtProperties jwtProperties;
//
//    /**
//     * 校验jwt
//     *
//     * @param request
//     * @param response
//     * @param handler
//     * @return
//     * @throws Exception
//     */
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        //判断当前拦截到的是Controller的方法还是其他资源
//        if (!(handler instanceof HandlerMethod)) {
//            //当前拦截到的不是动态方法，直接放行
//            return true;
//        }
//
//        //1、从请求头中获取令牌
//        String token = request.getHeader(jwtProperties.getUserTokenName());
//
//        //2、校验令牌
//        try {
//            log.info("jwt校验:{}", token);
//            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
//            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
//            BaseContext.setCurrentId(userId);
//            System.out.println("---------------------------------------------------    " + userId);
//            log.info("当用户id：{}", userId);
//            //3、通过，放行
//            return true;
//        } catch (Exception ex) {
//            //4、不通过，响应401状态码
//            response.setStatus(401);
//            return false;
//        }
//    }
//
//
//}
//
///**
// * jwt令牌校验的拦截器
// */
//@Component
//@Slf4j
//public class JwtTokenUserInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    private JwtProperties jwtProperties;
//
//    /**
//     * 校验jwt
//     *
//     * @param request
//     * @param response
//     * @param handler
//     * @return
//     * @throws Exception
//     */
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        //判断当前拦截到的是Controller的方法还是其他资源
//        if (!(handler instanceof HandlerMethod)) {
//            //当前拦截到的不是动态方法，直接放行
//            return true;
//        }
//
//        //1、从请求头中获取令牌
//        String token = request.getHeader(jwtProperties.getUserTokenName());
//
//        //2、校验令牌
//        try {
//            log.info("jwt校验:{}", token);
//            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
//            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
//            log.info("当前用户的id：{}", userId);
//            BaseContext.setCurrentId(userId);
//            //3、通过，放行
//            return true;
//        } catch (Exception ex) {
//            //4、不通过，响应401状态码
//            response.setStatus(401);
//            return false;
//        }
//
//
//
//    }
//}
/**
 * JWT令牌校验的拦截器 - 用户端
 */
@Component // 关键：添加@Component注解让Spring管理
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 前置处理 - 在Controller方法执行前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截器被触发，请求路径: {}", request.getRequestURI());

        // 判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            // 当前拦截到的不是动态方法，直接放行（静态资源等）
            log.info("拦截到非Controller方法，直接放行");
            return true;
        }

        // 1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());
        log.info("获取到的token: {}", token);

        // 2、校验令牌
        try {
            log.info("开始JWT校验，密钥名称: {}", jwtProperties.getUserTokenName());
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户ID：{}", userId);

            // 将用户ID存储到线程局部变量中
            BaseContext.setCurrentId(userId);
            log.info("用户ID已设置到上下文");

            // 3、通过，放行
            return true;
        } catch (Exception ex) {
            log.error("JWT校验失败: {}", ex.getMessage());
            // 4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }

    /**
     * 请求完成后清理资源
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理线程局部变量，防止内存泄漏
        BaseContext.removeCurrentId();
        log.info("清理线程上下文");
    }
}
