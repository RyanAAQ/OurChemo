package ng.ourChemo.services;

import ng.ourChemo.data.models.User;
import ng.ourChemo.data.repositories.UserRepository;
import ng.ourChemo.dtos.requests.RegisterUserRequest;
import ng.ourChemo.dtos.requests.UserLoginRequest;
import ng.ourChemo.dtos.requests.UserLogoutRequest;
import ng.ourChemo.dtos.responses.RegisterUserResponse;
import ng.ourChemo.dtos.responses.UserLoginResponse;
import ng.ourChemo.dtos.responses.UserLogoutResponse;
import ng.ourChemo.exceptions.AuthenticationException;
import ng.ourChemo.exceptions.ValidationException;
import ng.ourChemo.utils.Mapper;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RegisterUserResponse registerChemist(RegisterUserRequest request) {
        if (request.getFullName() == null || request.getFullName().isEmpty())
            throw new ValidationException("Full name is required");
        if (request.getUsername() == null || request.getUsername().isEmpty())
            throw new ValidationException("Username is required");
        if (request.getPassword() == null || request.getPassword().isEmpty())
            throw new ValidationException("Password is required");

        String username = request.getUsername().toLowerCase();

        if (userRepository.findByUsername(username) != null)
            throw new AuthenticationException("Username " + username + " already exists");

        User user = Mapper.mapToUser(request);
        userRepository.save(user);

        RegisterUserResponse response = new RegisterUserResponse();
        response.setUsername(username);
        response.setFullName(request.getFullName());
        response.setMessage("Registered successfully");
        return response;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty())
            throw new ValidationException("Username is required");
        if (request.getPassword() == null || request.getPassword().isEmpty())
            throw new ValidationException("Password is required");

        String username = request.getUsername().toLowerCase();
        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new ValidationException("User with username " + username + " does not exist");
        if (!user.getPassword().equals(request.getPassword()))
            throw new ValidationException("Incorrect password");

        user.setLoggedIn(true);
        userRepository.save(user);

        UserLoginResponse response = new UserLoginResponse();
        response.setUsername(username);
        response.setLoggedIn(true);
        response.setMessage("Login successful");
        return response;
    }

    @Override
    public UserLogoutResponse logout(UserLogoutRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty())
            throw new ValidationException("Username is required");

        String username = request.getUsername().toLowerCase();
        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new ValidationException("User not found");

        user.setLoggedIn(false);
        userRepository.save(user);

        UserLogoutResponse response = new UserLogoutResponse();
        response.setUsername(user.getUsername());
        response.setLoggedIn(false);
        response.setMessage("Logout successful");
        return response;
    }
}
