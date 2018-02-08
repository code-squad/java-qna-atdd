package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class QuestionApiAcceptanceTest extends AcceptanceTest{
    private User loginUser;

    @Before
    public void setUp() {
        loginUser = defaultUser();
    }

    private QuestionDto createQuestionDto() {
        return new QuestionDto("새로운 질문입니다.", "새로운 질문 내용입니다.");
    }

    @Test
    public void create() {
        QuestionDto questionDto = createQuestionDto();

        String location = createAuthResource("/api/questions", questionDto);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(dbQuestion, is(questionDto));

    }

    @Test
    public void showQuestion() {
        QuestionDto createQuestion = createQuestionDto();
        String location = createAuthResource("/api/questions", createQuestion);

        QuestionDto insertedQuestion = getResource(location, QuestionDto.class);

        assertThat(insertedQuestion, is(createQuestion));

    }

    @Test
    public void update() {
        QuestionDto newQuestion = createQuestionDto();
        String location = createAuthResource("/api/questions", newQuestion);

        QuestionDto insertedQuestion = getResource(location, QuestionDto.class);

        QuestionDto updateQuestion = new QuestionDto(insertedQuestion.getId(), "수정된 제목", "수정된 내용");

        basicAuthTemplate(loginUser).put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);

        assertThat(dbQuestion, is(updateQuestion));
    }

    @Test
    public void update_다른사람() {
        User anotherUser = findByUserId("sanjigi");
        QuestionDto newQuestion = createQuestionDto();
        String location = createAuthResource("/api/questions", newQuestion);

        QuestionDto insertedQuestion = getResource(location, QuestionDto.class);

        QuestionDto updateQuestion = new QuestionDto(insertedQuestion.getId(), "수정된 제목", "수정된 내용");

        basicAuthTemplate(anotherUser).put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);

        assertThat(dbQuestion, is(insertedQuestion));
    }

    @Test
    public void delete() {
        QuestionDto newQuestion = createQuestionDto();
        String location = createAuthResource("/api/questions", newQuestion);

        basicAuthTemplate(loginUser).delete(location);
        QuestionDto questionDto = getResource(location, QuestionDto.class);
        assertNull(questionDto);

    }

    @Test
    public void delete_다른사람() {
        User anotherUser = findByUserId("sanjigi");
        QuestionDto newQuestion = createQuestionDto();
        String location = createAuthResource("/api/questions", newQuestion);

        basicAuthTemplate(anotherUser).delete(location);

        QuestionDto questionDto = getResource(location, QuestionDto.class);
        assertThat(questionDto, is(newQuestion));
    }
}
