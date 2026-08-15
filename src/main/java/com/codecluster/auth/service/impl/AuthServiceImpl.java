package com.codecluster.auth.service.impl;

import com.codecluster.auth.dto.GenerateJwtDto;
import com.codecluster.auth.dto.TokenValidationResponse;
import com.codecluster.auth.dto.request.RegisterRequest;
import com.codecluster.auth.dto.response.AuthResponse;
import com.codecluster.auth.entity.*;
import com.codecluster.auth.enums.UserStatus;
import com.codecluster.auth.exception.*;
import com.codecluster.auth.repository.InstituteMembersRepo;
import com.codecluster.auth.repository.RoleRepository;
import com.codecluster.auth.repository.UserRepository;
import com.codecluster.auth.security.JwtService;
import com.codecluster.auth.service.AuthService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.codecluster.auth.dto.response.UserResponse;
import com.codecluster.auth.dto.request.LoginRequest;

@Service
public class AuthServiceImpl implements AuthService {

    //fields

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final InstituteMembersRepo instituteMembersRepo;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            InstituteMembersRepo instituteMembersRepo) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.instituteMembersRepo = instituteMembersRepo;
    }

    @Override
    public UserResponse register(RegisterRequest request) {

        // Check if email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        if(userRepository.existsByUsername(request.getUserName())){
            throw new UsernameAlreadyExistsException("Username '"+request.getUserName()+"' already exists");
        }


        // Find role
        Optional<Role> role = roleRepository.findByRoleName("USER");
        if(role.isEmpty()){
            throw new RoleNotFoundException("Unable to fetch role, Try again later!");
        }

        //Create the User object
        User user = new User();
        user.setName(request.getFirstName()+" "+request.getLastName());
        user.setUsername(request.getUserName());
        user.setEmail(request.getEmail());
        user.setCreatedAt(OffsetDateTime.now());
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setStatus(UserStatus.active);
        user.setUpdatedAt(OffsetDateTime.now());

        UserRole userRole = new UserRole();
        userRole.setRole(role.get());
        userRole.setUser(user);
        userRole.setAssignedAt(OffsetDateTime.now());

        user.getUserRoles().add(userRole);
        //Save User
        User savedUser = userRepository.save(user);
        if(savedUser.getUserId() == null){
            throw new RuntimeException("Unable to save User, Try again later!");
        }
        /// saving userRole
        //creating the response object
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(savedUser.getUsername());
        userResponse.setId(savedUser.getUserId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setName(savedUser.getName());
        userResponse.setRole(savedUser.getUserRoles().getFirst().getRole().getRoleName());
        userResponse.setActive(savedUser.getStatus() == UserStatus.active);
        userResponse.setCreatedAt(savedUser.getCreatedAt());

        return userResponse;


    }

    /**
     * checks username and password
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        User user = getUserByUsernameOrPassword(request.getPreferredId());
        System.out.println(user.getUsername());
        ///  check user password
        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new InvalidCredentialsException("Invalid Username Or Password");
        }
        ///  gets user Role
        Role role = user.getUserRoles().getFirst().getRole();
        //generate JWT token
        //forming dto to pass to jwtService inorder to generate token
        GenerateJwtDto jwtDto = new GenerateJwtDto();
        jwtDto.setUserId(user.getUserId().toString());
        jwtDto.setUsername(user.getUsername());
        jwtDto.setUserRole(role.getRoleName());
        Optional<InstituteMember> instituteMember = instituteMembersRepo.findByUser(user);
        if(instituteMember.isPresent()){
            jwtDto.setInstituteId(instituteMember.get().getInstitute().getId().toString());
            jwtDto.setInstituteRole(instituteMember.get().getMemberRole().toString());
        }

        String accessToken =
                jwtService.generateAccessToken(jwtDto);

        //building UserResponse

       return getAuthResponse(user, accessToken);


    }

    /** Extracts claims from jwt and returns
     * Input String Authorization : Containing JWT
     * Returns JwtClaims TokenValidationResponse Object :
     *     String userId;
     *     String username;
     *     String role;
     *     String instituteId; - may be null
     *     String instituteRole; - may be null
     */
    @Override
    public TokenValidationResponse validateToken(String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new InvalidCredentialsException("Missing or invalid Authorization header");
        }

        String token = authorization.substring(7);

        if (!jwtService.validateToken(token)) {
            throw new InvalidCredentialsException("Invalid or expired token");
        }

        GenerateJwtDto claims = jwtService.extractClaims(token);

        TokenValidationResponse response = new TokenValidationResponse();
        response.setUserId(claims.getUserId());
        response.setUsername(claims.getUsername());
        response.setRole(claims.getUserRole());
        response.setInstituteId(claims.getInstituteId());
        response.setInstituteRole(claims.getInstituteRole());

        return response;
    }

    ///  Helper method to generate AuthResponse
    private static AuthResponse getAuthResponse(User user, String accessToken) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getUserId());
        userResponse.setEmail(user.getEmail());
        userResponse.setName(user.getName());
        userResponse.setActive(user.getStatus() == UserStatus.active);
        userResponse.setRole(user.getUserRoles().getFirst().getRole().getRoleName());
        userResponse.setUsername(user.getUsername());
        userResponse.setCreatedAt(user.getCreatedAt());
        //Build AuthResponse

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(900);
        response.setUser(userResponse);
        return response;
    }

    ///  Private method to get user by username or email
    private User getUserByUsernameOrPassword(String preferredId) {
        if(preferredId.contains("@")){
            return userRepository.findUsersByEmail(preferredId).orElseThrow(()->new UserNotFoundException("Email not registered!"));
        }
        return userRepository.findUserByUsername(preferredId).orElseThrow(()->new UserNotFoundException("User Not Found!"));

    }


}