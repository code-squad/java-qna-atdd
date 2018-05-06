package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class QuestionTest {

    private User defaultUser;
    private User otherUser;

    private Question saveQuestionBy(User user) {
        String title = "test";
        String contents = "contents";
        Question question = new Question(title, contents);
        question.writeBy(user);

        return question;
    }

    @Before
    public void init() {
        defaultUser = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        otherUser = new User(2, "sanjigi", "test", "산지기", "sanjigi@slipp.net");
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문수정_타인의글() {
        Question question = saveQuestionBy(defaultUser);

        Question updatedQuestion = new Question("updated title", "updated contents");
        question.update(otherUser, updatedQuestion);

    }

    @Test
    public void 질문수정_자신의글() {
        Question question = saveQuestionBy(defaultUser);

        Question updatedQuestion = new Question("updated title", "updated contents");
        question.update(defaultUser, updatedQuestion);

        assertThat(question.getTitle().equals(updatedQuestion.getTitle())).isEqualTo(true);
        assertThat(question.getContents().equals(updatedQuestion.getContents())).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문삭제_타인의글() {
        Question question = saveQuestionBy(defaultUser);
        question.delete(otherUser);

        assertThat(question.isDeleted()).isEqualTo(false);
    }

    @Test
    public void 질문삭제_자신의글() {
        Question question = saveQuestionBy(defaultUser);
        question.delete(defaultUser);

        assertThat(question.isDeleted()).isEqualTo(true);
    }
}