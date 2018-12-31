package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.other;
import static codesquad.domain.UserTest.user;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);
    public static final String URL = "/api/users";

    @Test
    public void create() throws Exception {
        String location = createResource(URL, user);
        User dbUser = basicAuthTemplate(findByUserId(user.getUserId())).getForObject(location, User.class);

        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        String location = createResource(URL, other);
        ResponseEntity<Void> response = basicAuthTemplate(defaultUser()).getForEntity(location, Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        String location = createResource(URL, user);
        User original = getResource(location, User.class, user);

        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<User> responseEntity =
                basicAuthTemplate(user).exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), User.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        String location = createResource(URL, user);
        User original = getResource(location, User.class, user);

        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<String> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_다른_사람() throws Exception {
        String location = createResource(URL, user);

        User updateUser = new User(user.getUserId(), "password", "name2", "javajigi@slipp.net2");

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate(otherUser()).exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
