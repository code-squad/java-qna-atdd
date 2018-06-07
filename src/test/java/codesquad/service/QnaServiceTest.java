package codesquad.service;

import codesquad.domain.*;
import codesquad.exceptions.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityNotFoundException;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    private User javajigi;
    private User sanjigi;
    private Question question;
    private static final Long DEFAULT_QUESTION_ID = 1L;
    private static final Long DEFAULT_ANSWER_ID = 1L;

    @Before
    public void setup() {
        sanjigi = new User("sanjigi", "password", "name", "sanjigi@slipp.net");
        javajigi = new User("javajigi", "password", "name", "javajigi@slipp.net");
        question = new Question("title", "good");
    }

    @Test(expected = EntityNotFoundException.class)
    public void read_fail_question_not_found() {
        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

        qnaService.findQuestionById(DEFAULT_QUESTION_ID);
    }

    @Test
    public void read_success() {
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        assertThat(qnaService.findQuestionById(DEFAULT_QUESTION_ID), is(question));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_fail_owner_not_match() {
        question.writeBy(javajigi);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(questionRepository.findById(anyLong()).filter(q -> q.isOwner(sanjigi))).thenReturn(Optional.empty());

        qnaService.update(sanjigi, DEFAULT_QUESTION_ID, question.toQuestionDto());
    }

    @Test
    public void update_success_owner_match() {
        question.writeBy(javajigi);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        when(questionRepository.findById(anyLong()).filter(q -> q.isOwner(javajigi))).thenReturn(Optional.of(question));

        qnaService.update(javajigi, DEFAULT_QUESTION_ID, question.toQuestionDto());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_fail_owner_not_match() {
        question.writeBy(javajigi);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

        qnaService.deleteQuestion(sanjigi, DEFAULT_QUESTION_ID);
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_fail_answer_writer_not_match() {
        question.writeBy(javajigi);
        question.addAnswer(new Answer(sanjigi, "hello"));
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));

        qnaService.deleteQuestion(javajigi, DEFAULT_QUESTION_ID);

    }

    @Test
    public void delete_success() {
        question.writeBy(javajigi);
        question.addAnswer(new Answer(javajigi, "hello"));
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        doNothing().when(deleteHistoryService).registerHistory(javajigi, question);
        when(questionRepository.save(question)).thenReturn(question);

        Question returned = qnaService.deleteQuestion(javajigi, DEFAULT_QUESTION_ID);
        assertThat(returned.isDeleted(), is(true));
    }

    @Test
    public void add_answer() {
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));
        qnaService.addAnswer(javajigi, DEFAULT_QUESTION_ID, "hello");
        verify(answerRepository, times(1)).save(any(Answer.class));
    }

    @Test
    public void update_answer_success() {
        Answer original = new Answer(javajigi, "contents");
        when(answerRepository.findById(anyLong())).thenReturn(Optional.of(original));
        String updatecontents = "updated contents";
        Answer updateAnswer = qnaService.update(javajigi, DEFAULT_ANSWER_ID, updatecontents);
        assertThat(updateAnswer.getContents(), is(updatecontents));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_answer_fail() {
        Answer original = new Answer(javajigi, "contents");
        when(answerRepository.findById(anyLong())).thenReturn(Optional.of(original));
        qnaService.update(sanjigi, DEFAULT_ANSWER_ID, "updated contents");
    }

    @Test
    public void delete_answer_success() {
        Answer original = new Answer(javajigi, "contents");
        when(answerRepository.findById(anyLong())).thenReturn(Optional.of(original));
        Answer deletedAnswer = qnaService.deleteAnswer(javajigi, DEFAULT_ANSWER_ID);
        assertTrue(deletedAnswer.isDeleted());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_answer_fail() {
        Answer original = new Answer(javajigi, "contents");
        when(answerRepository.findById(anyLong())).thenReturn(Optional.of(original));
        qnaService.deleteAnswer(sanjigi, DEFAULT_ANSWER_ID);
    }
}
