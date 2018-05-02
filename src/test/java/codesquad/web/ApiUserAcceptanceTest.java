package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import codesquad.domain.User;
import codesquad.dto.UserDto;
import support.test.AcceptanceTest;

public class ApiUserAcceptanceTest extends AcceptanceTest {

    private UserDto newUserDto(String userId) {
        return new UserDto(userId, "password", "name", "javajigi@slipp.net");
    }

    private UserDto newUserDto(String userId, String name) {
        return new UserDto(userId, "password", name, "javajigi@slipp.net");
    }

    @Test
    public void create() throws Exception {
        UserDto newUser = newUserDto("testuser1");
        String location = createResource("/api/users", newUser);

        assertThat(getResource(location, UserDto.class, findByUserId(newUser.getUserId())), is(newUser));
    }
    
    @Test
    public void show_다른_사람() throws Exception {
        UserDto newUser = newUserDto("testuser2");
        String location = createResource("/api/users", newUser);

        assertThat(getResource(location, defaultUser()).getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() throws Exception {
        UserDto newUser = newUserDto("testuser3");
        String location = createResource("/api/users", newUser);
        
        User loginUser = findByUserId(newUser.getUserId());
        UserDto updateUser = newUserDto(newUser.getUserId(), "differentName");
        basicAuthTemplate(loginUser).put(location, updateUser);
        
        assertThat(getResource(location, UserDto.class, loginUser), is(updateUser));
    }
    
    @Test
    public void update_다른_사람() throws Exception {
        UserDto newUser = newUserDto("testuser4");
        String location = createResource("/api/users", newUser);

        UserDto updateUser = newUserDto(newUser.getUserId(), "differentName");
        basicAuthTemplate(defaultUser()).put(location, updateUser);
        
        assertThat(getResource(location, UserDto.class, findByUserId(newUser.getUserId())), is(newUser));
    }
}
