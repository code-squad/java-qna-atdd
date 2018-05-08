package codesquad.web;

import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final String API_QUESTION_URI = "/api/questions";
    private static final String API_ANSWER_PATH = "/answers";

    @Test
    public void addAnswer() {
        QuestionDto newQuestion = new QuestionDto("질문제목", "질문본문");
        final String questionLocation = createResource(API_QUESTION_URI, newQuestion, defaultUser());

        AnswerDto newAnswer = new AnswerDto("답변내용1");
        final String location = createResource(questionLocation + API_ANSWER_PATH, newAnswer, defaultUser());

        final AnswerDto created = getResource(location, AnswerDto.class);
        newAnswer.setId(created.getId());
        newAnswer.setWriter(defaultUser().toUserDto());
        assertThat(created, is(newAnswer));
    }

    @Test
    public void deleteAnswer() {
        QuestionDto newQuestion = new QuestionDto("질문제목", "질문본문");
        final String questionLocation = createResource(API_QUESTION_URI, newQuestion, defaultUser());

        AnswerDto newAnswer = new AnswerDto("답변내용1");
        final String location = createResource(questionLocation + API_ANSWER_PATH, newAnswer, defaultUser());

        final ResponseEntity<Void> deleteResponse = basicAuthTemplate().exchange(location, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        assertThat(deleteResponse.getStatusCode(), is(HttpStatus.NO_CONTENT));

        final AnswerDto created = getResource(location, AnswerDto.class);

        assertThat(created, nullValue());
    }

    @Test
    public void deleteAnswer_other_user() {
        QuestionDto newQuestion = new QuestionDto("질문제목", "질문본문");
        final String questionLocation = createResource(API_QUESTION_URI, newQuestion, defaultUser());

        AnswerDto newAnswer = new AnswerDto("답변내용1");
        final String location = createResource(questionLocation + API_ANSWER_PATH, newAnswer, defaultUser());

        final ResponseEntity<Void> deleteResponse = basicAuthTemplate(findByUserId(SECOND_LOGIN_USER)).exchange(location, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        assertThat(deleteResponse.getStatusCode(), is(HttpStatus.FORBIDDEN));

        final AnswerDto created = getResource(location, AnswerDto.class);

        assertThat(created, notNullValue());
    }
}
