package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest {

    private User defaultUser;
    private User otherUser;
    private Answer answer;

    private void createAnswer() {
        answer = new Answer(defaultUser, "contents");
    }

    @Before
    public void init() {
        defaultUser = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        otherUser = new User(2, "sanjigi", "test", "산지기", "sanjigi@slipp.net");
        createAnswer();
    }

    @Test
    public void 자기자신_댓글수정() {
        String updatingContents = "updating contents";
        answer.update(defaultUser, updatingContents);

        assertThat(answer.getContents()).isEqualTo(updatingContents);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 타인_댓글수정() {
        String updatingContents = "updating contents";
        answer.update(otherUser, updatingContents);

        assertThat(answer.getContents()).isEqualTo(updatingContents);
    }

    @Test
    public void 자기자신_댓글삭제() {
        answer.delete(defaultUser);

        assertThat(answer.isDeleted()).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 타인_댓글삭제() {
        answer.delete(otherUser);

        assertThat(answer.isDeleted()).isEqualTo(false);
    }

}