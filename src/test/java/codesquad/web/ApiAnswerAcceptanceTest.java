package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.dto.AnswerDto;
import org.junit.Before;
import org.junit.Test;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private String url;

    @Test
    public void create() {
        AnswerDto addAnswerDto = new AnswerDto(3L, "contents");
        assertNull(findByAnswerId(addAnswerDto.getId()));
        String location = createBasicTemplateResource("/api/questions/1/answers", addAnswerDto.getContents());

        AnswerDto dbAnswer = getResource(location, AnswerDto.class, findByUserId(defaultUser().getUserId()));
        assertThat(dbAnswer, is(addAnswerDto));
    }

    @Test
    public void show() {
        AnswerDto answerDto = getResource(defaultAnswer().generateApiUrl(),
                AnswerDto.class, findByUserId(defaultUser().getUserId()));
        assertThat(answerDto, is(defaultAnswer().toAnswerDto()));
    }

    @Test
    public void update() {
        AnswerDto updateAnswer = new AnswerDto(defaultAnswer().getId(), "updateContents");
        String location = defaultAnswer().generateApiUrl();
        basicAuthTemplate(defaultUser()).put(location, updateAnswer.getContents());

        AnswerDto dbAnswer = getResource(location, AnswerDto.class, findByUserId(defaultUser().getUserId()));
        assertThat(dbAnswer, is(updateAnswer));
    }

    @Test
    public void update_실패() {
        AnswerDto updateAnswer = new AnswerDto(5L, "updateContents");
        String location = defaultAnswer().generateApiUrl();
        basicAuthTemplate(defaultUser()).put(location, updateAnswer.getContents());

        AnswerDto dbAnswer = getResource(location, AnswerDto.class, findByUserId(defaultUser().getUserId()));
        assertThat(dbAnswer, not(updateAnswer));
    }

    @Test
    public void delete() {
        long answerId = 2;
        assertFalse(findByAnswerId(answerId).isDeleted());
        String location = "/api/questions/1/answers/" + answerId;
        basicAuthTemplate(findByUserId("sanjigi")).delete(location);

        assertTrue(findByAnswerId(answerId).isDeleted());
    }

    @Test
    public void delete_실패() {
        assertFalse(findByAnswerId(defaultAnswer().getId()).isDeleted());
        String location = defaultAnswer().generateApiUrl();
        basicAuthTemplate(findByUserId("sanjigi")).delete(location);

        assertFalse(findByAnswerId(defaultAnswer().getId()).isDeleted());
    }
}
