package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class QuestionTest {
    private static User newUser(String userId, String password) {
        return new User(1L, userId, password, "name", "javajigi@slipp.net");
    }

    @Test
    public void update_owner() {
        User originUser = newUser("testid", "testpass");
        User targetUser = newUser("testid", "testpass");

        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        Question target = new Question("modified title", "modified contents");
        target.writeBy(targetUser);

        origin.update(target, originUser);
        assertThat(origin.getTitle(), is(target.getTitle()));
        assertThat(origin.getContents(), is(target.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        User originUser = newUser("testid", "testpass");
        User targetUser = newUser("testidaa", "testpass");

        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        Question target = new Question("modified title", "modified contents");
        target.writeBy(targetUser);

        origin.update(target, targetUser);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_match_password() {
        User originUser = newUser("testid", "testpass");
        User targetUser = newUser("testid", "testpassffff");

        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        Question target = new Question("modified title", "modified contents");
        target.writeBy(targetUser);

        origin.update(target, targetUser);
    }

    @Test
    public void checkAnswerExist() {
        User originUser = newUser("testid", "testpass");
        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        origin.addAnswer(new Answer(originUser, "teste"));

        assertTrue(origin.checkAnswerExist());
    }

    @Test
    public void checkAnswerNotExist() {
        User originUser = newUser("testid", "testpass");
        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        assertFalse(origin.checkAnswerExist());
    }

    @Test
    public void checkAllAnswerWriterIsSameWithWriter() {
        User originUser = newUser("testid", "testpass");
        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        origin.addAnswer(new Answer(originUser, "teste1"));
        origin.addAnswer(new Answer(originUser, "teste2"));
        origin.addAnswer(new Answer(originUser, "teste3"));

        assertTrue(origin.checkAllAnswerWriterIsSameWithWriter());
    }

    @Test
    public void checkAllAnswerWriterIsNotSameWithWriter() {
        User originUser = newUser("testid", "testpass");
        User originUser2 = newUser("testid2", "testpass2");
        User originUser3 = newUser("testid3", "testpass3");

        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        origin.addAnswer(new Answer(originUser, "teste1"));
        origin.addAnswer(new Answer(originUser2, "teste"));
        origin.addAnswer(new Answer(originUser3, "teste"));

        assertFalse(origin.checkAllAnswerWriterIsSameWithWriter());
    }

    @Test
    public void allAnswerDelete() throws CannotDeleteException {
        User originUser = newUser("testid", "testpass");
        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        origin.addAnswer(new Answer(originUser, "teste1"));
        origin.addAnswer(new Answer(originUser, "teste2"));
        origin.addAnswer(new Answer(originUser, "teste3"));

        List<DeleteHistory> deleteHistories = new ArrayList<>();
        origin.deleteAllAnswers(deleteHistories);
        assertThat(deleteHistories.size(), is(3));
    }
}
