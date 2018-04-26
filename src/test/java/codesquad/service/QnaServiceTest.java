package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnarService;

    @Test
    public void question_success() throws Exception {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        qnarService.create(user,new Question("제목입니다.", "내용물입니다."));
    }

    @Test
    public void question_delete_sucess() throws UnAuthenticationException, CannotDeleteException {

        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        Question question = new Question("제목입니다.", "내용물입니다.");
        question.writeBy(user);

        when(questionRepository.findOne(0l)).thenReturn(question);
        qnarService.deleteQuestion(user,0l);

    }

    @Test(expected = UnAuthenticationException.class)
    public void question_delete_fail() throws UnAuthenticationException, CannotDeleteException {

        User originalUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        Question question = new Question("제목입니다.", "내용물입니다.");
        question.writeBy(originalUser);
        User testUser = new User("gangjigi", "password123", "name", "javajigi@slipp.net");

        when(questionRepository.findOne(0l)).thenReturn(question);
        qnarService.deleteQuestion(testUser,0l);

    }

}
