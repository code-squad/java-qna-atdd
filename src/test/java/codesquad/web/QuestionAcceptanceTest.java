package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

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
import org.springframework.web.client.RestTemplate;

import antlr.debug.GuessingEvent;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.service.QnaService;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
	
	@Autowired
	QnaService qnaService;

	public ResponseEntity<String> makeTestData(TestRestTemplate myTemplate, String title, String contents) {
		HtmlFormDataBuilder dataBuilder = HtmlFormDataBuilder.urlEncodedForm().
				addParameter("title", title).addParameter("contents", contents);
		HttpEntity<MultiValueMap<String, Object>> request = dataBuilder.build();
		return myTemplate.postForEntity("/questions", request, String.class);
		
	}
	
	
	@Test
	public void create() throws Exception {
		ResponseEntity<String> response = makeTestData(basicAuthTemplate(),"create 제목", "create 내용");
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
	}
	

	@Test
	public void showList() throws Exception {
		makeTestData(basicAuthTemplate(),"showList 제목", "showList 내용");
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
		makeTestData(basicAuthTemplate(),"update 제목", "update 내용");
		
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
		makeTestData(basicAuthTemplate(),"updatenoAuth 제목", "updatenoAuth 내용");
		HtmlFormDataBuilder dataBuilder = HtmlFormDataBuilder.urlEncodedForm().addParameter("_method", "put")
				.addParameter("title", "수정 안하고싶은 제목입니다").addParameter("contents", "수정 안하고싶은 내용입니다");
		HttpEntity<MultiValueMap<String, Object>> request = dataBuilder.build();
		template().put(String.format("/questions/%d", 1), request, String.class);

		ResponseEntity<String> response = template().getForEntity("/", String.class);
		log.debug("body : {}", response.getBody());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(!response.getBody().contains("수정 안하고싶은 제목입니다"));
	}
	

	private Long findQuestionId(String title) {
		for (Question question : qnaService.findAll()) {
			log.debug("question : {}",question);
			if(question.getTitle().equals(title))
				return question.getId();
		}
		return null;
	}
	
	@Test
	public void delete() throws Exception {
		makeTestData(basicAuthTemplate(), "delete 제목", "delete 내용");
		Long questionId = Optional.ofNullable(findQuestionId("delete 제목")).orElseThrow(null);
		basicAuthTemplate().delete(String.format("/questions/%d",questionId));
		
		ResponseEntity<String> response = basicAuthTemplate().getForEntity("/", String.class);
		log.debug("body : {}", response.getBody());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(!response.getBody().contains("delete 제목"));
	}
	
	@Test
	public void delete_질문자사용자같음() throws Exception {
		makeTestData(basicAuthTemplate(), "delete_질문자사용자같음 제목", "delete 내용");
		Long questionId = Optional.ofNullable(findQuestionId("delete_질문자사용자같음 제목")).orElseThrow(null);
		basicAuthTemplate().delete(String.format("/questions/%d",questionId));
		
		ResponseEntity<String> response = basicAuthTemplate().getForEntity("/", String.class);
		log.debug("body : {}", response.getBody());
		assertTrue(!response.getBody().contains("delete_질문자사용자같음 제목"));
	}

	@Test
	public void delete_질문자사용자다름() throws Exception {
		makeTestData(basicAuthTemplate(), "delete_질문자사용자다름 제목", "delete 내용");
		Long questionId = Optional.ofNullable(findQuestionId("delete_질문자사용자다름 제목")).orElseThrow(null);
		template().delete(String.format("/questions/%d",questionId));
		
		ResponseEntity<String> response = basicAuthTemplate().getForEntity("/", String.class);
		log.debug("body : {}", response.getBody());
		assertTrue(response.getBody().contains("delete_질문자사용자다름 제목"));
	}
	
	@Test
	public void delete_질문사용자같음_답변글쓴이다름() throws Exception {
		makeTestData(basicAuthTemplate(),"delete_질문사용자같음쓴이다름 제목", "delete_질문사용자같음쓴이다름 내용");
		Long questionId = Optional.ofNullable(findQuestionId("delete_질문사용자같음쓴이다름 제목")).orElseThrow(null);
		
		User answerUser = findByUserId("sanjigi");
		Answer newAnswer = new Answer(answerUser , "답변 글쓴이는 산지기다");
		createResource(basicAuthTemplate(answerUser), String.format("/api/questions/%d/answers", questionId), newAnswer);
		
		basicAuthTemplate().delete(String.format("/questions/%d",questionId));
		
		ResponseEntity<String> response = basicAuthTemplate().getForEntity("/", String.class);
		log.debug("body : {}", response.getBody());
		assertTrue(response.getBody().contains("delete_질문사용자같음쓴이다름 제목"));
	}
	
	@Test
	public void delete_질문사용자같음_답변글쓴이같음() throws Exception {
		makeTestData(basicAuthTemplate(),"delete_질문사용자같음쓴이같음 제목", "delete_질문사용자같음쓴이같음 내용");
		Long questionId = Optional.ofNullable(findQuestionId("delete_질문사용자같음쓴이같음 제목")).orElseThrow(null);
		
		Answer newAnswer = new Answer(defaultUser() , "답변 글쓴이는 ksm0814다");
		createResource(basicAuthTemplate(), String.format("/api/questions/%d/answers", questionId), newAnswer);
		
		basicAuthTemplate().delete(String.format("/questions/%d",questionId));
		
		ResponseEntity<String> response = basicAuthTemplate().getForEntity("/", String.class);
		log.debug("body : {}", response.getBody());
		assertTrue(!response.getBody().contains("delete_질문사용자같음쓴이다같음제목"));
	}
	


}
