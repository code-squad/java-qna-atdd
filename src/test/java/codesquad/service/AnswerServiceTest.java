package codesquad.service;

import codesquad.NoSuchEntityException;
import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AnswerServiceTest {
    private static final User ANSWER_WRITER = new User("ANSWER_WRITER", "password", "name", "email");

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void findById_exists() {
        when(answerRepository.findById(anyLong())).thenReturn(Optional.of(new Answer("test")));

        Answer answer = qnaService.findAnswerById(anyLong());
        verify(answerRepository).findById(anyLong());
        assertEquals("test", answer.getContents());
    }

    @Test(expected = NoSuchEntityException.class)
    public void findById_does_NOT_exist() {
        when(answerRepository.findById(anyLong())).thenReturn(Optional.empty());

        qnaService.findAnswerById(anyLong());
        verify(answerRepository).findById(anyLong());
    }

    @Test
    public void addAnswer_question_exists() {
        Question question = new Question("test", "content");
        AnswerDto answerDto = new AnswerDto("answer content");
        when(questionRepository.findById(3L)).thenReturn(Optional.of(question));

        qnaService.addAnswer(ANSWER_WRITER, 3L, answerDto);
        verify(answerRepository).save(answerDto.toAnswer());
    }

    @Test(expected = NoSuchEntityException.class)
    public void addAnswer_question_does_NOT_exist() {
        AnswerDto answerDto = new AnswerDto("answer content");
        when(questionRepository.findById(3L)).thenReturn(Optional.empty());

        qnaService.addAnswer(ANSWER_WRITER, 3L, answerDto);
    }

    @Test
    public void deleteAnswer_exists() {
        Answer answer = new Answer("answer content");
        answer.writeBy(ANSWER_WRITER);
        when(answerRepository.findById(3L)).thenReturn(Optional.of(answer));

        qnaService.deleteAnswer(ANSWER_WRITER, 3L);
        verify(deleteHistoryService).saveAll(anyList());
    }

    @Test(expected = NoSuchEntityException.class)
    public void deleteAnswer_does_NOT_exist() {
        when(answerRepository.findById(3L)).thenReturn(Optional.empty());

        qnaService.deleteAnswer(ANSWER_WRITER, 3L);
    }
}