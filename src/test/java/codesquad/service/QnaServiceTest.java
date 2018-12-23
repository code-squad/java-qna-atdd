package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.AnswerRepository;
import codesquad.domain.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.AcceptanceTest;

import java.util.Optional;

import static codesquad.domain.AnswerTest.DELETE_ANSWER;
import static codesquad.domain.AnswerTest.NEW_ANSWER;
import static codesquad.domain.QuestionTest.QUESTION_FOR_DELETE;
import static codesquad.domain.QuestionTest.QUESTION;
import static codesquad.domain.QuestionTest.QUESTION_FOR_UPDATE;
import static codesquad.domain.QuestionTest.QUESTION_FOR_UPDATE_OTHER_USER;
import static codesquad.domain.UserTest.CHOI;
import static codesquad.domain.UserTest.SANJIGI;
import static codesquad.domain.UserTest.SING;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends AcceptanceTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;



    @Before
    public void setUp() throws Exception {
        when(questionRepository.findById((long)1)).thenReturn(Optional.of(QUESTION));
        when(questionRepository.findById((long)4)).thenReturn(Optional.of(QUESTION_FOR_DELETE));
        when(answerRepository.save(NEW_ANSWER)).thenReturn(NEW_ANSWER);
        when(answerRepository.findById(1L)).thenReturn(Optional.of(NEW_ANSWER));
        when(answerRepository.findById(3L)).thenReturn(Optional.of(DELETE_ANSWER));

    }

    @Test
    public void update() {
        qnaService.update(CHOI, 1, QUESTION_FOR_UPDATE);
        softly.assertThat(QUESTION.getTitle()).isEqualTo(QUESTION_FOR_UPDATE.getTitle());
        softly.assertThat(QUESTION.getContents()).isEqualTo(QUESTION_FOR_UPDATE.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateWithInvalidUser() {
        qnaService.update(SANJIGI, 1, QUESTION_FOR_UPDATE_OTHER_USER);
    }

    @Test
    public void deleteQuestion() throws CannotDeleteException {
        qnaService.deleteQuestion(CHOI, (long)4);
        softly.assertThat(QUESTION_FOR_DELETE.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestionWithInvalidUser() throws CannotDeleteException {
        qnaService.deleteQuestion(SING, (long)1);
    }

    @Test
    public void addAnswer(){
        softly.assertThat(qnaService.addAnswer(CHOI, 1L, NEW_ANSWER.getContents()).getContents())
                .isEqualTo(NEW_ANSWER.getContents());
    }

    @Test
    public void deleteAnswer() throws CannotDeleteException{
        qnaService.deleteAnswer(CHOI, 3L);
        softly.assertThat(DELETE_ANSWER.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAnswerWithInvalidUser() throws CannotDeleteException{
        qnaService.deleteAnswer(SING, 1L);
    }
}