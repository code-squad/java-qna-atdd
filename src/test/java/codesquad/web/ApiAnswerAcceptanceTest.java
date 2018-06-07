package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.dto.AnswerDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    public void create() {
        String contents = "testContents";

        String location = createResource("/api/questions/1/answers", contents, basicAuthTemplate());
        log.debug("location : {}", location);
        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        log.debug("Answer : {}", dbAnswer);
        assertThat(dbAnswer.getContents(), is(contents));
    }

    @Test
    public void create_no_login() {
        String contents = "testContents2";
        ResponseEntity<String> response = template().postForEntity("/api/questions/1/answers", contents, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() {
        String newContents = "testContents3";
        String location = createResource("/api/questions/1/answers", newContents, basicAuthTemplate());

        String updateContents = newContents + 1;
        basicAuthTemplate().put(location, updateContents);

        Answer dbAnswer = getResource(location, Answer.class, defaultUser());
        assertThat(updateContents, is(dbAnswer.getContents()));
    }

    @Test
    public void update_다른_사용자() {
        String newContents = "testContents4";
        String location = createResource("/api/questions/1/answers", newContents, basicAuthTemplate());

        String updateContents = newContents + 1;
        basicAuthTemplate(findByUserId("riverway")).put(location, updateContents);

        Answer dbAnswer = getResource(location, Answer.class, findByUserId("riverway"));
        assertThat(newContents, is(dbAnswer.getContents()));
    }

    @Test
    public void delete_login() {
        String newContents = "testContents5";
        String location = createResource("/api/questions/1/answers", newContents, basicAuthTemplate());

        basicAuthTemplate().delete(location);

        assertNull(getResource(location, Answer.class, defaultUser()));
    }

    @Test
    public void delete_다른_사용자() {
        String newContents = "testContents5";
        String location = createResource("/api/questions/1/answers", newContents, basicAuthTemplate());

        basicAuthTemplate(findByUserId("riverway")).delete(location);

        assertNotNull(getResource(location, Answer.class, findByUserId("riverway")));
    }
}