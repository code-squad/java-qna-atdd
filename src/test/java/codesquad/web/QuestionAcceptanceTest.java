package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.dto.QuestionDto;
import codesquad.service.QnaService;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
	

	public ResponseEntity<String> makeTestData(String title, String contents) {
		HtmlFormDataBuilder dataBuilder = HtmlFormDataBuilder.urlEncodedForm().
				addParameter("title", title).addParameter("contents", contents);
		HttpEntity<MultiValueMap<String, Object>> request = dataBuilder.build();
		return basicAuthTemplate().postForEntity("/questions", request, String.class);
		
	}
	
	@Test
	public void create() throws Exception {
		ResponseEntity<String> response = makeTestData("create 제목", "create 내용");
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
	}
	

	@Test
	public void showList() throws Exception {
		makeTestData("showList 제목", "showList 내용");
		ResponseEntity<String> response = template().getForEntity("/", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody().contains("showList 제목"), is(true));
	}

	@Test
	public void detail() throws Exception {
		ResponseEntity<String> response = basicAuthTemplate(defaultUser())
				.getForEntity(String.format("/questions/%d", 1), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody().contains(Long.toString(defaultUser().getId())), is(true));
		assertThat(response.getBody().contains("ksm0814 1번 사용자의 질문"), is(true));

	}

	@Test
	public void updateForm() throws Exception {
		ResponseEntity<String> response = basicAuthTemplate(defaultUser())
				.getForEntity(String.format("/questions/%d/form", defaultUser().getId()), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void updateForm_no_login() throws Exception {
		ResponseEntity<String> response = template()
				.getForEntity(String.format("/questions/%d/form", defaultUser().getId()), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void update() throws Exception {
		makeTestData("update 제목", "update 내용");
		
		HtmlFormDataBuilder dataBuilder = HtmlFormDataBuilder.urlEncodedForm().addParameter("_method", "put")
				.addParameter("title", "수정하고싶은 제목입니다").addParameter("contents", "수정하고싶은 내용입니다");
		HttpEntity<MultiValueMap<String, Object>> request = dataBuilder.build();
		basicAuthTemplate().put(String.format("/questions/%d", 1), request, String.class);
		ResponseEntity<String> response = basicAuthTemplate().getForEntity("/", String.class);
		
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(response.getBody().contains("수정하고싶은 제목입니다"));
	}

	@Test
	public void update_no_auth() throws Exception {
		HtmlFormDataBuilder dataBuilder = HtmlFormDataBuilder.urlEncodedForm().addParameter("_method", "put")
				.addParameter("title", "수정 안하고싶은 제목입니다").addParameter("contents", "수정 안하고싶은 내용입니다");
		HttpEntity<MultiValueMap<String, Object>> request = dataBuilder.build();
		template().put(String.format("/questions/%d", 1), request, String.class);

		ResponseEntity<String> response = template().getForEntity("/", String.class);
		log.debug("body : {}", response.getBody());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(!response.getBody().contains("수정 안하고싶은 제목입니다"));
	}

	
	@Test
	public void delete() throws Exception {
		basicAuthTemplate().delete(String.format("/questions/%d", 2));

		ResponseEntity<String> response = basicAuthTemplate().getForEntity("/", String.class);
		log.debug("body : {}", response.getBody());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(!response.getBody().contains("ksm0814 1번 사용자의 질문2"));
	}

	@Test
	public void delete_no_auth() throws Exception {
		template().delete(String.format("/questions/%d", 1));

		ResponseEntity<String> response = template().getForEntity("/", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

}
