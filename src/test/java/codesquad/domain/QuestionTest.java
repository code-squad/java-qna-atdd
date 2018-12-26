package codesquad.domain;

import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;

public class QuestionTest extends BaseTest {
    public static final Question DEFAULT_QUESTION = new Question(3L, JAVAJIGI, "test title", "test contents");
    public static final Question UPDATED_QUESTION = new Question(4L, JAVAJIGI, "updated title", "updated contents");
    public static final Question OTHERS_QUESTION = new Question(5L, SANJIGI, "others title", "others contents");

    public static Question newQuestion(String title, String contents) {
        return new Question(title, contents);
    }

    //TODO: 리팩토링

    @Test
    public void update_owner() throws Exception {
        User loginUser = JAVAJIGI;

        Question origin = DEFAULT_QUESTION;
        origin.writeBy(JAVAJIGI);

        Question target = UPDATED_QUESTION;

        origin.update(loginUser, target);
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        User loginUser = JAVAJIGI;

        Question origin = DEFAULT_QUESTION;
        origin.writeBy(SANJIGI);

        Question target = UPDATED_QUESTION;

        origin.update(loginUser, target);
    }

    @Test
    public void delete_삭제가능_작성자의답변() throws Exception {
        User loginUser = JAVAJIGI;

        Question origin = DEFAULT_QUESTION;
        origin.writeBy(JAVAJIGI);

        origin.delete(loginUser);
        softly.assertThat(origin.isDeleted()).isEqualTo(true);
    }

    @Test
    public void delete_삭제가능_답변없음() throws Exception {
        User loginUser = JAVAJIGI;

        Question origin = newQuestion("test", "test");
        origin.writeBy(JAVAJIGI);

        origin.delete(loginUser);
        softly.assertThat(origin.isDeleted()).isEqualTo(true);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_삭제불가_타인의질문() throws Exception {
        User loginUser = JAVAJIGI;

        Question origin = DEFAULT_QUESTION;
        origin.writeBy(SANJIGI);

        origin.delete(loginUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_삭제불가_타인답변존재() throws Exception {
        User loginUser = JAVAJIGI;

        Question origin = DEFAULT_QUESTION;
        origin.writeBy(JAVAJIGI);
        origin.addAnswer(new Answer(SANJIGI, "Test Answer"));

        origin.delete(loginUser);

    }

    //TODO : Mock을 이용해 QnaService에 대한 테스트
}