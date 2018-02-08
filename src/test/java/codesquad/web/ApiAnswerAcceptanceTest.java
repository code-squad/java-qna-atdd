package codesquad.web;

import codesquad.domain.Answer;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by hoon on 2018. 2. 7..
 */
public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private String contents = "test댓글";
    private QuestionDto question;

    @Before
    public void setup() {
        question = new QuestionDto("abc", "abc");
    }

    @Test
    public void create() throws Exception {
        String location = createResource(defaultUser(), "/api/questions", question);

        ResponseEntity<String> answerResponse = basicAuthTemplate(defaultUser()).postForEntity(location + "/answers", contents, String.class);
        assertThat(answerResponse.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void update() {
        String location = createResource(defaultUser(), "/api/questions", question);

        location = createResource(defaultUser(), location + "/answers", contents);

        basicAuthTemplate(defaultUser()).put(location, new AnswerDto("update contents"));

        Answer answer = getResource(location, Answer.class, defaultUser());
        assertThat(answer.getContents(), is("update contents"));
    }

    @Test
    public void delete() {
        String location = createResource(defaultUser(), "/api/questions", question);

        location = createResource(defaultUser(), location + "/answers", contents);

        basicAuthTemplate(defaultUser()).delete(location);

        Answer answer = getResource(location, Answer.class, defaultUser());

        assertTrue(answer.isDeleted());
    }
}
