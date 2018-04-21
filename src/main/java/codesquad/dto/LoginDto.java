package codesquad.dto;

import javax.validation.constraints.Size;

public class LoginDto {

    @Size(min = 3, max = 20)
    private String userId;

    @Size(min = 6, max = 20)
    private String password;

    public LoginDto() {
    }

    public LoginDto(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
