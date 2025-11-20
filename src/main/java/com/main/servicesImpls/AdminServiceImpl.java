package com.main.servicesImpls;

import com.main.dtos.AdminDto;
import com.main.entities.Admin;

import com.main.mappers.InvoiceMapper;
import com.main.repositories.AdminRepository;
import com.main.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private InvoiceMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminDto registerAdmin(AdminDto adminDto) {
        // Validate email
        if (adminRepo.existsByEmail(adminDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        
        // Validate phone number format
        if (adminDto.getPhoneNumber() == null || !adminDto.getPhoneNumber().matches("^[6-9]\\d{9}$")) {
            throw new IllegalArgumentException("Phone number must be 10 digits and start with 6, 7, 8, or 9");
        }
        
        if (adminRepo.existsByPhoneNumber(adminDto.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already in use");
        }
        
        Admin admin = adminMapper.dtoToAdmin(adminDto);
        String hashedPassword = passwordEncoder.encode(adminDto.getPassword());
        admin.setPassword(hashedPassword);
        Admin saved = adminRepo.save(admin);
        AdminDto dto = adminMapper.adminToDto(saved);
        // Never expose hashed password in API responses
        dto.setPassword(null);
        return dto;
    }
}
