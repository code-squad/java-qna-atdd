package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.NotFoundException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by hoon on 2018. 2. 6..
 */
@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest {

    @InjectMocks
    private QnaService qnaService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    private Question question;
    private User user;
    private User wrongUser;

    @Before
    public void setup() {
        question = new Question("title", "contents");
        user = new User(0, "test", "test", "test", "test");
        wrongUser = new User(1,"test2", "test2", "test2", "test2");
        question.writeBy(user);
    }

    @Test
    public void update() {
        when((questionRepository.findOne(3L))).thenReturn(question);

        qnaService.updateQuestion(3L, user, new QuestionDto("abc", "abc"));

        assertThat(qnaService.findById(3L).getTitle(), is("abc"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_질문한_사람과_로그인한_사람이_다른_경우() {
        when((questionRepository.findOne(3L))).thenReturn(question);

        qnaService.updateQuestion(3L, wrongUser, new QuestionDto("abc", "abc"));
    }


    @Test
    public void delete() {
        when((questionRepository.findOne(3L))).thenReturn(question);

        qnaService.deleteQuestion(user, 3L);

        assertTrue(qnaService.findById(3L).isDeleted());
    }

    @Test(expected = NotFoundException.class)
    public void delete_질문이_존재하지_않는_경우() {
        when((questionRepository.findOne(1L))).thenReturn(null);
        qnaService.deleteQuestion(user, 1L);
    }

    @Test
    public void delete_질문한_사람과_로그인한_사람이_같으면서_답변이_없는_경우() {
        Question question = new Question("test", "test");
        question.writeBy(user);

        when((questionRepository.findOne(0L))).thenReturn(question);

        qnaService.deleteQuestion(user, 0L);

        Question deletedQuestion = qnaService.findById(0L);
        assertTrue(deletedQuestion.isDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_질문한_사람과_로그인한_사람이_같으면서_답변의_글쓴이가_다른_경우() {
        Question question = new Question("test", "test");
        question.writeBy(user);

        question.addAnswer(new Answer(wrongUser, "1"));
        question.addAnswer(new Answer(wrongUser, "2"));

        when((questionRepository.findOne(0L))).thenReturn(question);

        qnaService.deleteQuestion(user, 0L);
    }

    @Test
    public void delete_질문한_사람과_로그인한_사람이_같으면서_답변의_글쓴이도_같은_경우() {
        Question question = new Question("test", "test");
        question.writeBy(user);

        question.addAnswer(new Answer(user, "1"));
        question.addAnswer(new Answer(user, "2"));

        when((questionRepository.findOne(0L))).thenReturn(question);

        qnaService.deleteQuestion(user, 0L);

        Question deletedQuestion = qnaService.findById(0L);
        assertTrue(deletedQuestion.isDeleted());
        assertTrue(deletedQuestion.getAnswers().isAllDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void CannotDeleteException_테스트() {
        when((questionRepository.findOne(3L))).thenReturn(question);

        qnaService.deleteQuestion(wrongUser, 3L);
    }
}
