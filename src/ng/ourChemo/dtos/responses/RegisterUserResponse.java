package ng.ourChemo.dtos.responses;

import lombok.Data;

@Data
public class RegisterUserResponse {
    private String username;
    private String fullName;
    private String message;

}
