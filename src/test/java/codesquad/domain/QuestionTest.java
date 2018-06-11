package codesquad.domain;

import codesquad.CannotDeleteException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class QuestionTest {
    private static final Logger log =  LoggerFactory.getLogger(QuestionTest.class);
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    public static final User JIMMY = new User(3L, "jimmy", "password", "name", "jaeyeon93@naver.com");

    private Question question;
    private Question question2;
    private Answer answer1;
    private Answer answer2;

    @Before
    public void setUp() {
        question = new Question(3L,"로그인 default", "댓글 없음", JAVAJIGI);
        question2 = new Question(4L, "로그인 jimmy", "댓글 여러명", JIMMY);
        answer1 = new Answer(JAVAJIGI,"자바지기 쓴 댓글");
        answer2 = new Answer(JIMMY,"지미가 쓴 댓글");
    }

    @Test
    public void updateTest() {
        Question target = new Question("제목 수정 완료", "내용 수정 완료");
        question.update(JAVAJIGI, target);
        assertEquals("제목 수정 완료", question.getTitle());
    }

    @Test(expected = IllegalStateException.class)
    public void updateFailTest() {
        Question target = new Question("제목 수정 시도", "내용 수정 시도");
        question.update(SANJIGI, target);
    }

    @Test
    public void 답변없음로그인유저같음() throws Exception {
        log.info("question : {}, answer : {}", question.getWriter(), question.getAnswers());
        question.delete(JAVAJIGI);
    }

    @Test(expected = CannotDeleteException.class)
    public void 답변없음로그인유저다름() throws Exception {
        log.info("question : {}, answer : {}", question.getWriter(), question.getAnswers());
        log.info("user : {}", JIMMY.toString());
        question.delete(JIMMY);
    }

    @Test
    public void 답변있음글쓴이같음() throws Exception {
        question.addAnswer(answer1);
        log.info("question : {}, answer : {}", question.getWriter(), question.getAnswers());
        question.delete(JAVAJIGI);
    }

    @Test(expected = CannotDeleteException.class)
    public void 답변있음로그인유저다름() throws Exception {
        question.addAnswer(answer1);
        log.info("question : {}, answer : {}", question.getWriter(), question.getAnswers());
        question.delete(JIMMY);
    }

    @Test(expected = CannotDeleteException.class)
    public void 답변여러개로그인같음() throws Exception {
        question.addAnswer(answer1);
        question.addAnswer(answer2);
        log.info("question : {}, answer : {}", question.getWriter(), question.getAnswers());
        question.delete(JAVAJIGI);
    }

    @Test(expected = CannotDeleteException.class)
    public void 답변여러개로그인다름() throws Exception {
        question.addAnswer(answer1);
        question.addAnswer(answer2);
        log.info("question : {}, answer : {}", question.getWriter(), question.getAnswers());
        question.delete(JIMMY);
    }
}
