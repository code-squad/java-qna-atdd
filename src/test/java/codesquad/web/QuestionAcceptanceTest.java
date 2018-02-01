package codesquad.web;

import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class QuestionAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	public void createForm() throws Exception {
		ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void create_no_login() throws Exception {
		ResponseEntity<String> response = create(template());

		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void create_login() throws Exception {
		ResponseEntity<String> response = create(basicAuthTemplate());

		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(questionRepository.findOne(Long.valueOf(3)).getTitle(), is("질문제목1"));
		assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
	}

	private ResponseEntity<String> create(TestRestTemplate template) throws Exception {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
		builder.addParameter("title", "질문제목1");
		builder.addParameter("contents", "질문본문1");

		return template.postForEntity("/questions", builder.getOrPost(), String.class);
	}

	@Test
	public void list() throws Exception {
		ResponseEntity<String> response = template().getForEntity("/questions", String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody().contains("runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?"), is(true));
	}

	@Test
	public void show() throws Exception {
		ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", 2), String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody().contains("runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?"), is(true));
	}

	@Test
	public void updateForm_no_login() throws Exception {
		ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 1),
				String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void updateForm_login() throws Exception {
		User loginUser = defaultUser();
		ResponseEntity<String> response = basicAuthTemplate(loginUser)
				.getForEntity(String.format("/questions/%d/form", 1), String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody().contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"), is(true));
	}

	@Test
	public void update_no_login() throws Exception {
		ResponseEntity<String> response = update(template(), 1);

		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void update_login() throws Exception {
		ResponseEntity<String> response = update(basicAuthTemplate(), 1);

		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertTrue(response.getHeaders().getLocation().getPath().startsWith("/questions"));
	}

	@Test
	public void update_user_not_match() throws Exception {
		ResponseEntity<String> response = update(basicAuthTemplate(), 2);

		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	private ResponseEntity<String> update(TestRestTemplate template, long id) throws Exception {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
		builder.addParameter("title", "질문제목 바꿈");
		builder.addParameter("contents", "질문본문도 바꿈");

		return template.postForEntity(String.format("/questions/%d", id), builder.put(), String.class);
	}

	@Test
	public void delete_no_login() throws Exception {
		ResponseEntity<String> response = delete(template(), 1);

		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void delete_login() throws Exception {
		ResponseEntity<String> response = delete(basicAuthTemplate(), 1);

		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertTrue(response.getHeaders().getLocation().getPath().startsWith("/questions"));
	}

	@Test
	public void delete_user_not_match() throws Exception {
		ResponseEntity<String> response = delete(basicAuthTemplate(), 2);

		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	private ResponseEntity<String> delete(TestRestTemplate template, long id) throws Exception {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

		return template.postForEntity(String.format("/questions/%d", id), builder.delete(), String.class);
	}

}
