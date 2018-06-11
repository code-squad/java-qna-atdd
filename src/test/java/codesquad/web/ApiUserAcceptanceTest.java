package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import codesquad.domain.User;
import codesquad.dto.UserDto;
import support.test.AcceptanceTest;

import java.util.Arrays;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    private static String PATH_CREATE = "/api/users";

    @Test
    public void create() throws Exception {
        UserDto newUser = createUserDto("testuser1");
        ResponseEntity<String> response = requestPost(template(), PATH_CREATE, newUser);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        UserDto dbUser = requestGetForRest(basicAuthTemplate(findByUserId(newUser.getUserId())), response.getHeaders().getLocation().getPath(), UserDto.class);
        assertThat(dbUser, is(newUser));
    }

    @Test
    public void create_invalid_request_body_min() throws Exception {
        UserDto newUser = createUserDto(createUserName(2));
        ResponseEntity<String> response = requestPost(template(), PATH_CREATE, newUser);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void create_invalid_request_body_max() throws Exception {
        UserDto newUser = createUserDto(createUserName(21));
        ResponseEntity<String> response = requestPost(template(), PATH_CREATE, newUser);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void show() throws Exception {
        UserDto newUser = createUserDto("colin");
        String location = createResource(template(), PATH_CREATE, newUser);

        ResponseEntity<String> response = requestGet(basicAuthTemplate(newUser.toUser()), location, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("user info : {}", response.getBody());
    }

    @Test
    public void show_다른_사람() throws Exception {
        UserDto newUser = createUserDto("testuser2");
        String location = createResource(template(), PATH_CREATE, newUser);

        ResponseEntity<String> response = requestGet(basicAuthTemplate(defaultUser()), location, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() throws Exception {
        UserDto newUser = createUserDto("testuser3");
        String location = createResource(template(), PATH_CREATE, newUser);
        
        User loginUser = findByUserId(newUser.getUserId());
        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(loginUser).put(location, updateUser);

        UserDto dbUser = requestGetForRest(basicAuthTemplate(findByUserId(newUser.getUserId())), location, UserDto.class);
        assertThat(dbUser, is(updateUser));
    }
    
    @Test
    public void update_다른_사람() throws Exception {
        UserDto newUser = createUserDto("testuser4");
        String location = createResource(template(), PATH_CREATE, newUser);

        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(defaultUser()).put(location, updateUser);

        UserDto dbUser = requestGetForRest(basicAuthTemplate(findByUserId(newUser.getUserId())), location, UserDto.class);
        assertThat(dbUser, is(newUser));
    }

    private String createUserName(int length) {
        char[] name = new char[length];
        Arrays.fill(name, '*');
        return String.valueOf(name);
    }

    private UserDto createUserDto(String userId) {
        return new UserDto(userId, "password", "name", "javajigi@slipp.net");
    }
}
