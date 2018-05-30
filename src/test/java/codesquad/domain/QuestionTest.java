package codesquad.domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class QuestionTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    private Question question;

    @Before
    public void setUp() {
        question = new Question("제목1", "내용1", JAVAJIGI);
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
}
