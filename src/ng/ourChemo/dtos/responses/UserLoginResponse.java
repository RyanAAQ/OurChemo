package ng.ourChemo.dtos.responses;

import lombok.Data;

@Data
public class UserLoginResponse {
    private String username;
    private boolean isLoggedIn;
    private String message;
}
