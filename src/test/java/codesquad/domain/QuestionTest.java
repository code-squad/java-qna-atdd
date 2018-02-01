package codesquad.domain;

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
}
