package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class QuestionAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

	@Autowired
	QuestionRepository questionRepository;

	@Test
	public void 전체_질문_보기() {
		ResponseEntity<String> response = template().getForEntity("/questions", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void 질문_추가_화면() {
		ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void 특정_질문_보는_화면() {
		ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void 회원_질문_추가() {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

		TestRestTemplate template = basicAuthTemplate();

		builder.addParameter("title", "테스트다!");
		builder.addParameter("contents", "테스트내용이다!");

		HttpEntity<MultiValueMap<String, Object>> request = builder.build();

		ResponseEntity<String> response = template.postForEntity("/questions", request, String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(questionRepository.findOne(Long.valueOf(3)).getTitle(), is("테스트다!"));
		assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
	}

	@Test
	public void 비회원_질문_추가() {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

		builder.addParameter("title", "비회원이다!");
		builder.addParameter("contents", "비회원이 질문한다!");

		HttpEntity<MultiValueMap<String, Object>> request = builder.build();

		ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void 내_질문_수정() {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

		TestRestTemplate template = basicAuthTemplate();

		builder.addParameter("title", "질문수정한다!");
		builder.addParameter("contents", "질문수정이다아아아!");

		HttpEntity<MultiValueMap<String, Object>> request = builder.putBuild();

		ResponseEntity<String> response = template.postForEntity("/questions/1", request, String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(questionRepository.findOne(Long.valueOf(1)).getTitle(), is("질문수정한다!"));
		assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
	}

	@Test
	public void 남의_질문_수정() {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

		TestRestTemplate template = basicAuthTemplate();

		builder.addParameter("title", "질문수정한다!");
		builder.addParameter("contents", "질문수정이다아아아!");

		HttpEntity<MultiValueMap<String, Object>> request = builder.putBuild();

		ResponseEntity<String> response = template.postForEntity("/questions/2", request, String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void 내_질문_삭제() {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

		TestRestTemplate template = basicAuthTemplate();

		HttpEntity<MultiValueMap<String, Object>> request = builder.deleteBuild();

		ResponseEntity<String> response = template.postForEntity("/questions/1", request, String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
		assertTrue(questionRepository.findOne(Long.valueOf(1)).isDeleted());
	}

	@Test
	public void 남의_질문_삭제() {
		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

		TestRestTemplate template = basicAuthTemplate();

		HttpEntity<MultiValueMap<String, Object>> request = builder.deleteBuild();

		ResponseEntity<String> response = template.postForEntity("/questions/2", request, String.class);

		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}
	
}
