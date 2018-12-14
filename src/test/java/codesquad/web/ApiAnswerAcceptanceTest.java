package codesquad.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.RestJsonDataBuilder;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    private static RestJsonDataBuilder restJsonDataBuilder;

    @Test
    public void create() {
        String contents = "answer contents";
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions/1/answers");
        ResponseEntity<Void> responseEntity = restJsonDataBuilder.createEntity(basicAuthTemplate(), contents, Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        softly.assertThat(restJsonDataBuilder.getLocation()).isEqualTo("/api/questions/1/answers/5");
    }

    @Test
    public void delete_success() {
        String contents = "answer contents";
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions/1/answers");

        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(basicAuthTemplate(), contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> responseEntity = restJsonDataBuilder.deleteEntity(basicAuthTemplate(), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_by_other() {
        String contents = "answer contents";
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions/1/answers");

        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(basicAuthTemplate(), contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> responseEntity = restJsonDataBuilder.deleteEntity(basicAuthTemplate(secondUser()), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_not_exist() {
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions/1/answers/4");

        ResponseEntity<Void> responseEntity = restJsonDataBuilder.deleteEntity(basicAuthTemplate(), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
