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

public class ApiUserAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @Test
    public void create() throws Exception {
        UserDto newUser = createUserDto("testuser1");

        String location = createResource("/api/users", newUser, template());
        log.debug("path : {}", location);
        UserDto dbUser = getResource(location, UserDto.class, findByUserId(newUser.getUserId()));
        assertThat(dbUser, is(newUser));
    }

    @Test
    public void show_다른_사람() throws Exception {
        UserDto newUser = createUserDto("testuser2");

        String location = createResource("/api/users", newUser, template());

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity(location, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private UserDto createUserDto(String userId) {
        return new UserDto(userId, "password", "name", "javajigi@slipp.net");
    }

    @Test
    public void update_login() throws Exception {
        UserDto newUser = createUserDto("testuser3");

        String location = createResource("/api/users", newUser, template());

        User loginUser = findByUserId(newUser.getUserId());
        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(loginUser).put(location, updateUser);

        UserDto dbUser = getResource(location, UserDto.class, findByUserId(newUser.getUserId()));
        assertThat(dbUser, is(updateUser));
    }

    @Test
    public void update_다른_사람() throws Exception {
        UserDto newUser = createUserDto("testuser4");

        String location = createResource("/api/users", newUser, template());

        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(defaultUser()).put(location, updateUser);

        UserDto dbUser = getResource(location, UserDto.class, findByUserId(newUser.getUserId()));
        assertThat(dbUser, is(newUser));
    }
}
