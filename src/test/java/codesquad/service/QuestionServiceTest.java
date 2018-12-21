package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static codesquad.domain.AnswerTest.newAnswer;
import static codesquad.domain.QuestionTest.newQuestion;
import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest extends BaseTest {
    private static final Logger log = LogManager.getLogger(QuestionServiceTest.class);
    private static final Answer ANSWER = newAnswer("댓글입니다.");
    private static Question question = newQuestion(JAVAJIGI);
    private static Question updateQuestion = newQuestion("안녕하세여", "나는 바뀐 질문이에요");
    
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;


    @Before
    public void setUp() {
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        when(answerRepository.findById(ANSWER.getId())).thenReturn(Optional.of(ANSWER));

    }

    @Test
    public void update_question() {
        Question newQuestion = qnaService.update(question.getWriter(), question.getId(), updateQuestion);
        softly.assertThat(newQuestion).isEqualTo(question);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_no_question() {
        Question newQuestion = qnaService.update(SANJIGI, question.getId(), updateQuestion);
        softly.assertThat(newQuestion).isEqualTo(question);
    }

    @Test
    public void delete() throws Exception {
        qnaService.deleteQuestion(question.getWriter(), question.getId());
        softly.assertThat(question.isDeleted()).isTrue();
    }


    @Test(expected = UnAuthorizedException.class)
    public void delete_no() throws Exception {
        qnaService.deleteQuestion(SANJIGI, 100L);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_no_other_user() throws Exception {
        qnaService.deleteQuestion(SANJIGI, question.getId());
    }

    @Test
    public void update_answer() {
        Answer newAnswer = qnaService.updateAnswer(ANSWER.getWriter(), ANSWER.getId(), "바뀐댓글입니다.");
        softly.assertThat(ANSWER.getContents()).isEqualTo(newAnswer.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_no_answer_other_user() throws Exception {
        qnaService.updateAnswer(SANJIGI, ANSWER.getId(), "바뀐댓글입니다.");

    }

    @Test
    public void deleted_answer() throws Exception {
        qnaService.deleteAnswer(JAVAJIGI, ANSWER.getId());

    }

    @Test(expected = CannotDeleteException.class)
    public void deleted_other_answer() throws Exception {
        qnaService.deleteAnswer(SANJIGI, ANSWER.getId());

    }
}


