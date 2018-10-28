package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import support.domain.UserGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class QuestionTest {

    @Test
    public void check_Owner() {
        User loginUser = UserGenerator.newUser("javajigi");
        User target = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");

        Question question = new Question("title", "content");
        question.writeBy(loginUser);

        assertThat(question.isOwner(target), is(true));
    }

    @Test
    public void update_question() throws Exception {
        User loginUser = UserGenerator.JAVAJIGI;
        Question origin = new Question("title", "content");
        origin.writeBy(UserGenerator.JAVAJIGI);

        Question expected = new Question("title1", "content1");
        expected.writeBy(UserGenerator.JAVAJIGI);

        origin.update(loginUser, expected);

        assertThat(origin.getTitle(), is(expected.getTitle()));
        assertThat(origin.getContents(), is(expected.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_question() throws Exception {
        User loginUser = UserGenerator.SANJIGI;
        Question origin = new Question("title", "content");
        origin.writeBy(UserGenerator.JAVAJIGI);

        Question expected = new Question("title1", "content1");
        expected.writeBy(UserGenerator.JAVAJIGI);

        origin.update(loginUser, expected);
    }

}
