package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@Transactional
public class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    private User user1;
    private User user2;

    @Before
    public void setUp() throws Exception {
        user1 = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
        user2 = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    }

    @Test
    public void create() {
        User user1 = getTestUser();
        Question question = getTestQuestion();
        question.writeBy(user1);
        when(questionRepository.save(question)).thenReturn(question);
        qnaService.create(user1, question);
        assertThat(question.getTitle()).isEqualTo("title");

    }

    @Test
    public void update() {
        Question question = getTestQuestion();
        when(questionRepository.findOne(1L)).thenReturn(question);
        Question updateQuestion =qnaService.update(getTestUser(), 1, new Question("update", "update"));
        assertThat(updateQuestion.getTitle()).isEqualTo("update");
    }

    @Test (expected = UnAuthorizedException.class)
    public void update_unauthorized() {
        Question question = getTestQuestion();
        when(questionRepository.findOne(1L)).thenReturn(question);
        qnaService.update(getWrongUser(), 1, new Question("update", "update"));
    }

    @Test
    public void delete() throws CannotDeleteException {
        Question question = getTestQuestion();
        when(questionRepository.findOne(1L)).thenReturn(question);
        qnaService.deleteQuestion(getTestUser(), 1L);
        assertThat(question.isDeleted()).isTrue();

    }

    @Test (expected = UnAuthorizedException.class)
    public void delete_unauthorized() throws CannotDeleteException {
        Question question = getTestQuestion();
        when(questionRepository.findOne(1L)).thenReturn(question);
        qnaService.deleteQuestion(getWrongUser(), 1L);
    }

    @Test
    public void findById() {
        Question question = getTestQuestion();
        when(questionRepository.findOne(1L)).thenReturn(question);
        Question result = qnaService.findById(1L);
        assertThat(result.getTitle()).isEqualTo("title");
    }

    @Test
    public void addAnswer() {
        Question question = getTestQuestion();
        when(questionRepository.findOne(1L)).thenReturn(question);
        qnaService.addAnswer(user1, 1L, "answer");
        question = qnaService.findById(1L);
        assertThat(question.getAnswer(0).getContents()).isEqualTo("answer");
    }

    private Question getTestQuestion() {
        Question question = new Question("title", "content");
        question.writeBy(getTestUser());
        return question;
    }

    private User getTestUser() {
        return  new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    }

    private User getWrongUser() {
        return new User(2L,"sanjigi", "password", "name", "javajigi@slipp.net");
    }

}
