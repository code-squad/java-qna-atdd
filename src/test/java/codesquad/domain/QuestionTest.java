package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {

    private Question question;

    private User defaultUser;

    private static boolean compareTitleAndContents(Question o1, Question o2) {
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.getTitle().equals(o2.getTitle()) && o1.getContents().equals(o2.getContents());
    }

    @Before
    public void setUp() throws Exception {
        defaultUser = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        question = new Question("test", "content");
        question.writeBy(defaultUser);

        Answer answer = new Answer(defaultUser, "테스트 답변");
        answer.toQuestion(question);
        question.addAnswer(answer);
    }

    @Test
    public void delete() throws Exception {
        List<DeleteHistory> histories = question.delete(defaultUser);
        assertThat(question.isDeleted()).isTrue();
        assertThat(histories.size()).isEqualTo(2);
        assertThat(question.getCountOfAnswers()).isEqualTo(0);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_권한이없는유저() throws Exception {
        User user = new User(3, "gunju", "test", "고건주", "gunju@slipp.net");
        question.delete(user);
    }

    @Test
    public void isOwner() throws Exception {
        assertThat(question.isOwner(defaultUser)).isTrue();
        assertThat(question.isOwner(new User(3, "gunju", "test", "고건주", "gunju@slipp.net"))).isFalse();
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_권한이없는유저() throws Exception {
        User user = new User(3, "gunju", "test", "고건주", "gunju@slipp.net");
        question.update(user, new Question("test", "test"));
    }

    @Test
    public void update() throws Exception {
        Question updatedQuestion = new Question("update", "update test");
        question.update(defaultUser, updatedQuestion);
        assertThat(compareTitleAndContents(question, updatedQuestion)).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_로그인한유저가작성하지않은답변이있는경우() throws Exception {
        User gunju = new User(3, "gunju", "test", "고건주", "gunju@slipp.net");

        Answer answer = new Answer(gunju, "테스트 답변2");
        question.addAnswer(answer);
        question.delete(defaultUser);
    }

}