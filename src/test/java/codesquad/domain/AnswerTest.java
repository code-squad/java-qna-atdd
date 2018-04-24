package codesquad.domain;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class AnswerTest {

    private User defaultUser;
    private User other;

    @Before
    public void setUp() {
        defaultUser = new User(1L, "javajigi", "test", "홍길동", "foo@bar.com");
        other = new User(2L, "sanjigi", "test", "김둘리", "a@b.com");
    }

    @Test
    public void 답변을_삭제_상태로_변경하면_질문의_답변목록에서도_제외된다() throws CannotDeleteException {
        //given
        Question question = createQuestionBy(defaultUser);
        Answer answer = answerWithDefaultUser(question);

        //when
        answer.delete(defaultUser);

        //then
        Assertions.assertThat(question.getAnswers()).isEmpty();
        Assertions.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 타인의_답변을_삭제할_수_없다() throws CannotDeleteException {
        //given
        Question question = createQuestionBy(defaultUser);
        Answer answer = answerWithDefaultUser(question);

        //when
        answer.delete(other);

        //then
        Assertions.fail("타인의 질문을 삭제하려 하면 예외가 발생해야 한다.");
    }

    @Test
    public void 답변을_수정할_수_있다() throws CannotUpdateException {
        //given
        Question question = createQuestionBy(defaultUser);
        Answer answer = answerWithDefaultUser(question);

        String newAnswer = "updated answer";

        //when
        answer.update(defaultUser, newAnswer);

        //then
        Assertions.assertThat(answer.getContents()).isEqualTo(newAnswer);
    }

    @Test(expected = CannotUpdateException.class)
    public void 타인의_답변을_수정할_수_없다() throws CannotUpdateException {
        //given
        Question question = createQuestionBy(defaultUser);
        Answer answer = answerWithDefaultUser(question);

        //when
        answer.update(other, "new answer");

        //then
        Assertions.fail("타인의 질문을 수정하려 하면 예외가 발생해야 한다.");
    }

    private Question createQuestionBy(User user) {
        Question question = new Question("mytitle", "mycontents");
        question.writeBy(user);
        return question;
    }

    private Answer answerWithDefaultUser(Question question) {
        Answer answer = new Answer(defaultUser, "this is my answer");
        question.addAnswer(answer);
        return answer;
    }
}