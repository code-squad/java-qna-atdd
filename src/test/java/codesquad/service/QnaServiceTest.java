package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static codesquad.domain.AnswerTest.*;
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
    DeleteHistory deleteHistory;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void setUp() throws Exception {
        when(questionRepository.findById(QUESTION.getId())).thenReturn(Optional.of(QUESTION));
        when(answerRepository.findById(ANSWER.getId())).thenReturn(Optional.of(ANSWER));

        deleteHistory = new DeleteHistory(ContentType.ANSWER, ANSWER.getId(), ANSWER.getWriter());
        when(deleteHistoryService.save(deleteHistory)).thenReturn(deleteHistory);
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

    @Test
    public void deleteAnswer_성공() throws CannotDeleteException {
        Answer answer = qnaService.deleteAnswer(BRAD, ANSWER.getId());
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAnswer_다른유저() throws CannotDeleteException {
        Answer answer = qnaService.deleteAnswer(JUNGHYUN, ANSWER.getId());
    }

    @Test
    public void 질문삭제시_답변삭제_성공() {
        List<Answer> answersForTest = new ArrayList(Arrays.asList(ANSWER, ANSWER2, ANSWER4, ANSWER5, ANSWER6, ANSWER7));
        ReflectionTestUtils.setField(QUESTION, "answers", answersForTest);
        qnaService.deleteQuestion(BRAD, QUESTION.getId());

        softly.assertThat(QUESTION.isDeleted()).isTrue();
        for (Answer answer : answersForTest) {
            softly.assertThat(answer.isDeleted()).isTrue();
        }
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문삭제시_답변삭제_다른유저답변_존재() {
        List<Answer> answersForTest = new ArrayList(Arrays.asList(ANSWER, ANSWER4, ANSWER5, ANSWER3, ANSWER7, ANSWER6));
        ReflectionTestUtils.setField(QUESTION, "answers", answersForTest);
        qnaService.deleteQuestion(BRAD, QUESTION.getId());

        // 롤백 테스트
        for (Answer answer : answersForTest) {
            softly.assertThat(answer.isDeleted()).isFalse();
        }
        softly.assertThat(QUESTION.isDeleted()).isFalse();
    }
}