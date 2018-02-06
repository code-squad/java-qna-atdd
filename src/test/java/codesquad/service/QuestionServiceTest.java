package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
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

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest {

    @InjectMocks
    QnaService qnaService;

    @Mock
    QuestionRepository questionRepository;

    User user;
    QuestionDto questionDto;

    @Before
    public void init(){
        user = new User(0, "", "", "", "");
        questionDto = new QuestionDto("111", "222");
        qnaService.create(user, questionDto);
    }

    @Test
    public void delete() throws CannotDeleteException {
        Question question = new Question("1","2");
        question.writeBy(user);

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.deleteQuestion(user, 1);
        assertThat(qnaService.findById(1L).isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_fail() throws CannotDeleteException {
        Question question = new Question("1","2");
        question.writeBy(new User(1, "javajigi", "1","1", "1"));

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.deleteQuestion(user, 1);
    }

    @Test
    public void update() throws UnAuthenticationException {
        Question question = new Question("1","2");
        question.writeBy(user);

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.update(user, 1, questionDto);
        assertThat(qnaService.findById(1L).isContentsEquals(questionDto.toQuestion()), is(true));
    }

    @Test(expected = UnAuthenticationException.class)
    public void update_fail() throws UnAuthenticationException {
        Question question = new Question("1","2");
        question.writeBy(new User(1, "javajigi", "1","1", "1"));

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.update(user, 1, questionDto);
    }

}
