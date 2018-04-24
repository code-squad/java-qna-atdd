package codesquad.domain;

import codesquad.dto.QuestionDto;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class QuestionTest {

    private User defaultUser;
    private User other;

    @Before
    public void setUp() {
        defaultUser = new User(1L, "javajigi", "test", "홍길동", "foo@bar.com");
        other = new User(2L, "sanjigi", "test", "김둘리", "a@b.com");
    }

    @Test(expected = CannotUpdateException.class)
    public void 타인의_질문을_수정_시도시_예외발생() throws CannotUpdateException {
        //given
        Question question = createQuestionBy(defaultUser);
        QuestionDto dto = new QuestionDto("newtitle", "newcontents");

        //when
        question.update(other, dto);

        //then
        Assertions.fail("타인의 질문을 수정하려 하면 예외가 발생해야 한다.");
    }

    @Test
    public void 자신의_질문을_수정_가능하다() throws CannotUpdateException {
        //given
        Question question = createQuestionBy(defaultUser);
        QuestionDto dto = new QuestionDto("newtitle", "newcontents");

        //when
        question.update(defaultUser, dto);

        //then
        Assertions.assertThat(question.getTitle()).isEqualTo(dto.getTitle());
        Assertions.assertThat(question.getContents()).isEqualTo(dto.getContents());
    }

    @Test(expected = CannotDeleteException.class)
    public void 타인의_질문_삭제_시도시_예외발생() throws CannotDeleteException {
        //given
        Question question = createQuestionBy(defaultUser);

        //when
        question.delete(other);

        //then
        Assertions.fail("타인의 질문을 삭제하려 하면 예외가 발생해야 한다.");
    }

    @Test
    public void 자신의_질문을_삭제할_수_있다() throws CannotDeleteException {
        //given
        Question question = createQuestionBy(defaultUser);
        Assertions.assertThat(question.isDeleted()).isFalse();

        //when
        question.delete(defaultUser);

        //then
        Assertions.assertThat(question.isDeleted()).isTrue();
    }

    @Test
    public void 질문은_답변들을_가진다() {
        //given
        Question question = createQuestionBy(defaultUser);
        Answer answer = new Answer(defaultUser, "this is my answer");

        Assertions.assertThat(question.getAnswers()).isEmpty();
        Assertions.assertThat(answer.getQuestion()).isNull();

        //when
        question.addAnswer(answer);

        //then
        Assertions.assertThat(question.getAnswers().size()).isEqualTo(1);
        Assertions.assertThat(answer.getQuestion()).isNotNull();
    }

    private Question createQuestionBy(User user) {
        Question question = new Question("mytitle", "mycontents");
        question.writeBy(user);
        return question;
    }
}