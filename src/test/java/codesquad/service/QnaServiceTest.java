package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    public static Question originalQuestion = new Question("title", "contents");
    public static Question modifiedQuestion = new Question("updatedTitle", "updatedContents");

    public static User owner = new User(1, "javajigi", "password", "name", "javajigi@slipp.net");
    public static User other = new User(2, "sanjigi", "password", "name", "sanjigi@slipp.net");

    @Before
    public void setUp() throws Exception {
        originalQuestion.setId(1);
        originalQuestion.writeBy(owner);
        modifiedQuestion.setId(2);
        modifiedQuestion.writeBy(owner);
    }

    @Test
    public void 질문수정_로그인O() throws Exception {
        when(questionRepository.findById(originalQuestion.getId())).thenReturn(Optional.of(originalQuestion));
        Question forUpdate = questionRepository.findById(originalQuestion.getId()).get();
        when(questionRepository.save(forUpdate)).thenReturn(forUpdate);
        forUpdate.update(modifiedQuestion, owner);
        Question updatedQuestion = qnaService.update(owner, originalQuestion.getId(), modifiedQuestion);
        softly.assertThat(updatedQuestion.getTitle()).isEqualTo(modifiedQuestion.getTitle());
    }

    @Test(expected = UnAuthenticationException.class)
    public void 질문수정_로그인X() throws Exception {
        when(questionRepository.findById(originalQuestion.getId())).thenReturn(Optional.of(originalQuestion));
        qnaService.update(null, originalQuestion.getId(), modifiedQuestion);
    }

    @Test(expected = UnAuthenticationException.class)
    public void 질문수정_다른유저() throws Exception {
        when(questionRepository.findById(originalQuestion.getId())).thenReturn(Optional.of(originalQuestion));
        qnaService.update(other, originalQuestion.getId(), modifiedQuestion);
    }

    @Test
    public void 질문삭제_로그인O() throws Exception {
        when(questionRepository.findById(originalQuestion.getId())).thenReturn(Optional.of(originalQuestion));
        qnaService.delete(owner, originalQuestion.getId());
        softly.assertThat(originalQuestion.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문삭제_로그인X() throws Exception {
        when(questionRepository.findById(originalQuestion.getId())).thenReturn(Optional.of(originalQuestion));
        qnaService.delete(null, originalQuestion.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문삭제_다른유저() throws Exception {
        when(questionRepository.findById(originalQuestion.getId())).thenReturn(Optional.of(originalQuestion));
        qnaService.delete(other, originalQuestion.getId());
    }
}
