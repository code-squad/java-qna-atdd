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
        ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();        //질문 만듦

        User dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, User.class);
        softly.assertThat(dbUser).isNotNull();
    }

    @Test
    public void show_다른_사람() throws Exception {
        User newUser = newUser("testUser2");
        ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        ResponseEntity<Void> showUser = basicAuthTemplate(findByUserId("testUser2")).getForEntity(location, Void.class);
        softly.assertThat(showUser.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update() throws Exception {
        User newUser = newUser("testuser3");        //user생성할때 id를 0으로 줘도 userRepository에 들어갈땐 id가 3으로 들어간다.
        ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        User original = basicAuthTemplate(newUser).getForObject(location, User.class);
        User updateUser = new User(9L, original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");
        log.debug("original : {}", original);

        ResponseEntity<User> responseEntity =
                basicAuthTemplate(newUser).exchange(location , HttpMethod.PUT, createHttpEntity(updateUser), User.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().getName()).isEqualTo("javajigi2");
    }

    @Test
    public void update_no_login() throws Exception {
        User newUser = newUser("testuser4");
        ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        User original = basicAuthTemplate(newUser).getForObject(location, User.class);

        User updateUser = new User(original.getId(), original.getUserId(), original.getPassword(),
                        "javajigi2", "javajigi2@slipp.net");

        ResponseEntity<String> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }


    @Test
    public void update_다른_사람() throws Exception {
        User newUser = newUser("testuser5");
        ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();

        User updateUser = new User(newUser.getId(), newUser.getUserId(), "password2", "name2", "javajigi@slipp.net2");

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


}
