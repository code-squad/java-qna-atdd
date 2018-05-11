package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import codesquad.domain.UserRepository;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class LoginAcceptanceTest extends AcceptanceTest {

  private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

  @Autowired
  private UserRepository userRepository;
  private HtmlFormDataBuilder htmlFormDataBuilder = null;

  @Before
  public void setUp() throws Exception {
    htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm().addParameter("userId", "javajigi");
  }

  @Test
  public void login_성공() {

    HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
        .addParameter("password", "test").build();
    ResponseEntity<String> response = template()
        .postForEntity("/users/login", request, String.class);

    assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
  }

  @Test
  public void login_실패() {

    HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
        .addParameter("password", "wrong").build();
    ResponseEntity<String> response = template()
        .postForEntity("/users/login", request, String.class);

    assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    assertThat(response.getHeaders().getLocation().getPath(), is("/user/login_failed"));
  }
}
