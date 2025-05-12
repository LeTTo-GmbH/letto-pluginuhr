package at.letto.basespringboot.security;

import at.letto.security.LettoToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

/**
 * Authentication-Objekt mit einem LettoToken welches im SecurityContext gespeichert wird
 */
public class JwtAuthentication implements Authentication {
    private final LettoToken lettoToken;

    public JwtAuthentication(LettoToken lettoToken) {
        this.lettoToken = lettoToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(lettoToken.getRolesArray());
    }

    @Override
    public String getCredentials() {
        if (lettoToken==null) return null;
        return lettoToken.getUsername();
    }

    @Override
    public LettoToken getDetails() {
        return lettoToken;
    }

    @Override
    public String getPrincipal() {
        if (lettoToken==null) return null;
        return lettoToken.getIssuer();
    }

    @Override
    public boolean isAuthenticated() {
        if (lettoToken==null) return false;
        return lettoToken.isValid() && lettoToken.isTokenNotExpired();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        if (lettoToken==null) return null;
        return lettoToken.getUsername();
    }

}
