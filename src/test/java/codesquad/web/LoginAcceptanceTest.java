package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import java.util.Arrays;

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

import codesquad.domain.UserRepository;
import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

	@Autowired
	private UserRepository userRepository;

	@Test
	public void createLoginForm() throws Exception {
		ResponseEntity<String> response = template().getForEntity("/users/login", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		log.debug("body : {}", response.getBody());
	}

	@Test
	public void login() {
		HtmlFormDataBuilder dataBuilder = HtmlFormDataBuilder.urlEncodedForm().addParameter("userId", "ksm0814")
				.addParameter("password", "k5696");

		HttpEntity<MultiValueMap<String, Object>> request = dataBuilder.build();
		ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertNotNull(userRepository.findByUserId("ksm0814"));
		assertThat(response.getHeaders().getLocation().getPath(), is("/users"));

	}

	@Test
	public void loginFail() {
		HtmlFormDataBuilder dataBuilder = HtmlFormDataBuilder.urlEncodedForm().addParameter("userId", "ksm0814")
				.addParameter("password", "notpass");

		HttpEntity<MultiValueMap<String, Object>> request = dataBuilder.build();
		ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
		assertNotNull(userRepository.findByUserId("ksm0814"));
	}

}
