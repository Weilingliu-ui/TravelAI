package com.travelai.travelai.config;

import com.travelai.travelai.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置
 * <p>
 * 无状态JWT认证模式，API路径权限控制。
 * 当前阶段：开放所有API端点，后续逐步收紧。
 *
 * @author TravelAI Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（JWT + REST API 不需要）
                .csrf(AbstractHttpConfigurer::disable)

                // 无状态会话
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 请求路径权限配置
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 预检请求放行
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Actuator 健康检查
                        .requestMatchers("/actuator/health").permitAll()
                        // Swagger/Knife4j 文档 — 匿名访问
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/doc.html"
                        ).permitAll()
                        // 认证接口 — 匿名访问
                        .requestMatchers("/api/auth/**").permitAll()
                        // 基础数据（省份/城市/景点）— 匿名访问
                        .requestMatchers(
                                "/api/provinces/**",
                                "/api/cities/**",
                                "/api/attractions/**",
                                "/api/files/**"
                        ).permitAll()
                        // 业务接口 — 需要JWT认证
                        .requestMatchers(
                                "/api/users/**",
                                "/api/travel-plans/**",
                                "/api/favorites/**",
                                "/api/comments/**"
                        ).authenticated()
                        // 其余API默认需要认证
                        .requestMatchers("/api/**").authenticated()
                        // 静态资源
                        .requestMatchers("/static/**", "/public/**").permitAll()
                        // 其余请求需要认证
                        .anyRequest().authenticated()
                )

                // 异常处理
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(401);
                            response.getWriter().write(
                                    "{\"code\":401,\"message\":\"未授权，请先登录\",\"timestamp\":" +
                                            System.currentTimeMillis() + "}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(403);
                            response.getWriter().write(
                                    "{\"code\":403,\"message\":\"无权限访问\",\"timestamp\":" +
                                            System.currentTimeMillis() + "}"
                            );
                        })
                )

                // JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
