package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Answers;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    private User user;
    private User user2;
    private User user3;
    private Question question;

    @Before
    public void setUp() {
        user = new User(1L, "test", "test123", "test", "test@test.com");
        user2 = new User(2L, "qqqq", "test123", "test", "test@test.com");
        user3 = new User(3L, "wwww", "test123", "test", "test@test.com");
        question = new Question("title", "contents");
    }

    @Test
    public void create() {
        when(questionRepository.save(question)).thenReturn(question);

        Question questionReturned = qnaService.create(user, question);
        assertThat(questionReturned).isNotNull();
    }

    @Test
    public void update() throws IllegalAccessException {
        question.writeBy(user2);
        when(questionRepository.save(question)).thenReturn(question);

        qnaService.create(user2, question);
        Question newQuestion = new Question("hello", "world");
        newQuestion.writeBy(user2);

        when(questionRepository.save(newQuestion)).thenReturn(newQuestion);
        when(questionRepository.findOne(2L)).thenReturn(question);

        Question updatedQuestion = qnaService.update(user2, 2, newQuestion);

        assertThat(updatedQuestion.getTitle()).isEqualTo(newQuestion.getTitle());
        assertThat(updatedQuestion.getContents()).isEqualTo(newQuestion.getContents());
    }

    @Test
    public void delete() throws CannotDeleteException {
        question.writeBy(user3);
        when(questionRepository.findOne(3L)).thenReturn(question);
        when(questionRepository.save(question)).thenReturn(question);
        qnaService.create(user3, question);
        qnaService.deleteQuestion(user3, 3);

        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_NotMyQuestion() throws CannotDeleteException {
        question.writeBy(user3);
        when(questionRepository.findOne(3L)).thenReturn(question);
        when(questionRepository.save(question)).thenReturn(question);
        qnaService.create(user3, question);
        qnaService.deleteQuestion(user2, 3);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_NotMyAnswerInQuestion() throws CannotDeleteException {
        question.writeBy(user3);
        question.addAnswer(new Answer(user, "hello hello"));

        when(questionRepository.findOne(3L)).thenReturn(question);
        when(questionRepository.save(question)).thenReturn(question);
        qnaService.create(user3, question);
        qnaService.deleteQuestion(user3, 3);
    }

    @Test
    public void delete_answer() throws CannotDeleteException {
        Answer answer = new Answer(1L, user, question, "test12345");
        when(answerRepository.findOne(1L)).thenReturn(answer);
        qnaService.deleteAnswer(user, answer.getId());
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_NotMyAnswer() throws CannotDeleteException {
        Answer answer = new Answer(1L, user, question, "test12345");
        when(answerRepository.findOne(1L)).thenReturn(answer);
        qnaService.deleteAnswer(user2, answer.getId());
    }
}
