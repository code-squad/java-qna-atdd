package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {
    private static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    private static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    private Question question;

    @Before
    public void setup() {
        question = new Question("질문있어요.", "true가 트루?");
    }

    @Test
    public void isOwner() {
        question.writeBy(JAVAJIGI);
        assertThat(question.isOwner(JAVAJIGI)).isTrue();
    }

    @Test
    public void isNotOwner() {
        question.writeBy(JAVAJIGI);
        assertThat(question.isOwner(SANJIGI)).isFalse();
    }

    @Test
    public void addAnswer() {
        question.addAnswer(new Answer(JAVAJIGI, "그럼 뭐겠냐?"));
        question.addAnswer(new Answer(SANJIGI, "까칠하시네"));
        assertThat(question.getAnswerCount()).isEqualTo(2);
    }

    @Test
    public void update() {
        question.writeBy(JAVAJIGI);
        String updateTitle = "질문있습니다.";
        String updateContents = "true는 어떨 때 사용하나요?";
        Question updated = new Question(updateTitle, updateContents);
        question.update(JAVAJIGI, updated);
        assertThat(question.getTitle()).isEqualTo(updateTitle);
        assertThat(question.getContents()).isEqualTo(updateContents);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        question.writeBy(JAVAJIGI);
        String updateTitle = "질문있습니다.";
        String updateContents = "true는 어떨 때 사용하나요?";
        Question updated = new Question(updateTitle, updateContents);
        question.update(SANJIGI, updated);
    }

    @Test
    public void delete() throws Exception {
        question.writeBy(JAVAJIGI);
        question.delete(JAVAJIGI);
        assertThat(question.isDeleted()).isTrue();
        assertThat(question.getAnswerCount()).isEqualTo(0);
    }

    @Test
    public void delete_with_answer() throws Exception {
        question.writeBy(JAVAJIGI);
        question.addAnswer(new Answer(JAVAJIGI, "답변"));
        question.deleteWithAnswers(JAVAJIGI);
        assertThat(question.isDeleted()).isTrue();
        assertThat(question.getAnswerCount()).isEqualTo(0);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_with_other_answer() throws Exception {
        question.writeBy(JAVAJIGI);
        question.addAnswer(new Answer(SANJIGI, "답변"));
        question.deleteWithAnswers(JAVAJIGI);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws Exception {
        question.writeBy(JAVAJIGI);
        question.delete(SANJIGI);
    }
}
