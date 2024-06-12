package at.letto.basespringboot.security;

import at.letto.security.LettoToken;
import at.letto.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Setter private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestHeader = request.getHeader(SecurityConstants.TOKEN_HEADER);
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            String  authToken = requestHeader.substring(7);
            LettoToken lettoToken = new LettoToken(authToken,jwtSecret);
            JwtAuthentication authentication = new JwtAuthentication(lettoToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}