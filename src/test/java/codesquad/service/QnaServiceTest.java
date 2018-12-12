package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
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

import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    private static final Logger log = getLogger(QnaServiceTest.class);
    public static final long VALID_QUESTION_ID = 1L;
    public static final long INVALID_QUESTION_ID = 100L;
    public static final long VALID_USER_ID = 1L;
    public static final long INVALID_USER_ID = 2L;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    User user;
    User fakeUser;
    Question question;
    Question modifiedQuestion;

    @Before
    public void setUp() throws Exception {
        user = new User("brad903", "1234", "Brad", "brad903@naver.com");
        user.setId(VALID_USER_ID);
        fakeUser = new User("leejh903", "1234", "브래드", "leejh903@gmail.com");
        fakeUser.setId(INVALID_USER_ID);
        question = new Question("제목 테스트", "내용 테스트 - 코드스쿼드 qna-atdd step2 진행중입니다");
        question.writeBy(user);
        question.setId(VALID_QUESTION_ID);
        modifiedQuestion = new Question("업데이트된 제목", "업데이트된 내용입니다");
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
    }

    @Test
    public void update_succeed() {
        Question updatedQuestion = qnaService.update(user, question.getId(), modifiedQuestion);
        softly.assertThat(updatedQuestion.getContents()).isEqualTo(modifiedQuestion.getContents());
        softly.assertThat(updatedQuestion.getTitle()).isEqualTo(modifiedQuestion.getTitle());
    }

    @Test(expected = EntityNotFoundException.class)
    public void update_cannot_found_question() {
        Question updatedQuestion = qnaService.update(user, INVALID_QUESTION_ID, modifiedQuestion);
    }

    @Test
    public void delete_succeed() throws CannotDeleteException {
        qnaService.deleteQuestion(user, VALID_QUESTION_ID);
        softly.assertThat(question.isDeleted()).isEqualTo(true);
    }

    @Test(expected = EntityNotFoundException.class)
    public void delete_cannot_found_question() throws CannotDeleteException {
        qnaService.deleteQuestion(user, INVALID_QUESTION_ID);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_same_writer() throws CannotDeleteException {
        qnaService.deleteQuestion(fakeUser, VALID_QUESTION_ID);
    }


}