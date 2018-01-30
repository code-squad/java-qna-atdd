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
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    private User user;
    private User user2;
    private User user3;
    private Question question;

    @Before
    public void setUp(){
        user = new User("test", "test123", "test", "test@test.com");
        user2 = new User("qqqq", "test123", "test", "test@test.com");
        user3 = new User("wwww", "test123", "test", "test@test.com");
        question = new Question("title", "contents");
    }

    @Test
    public void create(){
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

        assertThat(qnaService.deleteQuestion(user3, 3)).isTrue();
    }
}
