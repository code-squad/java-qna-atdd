package codesquad.web;

import codesquad.converter.HtmlFormDataBuilder;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log =  LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    private User writer;
    private Question question;

    @Before
    public void setUp() {
        writer = new User("jimmy", "12345", "jimmy", "jaeyeon93@naver.com");
        question = new Question(3L,"제목11", "내용11", writer);
    }

    @Test
    public void create() throws Exception {
        question = new Question(3L,"제목112", "내용119", writer);
        String path = createResource("/api/questions", question, writer);
        log.info("path is : {}", path);
        assertThat(getResource(path, Question.class, writer), is(question));
    }

    @Test
    public void listTest() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/api/questions", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void showQuestion() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/api/questions/1", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void update() throws Exception {
        question = new Question(3L,"제목112", "내용119", writer);
        String path = createResource("/api/questions", question, writer);
        Question newQuestion = new Question(3L, "제목수정", "내용수정", writer);
        basicAuthTemplate(writer).put(path, newQuestion);
        assertThat(getResource(path, Question.class, writer), is(newQuestion));
    }

    @Test
    public void delete() throws Exception {
        question = new Question(3L,"제목112", "내용119", writer);
        String path = createResource("/api/questions", question, writer);
        basicAuthTemplate(writer).delete(path);
    }
}
