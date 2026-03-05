package edu.ban7.demo_spring_bsd_24_26.controller;

import edu.ban7.demo_spring_bsd_24_26.dao.AppUserDao;
import edu.ban7.demo_spring_bsd_24_26.model.AppUser;
import edu.ban7.demo_spring_bsd_24_26.security.AppUserDetails;
import edu.ban7.demo_spring_bsd_24_26.security.ISecuriteUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class AuthController {

    protected final PasswordEncoder passwordEncoder;
    protected final AuthenticationProvider authenticationProvider;
    protected final ISecuriteUtils securiteUtils;
    private final AppUserDao appUserDao;

    @PostMapping("/inscription")
    public ResponseEntity<Void> inscription(@RequestBody AppUser user) throws IOException {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAdmin(false);

        appUserDao.save(user);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/connexion")
    public ResponseEntity<String> connexion(@RequestBody AppUser user) {

            try {
                AppUserDetails userDetails = (AppUserDetails) authenticationProvider
                        .authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        user.getEmail(),
                                        user.getPassword()))
                        .getPrincipal();

                return new ResponseEntity<>(securiteUtils.generateToken(userDetails), HttpStatus.OK);

            } catch (AuthenticationException e) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
    }
}
