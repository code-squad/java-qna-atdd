package codesquad.domain;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {
    private Question question;
    private Answer answer;

    @Test
    public void create_login() {
        answer = new Answer(1L, UserTest.SANJIGI, new Question("제목", "내용"), "댓글입니다.");
        softly.assertThat(answer.isOwner(UserTest.SANJIGI));
    }

    @Test (expected = UnAuthorizedException.class)
    public void create_no_login() {
        answer = new Answer(null,"내용");
    }

    @Test
    public void delete() {
        answer = new Answer(UserTest.JAVAJIGI, "내용입니다.");
        softly.assertThat(answer.delete(UserTest.JAVAJIGI).isDeleted()).isTrue();
    }

    @Test (expected = UnAuthenticationException.class)
    public void delete_no_login() {
        answer = new Answer(UserTest.JAVAJIGI, "내용입니다.");
        answer.delete(null);
    }

    @Test (expected = UnAuthorizedException.class)
    public void delete_other_answer() {
        answer = new Answer(UserTest.JAVAJIGI, "내용입니다.");
        answer.delete(UserTest.SANJIGI);
    }
}
