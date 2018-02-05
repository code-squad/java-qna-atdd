package codesquad.web;

import codesquad.domain.Question;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() {
        QuestionDto questionDto = new QuestionDto(3L, "title", "contents");
        assertNull(findByQuestionId(3L));
        String url = defaultQuestion().generateApiUrl();
        String location = createBasicTemplateResource(url.substring(0, url.length()-2), questionDto);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, findByUserId(defaultUser().getUserId()));
        assertThat(dbQuestion, is(questionDto));
    }

    @Test
    public void show() {
        QuestionDto questionDto = getResource(defaultQuestion().generateApiUrl(),
                QuestionDto.class, findByUserId(defaultUser().getUserId()));
        assertThat(questionDto, is(defaultQuestion().toQuestionDto()));
    }

    @Test
    public void update() {
        QuestionDto updateQuestion = new QuestionDto(defaultQuestion().getId(), "updateTitle", "updateContents");
        String location = defaultQuestion().generateApiUrl();
        basicAuthTemplate(defaultUser()).put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, findByUserId(defaultUser().getUserId()));
        assertThat(dbQuestion, is(updateQuestion));
    }

    @Test
    public void update_실패() throws Exception {
        QuestionDto existingQuestionDto = defaultQuestion().toQuestionDto();
        QuestionDto updateQuestion = new QuestionDto(5L, "updateTitle", "updateContents");
        String location = defaultQuestion().generateApiUrl();
        basicAuthTemplate(defaultUser()).put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, findByUserId(defaultUser().getUserId()));
        assertThat(dbQuestion, not(updateQuestion));
        assertThat(dbQuestion, is(existingQuestionDto));
    }

    @Test
    public void delete() {
        long questionId = 2;
        assertFalse(findByQuestionId(questionId).isDeleted());
        String location = "/api/questions/" + questionId;
        basicAuthTemplate(findByUserId("sanjigi")).delete(location);

        assertTrue(findByQuestionId(questionId).isDeleted());
    }

    @Test
    public void delete_실패() {
        assertFalse(findByQuestionId(defaultQuestion().getId()).isDeleted());
        String location = defaultQuestion().generateApiUrl();
        basicAuthTemplate(findByUserId("sanjigi")).delete(location);

        assertFalse(findByQuestionId(defaultQuestion().getId()).isDeleted());
    }
}
