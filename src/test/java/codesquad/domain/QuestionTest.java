package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

public class QuestionTest {
    private Question question;

    @Before
    public void setUp() {
        question = new Question("제목입니다.", "내용 입니다.");
        question.writeBy(UserTest.newUser(1L));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_other() {
        Question updateQuestion = new Question("업데이트된 제목", "업데이트된 내용");
        question.modify(updateQuestion, UserTest.newUser(2L));
    }

    @Test
    public void update_owner() {
        Question updateQuestion = new Question("업데이트된 제목", "업데이트된 내용");
        question.modify(updateQuestion, UserTest.newUser(1L));
    }

    @Test
    public void delete_owner() {
        question.delete(UserTest.newUser(1L));
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_other() {
        question.delete(UserTest.newUser(2L));
    }

}
