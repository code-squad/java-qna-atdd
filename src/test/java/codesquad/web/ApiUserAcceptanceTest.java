package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @Before
    public void setUp() throws Exception {
        super.setDefaultRequestUrl("/api/users");
    }

    @Test
    public void create() throws Exception {
        UserDto newUser = createUserDto("testuser1");
        String location = createResource(newUser);
        logger.debug(location);

        User loginUser = findByUserId(newUser.getUserId());
        UserDto dbUser = getResource(location, basicAuthTemplate(loginUser), UserDto.class);
        assertThat(dbUser, is(newUser));
    }

    @Test
    public void show_다른_사람() throws Exception {
        UserDto newUser = createUserDto("testuser2");
        String location = createResource(newUser);

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity(location, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private UserDto createUserDto(String userId) {
        return new UserDto(userId, "password", "name", "javajigi@slipp.net");
    }

    @Test
    public void update() throws Exception {
        UserDto newUser = createUserDto("testuser3");
        String location = createResource(newUser);

        User loginUser = findByUserId(newUser.getUserId());
        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(loginUser).put(location, updateUser);

        UserDto dbUser = getResource(location, basicAuthTemplate(loginUser), UserDto.class);
        assertThat(dbUser, is(updateUser));
    }

    @Test
    public void update_다른_사람() throws Exception {
        UserDto newUser = createUserDto("testuser4");
        String location = createResource(newUser);

        UserDto updateUser = new UserDto(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");
        basicAuthTemplate(defaultUser()).put(location, updateUser);

        User loginUser = findByUserId(newUser.getUserId());
        UserDto dbUser = getResource(location, basicAuthTemplate(loginUser), UserDto.class);
        assertThat(dbUser, is(newUser));
    }
}
