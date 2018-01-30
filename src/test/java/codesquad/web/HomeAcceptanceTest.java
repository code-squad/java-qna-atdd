package codesquad.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HomeAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(HomeAcceptanceTest.class);

    @Test
    public void createForm() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(defaultQuestion().getTitle()), is(true));
    }
}
