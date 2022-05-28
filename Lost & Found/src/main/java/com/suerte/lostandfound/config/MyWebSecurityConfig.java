package com.suerte.lostandfound.config;

import com.suerte.lostandfound.constant.LoginConstant;
import com.suerte.lostandfound.constant.RedisConstant;
import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.service.UserService;
import com.suerte.lostandfound.vo.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Configuration
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;

    @Bean
    PasswordEncoder passwordEncoder() {
        //参数为密钥的迭代次数（也可不配置，默认为10）
        return new BCryptPasswordEncoder(12);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用userService来认证信息，此时是使用数据库认证,从数据库中认证用户
        // userService必须实现UserDetailsService接口
        auth.userDetailsService(userService);
    }

    @Autowired
    private RedissonClient redissonClient;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**")
                .hasRole("admin")
                .antMatchers("/user/**")
                .access("hasAnyRole('admin','user')")
//                .antMatchers("/db/**")
//                .authenticated()
//                .and()
//                .formLogin()
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//                        System.out.println(authentication.getPrincipal());
//                    }
//                })
//                .permitAll()
                .and()
                .csrf()
                .disable();


        http.cors().and().csrf().disable();

        //单用户登录，如果有一个登录了，同一个用户在其他地方登录将前一个剔除下线
        http.sessionManagement().maximumSessions(1);//.expiredSessionStrategy(expiredSessionStrategy());
        //单用户登录，如果有一个登录了，同一个用户在其他地方不能登录
//        http.sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(true);

        //开启了登出页面,如果跳转不了就关闭csrf功能。或者表单来提交并且用post请求
        //默认的登出页面为login。logoutSuccessUrl可以设置登出页面
        // handler和Url冲突
        http.logout()
                .invalidateHttpSession(true)
                .deleteCookies("remove","user")
                .logoutUrl("/toLogout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        User attribute = (User) request.getSession().getAttribute(LoginConstant.USER);
//                        request.getSession().removeAttribute(LoginConstant.USER);
                        redissonClient.getKeys().delete(RedisConstant.USER_LOGIN_PREFIX + ((User)authentication.getPrincipal()).getId());
                        // 删除Cookie
//                        Cookie cookie = new Cookie(LoginConstant.USER, null);
//                        cookie.setMaxAge(0);
//                        response.addCookie(cookie);
                        response.sendRedirect("/toSignIn");
                    }
                });
//                .logoutSuccessUrl("/toSignIn");

        //退出时情况cookies
        http.logout().deleteCookies("JESSIONID");


        //解决中文乱码问题
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8"); filter.setForceEncoding(true);
        //
        http.addFilterBefore(filter, CsrfFilter.class);


        //开启记住我功能并设置表单传上来的参数  cookie 默认保存
        http.rememberMe().rememberMeParameter("remember me");
        //我的页面是USERNAME和PASSWORD。所以要设置对应的usernameParameter和passwordParameter
        http.formLogin().loginPage("/toSignIn")
                .loginProcessingUrl("/login")
                .usernameParameter("username").passwordParameter("password")
//                .successForwardUrl("/user/index").defaultSuccessUrl("/user/index")
                // 定义了handle之后forwardUrl就失效了
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        Object principal = authentication.getPrincipal();

                        Object credentials = authentication.getCredentials();
                        Object details = authentication.getDetails();
                        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                        System.out.println(principal);
                        System.out.println(details);
                        System.out.println(credentials);
                        System.out.println(authorities);
                        request.getSession().setAttribute(LoginConstant.USER,principal);
//                        Cookie cookie = new Cookie(LoginConstant.USER, user.getId()+"");
//                        response.addCookie(cookie);
                        response.sendRedirect("/user/index");
                    }
                })
                .failureForwardUrl("/toSignIn")
                // 自定义登录失败处理器
                .failureHandler(new AuthenticationFailureHandler() {
                                    @Override
                                    public void onAuthenticationFailure(HttpServletRequest req,
                                                                        HttpServletResponse resp,
                                                                        AuthenticationException e)
                                            throws IOException, ServletException {
//                                        resp.setContentType("application/json;charset=utf-8");
//                                        PrintWriter out = resp.getWriter();
//                                        resp.setStatus(401);
//                                        Map<String, Object> map = new HashMap<>();
//                                        map.put("status", 401);
//                                        if (e instanceof LockedException) {
//                                            map.put("msg", "账户被锁定，登录失败!"); } else if (e instanceof BadCredentialsException) {
//                                            map.put("msg", "账户名或密码输入错误，登录失败!"); } else if (e instanceof DisabledException) {
//                                            map.put("msg", "账户被禁用，登录失败!"); } else if (e instanceof AccountExpiredException) {
//                                            map.put("msg", "账户已过期，登录失败!"); } else if (e instanceof CredentialsExpiredException) {
//                                            map.put("msg", "密码已过期，登录失败!"); } else {
//                                            map.put("msg", "登录失败!");
//                                        }
//
                                        String msg = "";
                                        if (e instanceof LockedException) {
                                            msg = "账户被锁定";
                                        } else if (e instanceof BadCredentialsException) {
                                            msg = "账户名或密码输入错误";
                                        } else if (e instanceof DisabledException) {
                                            msg = "账户被禁用";
                                        } else if (e instanceof AccountExpiredException) {
                                            msg = "账户已过期";
                                        } else if (e instanceof CredentialsExpiredException) {
                                            msg = "密码已过期";
                                        } else {
                                            msg = "登录失败!";
                                        }
                                        HttpResult error = HttpResult.error(401, msg);
                                        req.setAttribute("info",error);
                                        req.getRequestDispatcher("/toSignIn").forward(req,resp);
//                                        ObjectMapper om = new ObjectMapper();
//                                        out.write(om.writeValueAsString(map));
//                                        out.flush();
//                                        out.close();
                                    }
                                }
                );
    }
}