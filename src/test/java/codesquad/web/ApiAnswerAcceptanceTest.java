package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final String ANSWERS_URL = "/answers";
    private String location;

    @Before
    public void init() {
        QuestionDto newQuestion = makeTestQuestionDto();
        location = createResource("/api/qna", newQuestion);
    }

    @Test
    public void create() {
        AnswerDto newAnswer = new AnswerDto("comment");
        String answerLocation = createResource(location + ANSWERS_URL, newAnswer);

        AnswerDto dbAnswer = getResoure(answerLocation, AnswerDto.class, defaultUser());
        assertThat(dbAnswer, is(newAnswer));
    }

    @Test
    public void create_guest() {
        AnswerDto newAnswer = new AnswerDto("comment");
        ResponseEntity<String> response = basicAuthTemplate(User.GUEST_USER)
                .postForEntity(location + ANSWERS_URL, newAnswer, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete() {
        AnswerDto newAnswer = new AnswerDto("comment");
        String answerLocation = createResource(location + ANSWERS_URL, newAnswer);

        HttpEntity httpEntity = makeHttpEntity();

        ResponseEntity<String> response = basicAuthTemplate()
                .exchange(answerLocation, HttpMethod.DELETE, httpEntity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void delete_guest() {
        AnswerDto newAnswer = new AnswerDto("comment");
        String answerLocation = createResource(location + ANSWERS_URL, newAnswer);

        HttpEntity httpEntity = makeHttpEntity();

        ResponseEntity<String> response = basicAuthTemplate(User.GUEST_USER)
                .exchange(answerLocation, HttpMethod.DELETE, httpEntity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_another() {
        AnswerDto newAnswer = new AnswerDto("comment");
        String answerLocation = createResource(location + ANSWERS_URL, newAnswer);

        HttpEntity httpEntity = makeHttpEntity();
        User anotherUser = makeAnotherTestUser();

        ResponseEntity<String> response = basicAuthTemplate(anotherUser)
                .exchange(answerLocation, HttpMethod.DELETE, httpEntity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }
}
