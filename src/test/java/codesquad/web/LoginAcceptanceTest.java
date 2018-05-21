package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import codesquad.domain.UserRepository;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class LoginAcceptanceTest extends AcceptanceTest {

	@Autowired
	UserRepository userRepository;

	@Test
	public void loginFail() throws Exception {
		HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
		htmlFormDataBuilder.addParameter("userId", "durin");
		htmlFormDataBuilder.addParameter("password", "1234");
		HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();		
	
		ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);
		
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(Optional.empty(), is(userRepository.findByUserId("durin"))); 
	}
	
	@Test
	public void loginSuccess() throws Exception {
		HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
		htmlFormDataBuilder.addParameter("userId", "gram");
		htmlFormDataBuilder.addParameter("password", "1234");
		HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

		ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class); 
		
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertNotNull(userRepository.findByUserId("gram"));
		assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
	}


}
