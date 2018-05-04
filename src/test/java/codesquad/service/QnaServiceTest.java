package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import javax.annotation.Resource;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnarService;

    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");


    @Test
    public void question_success() throws Exception {
        qnarService.create(SANJIGI,new Question("제목입니다.", "내용물입니다."));
    }

    @Test
    public void question_delete_sucess() throws UnAuthenticationException, CannotDeleteException {

        Question question = new Question("제목입니다.", "내용물입니다.");
        question.writeBy(SANJIGI);

        when(questionRepository.findOne(0l)).thenReturn(question);
        qnarService.deleteQuestion(SANJIGI,0l);

    }

    @Test(expected = UnAuthenticationException.class)
    public void question_delete_fail() throws UnAuthenticationException, CannotDeleteException {

        Question question = new Question("제목입니다.", "내용물입니다.");
        question.writeBy(SANJIGI);
        User testUser = new User("gangjigi", "password123", "name", "javajigi@slipp.net");

        when(questionRepository.findOne(0l)).thenReturn(question);
        qnarService.deleteQuestion(testUser,0l);
    }

    @Test
    public void answer_add_success() throws UnAuthenticationException {

        Question question = new Question("제목입니다.", "내용물입니다.");
        question.writeBy(JAVAJIGI);

        when(questionRepository.findOne(0l)).thenReturn(question);

        qnarService.addAnswer(JAVAJIGI,0l,new Answer(JAVAJIGI,"수정내용입니다."));
    }

    @Test(expected = UnAuthorizedException.class)
    public void answer_add_fail() throws UnAuthenticationException {

        Question question = new Question("제목입니다.", "내용물입니다.");
        question.writeBy(SANJIGI);

        when(questionRepository.findOne(0l)).thenReturn(question);

        qnarService.addAnswer(JAVAJIGI,0l,new Answer(JAVAJIGI,"수정내용입니다."));
    }

    @Test
    public void answer_delete_sucess() throws UnAuthenticationException {

        Answer answer = new Answer(SANJIGI,"답변내용");
        when(answerRepository.findOne(0l)).thenReturn(answer);

        qnarService.deleteAnswer(SANJIGI,0);

    }

    @Test(expected = UnAuthenticationException.class)
    public void answer_delete_fail() throws UnAuthenticationException {
        Answer answer = new Answer(SANJIGI,"답변내용");
        when(answerRepository.findOne(0l)).thenReturn(answer);

        qnarService.deleteAnswer(JAVAJIGI,0);

    }


}
