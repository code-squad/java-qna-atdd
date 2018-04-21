package codesquad.web;

import codesquad.domain.UserRepository;
import codesquad.utils.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class LoginAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);
	
	@Autowired
	private UserRepository userRepository;
	
	private ResponseEntity<String> login(String userId, String password) {
		return template().postForEntity("/users/login",
				HtmlFormDataBuilder.urlEncodedForm()
						.addParameter("userId", userId)
						.addParameter("password", password)
						.build(),
				String.class);
	}
	
	@Test
	public void login_success() {
		ResponseEntity<String> response = login(defaultUser().getUserId(), defaultUser().getPassword());
		assertNotNull(userRepository.findByUserId(defaultUser().getUserId()));
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
	}
	
	@Test
	public void login_failed() {
		ResponseEntity<String> response = login("test12344565434134", "test");
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(userRepository.findByUserId("test12344565434134"), is(Optional.empty()));
		log.debug("body : {}", response.getBody());
		assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요."), is(true));
	}
}
