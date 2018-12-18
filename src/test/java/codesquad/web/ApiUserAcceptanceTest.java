package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.newUser;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @Test
    public void create() throws Exception {
        User newUser = newUser("testuser1");
        String location = createResource("/api/users", newUser);
        User dbUser = template().getForObject(location, User.class, findByUserId(newUser.getUserId()));
        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        User newUser = newUser("testuser2");
        String location = createResource("/api/users", newUser);
        ResponseEntity<Void> response = basicAuthTemplate().getForEntity(location, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        User newUser = newUser("testuser3");
        String location = createResource("/api/users", newUser);
        User original = basicAuthTemplate(newUser).getForObject(location, User.class);

        User updateUser = new User(original.getId(), original.getUserId(), original.getPassword(), "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<User> responseEntity =
                basicAuthTemplate(newUser).exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), User.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        User newUser = newUser("testuser4");
        String location = createResource("/api/users", newUser);  // location : /api/users/3
        User original = basicAuthTemplate(newUser).getForObject(location, User.class);

        User updateUser = new User(original.getId(), original.getUserId(), original.getPassword(), "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<String> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_다른_사람() throws Exception {
        User newUser = newUser("testuser5");
        String location = createResource("/api/users", newUser);

        User updateUser = new User(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
