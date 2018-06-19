package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    private static final User LEARNER = new User(1L, "learner", "9229", "TAEWON", "htw@gmail.com");

    private String getUserId(QuestionDto questionDto, User writer) {
        Question question = questionDto.toQuestion();
        question.writeBy(writer);
        return question.getWriter().getUserId();
    }

    private long getQuestionId(String location) {
        return Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
    }

    @Test
    public void create() {
        QuestionDto newQuestion = new QuestionDto("API 질문 제목", "API 질문 내용");

        String location = createResource("/api/questions", newQuestion, LEARNER);

        long questionId = getQuestionId(location);
        newQuestion.setId(questionId);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, findByUserId(getUserId(newQuestion, LEARNER)));
        assertThat(dbQuestion, is(newQuestion));
    }

    @Test
    public void show_not_login() {
        long questionId = 2;
        String location = String.format("/api/questions/%d", questionId);
        ResponseEntity<String> response = template().getForEntity(location, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        QuestionDto questionDto = new QuestionDto(2L, "runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?", "설계를 희한하게 하는 바람에 꼬인 문제같긴 합니다만. 여쭙습니다. 상황은 mybatis select 실행될 시에 return object 의 getter 가 호출되면서인데요. getter 안에 다른 property 에 의존중인 코드가 삽입되어 있어서, 만약 다른 mybatis select 구문에 해당 property 가 없다면 exception 이 발생하게 됩니다.");
        QuestionDto dbQuestionDto = getResource(location, QuestionDto.class);
        assertThat(questionDto.equals(dbQuestionDto), is(true));
    }

    @Test
    public void update_owner_login() {
        QuestionDto newQuestion = new QuestionDto("Java8 Lambda에서 Exception 처리 방법은?", "Exception 로직을 만들어요.");

        String location = createResource("/api/questions", newQuestion, LEARNER);

        long questionId = getQuestionId(location);
        QuestionDto updateQuestion = new QuestionDto(questionId, "Java100 Lambda Excption 처리는?", "Java 100은 예외가 없어요.");
        basicAuthTemplate(LEARNER).put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(updateQuestion, is(dbQuestion));
    }

    @Test
    public void upate_not_owner_login() {
        QuestionDto newQuestion = new QuestionDto("Kotlin에서 Exception 처리 방법은?", "Kotlin을 안 배워서 몰라요:(");

        String location = createResource("/api/questions", newQuestion, LEARNER);

        long questionId = getQuestionId(location);
        QuestionDto updateQuestion = new QuestionDto(questionId, "Java100 Lambda Excption 처리는?", "Java 100은 예외가 없어요.");
        basicAuthTemplate(defaultUser()).put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(updateQuestion.equals(dbQuestion), is(false));
    }

    @Test
    public void delete() {
        QuestionDto newQuestion = new QuestionDto("Spring에서 Exception 처리 방법은?", "ExceptionHandler를 이용하면 돼!");

        String location = createResource("/api/questions", newQuestion, LEARNER);
        basicAuthTemplate(LEARNER).delete(location);

        ResponseEntity<String> response = basicAuthTemplate(LEARNER).getForEntity(location, String.class);
        assertThat(response.getBody().contains("deleted question"), is(true));
    }
}
