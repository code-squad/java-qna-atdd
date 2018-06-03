package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
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

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    private static final String ORIGINAL_CONTENT = "original";
    private static final String UPDATED_CONTENT = "updated";
    private static final String QUESTION_TITLE = "title";

    private final Question original = new Question(QUESTION_TITLE, ORIGINAL_CONTENT);
    private final Question updated = new Question(QUESTION_TITLE, UPDATED_CONTENT);
    private final User writer = new User("writer", "password", "pobi", "javajigi@slipp.net");

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void setUp() {
        original.writeBy(writer);
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(original));
    }

    @Test
    public void update_Success() {
        when(questionRepository.save(original)).thenReturn(original);

        qnaService.update(writer, original.getId(), updated.toQuestionDto());
        assertThat(original.getContents(), is(UPDATED_CONTENT));
    }

    @Test(expected = UnAuthenticationException.class)
    public void update_Question_Does_Not_Exist() {
        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

        qnaService.update(writer, original.getId(), updated.toQuestionDto());
        assertThat(original.getContents(), is(ORIGINAL_CONTENT));
    }

    @Test(expected = UnAuthenticationException.class)
    public void update_Different_Questions() {
        Question differentQuestion = new Question();
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(differentQuestion));

        qnaService.update(writer, original.getId(), updated.toQuestionDto());
        assertThat(original.getContents(), is(ORIGINAL_CONTENT));
        assertThat(differentQuestion.getContents(), is(ORIGINAL_CONTENT));
    }

    @Test
    public void delete_Success() {
        qnaService.deleteQuestion(writer, original.getId());
        verify(deleteHistoryService,times(1)).saveAll(anyList());
        assertThat(original.isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_Question_Does_Not_Exist() {
        when(questionRepository.findById(anyLong())).thenReturn(Optional.empty());

        qnaService.deleteQuestion(writer, original.getId());
        assertThat(original.isDeleted(), is(false));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_Already_Deleted() {
        original.deleteQuestion(writer);

        qnaService.deleteQuestion(writer, original.getId());
        verify(deleteHistoryService, times(0)).saveAll(anyList());
    }
}