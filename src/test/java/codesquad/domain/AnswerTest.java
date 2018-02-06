package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.*;

public class AnswerTest {

    private Question question;
    private Answer answer;
    private User user1;
    private User user2;

    @Before
    public void setUp() throws Exception {
        user1 = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
        user2 = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
        question = new Question("title", "content");
        answer = new Answer(user1,"answer");
        question.addAnswer(answer);
    }

    @Test
    public void isOwner() {
        assertThat(answer.isOwner(user1)).isTrue();
        assertThat(answer.isOwner(user2)).isFalse();
    }

    @Test
    public void update() {
        answer.update(user1, new AnswerDto("update"));
        assertThat(answer.getContents()).isEqualTo("update");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_권한없음() {
        answer.update(user2, new AnswerDto("update"));
    }

    @Test
    public void delete() {
        answer.delete(user1);
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_unauthorized() {
        answer.delete(user2);

    }
}