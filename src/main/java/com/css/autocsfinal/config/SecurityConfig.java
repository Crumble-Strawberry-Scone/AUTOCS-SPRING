package com.css.autocsfinal.config;

import com.css.autocsfinal.jwt.JwtAccessDeniedHandler;
import com.css.autocsfinal.jwt.JwtAuthenticationEntryPoint;
import com.css.autocsfinal.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(TokenProvider tokenProvider
            , JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
            , JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }



    /* 2. Security 설정을 무시할 정적 리소스 등록 */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().antMatchers("/css/**", "/js/**", "/images/**",
                "/lib/**");
    }

    /* 3. HTTP요청에 대한 권한별 설정(기존 세션 인증 -> 토큰 인증으로 변경함 :> 추가적으로 코드가 더 생김) */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .exceptionHandling()
                /* 기본 시큐리티 설정에서 JWT 토큰과 관련된 유효성과 권한 체크용 설정*/
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)//  필요한 권한 없이 접근(403)
                .accessDeniedHandler(jwtAccessDeniedHandler)          // 유효한 자격 증명 없을 시(401)
                .and()
                .authorizeRequests()

                .antMatchers("/").authenticated()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()   // cors를 위해 preflight 요청 처리용 option요청 허용
                // preflight란?
                // 요청할 url의 외부 도메인일 경우 웹 브라우저에서 자체 실행되며
                // options 메소드로 사전 요청을 보내게된다.
                // 사전에 요청이 안전한지 확인하기 위함이다(유효한지 서버에 미리 파악할 수 있도록 보내는 수단을 의미)
//                .antMatchers("/member/**").hasAnyRole("EMPLOYEE")
                //---------------------------------------------------------------여기에 추가하세용












               .anyRequest().permitAll()  // 어떤 요청이든 허용 가능, 구현전에 추가해보기
//                .and()
//                .formLogin()        // 로그인 form을 따로 이용해 로그인 처리할 것이다.
//                .loginPage("/login")  // login Page로 로그인페이지에서 submit요청하는 경로로 지정하겠다.
//                .successForwardUrl("/")      // 성공 시 페이지 설정
//                .and()
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/login"))  // 로그아웃 시 요청 경로
//                .deleteCookies("JSESSIONID")  // 쿠키 제거
//                .and()
//                .exceptionHandling()                     // 인가/인증 exception 핸들링 설정
//                .accessDeniedPage("/noAuthority")     // 인가 되지 않았을 때 - 권한이 없는 기능을 요청했을 떄 랜더랑 할 페이지
//                .and()
//                /* 세션 인증 방식을 쓰지 않겠다는 설정 */
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors()
                .and()
                /* jwt토큰 방식을 쓰겠다는 설정*/
                .apply(new JwtSecurityConfig(tokenProvider));
        return http.build();
    }
    /* 4. CORS(Cross-origin-resource-sharing) 설정용 Bean */
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//        configuration.setAllowedOrigins(Arrays.asList("http://autocs.coffit.today:3000"));  // 도커용
        configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Content-type"
                , "Access-Control-Allow-Headers", "Authorization"
                , "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
