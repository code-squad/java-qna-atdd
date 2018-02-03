package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest {
    private static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    private static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    private static final User BADAJIGI = new User(3L, "badajigi", "password", "name", "badajigi@slipp.net");
    private static final Question QUESTION = new Question("질문있어요.", "true가 트루?");

    @Before
    public void setup() {
        QUESTION.writeBy(JAVAJIGI);
        Answer answer = new Answer(1L, JAVAJIGI, QUESTION, "자문자답: 네");
    }

    @Test
    public void isOwner() {
        Answer answer = new Answer(1L, JAVAJIGI, QUESTION, "자문자답: 네");
        assertThat(answer.isOwner(JAVAJIGI)).isTrue();
        assertThat(answer.isOwner(SANJIGI)).isFalse();
    }

    @Test
    public void update() {
        Answer answer = new Answer(1L, JAVAJIGI, QUESTION, "자문자답: 네");
        String updateContents = "아닌것도 같고...";
        answer.update(JAVAJIGI, updateContents);
        assertThat(answer.getContents()).isEqualTo(updateContents);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        Answer answer = new Answer(1L, JAVAJIGI, QUESTION, "자문자답: 네");
        String updateContents = "아닌것도 같고...";
        answer.update(SANJIGI, updateContents);
    }

    @Test
    public void delete_owner() throws Exception {
        Answer answer = new Answer(1L, SANJIGI, QUESTION, "자문자답: 네");
        answer.delete(SANJIGI);
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws Exception {
        Answer answer = new Answer(1L, SANJIGI, QUESTION, "자문자답: 네");
        answer.delete(BADAJIGI);
        assertThat(answer.isDeleted()).isTrue();
    }
}
