package codesquad.service;

import codesquad.CannotFindException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    User loginUser;
    Question defaultQuestion;
    Question defaultQuestionWithoutWriter;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QnaService qnaService;

    @Before
    public void before() {
        loginUser = new User(1, "id", "pwd", "name", "m@il");
        defaultQuestionWithoutWriter = new Question("title", "contents");
        defaultQuestion = new Question("title", "contents");
        defaultQuestion.writeBy(loginUser);
    }

    @Test
    public void create() {
        when(questionRepository.save(defaultQuestion))
                .thenReturn(defaultQuestion);

        softly.assertThat(qnaService.create(loginUser, defaultQuestionWithoutWriter))
                .isEqualTo(defaultQuestion);
    }

    @Test
    public void findById() {
        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(defaultQuestion));

        softly.assertThat(qnaService.findById(1L)).isEqualTo(defaultQuestion);
    }

    @Test(expected = CannotFindException.class)
    public void findById_cannotFind(){
        when(questionRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

       qnaService.findById(1L);
    }

    @Test
    public void update() {
        when(questionRepository.save(defaultQuestion))
                .thenReturn(defaultQuestion);
        when(questionRepository.findById(1l))
                .thenReturn(Optional.of(defaultQuestion));

        softly.assertThat(qnaService.update(loginUser, 1, defaultQuestionWithoutWriter));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_fail() {
        User otherUser = new User(2, "otherId", "otherPwd", "otherName", "otherM@il");
        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(defaultQuestion));

        qnaService.update(otherUser, 1L, defaultQuestionWithoutWriter);
    }

    @Test
    public void deleteQuestion() {
    }

    @Test
    public void addAnswer() {
    }

    @Test
    public void deleteAnswer() {
    }
}