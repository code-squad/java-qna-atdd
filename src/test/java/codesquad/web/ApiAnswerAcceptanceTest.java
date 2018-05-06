package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Autowired
    QuestionRepository questionRepository;

    private Question question;

    @Before
    public void init() {
        question = questionRepository.findOne(1L);
    }

    /**
     * input : 댓글 내용
     * output : 댓글 Resource URI
     */
    @Test
    public void 댓글추가_로그인사용자() {
        String answer = "answer";
        String resourceLocation = createResourceByLoginUser(String.format("%s/answers", question.generateResourceURI()), answer);
        assertThat(resourceLocation.startsWith(String.format("%s/answers", question.generateResourceURI())), is(true));
    }

    /**
     * input : 수정할 댓글 내용
     * output : 수정된 댓글 Resource URI
     */
    @Test
    public void 댓글수정_자신의글() {
        String answer = "answer";
        String resourceLocation = createResourceByLoginUser(String.format("%s/answers", question.generateResourceURI()), answer);

        String updatingAnswer = "updating answer";
        ResponseEntity<String> response = basicAuthTemplate().exchange(
                resourceLocation, HttpMethod.PUT, new HttpEntity<>(updatingAnswer), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    /**
     * input : 수정할 댓글 내용
     * output : HTTP 403 Error(Forbidden)
     */
    @Test
    public void 댓글수정_타인의글() {
        String answer = "answer";
        String resourceLocation = createResourceByLoginUser(String.format("%s/answers", question.generateResourceURI()), answer);

        String updatingAnswer = "updating answer";
        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi")).exchange(
                resourceLocation, HttpMethod.PUT, new HttpEntity<>(updatingAnswer), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    /**
     * input : void
     * output : HTTP 204 (no content)
     */
    @Test
    public void 댓글삭제_자신의글() {
        String answer = "answer";
        String resourceLocation = createResourceByLoginUser(String.format("%s/answers", question.generateResourceURI()), answer);

        ResponseEntity<String> response = basicAuthTemplate().exchange(
                resourceLocation, HttpMethod.DELETE, new HttpEntity<>(null), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));

    }

    /**
     * input : void
     * output : HTTP 403 Error(Forbidden)
     */
    @Test
    public void 댓글삭제_타인의글() {
        String answer = "answer";
        String resourceLocation = createResourceByLoginUser(String.format("%s/answers", question.generateResourceURI()), answer);

        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi")).exchange(
                resourceLocation, HttpMethod.DELETE, new HttpEntity<>(null), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }
}