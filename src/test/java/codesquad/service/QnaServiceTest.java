package codesquad.service;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import support.test.BaseTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Mock
    private AnswerRepository answerRepository;

    private static final Logger logger = getLogger(QnaServiceTest.class);

    private Question standardQuestion = QuestionFixture.TEST_QUESTION;

    private Question nonStandardQuestion = QuestionFixture.TEST_QUESTION_SANJIGI;

    @Before
    public void setUp() {
        standardQuestion.addAnswer(AnswerFixture.TEST_ANSWER);
        nonStandardQuestion.addAnswer(AnswerFixture.TEST_ANSWER);
        nonStandardQuestion.addAnswer(AnswerFixture.TEST_ANSWER_SANJIGI);
    }

    @Test
    public void updateQuestion_성공_Test() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(standardQuestion));
        Question updatedQuestion = qnaService.update(UserFixture.JAVAJIGI_USER, 1L, QuestionFixture.UPDATE_QUESTION);
        softly.assertThat(QuestionFixture.UPDATE_QUESTION).isEqualTo(updatedQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateQuestion_실패_작성자_수정자_불일치() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(standardQuestion));
        qnaService.update(UserFixture.SANJIGI_USER, 1L, QuestionFixture.UPDATE_QUESTION);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_실패_작성자_삭제자_불일치() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(standardQuestion));
        qnaService.deleteQuestion(UserFixture.SANJIGI_USER, 1L);
    }

    @Test
    public void deleteQuestion_성공() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(standardQuestion));
        qnaService.deleteQuestion(UserFixture.JAVAJIGI_USER, 1L);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_실패_질문자와_답변자_불일치() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(nonStandardQuestion));
        qnaService.deleteQuestion(UserFixture.JAVAJIGI_USER, 1L);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAnswer_실패_본인X() {
        when(questionRepository.getOne(1L)).thenReturn(standardQuestion);
        qnaService.deleteAnswer(UserFixture.SANJIGI_USER, 1L, AnswerFixture.TEST_ANSWER);
    }

    @Test
    public void deleteAnswer_성공() {
        when(questionRepository.getOne(1L)).thenReturn(standardQuestion);
        qnaService.deleteAnswer(UserFixture.JAVAJIGI_USER, 1L, AnswerFixture.TEST_ANSWER);
    }

    @Test
    public void addAnswer_성공() {
        when(questionRepository.getOne(1L)).thenReturn(standardQuestion);
        qnaService.addAnswer(1L, AnswerFixture.TEST_ANSWER);
    }
}
