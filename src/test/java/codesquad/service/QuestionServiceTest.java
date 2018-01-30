package codesquad.service;

import static codesquad.domain.UserTest.newUser;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest {
    private static Question newQuestion(User origin) {
        Question question = new Question("title", "contents");
        question.writeBy(origin);

        return question;
    }

    private static Question newQuestion(String title, String contents) {
        return new Question(title, contents);
    }

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void createTest() {
        User origin = newUser("sanjigi");
        Question question = newQuestion(origin);

        when(questionRepository.save(question)).thenReturn(question);
        Question createdQuestion = qnaService.create(origin, question);

        assertThat(createdQuestion, is(question));
    }

    @Test
    public void findOneTest() {
        User origin = newUser("sanjigi");
        Question question = newQuestion(origin);

        when(questionRepository.findOne(question.getId())).thenReturn(question);
        Question foundQuestion = qnaService.findById(question.getId());

        assertThat(foundQuestion, is(question));
    }

    @Test
    public void updateTest() {
        User origin = newUser("sanjigi");
        Question question = newQuestion(origin);
        Question updatedQuestion = newQuestion(origin);
        updatedQuestion.update(origin, newQuestion("updated", "updated"));

        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(questionRepository.save(question)).thenReturn(updatedQuestion);

        assertThat(qnaService.update(origin, 0, updatedQuestion), is(updatedQuestion));
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateTest_by_not_owner() {
        User origin = newUser("sanjigi");
        Question question = newQuestion(origin);

        when(questionRepository.findOne(question.getId())).thenReturn(question);

        qnaService.update(newUser("another"), 0, question);
    }

    @Test
    public void deleteTest_by_owner() throws Exception {
        User origin = newUser("sanjigi");
        Question question = newQuestion(origin);
        assertFalse(question.isDeleted());

        when(questionRepository.findOne(question.getId())).thenReturn(question);
        qnaService.deleteQuestion(origin, question.getId());

        assertTrue(question.isDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteTest_by_other() throws Exception {
        User origin = newUser("sanjigi");
        Question question = newQuestion(origin);

        when(questionRepository.findOne(question.getId())).thenReturn(question);

        qnaService.deleteQuestion(newUser("another"), question.getId());
    }
}
