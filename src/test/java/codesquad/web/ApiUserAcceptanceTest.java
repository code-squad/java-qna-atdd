package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.UserDto;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final String USER_URL = "/api/users";

    @Test
    public void create() {
        UserDto newUser = createUserDto("testuser1");
        ResponseEntity<String> response = template().postForEntity(USER_URL, newUser, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = getResponseLocation(response);

        UserDto dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, UserDto.class);
        assertThat(dbUser, is(newUser));
    }

    @Test
    public void show_다른_사람() {
        UserDto newUser = createUserDto("testuser2");
        ResponseEntity<String> response = template().postForEntity(USER_URL, newUser, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = getResponseLocation(response);

        response = basicAuthTemplate(defaultUser()).getForEntity(location, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() {
        UserDto newUser = createUserDto("testuser3");
        ResponseEntity<String> response = template().postForEntity(USER_URL, newUser, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = getResponseLocation(response);

        User loginUser = findByUserId(newUser.getUserId());
        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(loginUser).put(location, updateUser);

        UserDto dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, UserDto.class);
        assertThat(dbUser, is(updateUser));
    }

    @Test
    public void update_다른_사람() {
        UserDto newUser = createUserDto("testuser4");
        ResponseEntity<String> response = template().postForEntity(USER_URL, newUser, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = Objects.requireNonNull(response.getHeaders().getLocation()).getPath();

        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(defaultUser()).put(location, updateUser);

        UserDto dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, UserDto.class);
        assertThat(dbUser, is(newUser));
    }
}
