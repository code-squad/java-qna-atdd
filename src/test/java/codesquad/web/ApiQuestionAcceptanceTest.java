package codesquad.web;

import codesquad.dto.QuestionDto;
import org.junit.Test;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private final static String QUESTION_API_URL = "/api/questions";
    @Test
    public void create() throws Exception {
        QuestionDto newQuestion = new QuestionDto("title", "test content");
        String location = createResourceWithAuth(QUESTION_API_URL, newQuestion);
        newQuestion.setId(Long.parseLong(location.substring(location.length() - 1)));

        QuestionDto dbQuestion = basicAuthTemplate().getForObject(location, QuestionDto.class);

        assertThat(newQuestion, is(dbQuestion));
    }

    @Test
    public void update() throws Exception {
        QuestionDto newQuestion = new QuestionDto("title", "test content");
        String location = createResourceWithAuth(QUESTION_API_URL, newQuestion);

        QuestionDto updateQuestion = new QuestionDto(Long.parseLong(location.substring(location.length() - 1)), "update title", "test2 content");
        basicAuthTemplate().put(location, updateQuestion);

        QuestionDto dbQuestion = findByQuestionId(updateQuestion.getId()).toQuestionDto();
        assertThat(dbQuestion, is(updateQuestion));
    }

    @Test
    public void update_다른_사람() throws Exception {
        QuestionDto newQuestion = new QuestionDto("title", "test content");
        String location = createResourceWithAuth(QUESTION_API_URL, newQuestion);

        QuestionDto updateQuestion = new QuestionDto(Long.parseLong(location.substring(location.length() - 1)), "update title", "test2 content");
        basicAuthTemplate(findByUserId("sanjigi")).put(location, updateQuestion);

        QuestionDto dbQuestion = findByQuestionId(updateQuestion.getId()).toQuestionDto();
        assertThat(dbQuestion, not(updateQuestion));
    }
}
