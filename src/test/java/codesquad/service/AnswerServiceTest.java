package codesquad.service;

import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;
import static codesquad.service.QuestionServiceTest.newQuestion;
import static java.util.Optional.ofNullable;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnswerServiceTest {
    private static final String ANSWER_CONTENTS = "test answer";
    private static final User OWNER = SANJIGI;
    private static final User OTHER_USER = JAVAJIGI;
    private static final Answer ANSWER = new Answer(OWNER, ANSWER_CONTENTS);
    private static final AnswerDto UPDATE_ANSWER = new AnswerDto("updated answer");

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void init() {
        when(answerRepository.findByIdAndDeletedFalse(1L)).thenReturn(ANSWER);
        when(answerRepository.save(ANSWER)).thenReturn(ANSWER);
    }

    @Test
    public void createTest() {
        Question question = newQuestion(OWNER);
        Answer answer = new Answer(OWNER, ANSWER_CONTENTS);

        when(questionRepository.findOne(0)).thenReturn(ofNullable(question));
        when(answerRepository.save(answer)).thenReturn(answer);
        Answer createdAnswer = qnaService.addAnswer(OWNER, 0, ANSWER_CONTENTS);

        assertThat(createdAnswer.getContents(), is(ANSWER_CONTENTS));
        assertThat(createdAnswer.getWriter(), is(OWNER));
    }

    @Test
    public void updateTest() {
        Answer updatedAnswer = qnaService.updateAnswer(OWNER, 1, UPDATE_ANSWER);

        assertThat(updatedAnswer.getContents(), is(UPDATE_ANSWER.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateTest_with_other() {
        qnaService.updateAnswer(OTHER_USER, 1, UPDATE_ANSWER);
    }

    @Test
    public void deleteTest() {
        Answer answer = qnaService.deleteAnswer(OWNER, 1);

        assertTrue(answer.isDeleted());
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteTest_with_other() {
        qnaService.deleteAnswer(OTHER_USER, 1);
    }
}
