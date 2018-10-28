package support.test;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";
    protected static final HtmlFormDataBuilderTemplate htmlFormDataBuilderTemplate = new HtmlFormDataBuilderTemplate();

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    public TestRestTemplate template() {
        return template;
    }

    public TestRestTemplate basicAuthTemplate() {
        return basicAuthTemplate(defaultUser());
    }

    public TestRestTemplate basicAuthTemplate(User loginUser) {
        return template.withBasicAuth(loginUser.getUserId(), loginUser.getPassword());
    }

    protected User defaultUser() {
        return findByUserId(DEFAULT_LOGIN_USER);
    }

    protected void deleteAllQuestions() {
        TestRepository.deleteAllQuestions(questionRepository);
    }

    protected Question createQuestion(User loginUser) {
        return TestRepository.createQuestion(questionRepository, loginUser);
    }

    protected Question createQuestion(QuestionRepository questionRepository, User loginUser, String title, String contents) {
        return TestRepository.createQuestion(questionRepository, loginUser, title, contents);
    }

    protected User findByUserId(String userId) {
        return TestRepository.findByUserId(userRepository, userId);
    }
}
