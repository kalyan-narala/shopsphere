package com.shopsphere.user.service;

import com.shopsphere.user.dto.LoginRequest;
import com.shopsphere.user.dto.LoginResponse;
import com.shopsphere.user.dto.RegisterRequest;
import com.shopsphere.user.dto.UserResponse;
import com.shopsphere.user.entity.User;
import com.shopsphere.user.enums.AccountStatus;
import com.shopsphere.user.enums.Role;
import com.shopsphere.user.exception.AccountNotActiveException;
import com.shopsphere.user.exception.EmailAlreadyExistsException;
import com.shopsphere.user.exception.InvalidCredentialsException;
import com.shopsphere.user.mapper.UserMapper;
import com.shopsphere.user.repository.UserRepository;
import com.shopsphere.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    /**
     * Registers a new user after checking email uniqueness,
     * encoding the password, and applying default account settings.
     */
    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered"
            );
        }

        User user = userMapper.toEntity(request);

        // Password must never be stored in plain text.
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Registration defaults are controlled by the service, not the client.
        user.setRole(Role.USER);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException("Account is not active");
        }

        user.setLastLoginAt(LocalDateTime.now());

        String accessToken = jwtService.generateToken(user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(3600)
                .build();
    }
}