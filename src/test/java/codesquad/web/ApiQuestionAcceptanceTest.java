package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.Objects;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final String QUESTION_URL = "/api/qna";
    private static final String ANSWERS_URL = "/answers";
    private static final String USERS_URL = "/api/users";

    private QuestionDto newQuestion;

    @Before
    public void init() {
        newQuestion = makeTestQuestionDto();
    }

    @Test
    public void create() {
        String location = createResource(QUESTION_URL, newQuestion);
        QuestionDto dbQuestion = getResoure(location, QuestionDto.class, defaultUser());
        assertTrue(dbQuestion.equalsTitleAndContent(newQuestion));
    }

    @Test
    public void create_guest() {
        ResponseEntity<String> response = basicAuthTemplate(User.GUEST_USER).postForEntity(QUESTION_URL, newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() {
        String location = createResource(QUESTION_URL, newQuestion);

        QuestionDto updateQuestion = new QuestionDto(newQuestion.getId(), "aaaaa", "sssss");
        basicAuthTemplate().put(location, updateQuestion);

        QuestionDto dbQuestion = getResoure(location, QuestionDto.class, defaultUser());
        assertTrue(dbQuestion.equalsTitleAndContent(updateQuestion));
    }

    @Test
    public void update_guest() {
        String location = createResource(QUESTION_URL, newQuestion);

        QuestionDto updateQuestion = new QuestionDto(newQuestion.getId(), "aaaaa", "sssss");
        basicAuthTemplate(User.GUEST_USER).put(location, updateQuestion);

        QuestionDto dbQuestion = getResoure(location, QuestionDto.class, defaultUser());

        // 바뀌었나 안바뀌었나 테스트
        assertTrue(dbQuestion.equalsTitleAndContent(newQuestion));
    }

    @Test
    public void delete() {
        // 질문도 default, 답변도 default, 삭제도 default

        String location = createResource(QUESTION_URL, newQuestion);

        AnswerDto newAnswer = new AnswerDto("comment");
        String answerLocation = createResource(location + ANSWERS_URL, newAnswer);
        AnswerDto dbAnswer = getResoure(answerLocation, AnswerDto.class, defaultUser());
        assertThat(dbAnswer, is(newAnswer));

        HttpEntity entity = makeHttpEntity();
        ResponseEntity<String> response = basicAuthTemplate().exchange(location,
                HttpMethod.DELETE, entity, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void delete_not_same_question_answer_writer() {
        // 질문은 default, 답변은 newUser, 삭제는 default

        String location = createResource(QUESTION_URL, newQuestion);

        UserDto newUser = createUserDto("testuser100");
        ResponseEntity<String> response = template().postForEntity(USERS_URL, newUser, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        AnswerDto newAnswer = new AnswerDto("comment");

        response = basicAuthTemplate(newUser.toUser()).postForEntity(location + ANSWERS_URL, newAnswer, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String answerLocation = Objects.requireNonNull(response.getHeaders().getLocation()).getPath();

        AnswerDto dbAnswer = getResoure(answerLocation, AnswerDto.class, newUser.toUser());
        assertThat(dbAnswer, is(newAnswer));

        HttpEntity entity = makeHttpEntity();
        response = basicAuthTemplate().exchange(location,
                HttpMethod.DELETE, entity, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_not_same_question_writer_loginUser() {
        // 질문은 default, 답변은 default, delete는 newUser
        String location = createResource(QUESTION_URL, newQuestion);

        UserDto newUser = createUserDto("testuser1000");
        ResponseEntity<String> response = template().postForEntity(USERS_URL, newUser, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        AnswerDto newAnswer = new AnswerDto("comment");

        String answerLocation = createResource(location + ANSWERS_URL, newAnswer);
        AnswerDto dbAnswer = getResoure(answerLocation, AnswerDto.class, defaultUser());
        assertThat(dbAnswer, is(newAnswer));

        HttpEntity entity = makeHttpEntity();
        response = basicAuthTemplate(newUser.toUser()).exchange(location,
                HttpMethod.DELETE, entity, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }
}
