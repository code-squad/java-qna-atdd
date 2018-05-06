package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.UserDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiUserAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    private UserDto createUserDto(String userId) {
        return new UserDto(userId, "password", "name", "javajigi@slipp.net");
    }

    @Test
    public void create() throws Exception {
        UserDto newUser = createUserDto("testuser1");

        String location = createResourceByGuest("/api/users", newUser);
        UserDto dbUser = getResource(location, UserDto.class, findByUserId(newUser.getUserId()));

        assertThat(dbUser, is(newUser));
    }

    @Test
    public void show_다른_사람() throws Exception {
        UserDto newUser = createUserDto("testuser2");

        String location = createResourceByGuest("/api/users", newUser);
        ResponseEntity<String> response = getResource(location, defaultUser());

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }


    @Test
    public void update() throws Exception {
        UserDto newUser = createUserDto("testuser3");

        String location = createResourceByGuest("/api/users", newUser);

        User loginUser = findByUserId(newUser.getUserId());
        UserDto updatedUser = new UserDto(loginUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(loginUser).put(location, updatedUser);

        UserDto dbUser = getResource(location, UserDto.class, findByUserId(loginUser.getUserId()));
        assertThat(dbUser, is(updatedUser));
    }


    @Test
    public void update_다른_사람() throws Exception {
        UserDto newUser = createUserDto("testuser4");

        String location = createResourceByGuest("/api/users", newUser);

        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(defaultUser()).put(location, updateUser);

        UserDto dbUser = getResource(location, UserDto.class, findByUserId(newUser.getUserId()));
        assertThat(dbUser, is(newUser));
    }
}