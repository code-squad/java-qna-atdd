package codesquad.web;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;


public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    public static final User SANJIGI = new User(2L, "sanjigi", "test", "name", "sanjigi@slipp.net");

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void create() throws Exception {
        AnswerDto newAnswer = createAnswerDto(3L);
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/api/questions/1/answers/", newAnswer, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        log.debug("location: {}", response.getHeaders().getLocation().toString());
        AnswerDto dbAnswer = getResource(response.getHeaders().getLocation().toString(), AnswerDto.class);
        log.debug("it's answers!: {}", answerRepository.findAnswersByQuestion(questionRepository.findOne(1L)));
        log.debug("newAnswer: {}", newAnswer.getContents());
        log.debug("dbanswer: {}", dbAnswer.getContents());
        assertTrue(dbAnswer.getContents().equals(newAnswer.getContents()));
    }

    @Test
    public void delete() throws Exception {
        basicAuthTemplate().delete("/api/questions/1/answers/1", String.class);
        Answer answer = answerRepository.findOne(1L);
        assertTrue(answer.isDeleted());
    }

    @Test
    public void delete_fail_noLogin() throws Exception {
        template().delete("/api/questions/2/answers/2", String.class);
        Answer answer = answerRepository.findOne(2L);
        assertTrue(!answer.isDeleted());
    }

    @Test
    public void delete_fail_anotherUser() throws Exception {
        basicAuthTemplate().delete("/api/questions/2/answers/2", String.class);
        Answer answer = answerRepository.findOne(2L);
        assertTrue(!answer.isDeleted());
    }

    private AnswerDto createAnswerDto() {
        return new AnswerDto("글입니다gg");
    }

    private AnswerDto createAnswerDto(long id) {
        return new AnswerDto(id, "글입니다gg");
    }
}
