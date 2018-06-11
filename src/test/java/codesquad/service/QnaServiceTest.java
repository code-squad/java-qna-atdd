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

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    @Mock
    private QuestionRepository questionRepo;

    @Mock
    private AnswerRepository answerRepo;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;

    private Question question;
    private Question updateQuestion;

    private User user;
    private User otherUser;

    @Before
    public void setUp() throws Exception {
        user = new User("colin", "password", "colin", "colin@codesquad.kr");
        otherUser = new User("jinbro", "password", "jinbro", "jinbro@codesquad.kr");
        question = new Question("test", "testing!");
        question.writeBy(user);
        updateQuestion = new Question("modify test", "modify");
        updateQuestion.writeBy(user);
    }

    @Test
    public void create() {
        Question newQuestion = new Question("new!", "new new contents");
        qnaService.create(user, newQuestion.toQuestionDto());
        verify(questionRepo, times(1)).save(newQuestion);
    }

    @Test(expected = EntityNotFoundException.class)
    public void read_not_exist() {
        when(questionRepo.findById(anyLong())).thenReturn(Optional.empty());
        qnaService.findQuestionById(anyLong());
    }

    @Test(expected = EntityNotFoundException.class)
    public void read_fail_not_exist() {
        qnaService.findQuestionById(10000L);
    }

    @Test
    public void findAll() {
        when(questionRepo.findByDeleted(false)).thenReturn(Collections.singletonList(question));
        assertEquals(1, qnaService.findAll().size());
    }

    @Test
    public void update() {
        updateQuestion.writeBy(user);
        when(questionRepo.findById(anyLong())).thenReturn(Optional.of(question));
        qnaService.updateQuestion(user, anyLong(), updateQuestion.toQuestionDto());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_fail_not_math_writer() {
        when(questionRepo.findById(anyLong())).thenReturn(Optional.of(question));
        qnaService.updateQuestion(otherUser, anyLong(), updateQuestion.toQuestionDto());
    }

    @Test
    public void delete() throws Exception {
        when(questionRepo.findById(anyLong())).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(user, anyLong());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_fail_not_owner() throws Exception {
        when(questionRepo.findById(anyLong())).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(otherUser, anyLong());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_fail_not_answer_owner() throws Exception {
        question.addAnswer(new Answer(otherUser, "test contents"));
        when(questionRepo.findById(anyLong())).thenReturn(Optional.of(question));
        qnaService.deleteQuestion(user, anyLong());
    }
}