package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest {

    @InjectMocks
    QnaService qnaService;

    @Mock
    QuestionRepository questionRepository;

    @Mock
    DeleteHistoryService deleteHistoryService;

    User user;
    User user1;
    QuestionDto questionDto;

    @Before
    public void init() {
        user = new User(1, "", "", "", "");
        user1 = new User(2, "", "", "", "");
        questionDto = new QuestionDto("111", "222");
        qnaService.create(user, questionDto);
    }

    @Test
    public void delete() throws CannotDeleteException {
        Question question = writeQuestions();

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.deleteQuestion(user, 1);
        assertThat(qnaService.findById(1L).isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_fail() throws CannotDeleteException {
        Question question = new Question("1", "2");
        question.writeBy(new User(1, "javajigi", "1", "1", "1"));

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.deleteQuestion(user, 1);
    }

    @Test
    public void update() throws UnAuthenticationException {
        Question question = writeQuestions();

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.update(user, 1, questionDto);
        assertThat(qnaService.findById(1L).isContentsEquals(questionDto.toQuestion()), is(true));
    }

    @Test(expected = UnAuthenticationException.class)
    public void update_fail() throws UnAuthenticationException {
        Question question = new Question("1", "2");
        question.writeBy(new User(1, "javajigi", "1", "1", "1"));

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.update(user, 1, questionDto);
    }


    @Test(expected = CannotDeleteException.class)
    public void 질문이_존재하지_않는_경우() throws CannotDeleteException {
        when(questionRepository.findOne(1L)).thenReturn(null);
        qnaService.deleteQuestion(user, 1);
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문한_사람과_로그인한_사람이_다른_경우() throws CannotDeleteException {
        Question question = writeQuestions();

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.deleteQuestion(user1, 1);
    }

    @Test
    public void 질문한_사람과_로그인한_사람이_같으면서_답변이_없는_경우_성공() throws CannotDeleteException {
        Question question = writeQuestions();

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.deleteQuestion(user, 1);
        assertThat(qnaService.findById(1L).isDeleted(), is(true));
    }

    @Test
    public void 질문한_사람과_로그인한_사람이_같으면서_답변의_글쓴이도_같은_경우_성공() throws CannotDeleteException {
        Question question = writeQuestions();
        addAnswer(user, question, "답변");

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.deleteQuestion(user, 1);
        assertThat(qnaService.findById(1L).isDeleted(), is(true));
    }

    @Test(expected = UnAuthorizedException.class)
    public void 질문한_사람과_로그인한_사람이_같으면서_답변의_글쓴이가_다른_경우() throws CannotDeleteException {
        Question question = writeQuestions();
        addAnswer(user1, question, "답변");

        when(questionRepository.findOne(1L)).thenReturn(question);

        qnaService.deleteQuestion(user, 1);
    }

    private Question writeQuestions() {
        Question question = new Question("1", "2");
        question.writeBy(user);
        return question;
    }

    private void addAnswer(User user1, Question question, String contents) {
        question.addAnswer(new Answer(user1, contents));
    }
}
