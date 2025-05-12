package at.letto.basespringboot.security;

import at.letto.basespringboot.config.BaseMicroServiceConfiguration;
import at.letto.databaseclient.service.BaseLettoRedisDBService;
import at.letto.login.restclient.RestLoginService;
import at.letto.restclient.endpoint.BaseEndpoints;
import at.letto.restclient.endpoint.EndpointInterface;
import at.letto.security.SecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Base64;
import static at.letto.basespringboot.security.BaseLettoUserDetailsService.*;
import static at.letto.security.LettoToken.ROLE_STUDENT;
import static at.letto.security.LettoToken.ROLE_TEACHER;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationFilter;

    @Autowired
    private BaseLettoUserDetailsService userInfoService;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(jwtAuthenticationProvider);
    }

    private EndpointInterface endpoint;

    public void init(BaseMicroServiceConfiguration mc, EndpointInterface endpoint) {
        mc.webSecurityConfig = this;
        setJwtSecret(mc.getJwtSecret(), mc.getJwtExpiration(),mc);
        this.endpoint= endpoint;
        /*userInfoService.loadUserList(mc.getUserGastPassword(), "gast",
                mc.getUserUserPassword(), "gast,user",
                mc.getUserAdminPassword(), "gast,user,admin"
        );*/
        userInfoService.loadUserList();
    }

    public void setJwtSecret(String jwtSecret, long jwtExpiration,BaseMicroServiceConfiguration mc) {
        RestLoginService restLoginService = new RestLoginService(mc.getLoginServiceUri());
        jwtAuthenticationProvider.init(jwtSecret, jwtExpiration,restLoginService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userInfoService);
        return provider;
    }

    @Autowired
    private ApplicationContext appContext;

    /** Root-Pfad und Service-Path für alle frei geben, nicht rekursiv */
    @Bean
    public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher("/*")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    /** Service-Pfad für alle frei, nicht rekursiv */
    @Bean
    public SecurityFilterChain filterChain2(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .headers(header->header.frameOptions(fo->fo.disable()))
                .exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher(endpoint.servicepath()+"/*")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    /** OPEN rekursiv*/
    @Bean
    public SecurityFilterChain filterChain3(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .headers(header->header.frameOptions(fo->fo.disable()))
                .exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher(endpoint.OPEN()+"/**", BaseEndpoints.OPEN+"/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    /** API rekursiv mit JWT-Token-Authentifikation */
    @Bean
    public SecurityFilterChain filterChain4(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher(endpoint.API()+"/**", BaseEndpoints.API+"/**")
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                new AntPathRequestMatcher(endpoint.API()+"/"),
                                new AntPathRequestMatcher(endpoint.OPENAPI()),
                                new AntPathRequestMatcher(endpoint.OPENAPI()+"/"),
                                new AntPathRequestMatcher(endpoint.OPENAPI()+"/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(endpoint.STUDENT() + "/**")).hasAnyAuthority(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN)
                        .requestMatchers(new AntPathRequestMatcher(endpoint.TEACHER() + "/**")).hasAnyAuthority(ROLE_TEACHER, ROLE_ADMIN)
                        .requestMatchers(new AntPathRequestMatcher(endpoint.ADMIN() + "/**")).hasAnyAuthority(ROLE_ADMIN)
                        .requestMatchers(new AntPathRequestMatcher(endpoint.GLOBAL() + "/**")).hasAnyAuthority(ROLE_GLOBAL)

                        .requestMatchers(
                                new AntPathRequestMatcher(BaseEndpoints.API+"/"),
                                new AntPathRequestMatcher(BaseEndpoints.API_OPEN),
                                new AntPathRequestMatcher(BaseEndpoints.API_OPEN+"/"),
                                new AntPathRequestMatcher(BaseEndpoints.API_OPEN+"/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(BaseEndpoints.API_STUDENT + "/**")).hasAnyAuthority(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN)
                        .requestMatchers(new AntPathRequestMatcher(BaseEndpoints.API_TEACHER + "/**")).hasAnyAuthority(ROLE_TEACHER, ROLE_ADMIN)
                        .requestMatchers(new AntPathRequestMatcher(BaseEndpoints.API_ADMIN + "/**")).hasAnyAuthority(ROLE_ADMIN)
                        .requestMatchers(new AntPathRequestMatcher(BaseEndpoints.API_GLOBAL + "/**")).hasAnyAuthority(ROLE_GLOBAL)

                        .anyRequest().authenticated()
                );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** AUTH rekursiv mit User-Authentifikation */
    @Bean
    public SecurityFilterChain filterChain5(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception->exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher(endpoint.AUTH()+"/**",BaseEndpoints.AUTH+"/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher( endpoint.AUTH_GAST() + "/**"))
                            .access((authentication, context) ->
                                    new AuthorizationDecision(check(authentication, context.getRequest(),"ROLE_"+ROLE_GAST)))

                        .requestMatchers(new AntPathRequestMatcher( endpoint.AUTH_USER() + "/**"))
                            .access((authentication, context) ->
                                    new AuthorizationDecision(check(authentication, context.getRequest(), "ROLE_"+ROLE_USER)))

                        .requestMatchers(new AntPathRequestMatcher( endpoint.AUTH_LETTO() + "/**"))
                            .access((authentication, context) ->
                                    new AuthorizationDecision(check(authentication, context.getRequest(), "ROLE_"+ROLE_LETTO)))

                        .requestMatchers(new AntPathRequestMatcher( endpoint.AUTH_ADMIN() + "/**"))
                            .access((authentication, context) ->
                                    new AuthorizationDecision(check(authentication, context.getRequest(),"ROLE_"+ROLE_ADMIN)))

                        .requestMatchers(new AntPathRequestMatcher( BaseEndpoints.AUTH_GAST + "/**"))
                        .access((authentication, context) ->
                                new AuthorizationDecision(check(authentication, context.getRequest(),"ROLE_"+ROLE_GAST)))

                        .requestMatchers(new AntPathRequestMatcher( BaseEndpoints.AUTH_USER + "/**"))
                        .access((authentication, context) ->
                                new AuthorizationDecision(check(authentication, context.getRequest(), "ROLE_"+ROLE_USER)))

                        .requestMatchers(new AntPathRequestMatcher( BaseEndpoints.AUTH_LETTO + "/**"))
                        .access((authentication, context) ->
                                new AuthorizationDecision(check(authentication, context.getRequest(), "ROLE_"+ROLE_LETTO)))

                        .requestMatchers(new AntPathRequestMatcher( BaseEndpoints.AUTH_ADMIN + "/**"))
                        .access((authentication, context) ->
                                new AuthorizationDecision(check(authentication, context.getRequest(),"ROLE_"+ROLE_ADMIN)))

                        .anyRequest().authenticated()
                );
        return http.build();
    }

    /**SESSION rekursiv mit Login-Seite und Authentifkations-Controller */
    @Bean
    public SecurityFilterChain filterChain6(HttpSecurity http) throws Exception {
        http.cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .httpBasic(withDefaults())
                .securityContext(securityContext -> securityContext.
                        securityContextRepository(new HttpSessionSecurityContextRepository())
                )
                .securityMatcher(endpoint.SESSION() + "/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher(endpoint.SESSION() + "/"),
                                new AntPathRequestMatcher(endpoint.SESSION() + "/*")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher(endpoint.SESSION_STUDENT() + "/**")).hasAnyAuthority(ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN)
                        .requestMatchers(new AntPathRequestMatcher(endpoint.SESSION_TEACHER() + "/**")).hasAnyAuthority(ROLE_TEACHER, ROLE_ADMIN)
                        .requestMatchers(new AntPathRequestMatcher(endpoint.SESSION_ADMIN() + "/**")).hasAnyAuthority(ROLE_ADMIN)
                        .requestMatchers(new AntPathRequestMatcher(endpoint.SESSION_GLOBAL() + "/**")).hasAnyAuthority(ROLE_GLOBAL)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage(endpoint.LOGIN()).permitAll())
                .logout(logout -> logout
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().newSession()
                        // .maximumSessions(1).maxSessionsPreventsLogin(true)
                )
        ;
        //http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    public boolean check(Object x, HttpServletRequest request, String role) {
        try {
            String[] credentials = new String(decoder.decode(
                    request.getHeader(SecurityConstants.TOKEN_HEADER).replaceAll("Basic ", "")
            )).split(":");
            String user = credentials[0];
            String pwd = credentials[1];
            UserDetails u = userInfoService.loadUserByUsername(user);

            if (u.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(role))) return false;
            if (u == null) return false;
            return passwordEncoder().matches(pwd, u.getPassword());
        } catch (Exception ex) {
            return false;
        }
    }

    /** Dekoder zum Entschlüsseln des JWT-Token-JSONs */
    private static Base64.Decoder decoder = Base64.getUrlDecoder();

}
