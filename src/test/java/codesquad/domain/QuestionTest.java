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


    // 질문한 사람과 로그인한 사람이 다른 경우
    @Test(expected = UnAuthorizedException.class)
    public void 질문삭제_타인의글() {
        Question question = saveQuestionBy(defaultUser);
        question.delete(otherUser);

        assertThat(question.isDeleted()).isEqualTo(false);
    }

    // 질문한 사람과 로그인한 사람이 같으면서 답변이 없는 경우
    @Test
    public void 질문삭제_자신의글_NO댓글() {
        Question question = saveQuestionBy(defaultUser);
        question.delete(defaultUser);

        assertThat(question.isDeleted()).isEqualTo(true);
    }

    // 질문한 사람과 로그인한 사람이 같으면서 답변의 글쓴이도 같은 경우
    @Test
    public void 질문삭제_자신의글_Only자신의댓글() {
        Question question = saveQuestionBy(defaultUser);
        Answer answer = new Answer(defaultUser, "contents");
        question.addAnswer(answer);

        question.delete(defaultUser);
        assertThat(question.isDeleted()).isEqualTo(true);
    }

    // 질문한 사람과 로그인한 사람이 같으면서 답변의 글쓴이가 다른 경우
    @Test(expected = UnAuthorizedException.class)
    public void 질문삭제_자신의글_타인댓글포함() {
        Question question = saveQuestionBy(defaultUser);
        Answer answer = new Answer(otherUser, "contents");
        question.addAnswer(answer);

        question.delete(defaultUser);
        assertThat(question.isDeleted()).isEqualTo(false);
    }
}