package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final String API_QUESTION_URI = "/api/questions";
    private static final String API_ANSWER_PATH = "/answers";

    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Test
    public void create() {
        QuestionDto newQuestion = createQuestionDto();
        final String location = createResource(API_QUESTION_URI, newQuestion, defaultUser());

        final QuestionDto created = getResource(location, QuestionDto.class);
        assertThat(created.getTitle(), is(newQuestion.getTitle()));
        assertThat(created.getContents(), is(newQuestion.getContents()));
    }

    @Test
    public void create_no_login() {
        QuestionDto newQuestion = createQuestionDto();

        final ResponseEntity<String> response = template().postForEntity(API_QUESTION_URI, newQuestion, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private QuestionDto createQuestionDto() {
        return createQuestionDto(0, "질문제목", "질문본문");
    }

    private QuestionDto createQuestionDto(long id, String title, String contents) {
        return new QuestionDto(id, title, contents);
    }

    @Test
    public void update() {
        QuestionDto newQuestion = createQuestionDto();
        String location = createResource(API_QUESTION_URI, newQuestion, defaultUser());
        final QuestionDto created = getResource(location, QuestionDto.class);
        QuestionDto updateQuestion = createQuestionDto(created.getId(), "수정질문", "수정답변");

        basicAuthTemplate(defaultUser()).put(location, updateQuestion);

        final QuestionDto updated = getResource(location, QuestionDto.class);

        assertThat(updated, is(updateQuestion));
    }

    @Test
    public void update_다른_사람() {
        User writer = findByUserId(SECOND_LOGIN_USER);
        QuestionDto newQuestion = createQuestionDto();
        String location = createResource(API_QUESTION_URI, newQuestion, writer);
        final QuestionDto created = getResource(location, QuestionDto.class);
        QuestionDto updateQuestion = createQuestionDto(created.getId(), "수정질문", "수정답변");

        final ResponseEntity<Void> updateResponse = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, new HttpEntity<>(newQuestion), Void.class);
        assertThat(updateResponse.getStatusCode(), is(HttpStatus.FORBIDDEN));

        final QuestionDto updated = getResource(location, QuestionDto.class);
        assertThat(updated, not(updateQuestion));
        assertThat(updated, is(created));
    }

    @Test
    public void delete() {
        QuestionDto newQuestion = createQuestionDto();
        String location = createResource(API_QUESTION_URI, newQuestion, defaultUser());
        final QuestionDto created = getResource(location, QuestionDto.class);

        basicAuthTemplate(defaultUser()).delete(location);

        final QuestionDto deleted = getResource(location, QuestionDto.class);

        assertThat(created, notNullValue());
        assertThat(deleted, nullValue());
    }

    @Test
    public void delete_other_user() {
        User writer = findByUserId(SECOND_LOGIN_USER);
        QuestionDto newQuestion = createQuestionDto();
        String location = createResource(API_QUESTION_URI, newQuestion, writer);
        final QuestionDto created = getResource(location, QuestionDto.class);

        final ResponseEntity<Void> deleteResponse = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);
        assertThat(deleteResponse.getStatusCode(), is(HttpStatus.FORBIDDEN));

        final QuestionDto deleted = getResource(location, QuestionDto.class);
        assertThat(deleted, is(created));
    }

    @Test
    public void delete_has_answer() {
        User writer = findByUserId(SECOND_LOGIN_USER);
        QuestionDto newQuestion = createQuestionDto();
        final String questionLocation = createResource(API_QUESTION_URI, newQuestion, writer);

        AnswerDto newAnswer = new AnswerDto("답변내용1");
        createResource(questionLocation + API_ANSWER_PATH, newAnswer, writer);

        final ResponseEntity<Void> deleteResponse = basicAuthTemplate(writer).exchange(questionLocation, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        assertThat(deleteResponse.getStatusCode(), is(HttpStatus.NO_CONTENT));

        QuestionDto deleted = getResource(questionLocation, QuestionDto.class);

        assertThat(deleted, nullValue());
    }

    @Test
    public void delete_has_answer_by_other_user() {
        User writer = findByUserId(SECOND_LOGIN_USER);
        QuestionDto newQuestion = createQuestionDto();
        final String questionLocation = createResource(API_QUESTION_URI, newQuestion, writer);

        User other = defaultUser();
        AnswerDto newAnswer = new AnswerDto("답변내용1");
        createResource(questionLocation + API_ANSWER_PATH, newAnswer, other);

        final ResponseEntity<Void> deleteResponse = basicAuthTemplate(writer).exchange(questionLocation, HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

        assertThat(deleteResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        QuestionDto notDeleted = getResource(questionLocation, QuestionDto.class);

        assertThat(notDeleted, notNullValue());
    }

    @Test
    public void list() {
        QuestionsDto beforeCreate = getResource(API_QUESTION_URI, QuestionsDto.class);

        QuestionDto newQuestion = createQuestionDto();
        createResource(API_QUESTION_URI, newQuestion, defaultUser());

        QuestionsDto afterCreate = getResource(API_QUESTION_URI, QuestionsDto.class);

        assertThat(afterCreate.getSize(), is(beforeCreate.getSize() + 1));
    }
}