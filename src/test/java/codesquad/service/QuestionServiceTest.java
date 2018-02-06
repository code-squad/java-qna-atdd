package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
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

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

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
    public void UnAuthorizedException_테스트() {
        when((questionRepository.findOne(3L))).thenReturn(question);

        qnaService.updateQuestion(3L, wrongUser, new QuestionDto("abc", "abc"));
    }


    @Test
    public void delete() {
        when((questionRepository.findOne(3L))).thenReturn(question);

        qnaService.deleteQuestion(user, qnaService.findById(3L));

        assertTrue(qnaService.findById(3L).isDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void CannotDeleteException_테스트() {
        when((questionRepository.findOne(3L))).thenReturn(question);

        qnaService.deleteQuestion(wrongUser, qnaService.findById(3L));
    }
}
