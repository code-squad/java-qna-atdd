package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import support.test.BaseTest;

import java.util.Optional;

import static codesquad.domain.AnswerTest.RED_ANSWER_QUESTION;
import static codesquad.domain.QuestionTest.RED_QUESTION;
import static codesquad.domain.QuestionTest.UNHEE_QUESTION;
import static codesquad.domain.UserTest.RED;
import static codesquad.domain.UserTest.UNHEE;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest extends BaseTest {
    private static final Logger log = getLogger(QuestionServiceTest.class);

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void delete_question_no_answer_success() throws CannotDeleteException {
        Question question = new Question(RED, "title", "contents");
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(RED, question.getId());
        softly.assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isTrue();
    }

    @Test
    public void delete_question_same_writer() throws CannotDeleteException {
        Question question = UNHEE_QUESTION;
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(UNHEE, question.getId());
        softly.assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_different_writer_answer() throws CannotDeleteException {
        Question question = UNHEE_QUESTION;
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(RED, question.getId());
        softly.assertThat(questionRepository.findById(question.getId()).get().isDeleted()).isFalse();
    }

    @Test
    public void deleteAnswer() {
        Answer answer = RED_ANSWER_QUESTION;
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        qnaService.deleteAnswer(RED, answer.getId());
        softly.assertThat(answerRepository.findById(answer.getId()).get().isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAnswer_different_writer_answer() {
        Answer answer = RED_ANSWER_QUESTION;
        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));

        qnaService.deleteAnswer(UNHEE, answer.getId());
        softly.assertThat(answerRepository.findById(answer.getId()).get().isDeleted()).isFalse();
    }
}
