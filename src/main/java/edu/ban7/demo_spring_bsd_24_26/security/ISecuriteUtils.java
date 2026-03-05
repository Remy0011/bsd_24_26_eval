package edu.ban7.demo_spring_bsd_24_26.security;

public interface ISecuriteUtils {
    String getRole(AppUserDetails userDetails);

    String generateToken(AppUserDetails userDetails);

    String getSubjectFromJwt(String jwt);
}
