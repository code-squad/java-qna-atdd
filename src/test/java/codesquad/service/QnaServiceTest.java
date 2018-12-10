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
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;

    public static User testUser = new User(1, "tester", "test", "tester", "test@test.com");
    public static User secondUser = new User(2, "tester2", "test2", "tester2", "test@test.com");
    public static Question original = new Question("title", "contents");
    public static Question updateQuestion = new Question("title2", "contents2");

    @Before
    public void setUp() throws Exception {
        original.writeBy(testUser);
        updateQuestion.writeBy(testUser);
        original.setId(1);
        updateQuestion.setId(1);
    }

    @Test
    public void create() {
        Question createQuestion = new Question("title", "contents");
        createQuestion.writeBy(testUser);
        when(questionRepository.save(createQuestion)).thenReturn(createQuestion);

        Question result = qnaService.create(testUser, new Question("title", "contents"));
        softly.assertThat(result.getTitle()).isEqualTo(createQuestion.getTitle());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_match_user_id() {
        when(qnaService.update(testUser, 1, new Question("title2", "contents"))).thenReturn(updateQuestion);

        qnaService.update(User.GUEST_USER, 1, new Question("title2", "contents2"));
    }

    @Test
    public void update_by_owner() {
        when(questionRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(original));
        when(qnaService.update(testUser, 1, new Question("title2", "contents"))).thenReturn(updateQuestion);

        Question update = qnaService.update(testUser, 1, new Question("title2", "contents2"));
        softly.assertThat(update.getWriter()).isEqualTo(testUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_match_user_id() throws CannotDeleteException {
        when(questionRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(original));

        qnaService.deleteQuestion(secondUser, 1);
    }

    @Test
    public void delete_by_owner() throws CannotDeleteException {
        when(questionRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(original));

        qnaService.deleteQuestion(testUser, 1);

    }
}