package support.test;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.service.QnaService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepositor;

    @Before
    public void before(){
        User defaultUser = new User(1, DEFAULT_LOGIN_USER, "test", "nnn", "n@n");
        Question question1 = new Question(1, "title", "contents");
        Question question2 = new Question(2, "title", "contents");
        question1.writeBy(defaultUser);
        question2.writeBy(defaultUser);

        userRepository.save(defaultUser);
        questionRepositor.save(question1);
        questionRepositor.save(question2);
    }

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

    protected Question defaultQuestion(){
        return findByQuestionId(1l);
    }

    protected Question findByQuestionId(long id) {
        return questionRepositor.findById(id).get();
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }
}
