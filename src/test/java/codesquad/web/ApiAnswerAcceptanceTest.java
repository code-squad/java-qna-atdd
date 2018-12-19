package codesquad.web;

import codesquad.domain.Answer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.SANJIGI;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LogManager.getLogger(ApiQuestionAcceptanceTest.class);
    private static final String URL = "/api/questions/1/answers";

    private static final String CONTENTS = "나는 댓글이다.";
    private static final String UPDATE_CONTENTS = "수정한 댓글입니다.";

    @Test
    public void create() {
        String location = createResource(URL, CONTENTS);

        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);
        softly.assertThat(dbAnswer).isNotNull();
    }


    @Test
    public void update() {
        String location = createResource(URL, CONTENTS);

        ResponseEntity<String> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(UPDATE_CONTENTS), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update_no_login() {
        String location = createResource(URL, CONTENTS);

        ResponseEntity<String> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(UPDATE_CONTENTS), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_no_otherUser() throws Exception {
        String location = createResource(URL, CONTENTS);

        ResponseEntity<String> responseEntity =
                basicAuthTemplate(SANJIGI).exchange(location, HttpMethod.PUT, createHttpEntity(UPDATE_CONTENTS), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("error message : {}", responseEntity.getBody());
    }


}
