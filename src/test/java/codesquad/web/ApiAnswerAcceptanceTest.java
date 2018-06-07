package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest{

    private ResponseEntity<String> response;

    private static final String BASE_URL = "/api/questions/1/answers";

    @Test
    public void create_success() {
        String contents = "this is answer";
        response = basicAuthTemplate().postForEntity(BASE_URL, contents, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();
        Answer dbAnswer = getResource(location, Answer.class, defaultUser());
        assertThat(contents, is(dbAnswer.getContents()));
    }

    @Test
    public void create_fail_no_login() {
        String contents = "this is answer";
        response = template().postForEntity(BASE_URL, contents, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void show_success() {
        String contents = "this is answer";
        String location = createResource(BASE_URL, contents);
        Answer dbAnswer = template().getForObject(location, Answer.class);
        assertThat(dbAnswer.getContents(), is(contents));
    }

    @Test
    public void update_success() {
        String contents = "this is answer";
        String location = createResource(BASE_URL, contents);
        String updateContents = "hello";
        basicAuthTemplate().put(location, updateContents);
        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        assertThat(dbAnswer.getContents(), is(updateContents));
    }

    @Test
    public void update_fail_other_user() {
        String contents = "this is answer";
        String location = createResource(BASE_URL, contents);
        User sanjigi = findByUserId("sanjigi");
        String updateContents = "hello";
        basicAuthTemplate(sanjigi).put(location, updateContents);
        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        assertThat(dbAnswer.getContents(), is(contents));
    }

    @Test
    public void update_fail_no_login() {
        String contents = "this is answer";
        String location = createResource(BASE_URL, contents);
        String updateContents = "hello";
        template().put(location, updateContents);
        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        assertThat(dbAnswer.getContents(), is(contents));
    }

    @Test
    public void delete_success() {
        String contents = "this is answer";
        String location = createResource(BASE_URL, contents);
        basicAuthTemplate().delete(location);
        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        assertTrue(dbAnswer.isDeleted());
    }

    @Test
    public void delete_fail_other_user() {
        String contents = "this is answer";
        String location = createResource(BASE_URL, contents);
        User sanjigi = findByUserId("sanjigi");
        basicAuthTemplate(sanjigi).delete(location);
        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        assertFalse(dbAnswer.isDeleted());
    }

    @Test
    public void delete_fail_no_login() {
        String contents = "this is answer";
        String location = createResource(BASE_URL, contents);
        template().delete(location);
        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        assertFalse(dbAnswer.isDeleted());
    }
}
