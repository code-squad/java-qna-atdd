package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ApiAnswerControllerTest extends AcceptanceTest {
    private final static String ANSWER_API_URL = "/api/answers";

    @Test
    public void create() throws Exception {
        AnswerDto newAnswer = new AnswerDto(1l, "test content");
        String location = createResourceWithAuth(ANSWER_API_URL, newAnswer);
        newAnswer.setId(Long.parseLong(location.substring(location.length() - 1)));
        AnswerDto dbAnswer = findByAnswerId(Long.parseLong(location.substring(location.length() - 1))).toAnswerDto();

        assertThat(newAnswer, is(dbAnswer));
    }

    @Test
    public void update() throws Exception {
        AnswerDto newAnswer = new AnswerDto(1l, "test content");
        String location = createResourceWithAuth(ANSWER_API_URL, newAnswer);
        newAnswer.setId(Long.parseLong(location.substring(location.length() - 1)));
        AnswerDto updatedAnswer = new AnswerDto(newAnswer.getId(), newAnswer.getQuestionId(), "test2 content");

        basicAuthTemplate().put(location, updatedAnswer);

        AnswerDto dbAnswer = getResource(location, AnswerDto.class, defaultUser());

        assertThat(updatedAnswer, is(dbAnswer));
    }

    @Test
    public void update_다른_사람() throws Exception {
        AnswerDto newAnswer = new AnswerDto(1l, "test content");
        String location = createResourceWithAuth(ANSWER_API_URL, newAnswer);
        newAnswer.setId(Long.parseLong(location.substring(location.length() - 1)));

        AnswerDto updatedAnswer = new AnswerDto(newAnswer.getId(), newAnswer.getQuestionId(), "test2 content");
        basicAuthTemplate(findByUserId("sanjigi")).put(location, updatedAnswer);

        AnswerDto dbAnswer = findByAnswerId(newAnswer.getId()).toAnswerDto();

        assertThat(updatedAnswer, not(dbAnswer));
    }

    @Test
    public void delete() throws Exception {
        AnswerDto newAnswer = new AnswerDto(1l, "test content");
        String location = createResourceWithAuth(ANSWER_API_URL, newAnswer);
        newAnswer.setId(Long.parseLong(location.substring(location.length() - 1)));

        basicAuthTemplate().delete(location);

        Answer dbAnswer = findByAnswerId(newAnswer.getId());

        Assert.assertTrue(dbAnswer.isDeleted());
    }

    @Test
    public void delete_다른사람() throws Exception {
        AnswerDto newAnswer = new AnswerDto(1l, "test content");
        String location = createResourceWithAuth(ANSWER_API_URL, newAnswer);
        newAnswer.setId(Long.parseLong(location.substring(location.length() - 1)));

        basicAuthTemplate(findByUserId("sanjigi")).delete(location);
        Answer dbAnswer = findByAnswerId(newAnswer.getId());

        Assert.assertTrue(!dbAnswer.isDeleted());
    }
}
