package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiAnswerControllerTest extends AcceptanceTest {
    private static final Logger logger = getLogger(ApiAnswerControllerTest.class);
    private static final String NEW_CONTENTS = "createAnswerTest";

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    public void create() {
        String location = createResource(getCreatePath(1L), NEW_CONTENTS);
        logger.debug("location : {}" , location);
        softly.assertThat(location.startsWith("/api/questions/1/answers/")).isTrue();
    }

    @Test
    public void createWithoutLogin() {
        ResponseEntity<Void> response = template().postForEntity(getCreatePath(1L), NEW_CONTENTS, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        String location = createResource(getCreatePath(1L), NEW_CONTENTS);
        logger.debug("location : {}", location);
        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);

        logger.debug("responseEntity : {}", responseEntity);
        softly.assertThat(responseEntity.getBody().isDeleted()).isTrue();
    }

    @Test
    public void deleteWithoutLogin() {
        String location = createResource(getCreatePath(1L), NEW_CONTENTS);
        logger.debug("location : {}", location);
        ResponseEntity<Answer> responseEntity =
                template().exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void deleteWithOtherUser() {
        String location = createResource(getCreatePath(1L), NEW_CONTENTS);
        logger.debug("location : {}", location);
        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate(sanjigiUser()).exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    private String getCreatePath(long questionId){
        return "/api/questions/" + questionId + "/answers";
    }
}