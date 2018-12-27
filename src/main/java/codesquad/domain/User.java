package codesquad.domain;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import support.domain.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
public class User extends AbstractEntity {
    public static final GuestUser GUEST_USER = new GuestUser();

    @Size(min = 3, max = 20)
    @Column(unique = true, nullable = false)
    private String userId;

    @Size(min = 3, max = 20)
    @Column(nullable = false)
    private String password;

    @Size(min = 3, max = 20)
    @Column(nullable = false)
    private String name;

    @Size(max = 50)
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

    public User setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User update(User loginUser, User updateUser) {
        if (isOwn(loginUser)) {
            this.name = updateUser.name;
            this.email = updateUser.email;
        }
        return this;
    }

    public boolean isOwn(User targetUser) {
        isLogin(targetUser);
        if(!this.equals(targetUser)){
            throw new UnAuthorizedException();
        }
        return true;
    }

    public boolean isLogin(User targetUser) {
        if (targetUser == null){
            throw new UnAuthenticationException();
        }
        return true;
    }

    public boolean matchPassword(String password) {
        if (!this.password.equals(password))
            throw new UnAuthenticationException();
        return true;
    }

    @JsonIgnore
    public  boolean isGuestUser() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(getUserId(), user.getUserId()) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getId(),user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getUserId(), getPassword());
    }
}
