package codesquad.service;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.exceptions.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityNotFoundException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    private User javajigi;
    private User sanjigi;
    private Question question;

    @Before
    public void setup() {
        sanjigi = new User("sanjigi", "password", "name", "sanjigi@slipp.net");
        javajigi = new User("javajigi", "password", "name", "javajigi@slipp.net");
        question = new Question("title", "good");
    }

    @Test(expected = EntityNotFoundException.class)
    public void read_fail_question_not_found() {
        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

        qnaService.findById(1);
    }

    @Test
    public void read_success() {
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        assertThat(qnaService.findById(1), is(question));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_fail_owner_not_match() {
        question.writeBy(javajigi);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(questionRepository.findById(anyLong()).filter(q -> q.isOwner(sanjigi))).thenReturn(Optional.empty());

        qnaService.update(sanjigi, 1, question.toQuestionDto());
    }

    @Test
    public void update_success_owner_match() {
        question.writeBy(javajigi);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(questionRepository.findById(anyLong()).filter(q -> q.isOwner(javajigi))).thenReturn(Optional.of(question));

        qnaService.update(javajigi, 1, question.toQuestionDto());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_fail_owner_not_match() {
        question.writeBy(javajigi);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

        qnaService.deleteQuestion(sanjigi, 1);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_fail_answer_writer_not_match() {
        question.writeBy(javajigi);
        question.addAnswer(new Answer(sanjigi, "hello"));
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(javajigi, 1);

    }

    @Test
    public void delete_success() {
        question.writeBy(javajigi);
        question.addAnswer(new Answer(javajigi, "hello"));
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        doNothing().when(deleteHistoryService).registerHistory(javajigi, question);
        when(questionRepository.save(question)).thenReturn(question);

        Question returned = qnaService.deleteQuestion(javajigi, 1);
        assertThat(returned.isDeleted(), is(true));
    }

}
