package com.codecluster.auth.service.impl;

import com.codecluster.auth.dto.request.RegisterRequest;
import com.codecluster.auth.dto.response.AuthResponse;
import com.codecluster.auth.repository.RoleRepository;
import com.codecluster.auth.repository.UserRepository;
import com.codecluster.auth.repository.UserRoleRepository;
import com.codecluster.auth.security.JwtService;
import com.codecluster.auth.service.AuthService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.codecluster.auth.entity.Role;
import com.codecluster.auth.entity.User;
import com.codecluster.auth.entity.UserStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.codecluster.auth.entity.UserRole;
import com.codecluster.auth.entity.UserRoleId;
import com.codecluster.auth.dto.response.UserResponse;
import com.codecluster.auth.exception.EmailAlreadyExistsException;
import com.codecluster.auth.exception.RoleNotFoundException;
import com.codecluster.auth.dto.request.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.codecluster.auth.exception.UserNotFoundException;

@Service
public class AuthServiceImpl implements AuthService {

    //fields

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        // Check if email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        // Find role
        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() ->
                        new RoleNotFoundException("Role not found"));

        // Generate unique username
        String username = generateUsername(
                request.getFirstName(),
                request.getLastName()
        );

        //Create the User object
        User user = new User();

        user.setUserId(UUID.randomUUID());

        user.setName(
                request.getFirstName() + " " + request.getLastName()
        );

        user.setUsername(username);

        user.setEmail(request.getEmail());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setStatus(UserStatus.ACTIVE);

        user.setCreatedAt(OffsetDateTime.now());

        user.setUpdatedAt(OffsetDateTime.now());

        //Save User
        userRepository.save(user);

        UserRole userRole = new UserRole();

        UserRoleId userRoleId = new UserRoleId();

        userRoleId.setUserId(user.getUserId());
        userRoleId.setRoleId(role.getRoleId());

        userRole.setId(userRoleId);

        userRole.setUser(user);

        userRole.setRole(role);

        userRole.setAssignedAt(OffsetDateTime.now());

        userRoleRepository.save(userRole);

        //generate access token and refresh token
        String accessToken =
                jwtService.generateAccessToken(user.getUsername());

        String refreshToken =
                jwtService.generateRefreshToken(user.getUsername());

        //creating the response object
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getUserId());

        userResponse.setEmail(user.getEmail());

        userResponse.setFirstName(request.getFirstName());

        userResponse.setLastName(request.getLastName());

        userResponse.setRole(role.getRoleName());

        userResponse.setActive(user.getStatus() == UserStatus.ACTIVE);

        userResponse.setCreatedAt(user.getCreatedAt());

        //Build AuthResponse
        AuthResponse response = new AuthResponse();

        response.setAccessToken(accessToken);

        response.setRefreshToken(refreshToken);

        response.setTokenType("Bearer");

        response.setExpiresIn(900);

        response.setUser(userResponse);

        return response;


    }

    private String generateUsername(String firstName, String lastName) {

        String baseUsername = (firstName + "." + lastName)
                .toLowerCase()
                .replaceAll("\\s+", "");

        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        //load the user

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Role role = user.getUserRoles()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("User has no role"))
                .getRole();

        //generate JWT token

        String accessToken =
                jwtService.generateAccessToken(user.getUsername());

        String refreshToken =
                jwtService.generateRefreshToken(user.getUsername());


        //building UserResponse

        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getUserId());

        userResponse.setEmail(user.getEmail());

        String[] nameParts = user.getName().split(" ", 2);

        userResponse.setFirstName(nameParts[0]);

        userResponse.setLastName(
                nameParts.length > 1 ? nameParts[1] : ""
        );

        userResponse.setRole(role.getRoleName());

        userResponse.setActive(user.getStatus() == UserStatus.ACTIVE);

        userResponse.setCreatedAt(user.getCreatedAt());

        //Build AuthResponse

        AuthResponse response = new AuthResponse();

        response.setAccessToken(accessToken);

        response.setRefreshToken(refreshToken);

        response.setTokenType("Bearer");

        response.setExpiresIn(900);

        response.setUser(userResponse);

        return response;


    }




}