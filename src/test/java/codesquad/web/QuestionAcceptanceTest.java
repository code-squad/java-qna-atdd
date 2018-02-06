package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.helper.HtmlFormDataBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by hoon on 2018. 2. 5..
 */
public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Autowired
    private QuestionRepository questionRepository;

    private Question question;

    @Before
    public void setup() {
        question = new Question("test", "test");
    }
    @Test
    public void create() throws Exception {

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "test")
                .addParameter("contents", "test")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode(), CoreMatchers.is(HttpStatus.FOUND));
        assertNotNull(questionRepository.findOne(3L));
    }

    @Test
    public void update() throws Exception {

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("title", "test111")
                .addParameter("contents", "test111")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(String.format("/questions/%d", 1), request, String.class);

        Question question = questionRepository.findOne(1L);

        assertThat(response.getStatusCode(), CoreMatchers.is(HttpStatus.FOUND));
        assertThat(question.getTitle(), is("test111"));

    }
}
