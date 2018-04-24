package codesquad.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import codesquad.domain.CannotDeleteException;
import codesquad.domain.QuestionNotFoundException;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService ;

    @Test
    public void 삭제되지_않은_질문_리스트를_조회한다() {
        //given
        ArgumentCaptor<Boolean> parameter = ArgumentCaptor.forClass(Boolean.class);

        //when
        qnaService.findAll();

        //then
        verify(questionRepository).findByDeleted(parameter.capture());
        Assertions.assertThat(parameter.getValue()).isFalse();
    }

    @Test
    public void 아이디를_통해_리뷰_하나를_조회한다() {
        //given
        long questionId = 1L;
        when(questionRepository.findOne(questionId)).thenReturn(Optional.of(new Question()));

        //when
        qnaService.findById(questionId);

        //then
        verify(questionRepository).findOne(questionId);
    }

    @Test
    public void 질문을_작성할_수_있다() {
        //given
        ArgumentCaptor<Question> parameter = ArgumentCaptor.forClass(Question.class);
        User user = createUserIdOf(1L);
        QuestionDto question = new QuestionDto("dummy_title", "dummy_contents");

        //when
        qnaService.create(user, question);

        //then
        verify(questionRepository).save(parameter.capture());
        Assertions.assertThat(parameter.getValue().getWriter().getName()).isEqualTo(user.getName());
    }

    @Test(expected = QuestionNotFoundException.class)
    public void 존재하지_않는_질문을_삭제할_수_없다() throws CannotDeleteException {
        //given
        long wrongQuestionId = 999L;
        User user = createUserIdOf(1L);

        when(questionRepository.findOne(wrongQuestionId)).thenReturn(Optional.empty());

        //when
        qnaService.deleteQuestion(user, wrongQuestionId);

        //then
        Assertions.fail("존재하지 않는 질문을 삭제 시도 하면 예외가 발생해야 한다.");
    }

    private User createUserIdOf(long id) {
        return new User(id, "foo", "hunter1", "name", "foo@bar.com");
    }
}