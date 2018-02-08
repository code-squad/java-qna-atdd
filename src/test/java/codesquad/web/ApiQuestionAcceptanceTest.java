package codesquad.web;

import codesquad.domain.Question;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by hoon on 2018. 2. 7..
 */
public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() throws Exception {
        QuestionDto question = new QuestionDto("abc", "abc");
        createResource(defaultUser(), "/api/questions", question);
    }

    @Test
    public void update() throws Exception {
        QuestionDto question = new QuestionDto("abc", "abc");
        String location = createResource(defaultUser(), "/api/questions", question);

        QuestionDto updatedQuestionDto = new QuestionDto("test", "test");
        basicAuthTemplate(defaultUser()).put(location, updatedQuestionDto);

        QuestionDto dbQuestionDto = getResource(location, QuestionDto.class, defaultUser());
        assertThat(dbQuestionDto.getTitle(), is("test"));
    }

    @Test
    public void delete() throws Exception {
        QuestionDto question = new QuestionDto("abc", "abc");
        String location = createResource(defaultUser(), "/api/questions", question);

        basicAuthTemplate(defaultUser()).delete(location);

        Question deletedQuestion = getResource(location, Question.class, defaultUser());
        assertTrue(deletedQuestion.isDeleted());
    }
}
