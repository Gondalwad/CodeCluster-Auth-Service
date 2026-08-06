package com.codecluster.auth.service.impl;

import com.codecluster.auth.dto.request.RegisterRequest;
import com.codecluster.auth.dto.response.AuthResponse;
import com.codecluster.auth.exception.UsernameAlreadyExistsException;
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
import java.util.Optional;
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

import com.codecluster.auth.dto.request.RefreshTokenRequest;
import com.codecluster.auth.dto.response.RefreshTokenResponse;
import org.springframework.security.core.userdetails.UserDetails;

import com.codecluster.auth.security.CustomUserDetails;

import java.util.Date;
import com.codecluster.auth.service.RedisService;

import com.codecluster.auth.dto.request.TokenValidateRequest;
import com.codecluster.auth.dto.response.TokenValidateResponse;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    //fields

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            RedisService redisService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.redisService = redisService;
    }


    // to register
    @Override
    public AuthResponse register(RegisterRequest request) {

        // Check if email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        if(userRepository.existsByUsername(request.getUserName())){
            throw new UsernameAlreadyExistsException("Username '"+request.getUserName()+"' already exists");
        }


        // Find role
        Role role = roleRepository.findByRoleName("USER").get();


        //Create the User object
        User user = new User();

        user.setName(request.getFirstName()+request.getLastName());

        user.setUsername(request.getUserName());

        user.setEmail(request.getEmail());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setStatus(UserStatus.active);

        user.setCreatedAt(OffsetDateTime.now());

        user.setUpdatedAt(OffsetDateTime.now());

        //Save User
        User savedUser = userRepository.save(user);

        /// saving userRole
        userRoleRepository.save(new UserRole(savedUser, role, OffsetDateTime.now()));


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

        userResponse.setActive(user.getStatus() == UserStatus.active);

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


    //to login

    @Override
    public AuthResponse login(LoginRequest request) {

        System.out.println("==================================");
        System.out.println("Login Request");
        System.out.println("Identifier = " + request.getUsernameOrEmail());
        System.out.println("Password   = " + request.getPassword());
        System.out.println("==================================");

        /*
        // authenticationManager.authenticate(
//         new UsernamePasswordAuthenticationToken(
//                 request.getUsernameOrEmail(),
//                 request.getPassword()
//         )
// );
*/

        System.out.println("Login identifier: " + request.getUsernameOrEmail());

       // authenticationManager.authenticate(
       //         new UsernamePasswordAuthenticationToken(
       //                 request.getUsernameOrEmail(),
       //                 request.getPassword()
       //         )
       // );

        System.out.println("Authentication successful");


        //load the user

        String login = request.getUsernameOrEmail();

        User user = userRepository.findByEmail(login)
                .orElseGet(() ->
                        userRepository.findByUsernameWithRoles(login)
                                .orElseThrow(() ->
                                        new UserNotFoundException("User not found")));

        System.out.println("================================");
        System.out.println("Database username : " + user.getUsername());
        System.out.println("Database email    : " + user.getEmail());
        System.out.println("Hash             : " + user.getPasswordHash());

        boolean matches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        System.out.println("Password matches = " + matches);
        System.out.println("================================");



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

        userResponse.setActive(user.getStatus() == UserStatus.active);

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

//to refresh token
    @Override
    public RefreshTokenResponse refreshToken(
            RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        System.out.println("================================");
        System.out.println("Refresh Token Received:");
        System.out.println(refreshToken);
        System.out.println("================================");

        String username =
                jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        UserDetails userDetails =
                new CustomUserDetails(user);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        String newAccessToken =
                jwtService.generateAccessToken(user.getUsername());

        RefreshTokenResponse response =
                new RefreshTokenResponse();

        response.setAccessToken(newAccessToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(900);

        return response;
    }

    //logout

    @Override
    public void logout(String header) {

        // Remove "Bearer "
        String token = header.substring(7);

        // Get token expiry
        Date expiry = jwtService.extractExpiration(token);

        // Calculate remaining validity
        long remainingTime =
                expiry.getTime() - System.currentTimeMillis();

        // Store only if token is still valid
        if (remainingTime > 0) {

            redisService.blacklistToken(
                    token,
                    remainingTime
            );
        }
    }

    //swagger implementation

    @Override
    public TokenValidateResponse validateToken(
            TokenValidateRequest request) {

        String token = request.getAccessToken();

        TokenValidateResponse response =
                new TokenValidateResponse();

        // Step 1 : Check Redis blacklist
        if (redisService.isBlacklisted(token)) {

            response.setValid(false);

            return response;
        }

        try {

            // Step 2 : Extract username
            String username =
                    jwtService.extractUsername(token);

            // Step 3 : Find user
            User user =
                    userRepository.findByUsernameWithRoles(username)
                            .orElseThrow(() ->
                                    new UserNotFoundException("User not found"));

            // Step 4 : Validate JWT
            UserDetails userDetails =
                    new CustomUserDetails(user);

            if (!jwtService.isTokenValid(token, userDetails)) {

                response.setValid(false);

                return response;
            }

            // Step 5 : Read role
            Role role =
                    user.getUserRoles()
                            .stream()
                            .findFirst()
                            .orElseThrow()
                            .getRole();

            response.setValid(true);

            response.setUserId(user.getUserId());

            response.setRole(role.getRoleName());

            response.setPermissions(List.of());

            return response;

        }
        catch (Exception e) {

            response.setValid(false);

            return response;

        }

    }

}