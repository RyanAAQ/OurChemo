package ng.ourChemo.services;

import ng.ourChemo.dtos.requests.RegisterUserRequest;
import ng.ourChemo.dtos.requests.UserLoginRequest;
import ng.ourChemo.dtos.requests.UserLogoutRequest;
import ng.ourChemo.dtos.responses.RegisterUserResponse;
import ng.ourChemo.dtos.responses.UserLoginResponse;
import ng.ourChemo.dtos.responses.UserLogoutResponse;

public interface AuthService {
    RegisterUserResponse registerChemist(RegisterUserRequest request);
    UserLoginResponse login(UserLoginRequest request);
    UserLogoutResponse logout(UserLogoutRequest request);
}
