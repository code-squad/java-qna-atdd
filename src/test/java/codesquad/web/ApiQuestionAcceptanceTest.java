package codesquad.web;

import codesquad.dto.QuestionDto;
import org.junit.Test;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void createAndSelect() {
        QuestionDto newQuestion = new QuestionDto("title test", "question test");
        String location = createQuestion(newQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(dbQuestion, is(newQuestion));
    }

    private String createQuestion(QuestionDto newQuestion) {
        return createResource("/api/questions", newQuestion);
    }

    @Test
    public void update() {
        QuestionDto newQuestion = new QuestionDto("title test2", "question test2");
        String location = createQuestion(newQuestion);

        QuestionDto updatedQuestion = new QuestionDto("updated title test2", "updated question test2");
        basicAuthTemplate().put(location, updatedQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(dbQuestion, is(updatedQuestion));
    }

    @Test
    public void updateFailedBecauseOfNotSameUser() {
        QuestionDto newQuestion = new QuestionDto("title test2", "question test2");
        String location = createQuestion(newQuestion);

        QuestionDto updatedQuestion = new QuestionDto("updated title test2", "updated question test2");
        basicAuthTemplate(defaultUserAsSANJIGI()).put(location, updatedQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(dbQuestion, is(newQuestion));
    }

    @Test
    public void delete() {
        QuestionDto newQuestion = new QuestionDto("title test3", "question test3");
        String location = createQuestion(newQuestion);

        basicAuthTemplate().delete(location);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertNull(dbQuestion);
    }

    @Test
    public void deleteFailedBecauseOfNotSameUser() {
        QuestionDto newQuestion = new QuestionDto("title test4", "question test4");
        String location = createQuestion(newQuestion);

        basicAuthTemplate(defaultUserAsSANJIGI()).delete(location);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class);
        assertThat(dbQuestion, is(newQuestion));
    }
}

