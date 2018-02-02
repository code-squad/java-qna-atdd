package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.dto.AnswerDto;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class AnswerAcceptanceTest extends AcceptanceTest {

    @Autowired
    private AnswerRepository repository;

    @Test
    @DirtiesContext
    public void create() throws Exception {
        AnswerDto answerDto = new AnswerDto("test contents");

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions/1/answers",
                                                                                         htmlRequest(answerDto),
                                                                                         String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(repository.findAll().size()).isEqualTo(3);
    }

    @Test
    @DirtiesContext
    public void update() throws Exception {
        AnswerDto answerDto = new AnswerDto("update test");
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange("/questions/1/answers/1",
                                                                                    HttpMethod.PUT,
                                                                                    htmlRequest(answerDto),
                                                                                    String.class);

        Assert.assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        Answer answer = repository.findOne(1L);
        assertThat(answer.getContents()).isEqualTo("update test");
    }

    @Test
    @DirtiesContext
    public void delete() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange("/questions/1/answers/1",
                                                                                    HttpMethod.DELETE,
                                                                                    HttpEntity.EMPTY, String.class);

        Assert.assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        Answer answer = repository.findOne(1L);
        Assert.assertThat(answer.isDeleted(), is(true));
    }

    @Test
    public void showForm() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity("/questions/1/answers/1/form", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void showForm_권한이없는사용자() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi")).getForEntity("/questions/1/answers/1/form",
                                                                                                  String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
