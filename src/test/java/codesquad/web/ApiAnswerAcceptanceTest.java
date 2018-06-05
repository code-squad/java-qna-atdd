package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log =  LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    private User writer;
    private Question question;
    private Answer answer;

    @Before
    public void setUp() {
        writer = new User("jimmy", "12345", "jimmy", "jaeyeon93@naver.com");
        question = new Question(3L, "question title", "question content", writer);
    }

    @Test
    public void create() throws Exception {
        question = new Question(3L,"제목112", "내용119", writer);
        String path = createResource("/api/questions", question, writer);
        log.info("path is : {}", path);
        assertThat(getResource(path, Question.class, writer), is(question));

        Answer answer = new Answer(4L, writer, question,"답글12345");
        log.info("answer is {}", answer.toString());
        path = createResource(String.format("/api/questions/%d/answers", getResource(path, Question.class, writer).getId()), answer,writer);
        assertThat(getResource(path, Answer.class, writer), is(answer));
    }

    @Test
    public void delete() throws Exception {
        Answer answer = new Answer(4L, writer, question,"답글12345");
        String path = createResource(String.format("/api/questions/%d/answers", 3), answer, writer);
        log.info("path is {}", path);
        // path is /api/questions/3/answer/4
        basicAuthTemplate(writer).delete(path);
    }
}
