package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.QuestionDto;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest{
	private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

	@Autowired
	private QuestionRepository questionRepository;

	public Question createQuestion(User user) throws Exception {
		Question question = new Question("question1", "This is my question.");
		question.writeBy(user);
		questionRepository.save(question);
		return question;
	}
	
	public Question createQuestion() throws Exception {
		Question question = new Question("question1", "This is my question.");
		questionRepository.save(question);
		return question;
	}
	
	@Test
	public void createForm() throws Exception {
		ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		log.debug("body : {}", response.getBody());
	}
	
	@Test
	public void create() throws Exception {
		HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
		htmlFormDataBuilder.addSampleQuestion();
		HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
		ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions/create", request, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is("/"));
	}

	@Test
	public void list() throws Exception {
		ResponseEntity<String> response = template().getForEntity("/questions", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		log.debug("body : {}", response.getBody());
	}
	
	@Test
	public void updateForm_no_login() throws Exception {
		Long questionId = createQuestion().getId();
		ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", questionId), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void updateForm_login() throws Exception {
		User loginUser = defaultUser();
		Question question = createQuestion(loginUser);
		ResponseEntity<String> response = basicAuthTemplate(loginUser)
				.getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody().contains(question.getContents()), is(true));
	}
	
	@Test
	public void update_no_login() throws Exception {
		ResponseEntity<String> response = update(template());
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("_method", "put");
		params.add("title", "testTitle");
		params.add("contents", "Test file contents.");
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);
		return template.postForEntity(String.format("/questions/%d", questionRepository.count()), request, String.class);
	}

	@Test
	public void update() throws Exception {
		User loginUser = defaultUser();
		Question question = createQuestion(loginUser);
		ResponseEntity<String> response = update(basicAuthTemplate(loginUser));
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertTrue(response.getHeaders().getLocation().getPath().startsWith("/questions"));
	}
	
	@Test
	public void delete_no_login() throws Exception {
		Question question = createQuestion();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("_method", "delete");
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);
		ResponseEntity<String> response = template().postForEntity(String.format("/questions/%d", questionRepository.count()), request, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void delete_login() throws Exception {
		User loginUser = defaultUser();
		Question question = createQuestion(loginUser);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("_method", "delete");
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(params, headers);
		ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d", questionRepository.count()), request, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is("/"));
	}
}
