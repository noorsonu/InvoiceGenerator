package com.main.servicesImpls;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.main.entities.Admin;
import com.main.repositories.AdminRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtAdminDetails implements UserDetailsService {
	
    private final AdminRepository adminRepo;

    public JwtAdminDetails(AdminRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Admin> adminOpt = adminRepo.findByEmail(email);
        Admin admin = adminOpt.orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));
        Set<SimpleGrantedAuthority> authorities = getDefaultAuthority();
        return new org.springframework.security.core.userdetails.User(admin.getEmail(),
                admin.getPassword(), true, true, true, true, authorities);
    }

    public Set<SimpleGrantedAuthority> getDefaultAuthority() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return authorities;
    }
}
