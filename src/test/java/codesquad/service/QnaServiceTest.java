package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.QuestionTest;
import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    private static final Question question = new Question(3L, JAVAJIGI,"test", "test");

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void setUp() {
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
    }

    @Test
    public void create() throws Exception {
        Question created = qnaService.create(JAVAJIGI, question);
        softly.assertThat(created).isEqualTo(question);
    }

    @Test
    public void update() throws Exception {
        Question updated = qnaService.update(JAVAJIGI, 3L, new Question("updated", "updated"));
        softly.assertThat(updated).isEqualTo(question);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        qnaService.update(SANJIGI, 3L, new Question("updated", "updated"));
    }

    @Test
    public void delete() throws Exception {
        qnaService.deleteQuestion(JAVAJIGI, 3L);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws Exception {
        qnaService.deleteQuestion(SANJIGI, 3L);
    }
}
