package ng.ourChemo.dtos.responses;

import lombok.Data;

@Data
public class UserLogoutResponse {
    private String username;
    private boolean isLoggedIn;
    private String message;

}
