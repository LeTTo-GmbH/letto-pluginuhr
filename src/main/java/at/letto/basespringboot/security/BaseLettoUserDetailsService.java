package at.letto.basespringboot.security;

import at.letto.basespringboot.service.RestUser;
import at.letto.security.SecurityConstants;
import at.letto.tools.config.MicroServiceConfigurationInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;

/** User-Service for receiving authentication-information of users */
@Service
public class BaseLettoUserDetailsService implements UserDetailsService {

    public static final String ROLE_GAST   = "gast";
    public static final String ROLE_USER   = "user";
    public static final String ROLE_ADMIN  = "admin";
    public static final String ROLE_GLOBAL = "global";
    public static final String ROLE_LETTO  = "letto";
    public static final String ROLE_GAST_USER  = ROLE_GAST+","+ROLE_USER;
    public static final String ROLE_GAST_USER_ADMIN_GLOBAL  = ROLE_GAST+","+ROLE_USER+","+ROLE_ADMIN+","+ROLE_GLOBAL;
    public static final String ROLE_GAST_LETTO  = ROLE_GAST+","+ROLE_LETTO;


    public void loadUserList(String gastPW, String gastRoles, String userPW, String userRoles, String adminPW, String adminRoles) {
        updateUser("gast", gastPW, gastRoles);
        updateUser("user", userPW, userRoles);
        updateUser("admin", adminPW, adminRoles);
    }


    @Value("${"+ MicroServiceConfigurationInterface.DEFuserGastPassword+"}")
    private String userGastPassword;
    @Value("${"+MicroServiceConfigurationInterface.DEFuserGastEncryptedPassword+"}")
    private String userGastEncryptedPassword;
    @Value("${"+MicroServiceConfigurationInterface.DEFuserGastRoles+"}")
    private String userGastRoles;

    @Value("${"+MicroServiceConfigurationInterface.DEFuserUserPassword+"}")
    private String userUserPassword;
    @Value("${"+MicroServiceConfigurationInterface.DEFuserUserEncryptedPassword+"}")
    private String userUserEncryptedPassword;
    @Value("${"+MicroServiceConfigurationInterface.DEFuserUserRoles+"}")
    private String userUserRoles;

    @Value("${"+MicroServiceConfigurationInterface.DEFuserAdminPassword+"}")
    private String userAdminPassword;
    @Value("${"+MicroServiceConfigurationInterface.DEFuserAdminEncryptedPassword+"}")
    private String userAdminEncryptedPassword;
    @Value("${"+MicroServiceConfigurationInterface.DEFuserAdminRoles+"}")
    private String userAdminRoles;

    @Value("${"+MicroServiceConfigurationInterface.DEFuserLettoPassword+"}")
    private String userLettoPassword;
    @Value("${"+MicroServiceConfigurationInterface.DEFuserLettoEncryptedPassword+"}")
    private String userLettoEncryptedPassword;
    @Value("${"+MicroServiceConfigurationInterface.DEFuserLettoRoles+"}")
    private String userLettoRoles;

    /** Liste aller Benutzer, welche sich an dem Server anmelden können */
    protected HashMap<String, RestUser> users = new HashMap<String, RestUser>();

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        if (users.containsKey(userName)) {
            RestUser restUser = users.get(userName);
            UserDetails user = User
                    .withUsername(restUser.getName())
                    .password(restUser.getEncodedpassword())
                    .roles(restUser.getRoles()).build();
            return user;
        } else throw(new UsernameNotFoundException("Username " + userName + " not found"));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(RestUser restUser) {
        return AuthorityUtils.createAuthorityList(restUser.getRoles());
    }

