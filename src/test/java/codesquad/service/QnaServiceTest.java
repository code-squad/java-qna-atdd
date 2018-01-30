package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void update() {
        User writer = new User("testId", "testP", "testN", "test@slipp.net");
        Question question = new Question(1, "title", "contents");
        question.writeBy(writer);
        when(questionRepository.findOne(question.getId())).thenReturn(question);

        QuestionDto updateQuestionDto = new QuestionDto(1, "updateTitle", "updateContents");
        qnaService.update(writer, updateQuestionDto);
        Question updateQuestion = updateQuestionDto.toQuestion();
        updateQuestion.writeBy(writer);
        assertThat(updateQuestion, is(question));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_wrong_writer() {
        User writer = new User("testId", "testP", "testN", "test@slipp.net");
        User wrongWriter = new User("testId2", "testP", "testN", "test@slipp.net");
        Question question = new Question(1, "title", "contents");
        question.writeBy(writer);
        when(questionRepository.findOne(question.getId())).thenReturn(question);

        QuestionDto updateQuestion = new QuestionDto(1, "updateTitle", "updateContents");
        qnaService.update(wrongWriter, updateQuestion);
        assertThat(updateQuestion, not(question));
    }

    @Test
    public void delete() throws Exception {
        User writer = new User(2, "sanjigi", "test", "산지기", "sanjigi@slipp.net");
        Question question = new Question(1, "title", "contents");
        question.writeBy(writer);
        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(questionRepository.exists(question.getId())).thenReturn(true);

        qnaService.deleteQuestion(writer, question.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_답글이_있을_때() throws Exception {
        User writer = new User("testId", "testP", "testN", "test@slipp.net");
        Question question = new Question(1, "title", "contents");
        question.addAnswer(new Answer());
        question.writeBy(writer);
        when(questionRepository.findOne(question.getId())).thenReturn(question);

        qnaService.deleteQuestion(writer, question.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_글이_없을_때() throws Exception {
        User writer = new User("testId", "testP", "testN", "test@slipp.net");
        Question question = new Question(10, "title", "contents");
        question.addAnswer(new Answer());
        question.writeBy(writer);
        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(questionRepository.exists(question.getId())).thenReturn(true);

        qnaService.deleteQuestion(writer, question.getId());
    }
}
