package support.test;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.web.HtmlFormDataBuilder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    private static final Long DEFAULT_QUESTION_ID = 1L;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;
    
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
    
    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected Question defaultQuestion() {
        return findById(DEFAULT_QUESTION_ID);
    }

    protected Question findById(Long id) {
        return questionRepository.findById(id).get();
    }
    protected String createResource(String path, Object bodyPayload) {
        ResponseEntity<String> response = basicAuthTemplate().postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        return response.getHeaders().getLocation().getPath();
    }

    protected <T> T getResource(String location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }

    protected HttpEntity getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        return new HttpEntity(headers);
    }

    protected HttpEntity<MultiValueMap<String, Object>> createQuestionReq() {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "질문 있어요!")
                .addParameter("contents", "글자 길이 3이상").build();
    }

    protected HttpEntity<MultiValueMap<String, Object>> updateQuestionReq() {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "질문 수정했음")
                .addParameter("contents", "내용 수정했음").build();
    }

}
