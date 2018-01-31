package codesquad.web;

import support.test.HtmlFormDataBuilder;
import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

	@Autowired
	private UserRepository userRepository;

	@Test
	public void login_성공() {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
		builder.addParameter("userId", "javajigi");
		builder.addParameter("password", "test");

		ResponseEntity<String> response = template().postForEntity("/users/login", builder.build(), String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
	}

	@Test
	public void login_패스워드_불일치() {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
		builder.addParameter("userId", "javajigi");
		builder.addParameter("password", "test1");

		ResponseEntity<String> response = template().postForEntity("/users/login", builder.build(), String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void login_아이디_없음() {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
		builder.addParameter("userId", "hacker");
		builder.addParameter("password", "test");

		ResponseEntity<String> response = template().postForEntity("/users/login", builder.build(), String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

}
