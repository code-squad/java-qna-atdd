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

    static User testUser = new User(1, "tester", "test", "tester", "test@test.com");
    static User secondUser = new User(2, "tester2", "test2", "tester2", "test@test.com");

    static Question original = new Question("title", "contents");
    static Question updateQuestion = new Question("title2", "contents2");

    static Answer answer1 = new Answer(1L, testUser, original, "contents1");
    static Answer answer2 = new Answer(2L, secondUser, original, "content2");

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

        Question update = qnaService.update(testUser, 1, new Question("title2", "contents2"));
        softly.assertThat(update.getWriter()).isEqualTo(testUser);
    }

    @Test
    public void q_delete_success() throws CannotDeleteException {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(original));
        original.addAnswer(answer1);

        qnaService.deleteQuestion(testUser, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void q_delete_by_other() throws CannotDeleteException {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(original));
        original.addAnswer(answer1);

        qnaService.deleteQuestion(secondUser, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void q_delete_cannot_delete_answer() throws CannotDeleteException {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(original));
        original.addAnswer(answer2);

        qnaService.deleteQuestion(testUser, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void q_delete_not_exist() throws CannotDeleteException {
//        when(questionRepository.findById(1L)).thenReturn(Optional.of(original));
        original.addAnswer(answer1);

        qnaService.deleteQuestion(testUser, 3L);
    }

    @Test
    public void add_answer() {
        String contents = "contents";
        Answer newAnswer = new Answer(testUser, contents);
        newAnswer.toQuestion(original);
        when(questionRepository.findById(1L)).thenReturn(Optional.of(original));

        Answer result = qnaService.addAnswer(testUser, 1L, "contents");
        softly.assertThat(result.getWriter()).isEqualTo(testUser);
    }

    @Test
    public void delete_answer_success() throws CannotDeleteException {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer1));

        Answer result = qnaService.deleteAnswer(testUser, 1L);
        softly.assertThat(result.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_by_other() throws CannotDeleteException {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer1));

        qnaService.deleteAnswer(secondUser, 1L);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_answer_not_exist() throws CannotDeleteException {
//        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer1));

        qnaService.deleteAnswer(testUser, 3L);
    }
}