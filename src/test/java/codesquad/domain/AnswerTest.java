package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest {

    private Answer answer;

    private User javajigi;

    private User gunju;

    @Before
    public void setUp() throws Exception {
        javajigi = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        gunju = new User(2, "gunju", "test", "건주", "test@email.com");

        answer = new Answer(javajigi, "test contents");
    }

    @Test
    public void isOwner() throws Exception {
        assertThat(answer.isOwner(javajigi)).isTrue();
        assertThat(answer.isOwner(gunju)).isFalse();
    }

    @Test
    public void update() throws Exception {
        Answer updateAnswer = new Answer(javajigi, "update test");
        answer.update(updateAnswer);

        assertThat(answer.getContents()).isEqualTo("update test");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_권한이없는사용자() throws Exception {
        Answer updateAnswer = new Answer(gunju, "update test");
        answer.update(updateAnswer);
    }

    @Test
    public void delete() throws Exception {
        answer.delete(javajigi);

        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_권한이없는사용자() throws Exception {
        answer.delete(gunju);
    }

}