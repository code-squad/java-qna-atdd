package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import codesquad.domain.User;
import codesquad.dto.UserDto;
import support.test.AcceptanceTest;

public class ApiUserAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() {
        UserDto newUser = createUserDto("testUser1");
        String location = createUser(newUser);
        
        UserDto dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, UserDto.class);
        assertThat(dbUser, is(newUser));
    }

    private String createUser(UserDto newUser) {
        return createResource("/api/users", newUser);
    }

    @Test
    public void show_Another_User() {
        UserDto newUser = createUserDto("testUser2");
        String location = createUser(newUser);

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity(location, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private UserDto createUserDto(String userId) {
        return new UserDto(userId, "password", "name", "javajigi@slipp.net");
    }

    @Test
    public void update() {
        UserDto newUser = createUserDto("testUser3");
        String location = createUser(newUser);
        
        User loginUser = findByUserId(newUser.getUserId());
        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(loginUser).put(location, updateUser);
        
        UserDto dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, UserDto.class);
        assertThat(dbUser, is(updateUser));
    }
    
    @Test
    public void update_Another_User() {
        UserDto newUser = createUserDto("testUser4");
        String location = createUser(newUser);
        
        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(defaultUser()).put(location, updateUser);
        
        UserDto dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, UserDto.class);
        assertThat(dbUser, is(newUser));
    }
}
