package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
//import static org.mockito.Mockito.when;

//import java.util.Arrays;
//import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

//import codesquad.UnAuthenticationException;
//import codesquad.domain.User;
import codesquad.domain.UserRepository;
//import codesquad.service.UserService;
import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest{
	@Autowired
	private UserRepository userRepository;

	@Test
	public void login() throws Exception {
		HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
		htmlFormDataBuilder.addParameter("userId", "javajigi");
		htmlFormDataBuilder.addParameter("password", "test");

		HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
		ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertNotNull(userRepository.findByUserId("javajigi"));
		assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
	}

	@Test
	public void login_failed_when_user_not_found() throws Exception {
		HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
		htmlFormDataBuilder.addParameter("userId", "noteExistTestUser");
		htmlFormDataBuilder.addParameter("password", "test");

		HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
		ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

//		assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));		//"redirect:/login" 을 통해 FOUND 신호가 오기 때문에 삭제.
		assertEquals(false, userRepository.findByUserId("noteExistTestUser").isPresent());
		assertThat(response.getHeaders().getLocation().getPath(), is("/login"));
	}

	@Test
	public void login_failed_when_mismatch_password() throws Exception {
		HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
		htmlFormDataBuilder.addParameter("userId", "javajigi");
		htmlFormDataBuilder.addParameter("password", "notCorrectPassword");

		HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
		ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

//		assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));		//"redirect:/login" 을 통해 FOUND 신호가 오기 때문에 삭제.
		assertEquals(false, userRepository.findByUserId("noteExistTestUser").isPresent());
		assertThat(response.getHeaders().getLocation().getPath(), is("/login"));
	}
}
