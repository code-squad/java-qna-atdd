package codesquad.service;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnAService qnAService;

    private User loginUser;

    @Before
    public void setup() throws UnAuthenticationException {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
        loginUser = userService.login(user.getUserId(), user.getPassword());
    }

    @Test
    public void anybody_can_access_to_question() {
        Iterable<Question> all = questionRepository.findAll();
        when(qnAService.findAll()).thenReturn(all);
        assertThat(Optional.of(questionRepository.findAll()).isPresent(), is(true));
    }

    /**
     * service와 controller에서는 @LoginUser와 같이 다르기 때문에
     * loginUser에 null을 넣음
     */
    @Test (expected = IllegalArgumentException.class)
    public void ask_a_question_without_login() {
        Question question = new Question("first", "test");
        when(questionRepository.save(question)).thenReturn(question);
        qnAService.create(null, new QuestionDto("first", "test"));
    }

    @Test
    public void create_question_success() {
        Question question = new Question("first", "test");
        question.writeBy(loginUser);
        when(questionRepository.save(question)).thenReturn(question);
        Question savedQuestion = qnAService.create(loginUser, new QuestionDto("first", "test"));
        assertThat(question, is(equalTo(savedQuestion)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_question_fail_nonExists_question() {
        Question noExistsQuestion = new Question("noExists", "cuz never created");
        qnAService.update(loginUser, noExistsQuestion.getId(), noExistsQuestion);
    }

}
