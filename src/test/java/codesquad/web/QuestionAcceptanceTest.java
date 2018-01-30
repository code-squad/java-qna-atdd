package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private static final Long QUESTION_ID = 1L;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    private HtmlFormDataBuilder htmlFormDataBuilder;

    private User writer;

    @Before
    public void setUp() throws UnAuthenticationException {
        writer = userRepository.findByUserId("sanjigi").get();
    }

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm()
                                                                               .addParameter("title", "hello~")
                                                                               .addParameter("contents", ":) :)")
                                                                               .build();

        ResponseEntity<String> response = basicAuthTemplate(writer).postForEntity("/questions/create", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(questionRepository.findByWriter(writer));
    }

    @Test
    public void update() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm()
                                                                               .addParameter("_method", "put")
                                                                               .addParameter("title", "good")
                                                                               .addParameter("contents", "day")
                                                                               .build();

        basicAuthTemplate().postForEntity("/questions/" + 2 , request, String.class);
        System.out.println(questionRepository.findAll());
        assertThat(questionRepository.findOne(2L).getContents(), is("day"));
    }

    @Test
    public void updateForm() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", QUESTION_ID), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void delete() {
        basicAuthTemplate().delete("/questions/" + QUESTION_ID);
        assertThat(questionRepository.findOne(QUESTION_ID).isDeleted()).isTrue();
    }
}
