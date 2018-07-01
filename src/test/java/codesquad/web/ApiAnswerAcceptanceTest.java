package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import codesquad.domain.Answer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    public void create_login() {
        String contents = "testContents";

        String location = createResource("/api/questions/1/answers", contents, basicAuthTemplate());
        log.debug("contents: {}" , contents);

        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        assertThat(dbAnswer.getContents(), is(contents));
    }

    @Test
    public void create_no_login(){
        String contents = "testContents2";
        ResponseEntity<String> response = template().postForEntity("/api/questions/1/answers", contents, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_login() {
        String contents = "test answer";
        String location = createResource("/api/questions/1/answers", contents, basicAuthTemplate());
        log.debug("locationn: {}", location);

        basicAuthTemplate().delete(location);
        log.debug("location2: {}", location);
        assertNull(getResource(location, Answer.class, defaultUser()));
    }

    @Test
    public void delete_no_login() {
        String contents = "test answer";
        String location = createResource("/api/questions/1/answers", contents, basicAuthTemplate());

        template().delete(location);

        assertNotNull(getResource(location, Answer.class, defaultUser()));
    }

}
