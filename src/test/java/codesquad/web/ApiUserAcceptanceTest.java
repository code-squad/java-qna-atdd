package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import support.test.RestJsonDataBuilder;

import static codesquad.domain.UserTest.newUser;

public class ApiUserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);
    private static RestJsonDataBuilder restJsonDataBuilder;

    @Test
    public void create() throws Exception {
        User newUser = newUser("testuser1");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/users");
        restJsonDataBuilder.createEntity(template(), newUser, String.class);

        User dbUser = restJsonDataBuilder.getResource(basicAuthTemplate(newUser), User.class);
        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        User newUser = newUser("testuser2");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/users");

        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(template(), newUser, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        response = basicAuthTemplate(defaultUser()).getForEntity(restJsonDataBuilder.getLocation(), Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        User newUser = newUser("testuser3");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/users");
        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(template(), newUser, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User original = restJsonDataBuilder.getResource(basicAuthTemplate(newUser), User.class);

        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<User> responseEntity = restJsonDataBuilder
                .updateEntity(basicAuthTemplate(newUser), updateUser, User.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() throws Exception {
        User newUser = newUser("testuser4");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/users");
        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(template(), newUser, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User original = restJsonDataBuilder.getResource(basicAuthTemplate(newUser), User.class);

        User updateUser = new User
                (original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<String> responseEntity = restJsonDataBuilder.updateEntity(template(), updateUser, String.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_다른_사람() throws Exception {
        User newUser = newUser("testuser5");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/users");
        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(template(), newUser, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        User updateUser = new User(newUser.getUserId(), "password", "name2", "javajigi@slipp.net2");

        ResponseEntity<Void> responseEntity =
                restJsonDataBuilder.updateEntity(basicAuthTemplate(defaultUser()), updateUser, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
