package codesquad.service;

import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;
import static java.util.Optional.ofNullable;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest {
    private Question QUESTION;
    private Question UPDATED_QUESTION;

    public static Question newQuestion(User origin) {
        Question question = new Question("title", "contents");
        question.writeBy(origin);

        return question;
    }

    public static Question newQuestion(String title, String contents) {
        return new Question(title, contents);
    }

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void init() {
        QUESTION = new Question("title", "contents");
        UPDATED_QUESTION = new Question("updated", "updated");
        QUESTION.writeBy(SANJIGI);
        UPDATED_QUESTION.writeBy(JAVAJIGI);

        when(questionRepository.findOne(QUESTION.getId())).thenReturn(ofNullable(QUESTION));
    }

    @Test
    public void createTest() {
        when(questionRepository.save(QUESTION)).thenReturn(QUESTION);
        Question createdQuestion = qnaService.create(SANJIGI, QUESTION);

        assertThat(createdQuestion, is(QUESTION));
    }

    @Test
    public void findOneTest() {
        Question foundQuestion = qnaService.findById(QUESTION.getId());

        assertThat(foundQuestion, is(QUESTION));
    }

    @Test
    public void updateTest() {
        when(questionRepository.save(QUESTION)).thenReturn(UPDATED_QUESTION);

        assertThat(qnaService.update(SANJIGI, 0, UPDATED_QUESTION), is(UPDATED_QUESTION));
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateTest_by_not_owner() {
        qnaService.update(JAVAJIGI, 0, QUESTION);
    }

    @Test
    public void deleteTest_by_owner() throws Exception {
        assertFalse(QUESTION.isDeleted());

        qnaService.deleteQuestion(SANJIGI, QUESTION.getId());

        assertTrue(QUESTION.isDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteTest_by_other() throws Exception {
        qnaService.deleteQuestion(JAVAJIGI, QUESTION.getId());
    }
}
