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
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author sangsik.kim
 */
@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    @Mock
    QuestionRepository questionRepository;

    @InjectMocks
    QnaService qnaService;

    private User defaultUser;
    private User anotherUser;
    private Question defaultQuestion;

    @Before
    public void setup() {
        defaultUser = new User(0, "sangsik", "test", "김상식", "sangsik@test.com");
        anotherUser = new User(1, "sion", "test", "송시온", "sion@test.com");
        defaultQuestion = new Question(0, "테스트 제목", "테스트 내용", defaultUser);
    }

    @Test
    public void update_success() throws Exception {
        when(questionRepository.findById(defaultQuestion.getId())).thenReturn(Optional.of(defaultQuestion));

        Question editedQuestion = new Question("제목 수정", "내용 수정");
        Question updatedQuestion = qnaService.update(defaultUser, defaultQuestion.getId(), editedQuestion);

        assertThat(updatedQuestion, is(editedQuestion));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_fail_another_user_try() {
        when(questionRepository.findById(defaultQuestion.getId())).thenReturn(Optional.of(defaultQuestion));

        Question updatedQuestion = new Question("제목 수정", "내용 수정");

        qnaService.update(anotherUser, defaultQuestion.getId(), updatedQuestion);
    }

    @Test
    public void delete_success() {
        when(questionRepository.findById(defaultQuestion.getId())).thenReturn(Optional.of(defaultQuestion));

        qnaService.deleteQuestion(defaultUser, defaultQuestion.getId());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_fail_another_user_try() {
        when(questionRepository.findById(defaultQuestion.getId())).thenReturn(Optional.of(defaultQuestion));

        qnaService.deleteQuestion(anotherUser, defaultQuestion.getId());
    }
}
