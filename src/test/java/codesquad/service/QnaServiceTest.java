package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static codesquad.domain.AnswerTest.ANSWER;
import static codesquad.domain.QuestionTest.*;
import static codesquad.domain.UserTest.BRAD;
import static codesquad.domain.UserTest.JUNGHYUN;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    private static final Logger log = getLogger(QnaServiceTest.class);
    public static final long WRONG_QUESTION_ID = 100L;
    public static final String UPDATED_CONTENTS = "업데이트 답변 콘텐츠";

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void setUp() throws Exception {
        when(questionRepository.findById(QUESTION.getId())).thenReturn(Optional.of(QUESTION));
        when(answerRepository.findById(ANSWER.getId())).thenReturn(Optional.of(ANSWER));
    }

    @Test
    public void update_succeed() {
        Question newQuestion = qnaService.updateQuestion(BRAD, QUESTION.getId(), UPDATED_QUESTION);
        softly.assertThat(newQuestion.getContents()).isEqualTo(UPDATED_QUESTION.getContents());
        softly.assertThat(newQuestion.getTitle()).isEqualTo(UPDATED_QUESTION.getTitle());
    }

    @Test(expected = EntityNotFoundException.class)
    public void update_cannot_found_question() {
        qnaService.updateQuestion(BRAD, WRONG_QUESTION_ID, UPDATED_QUESTION);
    }

    @Test
    public void delete_succeed() throws CannotDeleteException {
        qnaService.deleteQuestion(BRAD, QUESTION.getId());
        softly.assertThat(QUESTION.isDeleted()).isEqualTo(true);
    }

    @Test(expected = EntityNotFoundException.class)
    public void delete_cannot_found_question() throws CannotDeleteException {
        qnaService.deleteQuestion(BRAD, WRONG_QUESTION_ID);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_same_writer() throws CannotDeleteException {
        qnaService.deleteQuestion(JUNGHYUN, QUESTION.getId());
    }

    @Test
    public void update_answer() {
        Answer updatedAnswer = qnaService.updateAnswer(BRAD, ANSWER.getId(), UPDATED_CONTENTS);
        softly.assertThat(updatedAnswer.isSameContents(UPDATED_CONTENTS));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_answer_other_user() {
        qnaService.updateAnswer(JUNGHYUN, ANSWER.getId(), UPDATED_CONTENTS);
    }
}