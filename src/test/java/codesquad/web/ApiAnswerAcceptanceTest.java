package codesquad.web;

import codesquad.dto.AnswerDto;
import org.junit.Test;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Test
    public void createAndSelect() {
        AnswerDto newAnswer = new AnswerDto("answer test");
        String location = createAnswer(newAnswer);

        AnswerDto dbAnswer = getResource(location, AnswerDto.class);
        assertThat(dbAnswer.getContents(), is(newAnswer.getContents()));
    }

    private String createAnswer(AnswerDto newAnswer) {
        return createResource("/api/questions/1/answers", newAnswer);
    }

    @Test
    public void update() {
        AnswerDto newAnswer = new AnswerDto("answer test");
        String location = createAnswer(newAnswer);

        AnswerDto updatedAnswer = new AnswerDto("updated answer test2");
        basicAuthTemplate().put(location, updatedAnswer);

        AnswerDto dbAnswer = getResource(location, AnswerDto.class);
        assertThat(dbAnswer.getContents(), is(updatedAnswer.getContents()));
    }

    @Test
    public void updateFailedBecauseOfNotSameUser() {
        AnswerDto newAnswer = new AnswerDto("answer test2");
        String location = createAnswer(newAnswer);

        AnswerDto updatedAnswer = new AnswerDto("updated answer test2");
        basicAuthTemplate(defaultUserAsSANJIGI()).put(location, updatedAnswer);

        AnswerDto dbAnswer = getResource(location, AnswerDto.class);
        assertThat(dbAnswer, is(newAnswer));
    }

    @Test
    public void delete() {
        AnswerDto newAnswer = new AnswerDto("answer test3");
        String location = createAnswer(newAnswer);

        basicAuthTemplate().delete(location);

        AnswerDto dbAnswer = getResource(location, AnswerDto.class);
        assertNull(dbAnswer);
    }

    @Test
    public void deleteFailedBecauseOfNotSameUser() {
        AnswerDto newAnswer = new AnswerDto("answer test4");
        String location = createAnswer(newAnswer);

        basicAuthTemplate(defaultUserAsSANJIGI()).delete(location);

        AnswerDto dbAnswer = getResource(location, AnswerDto.class);
        assertThat(dbAnswer, is(newAnswer));
    }
}

