package codesquad.service;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
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

    @InjectMocks
    private QnaService qnaService;

    public static User user = new User(1, "finn", "test", "choi", "choi@naver.com");
    public static User other = new User(2, "pobi", "test", "park", "park@naver.com");
    public static Question question = new Question("title", "contents");
    public static Question updatedQuestion = new Question("updatedTitle", "updatedContents");

    @Before
    public void setUp() throws Exception {
        question.writeBy(user);
        question.setId(1);
        updatedQuestion.writeBy(user);
        updatedQuestion.setId(1);
    }

    @Test
    public void create() {
        Question createdQuestion = new Question("title", "contents");
        createdQuestion.writeBy(user);
        when(questionRepository.save(createdQuestion)).thenReturn(createdQuestion);

        Question result = qnaService.create(user, new Question("title", "contents"));
        softly.assertThat(result).isEqualTo(createdQuestion);
    }

    @Test
    public void update() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(qnaService.update(user, 1, new Question("updatedTitle", "updatedContents"))).thenReturn(updatedQuestion);

        qnaService.update(user, 1, new Question("updatedTitle", "updatedContents"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        when(qnaService.update(user, 1, new Question("updatedTitle", "updatedContents"))).thenReturn(updatedQuestion);

        qnaService.update(User.GUEST_USER, 1, new Question("updatedTitle", "updatedContents"));
    }

    @Test
    public void delete() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.delete(user, 1);
    }

    @Test(expected = Exception.class)
    public void delete_no_login() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.delete(null, 1);
    }

    @Test(expected = Exception.class)
    public void delete_not_owner() throws Exception {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        qnaService.delete(other, 1);
    }
}
