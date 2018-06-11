package codesquad.web;

import codesquad.domain.Answer;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    private String path;

    private AnswerDto validAnswer() {
        return new AnswerDto("answer contents");
    }

    private AnswerDto updateAnswer() {
        return new AnswerDto("update answer contents");
    }

    private AnswerDto invalidAnswer() {
        return new AnswerDto("");
    }

    @Before
    public void setUp() throws Exception {
        path = createResource(basicAuthTemplate(), "/api/questions", new QuestionDto("test", "test")) + "/answers";
        log.debug("answer path : {}", path);
    }

    @Test
    public void create() {
        ResponseEntity<String> response = requestPost(basicAuthTemplate(), path, validAnswer());
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void create_fail_unAuthentication() {
        ResponseEntity<String> response = requestPost(template(), path, validAnswer());
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void create_fail_invalid_answer() {
        ResponseEntity<String> response = requestPost(basicAuthTemplate(), path, invalidAnswer());
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void update() {
        String answerPath = createResource(basicAuthTemplate(), path, validAnswer());

        Answer updateAnswer = updateAnswer().toAnswer();
        basicAuthTemplate().put(answerPath, updateAnswer());

        Answer addedAnswer = requestGetForRest(basicAuthTemplate(), answerPath, Answer.class);
        assertEquals(updateAnswer.getContents(), addedAnswer.getContents());
    }

    @Test
    public void update_fail_unAuthentication() {
        String answerPath = createResource(basicAuthTemplate(), path, validAnswer());

        Answer updateAnswer = updateAnswer().toAnswer();
        template().put(answerPath, updateAnswer());

        Answer addedAnswer = requestGetForRest(basicAuthTemplate(), answerPath, Answer.class);
        assertNotEquals(updateAnswer.getContents(), addedAnswer.getContents());
    }

    @Test
    public void update_fail_unAuthorized() {
        String answerPath = createResource(basicAuthTemplate(), path, validAnswer());

        Answer updateAnswer = updateAnswer().toAnswer();
        basicAuthTemplate(findByUserId("sanjigi")).put(answerPath, updateAnswer());

        Answer addedAnswer = requestGetForRest(basicAuthTemplate(), answerPath, Answer.class);
        assertNotEquals(updateAnswer.getContents(), addedAnswer.getContents());
    }

    @Test
    public void delete() {
        String answerPath = createResource(basicAuthTemplate(), path, validAnswer());
        basicAuthTemplate().delete(answerPath, updateAnswer());

        ResponseEntity<Answer> response = requestGet(basicAuthTemplate(), answerPath, Answer.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void delete_fail_unAuthentication() {
        String answerPath = createResource(basicAuthTemplate(), path, validAnswer());
        template().delete(answerPath, updateAnswer());

        ResponseEntity<Answer> response = requestGet(basicAuthTemplate(), answerPath, Answer.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void delete_fail_unAuthorized() {
        String answerPath = createResource(basicAuthTemplate(), path, validAnswer());
        basicAuthTemplate(findByUserId("sanjigi")).delete(answerPath, updateAnswer());

        ResponseEntity<Answer> response = requestGet(basicAuthTemplate(), answerPath, Answer.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
