package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ApiDeleteAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiDeleteAcceptanceTest.class);
    private Question question;
    private User JIMMY;
    private Answer answer;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    @Before
    public void setUp() {
        JIMMY = new User("jimmy", "12345", "jimmy", "jaeyeon93@naver.com");
    }

    @Test
    public void 삭제가능() throws Exception {
        question = new Question(3L, "제목112", "내용119", defaultUser());
        String path = createResource("/api/questions", question, defaultUser());
        assertThat(getResource(path, Question.class, defaultUser()), is(question));

        basicAuthTemplate().delete(path);
        assertTrue(getResource(path, Question.class, defaultUser()).isDeleted());
    }

    @Test
    public void 삭제가능2() throws Exception {
        question = new Question(3L, "제목112", "내용119", defaultUser());
        String path = createResource("/api/questions", question, defaultUser());
        assertThat(getResource(path, Question.class, defaultUser()), is(question));
        //댓글추가
        Answer answer = new Answer(7L, defaultUser(), question, "답글12345");
        String path2 = createResource(String.format("/api/questions/%d/answers", getResource(path, Question.class, defaultUser()).getId()), answer, defaultUser());
        assertThat(getResource(path2, Answer.class, defaultUser()), is(answer));
        //삭제시도
        basicAuthTemplate().delete(path);
        assertTrue(getResource(path, Question.class, defaultUser()).isDeleted());
    }

    @Test
    public void 삭제가능3() throws Exception {
        // 질문추가
        question = new Question(3L, "제목112", "내용119", defaultUser());
        String path = createResource("/api/questions", question, defaultUser());
        assertThat(getResource(path, Question.class, defaultUser()), is(question));
        //댓글추가
        Answer answer = new Answer(7L, defaultUser(), question, "답글12345");
        String path2 = createResource(String.format("/api/questions/%d/answers", getResource(path, Question.class, defaultUser()).getId()), answer, defaultUser());
        assertThat(getResource(path2, Answer.class, defaultUser()), is(answer));
        Answer answer2 = new Answer(8L, defaultUser(), question, "새로운댓글");
        createResource(String.format("/api/questions/%d/answers", getResource(path, Question.class, defaultUser()).getId()), answer2, defaultUser());
        //삭제시도
        basicAuthTemplate().delete(path);
        assertTrue(getResource(path, Question.class, defaultUser()).isDeleted());
        log.info("delete history : {}", deleteHistoryRepository.findAll());
    }

    @Test
    public void 삭제불가능() throws Exception {
        question = new Question(3L, "제목112", "내용119", defaultUser());
        String path = createResource("/api/questions", question, defaultUser());

        basicAuthTemplate(JIMMY).delete(path);
        assertFalse(getResource(path, Question.class, defaultUser()).isDeleted());
    }

    @Test
    public void 삭제불가능2() throws Exception {
        question = new Question(3L, "제목112", "내용119", defaultUser());
        String path = createResource("/api/questions", question, defaultUser());
        assertThat(getResource(path, Question.class, defaultUser()), is(question));
        //댓글추가
        Answer answer = new Answer(7L, defaultUser(), question, "답글12345");
        path = createResource(String.format("/api/questions/%d/answers", getResource(path, Question.class, defaultUser()).getId()), answer, defaultUser());
        assertThat(getResource(path, Answer.class, defaultUser()), is(answer));
        //삭제시도
        basicAuthTemplate(JIMMY).delete(path);
        assertFalse(getResource(path, Question.class, defaultUser()).isDeleted());
    }
}
