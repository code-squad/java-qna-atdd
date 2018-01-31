package codesquad.web;

import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.service.QnaService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AnswerAcceptanceTest extends AcceptanceTest {
    private HtmlFormDataBuilder htmlFormDataBuilder;
    private Question question;

    @Autowired
    private QnaService qnaService;

    @Autowired
    private AnswerRepository answerRepository;

    @Before
    public void setUp() {
        question = qnaService.create(defaultUser(), new Question("this is title", "this is contents"));
    }

    @Test
    public void addAnswer() {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm()
                                                                               .addParameter("contents", "hello~")
                                                                               .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions/" + question.getId() + "/answers", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void updateAnswer() {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.urlEncodedForm()
                                                                               .addParameter("contents", "hahahahaha")
                                                                               .build();

        basicAuthTemplate(defaultUser()).put("/questions/" + question.getId() + "/answers/1/form", request, String.class);
        assertThat(answerRepository.findOne(1L).getContents(), is("hahahahaha"));
    }

    @Test
    public void delete() {
        basicAuthTemplate().delete("/questions/"+ question.getId() + "/answers/1");
        assertThat(answerRepository.findOne(1L).isDeleted(), is(true));
    }
}
