package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    Question question1;
    Question question2;

    @Before
    public void setUp() throws Exception {
        question1 = new Question("타이틀1", "내용1");
        question1.writeBy(UserTest.JAVAJIGI);

        question2 = new Question("타이틀2", "내용2");
        question2.writeBy(UserTest.SANJIGI);
    }

    @Test
    public void update_same_user() {
        Question updateQuestion = new Question("타이틀2", "내용2");
        updateQuestion.writeBy(UserTest.JAVAJIGI);

        question1.update(UserTest.JAVAJIGI, updateQuestion);
        softly.assertThat(question1.getTitle()).isEqualTo("타이틀2");
        softly.assertThat(question1.getContents()).isEqualTo("내용2");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_other_user() {
        Question updateQuestion = new Question("타이틀2", "내용2");
        updateQuestion.writeBy(UserTest.JAVAJIGI);
        question1.update(UserTest.SANJIGI, updateQuestion);
    }
}