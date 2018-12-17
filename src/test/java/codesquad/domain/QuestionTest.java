package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;

public class QuestionTest extends BaseTest {
    public static final Question qna1 = new Question("하하하", "이것은 내용입니다.");
    public static final Question qna2 = new Question("2번", "이것은 2번째 내용입니다.");

    public static Question newQuestion(User origin) {
        Question question = new Question("하하하", "이것은 내용입니다.");
        question.writeBy(origin);
        return question;
    }

    public static Question newQuestion(String title,String contents) {
        return new Question(title, contents);
    }


    @Test
    public void update_owner() throws Exception {
        Question origin = newQuestion(JAVAJIGI);
        Question target = newQuestion("나는 바뀝니다.","그렇습니다.");
        origin.update(JAVAJIGI,target);
        softly.assertThat(origin.getTitle()).isEqualTo(target.getTitle());
        softly.assertThat(origin.getContents()).isEqualTo(target.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        Question origin = newQuestion(JAVAJIGI);
        Question target = newQuestion("나는 안바뀝니다.","그렇습니다.");
        origin.update(SANJIGI,target);
    }

    @Test
    public void delete_owner() throws  Exception {
        Question origin = newQuestion(JAVAJIGI);
        origin.deleted(JAVAJIGI);
        softly.assertThat(origin.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws  Exception {
        Question origin = newQuestion(JAVAJIGI);
        origin.deleted(SANJIGI);
    }
}
