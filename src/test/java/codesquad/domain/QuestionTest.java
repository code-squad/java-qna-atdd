package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public static final long VALID_USER_ID = 1L;
    public static final long INVALID_USER_ID = 2L;

    User user;
    User fakeUser;
    Question question;
    Question modifiedQuestion;

    @Before
    public void setUp() throws Exception {
        user = new User("brad903", "1234", "Brad", "brad903@naver.com");
        user.setId(VALID_USER_ID);
        fakeUser = new User("leejh903", "1234", "브래드", "leejh903@gmail.com");
        fakeUser.setId(INVALID_USER_ID);
        question = new Question("제목 테스트", "내용 테스트 - 코드스쿼드 qna-atdd step2 진행중입니다");
        question.writeBy(user);
        modifiedQuestion = new Question("업데이트된 제목", "업데이트된 내용입니다");
    }

    @Test
    public void update_succeed() {
        Question updatedQuestion = question.update(user, modifiedQuestion);
        softly.assertThat(updatedQuestion.getTitle()).isEqualTo(modifiedQuestion.getTitle());
        softly.assertThat(updatedQuestion.getContents()).isEqualTo(modifiedQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_same_writer() {
        question.update(fakeUser, modifiedQuestion);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_same_writer() throws CannotDeleteException {
        question.delete(fakeUser);
    }

    @Test
    public void delete_succeed() throws CannotDeleteException {
        question.delete(user);
        softly.assertThat(question.isDeleted()).isEqualTo(true);
    }
}