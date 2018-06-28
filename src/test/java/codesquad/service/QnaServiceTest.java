package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.domain.DeleteHistories;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void delete() throws CannotDeleteException {
        long id = 3L;
        User learner = new User(id, "learner", "password", "taewon", "email@email.com");
        Question question = new Question(id, "서비스 테스트는 목을 이용하나요?", "mockito를 쓰면 될까요?");
        question.writeBy(learner);

        Optional<Question> maybeQuestion = Optional.of(question);
        when(questionRepository.findById(id)).thenReturn(maybeQuestion);

        DeleteHistories histories = qnaService.delete(learner, id);
        assertThat(histories.size(), is(1));
    }
}
