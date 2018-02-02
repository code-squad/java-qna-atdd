package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import support.domain.AbstractEntity;
import support.domain.ApiUrlGeneratable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;
import java.net.URI;

@Entity
public class User extends AbstractEntity implements ApiUrlGeneratable {
    public static final GuestUser GUEST_USER = new GuestUser();

    @Size(min = 3, max = 20)
    @Column(unique = true, nullable = false, length = 20)
    private String userId;

    @Size(min = 6, max = 20)
    @Column(nullable = false, length = 20)
    @JsonIgnore
    private String password;

    @Size(min = 3, max = 20)
    @Column(nullable = false, length = 20)
    private String name;

    @Email
    @Column(length = 50)
    private String email;

    public User() {
    }

    public User(String userId, String password, String name, String email) {
        this(0L, userId, password, name, email);
    }

    public User(long id, String userId, String password, String name, String email) {
        super(id);
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    private boolean matchUserId(String userId) {
        return this.userId.equals(userId);
    }

    public void update(User loginUser, User target) {
        if (!matchUserId(loginUser.getUserId())) {
            throw new UnAuthorizedException();
        }

        if (!matchPassword(target.getPassword())) {
            return;
        }

        this.name = target.name;
        this.email = target.email;
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }

    public UserDto toUserDto() {
        return new UserDto(this.userId, this.password, this.name, this.email);
    }

    @Override
    public URI generateApiUri() {
        return URI.create("/api/users/" + getId());
    }

    @JsonIgnore
    public boolean isGuestUser() {
        return false;
    }
    
    private static class GuestUser extends User {
        @Override
        public boolean isGuestUser() {
            return true;
        }
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
