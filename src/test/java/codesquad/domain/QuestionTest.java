package codesquad.domain;

import codesquad.etc.CannotDeleteException;
import codesquad.etc.UnAuthorizedException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {

    private static User newUser(String userId) {
        return newUser(userId, "password");
    }

    private static User newUser(String userId, String password) {
        return newUser(1L, userId, password);
    }

    private static User newUser(long id, String userId, String password) {
        return new User(id, userId, password, "name", "javajigi@slipp.net");
    }

    private static Question createQuestion(String loginUser, String title, String contents) {
        User user = newUser(loginUser);
        Question question = new Question(title, contents);
        question.setWriter(user);

        return question;
    }

    @Test
    public void update_owner() throws Exception {
        Question origin = createQuestion("javajigi", "title", "contents");
        User loginUser = newUser("javajigi");

        Question target = new Question("new title", "new contents");
        origin.update(loginUser, target);
        assertThat(origin.getTitle(), is(target.getTitle()));
        assertThat(origin.getContents(), is(target.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        Question origin = createQuestion("javajigi", "title", "contents");
        User loginUser = newUser(2L, "sanjigi", "pw");

        Question target = new Question("new title", "new content");
        origin.update(loginUser, target);
    }

    @Test
    public void delete_owner_with_no_comments() throws Exception {
        Question origin = createQuestion("javajigi", "title", "contents");
        User loginUser = newUser("javajigi");

        origin.delete(loginUser);
        assertThat(origin.isDeleted(), is(true));
    }

    @Test
    public void delete_owner_with_own_comments() throws Exception {
        Question origin = createQuestion("javajigi", "title", "contents");
        User loginUser = newUser("javajigi");
        origin.addAnswer(new Answer()
                .setContents("test")
                .setWriter(loginUser));

        origin.delete(loginUser);
        assertThat(origin.isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_owner_with_comments_written_by_others() throws Exception {
        Question origin = createQuestion("javajigi", "title", "contents");
        User sanjigi = newUser(2L, "sanjigi", "pw");
        origin.addAnswer(new Answer()
                .setContents("test")
                .setWriter(sanjigi));
        User loginUser = newUser("javajigi");

        origin.delete(loginUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws Exception {
        Question origin = createQuestion("javajigi", "title", "contents");
        User loginUser = newUser(2L, "sanjigi", "pw");

        origin.delete(loginUser);
    }
}
