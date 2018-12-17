package codesquad.domain;

import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;

public class QuestionTest extends BaseTest {
    public static final Question TEST_QUESTION = new Question("test title", "test contents");
    public static final Question UPDATED_QUESTION = new Question("updated title", "updated contents");

    @Test
    public void update_owner() throws Exception {
        User loginUser = JAVAJIGI;

        Question origin = TEST_QUESTION;
        origin.writeBy(JAVAJIGI);

        Question target = UPDATED_QUESTION;

        origin.update(loginUser, target);
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        User loginUser = JAVAJIGI;

        Question origin = TEST_QUESTION;
        origin.writeBy(SANJIGI);

        Question target = UPDATED_QUESTION;

        origin.update(loginUser, target);
    }

    @Test
    public void delete_owner() throws Exception {
        User loginUser = JAVAJIGI;

        Question origin = TEST_QUESTION;
        origin.writeBy(JAVAJIGI);

        origin.delete(loginUser);
        softly.assertThat(origin.isDeleted()).isEqualTo(true);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws Exception {
        User loginUser = JAVAJIGI;

        Question origin = TEST_QUESTION;
        origin.writeBy(SANJIGI);

        origin.delete(loginUser);
    }

    //TODO : Mock을 이용해 QnaService에 대한 테스트
}