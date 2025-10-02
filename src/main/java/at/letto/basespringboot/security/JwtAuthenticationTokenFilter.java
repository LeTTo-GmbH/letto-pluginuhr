package at.letto.basespringboot.security;

import at.letto.security.LettoToken;
import at.letto.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to check the JWT token in the request header.<br>
 * Speichert den LettoToken als Authentication-Objekt in der SecurityContext.<br>
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtService;

    @Autowired
    public JwtAuthenticationTokenFilter(JwtTokenService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestHeader = request.getHeader(SecurityConstants.TOKEN_HEADER);
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) try {
            // Extract the token from the header
            String  authToken = requestHeader.substring(7);
            // Create a LettoToken object from the token
            LettoToken lettoToken = jwtService.toLettoToken(authToken);
            if (lettoToken==null)
                SecurityContextHolder.clearContext();
            else {
                JwtAuthentication authentication = new JwtAuthentication(lettoToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Token abgelaufen - clear the security context
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }
}