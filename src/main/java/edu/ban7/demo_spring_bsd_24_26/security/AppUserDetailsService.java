package edu.ban7.demo_spring_bsd_24_26.security;

import edu.ban7.demo_spring_bsd_24_26.dao.AppUserDao;
import edu.ban7.demo_spring_bsd_24_26.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    protected AppUserDao utilisateurDao;

    @Autowired
    public AppUserDetailsService(AppUserDao utilisateurDao) {
        this.utilisateurDao = utilisateurDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<AppUser> optionalUtilisateur = utilisateurDao.findByEmail(email);

        if (optionalUtilisateur.isEmpty()) {
            throw new UsernameNotFoundException(email);
        } else {
            return new AppUserDetails(optionalUtilisateur.get());
        }
    }
}
