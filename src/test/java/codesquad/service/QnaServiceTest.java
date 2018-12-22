package codesquad.service;

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
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.AcceptanceTest;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends AcceptanceTest {
    @Mock
    QuestionRepository questionRepository;

    @InjectMocks
    QnaService qnaService;

    private User choi = new User(1, "choi", "1234");
    private User sing = new User(2, "sing", "1234");
    Question question = new Question("baseTitle", "baseContents");
    Question questionForUpdate = new Question("updateTitle", "updateContents");
    Question questionForUpdateOtherUser = new Question("otherTitle", "otherContents");
    Question questionForDelete = new Question("delTitle", "delContents");



    @Before
    public void setUp() throws Exception {
        question.writeBy(choi);
        questionForUpdate.writeBy(choi);
        questionForUpdateOtherUser.writeBy(sing);
        questionForDelete.writeBy(choi);
        when(questionRepository.findById((long)1)).thenReturn(Optional.of(question));
        when(questionRepository.findById((long)4)).thenReturn(Optional.of(questionForDelete));
    }

    @Test
    public void update() {
        qnaService.update(choi, 1, questionForUpdate);
        softly.assertThat(question.getTitle()).isEqualTo(questionForUpdate.getTitle());
        softly.assertThat(question.getContents()).isEqualTo(questionForUpdate.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateWithInvalidUser() {
        qnaService.update(choi, 1, questionForUpdateOtherUser);
    }

    @Test
    public void deleteQuestion() throws CannotDeleteException {
        qnaService.deleteQuestion(choi, (long)4);
        softly.assertThat(questionForDelete.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestionWithInvalidUser() throws CannotDeleteException {
        qnaService.deleteQuestion(sing, (long)1);
    }
}