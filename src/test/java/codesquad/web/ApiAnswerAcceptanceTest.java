package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private QuestionRepository questionRepository;


    @Before
    public void setUp() {
        writer = new User("jimmy", "12345", "jimmy", "jaeyeon93@naver.com");
        question = new Question(8L, "question title", "question content", writer);
        log.info("setUp 실행");
    }

    @Test
    public void create() throws Exception {
        question = new QuestionDto(7L,"제목112", "내용119", defaultUser()).toQuestion();
        String path = createResource("/api/questions", question, defaultUser());
        assertThat(getResource(path, Question.class, defaultUser()), is(question));
        Answer answer = new Answer(7L, defaultUser(), question,"답글12345");
        log.info("answer is {}", answer.toString());
        path = createResource(String.format("/api/questions/%d/answers", getResource(path, Question.class, defaultUser()).getId()), answer, defaultUser());
        log.info("question answer : {}", question.getAnswers());
        question = questionRepository.findById(7L).get();
        assertThat(getResource(path, Answer.class, defaultUser()), is(answer));
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