    public void loadUserList() {
        String gastPW    = SecurityConstants.gastPasswordEncrypted;
        String userPW    = SecurityConstants.userPasswordEncrypted;
        String adminPW   = SecurityConstants.adminPasswordEncrypted;
        String lettoPW   = SecurityConstants.lettoPasswordEncrypted;
        String gastRoles = ROLE_GAST;
        String userRoles = ROLE_GAST_USER;
        String adminRoles= ROLE_GAST_USER_ADMIN_GLOBAL;
        String lettoRoles= ROLE_GAST_LETTO;

        /*updateUser("gast", gastPW,  gastRoles);
        updateUser("user", userPW,  userRoles);
        updateUser("admin",adminPW, adminRoles);



        if (userGastEncryptedPassword != null && userGastEncryptedPassword.length()>0)
            gastPW = userGastEncryptedPassword;
        else if (userGastPassword != null && userGastPassword.length()>0)
            gastPW = (new BCryptPasswordEncoder()).encode(userGastPassword);
        if (userUserEncryptedPassword != null && userUserEncryptedPassword.length()>0)
            userPW = userUserEncryptedPassword;
        else if (userUserPassword != null && userUserPassword.length()>0)
            userPW = (new BCryptPasswordEncoder()).encode(userUserPassword);
        if (userAdminEncryptedPassword != null && userAdminEncryptedPassword.length()>0)
            adminPW = userAdminEncryptedPassword;
        else if (userAdminPassword != null && userAdminPassword.length()>0)
            adminPW = (new BCryptPasswordEncoder()).encode(userAdminPassword);
        if (userLettoEncryptedPassword != null && userLettoEncryptedPassword.length()>0)
            lettoPW = userLettoEncryptedPassword;
        else if (userLettoPassword != null && userLettoPassword.length()>0)
            lettoPW = (new BCryptPasswordEncoder()).encode(userLettoPassword);

        if (userGastRoles != null && userGastRoles.length()>0)
            gastRoles = userGastRoles;
        if (userUserRoles != null && userUserRoles.length()>0)
            userRoles = userUserRoles;
        if (userAdminRoles!=null && userAdminRoles.length()>0)
            adminRoles = userAdminRoles;
        if (userLettoRoles!= null && userLettoRoles.length()>0)
            lettoRoles = userLettoRoles;

        updateUser("gast", gastPW,  gastRoles);
        updateUser("user", userPW,  userRoles);
        updateUser("admin",adminPW, adminRoles);*/

        if (userGastEncryptedPassword != null && userGastEncryptedPassword.length()>0)
            gastPW = userGastEncryptedPassword;
        else if (userGastPassword != null && userGastPassword.length()>0)
            gastPW = (new BCryptPasswordEncoder()).encode(userGastPassword);
        if (userUserEncryptedPassword != null && userUserEncryptedPassword.length()>0)
            userPW = userUserEncryptedPassword;
        else if (userUserPassword != null && userUserPassword.length()>0)
            userPW = (new BCryptPasswordEncoder()).encode(userUserPassword);
        if (userAdminEncryptedPassword != null && userAdminEncryptedPassword.length()>0)
            adminPW = userAdminEncryptedPassword;
        else if (userAdminPassword != null && userAdminPassword.length()>0)
            adminPW = (new BCryptPasswordEncoder()).encode(userAdminPassword);
        if (userLettoEncryptedPassword != null && userLettoEncryptedPassword.length()>0)
            lettoPW = userLettoEncryptedPassword;
        else if (userLettoPassword != null && userLettoPassword.length()>0)
            lettoPW = (new BCryptPasswordEncoder()).encode(userLettoPassword);

        if (userGastRoles != null && userGastRoles.length()>0)
            gastRoles = userGastRoles;
        if (userUserRoles != null && userUserRoles.length()>0)
            userRoles = userUserRoles;
        if (userAdminRoles!=null && userAdminRoles.length()>0)
            adminRoles = userAdminRoles;
        if (userLettoRoles!= null && userLettoRoles.length()>0)
            lettoRoles = userLettoRoles;

        updateUser("gast", gastPW,  gastRoles);
        updateUser("user", userPW,  userRoles);
        updateUser("admin",adminPW, adminRoles);
        updateUser("letto",lettoPW, lettoRoles);
    }

    public void updateUserPassword(String user, String password) {
        user = user.trim();
        password = password.trim();
        RestUser restUser;
        if (user.length()>0) {
            if (users.containsKey(user)) {
                restUser = users.get(user);
            } else {
                restUser = new RestUser();
                restUser.setName(user);
            }
            restUser.setPassword(password);
            users.put(user,restUser);
        }
    }

    public void updateUserEncryptedPassword(String user, String password) {
        user = user.trim();
        password = password.trim();
        RestUser restUser;
        if (user.length()>0) {
            if (users.containsKey(user)) {
                restUser = users.get(user);
            } else {
                restUser = new RestUser();
                restUser.setName(user);
            }
            restUser.setEncodedpassword(password);
            users.put(user,restUser);
        }
    }

    public void updateUserRoles(String user, String role) {
        user = user.trim();
        String[] roles = role.trim().split(",");
        RestUser restUser;
        if (user.length()>0) {
            if (users.containsKey(user)) {
                restUser = users.get(user);
            } else {
                restUser = new RestUser();
                restUser.setName(user);
            }
            for (String r:roles)
                if (role.trim().length()>0)
                    restUser.addRole(r.trim());
            users.put(user,restUser);
        }
    }

    public void updateUser(String user, String password, String role) {
        user = user.trim();
        String[] roles = role.trim().split(",");
        password = password.trim();
        RestUser restUser;
        if (user.length()>0) {
            if (users.containsKey(user)) {
                restUser = users.get(user);
            } else {
                restUser = new RestUser();
                restUser.setName(user);
            }
            for (String r:roles)
                if (role.trim().length()>0)
                    restUser.addRole(r.trim());
            restUser.setEncodedpassword(password);
            users.put(user,restUser);
        }
    }

}
