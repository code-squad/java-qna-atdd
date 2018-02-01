package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QeustionTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    public static Question QUESTION1 = new Question("질문이있어요1","질문내용이에요1");
    public static Question QUESTION2 = new Question("질문이있어요2","질문내용이에요2");

    private Question newQuestion(Question question) {
        return new Question(question.getTitle(), question.getContents());
    }

    @Test
    public void updateTest() throws Exception {
        User loginUser = JAVAJIGI;
        Question originalQuestion = newQuestion(QUESTION1);
        originalQuestion.writeBy(loginUser);
        Question targetQuestion = newQuestion(QUESTION2);
        originalQuestion.update(loginUser, targetQuestion);
        assertThat(originalQuestion.getTitle(), is(targetQuestion.getTitle()));
        assertThat(originalQuestion.getContents(), is(targetQuestion.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateFailTestNoLogin() {
        User writer = JAVAJIGI;
        Question originalQuestion = newQuestion(QUESTION1);
        originalQuestion.writeBy(writer);
        Question targetQuestion = newQuestion(QUESTION2);
        originalQuestion.update(null, targetQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateFailTestNotOwner() {
        User writer = JAVAJIGI;
        User loginUser = SANJIGI;
        Question originalQuestion = newQuestion(QUESTION1);
        originalQuestion.writeBy(writer);
        Question targetQuestion = newQuestion(QUESTION2);
        originalQuestion.update(loginUser, targetQuestion);
    }

    @Test
    public void deleteTest() throws Exception {
        User loginUser = JAVAJIGI;
        Question targetQuestion = newQuestion(QUESTION1);
        targetQuestion.writeBy(loginUser);
        targetQuestion.delete(loginUser);
        assertThat(targetQuestion.isDeleted(), is(true));
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteFailTestNoLogin() {
        User writer = JAVAJIGI;
        User logingUser = null;
        Question targetQuestion = newQuestion(QUESTION1);
        targetQuestion.writeBy(writer);
        targetQuestion.delete(logingUser);
        assertThat(targetQuestion.isDeleted(), is(false));
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteFailTestNotOwner() {
        User writer = JAVAJIGI;
        User logingUser = SANJIGI;
        Question targetQuestion = newQuestion(QUESTION1);
        targetQuestion.writeBy(writer);
        targetQuestion.delete(logingUser);
        assertThat(targetQuestion.isDeleted(), is(false));
    }
}
