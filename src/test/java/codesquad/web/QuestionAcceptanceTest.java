package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest{
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private static final Long EXISTS_QUESTION_ID = 1L;
    private static final Long ANOTHER_EXISTS_QUESTION_ID = 3L;
    
	@Autowired
	QuestionRepository questionRepository;
	
	@Test
	public void createForm() {
		ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void createSuccess_login() {
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
				.addParameter("title", "test제목")
				.addParameter("contents", "test내용").build();
		
        User loginUser = defaultUser();
		ResponseEntity<String> response =  basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(questionRepository.findById(ANOTHER_EXISTS_QUESTION_ID).isPresent(), is(true));
		assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
	}
	
	@Test
	public void createFail_no_login() {
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
				.addParameter("title", "test제목")
				.addParameter("contents", "test내용").build();
		
		ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void showQuestion() {
		ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", EXISTS_QUESTION_ID), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void updateForm_login_owner() {
		ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity(String.format("/questions/%d/form", ANOTHER_EXISTS_QUESTION_ID), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		Question question = questionRepository.findById(ANOTHER_EXISTS_QUESTION_ID).get();
        assertThat(response.getBody().contains(question.getContents()), is(true));
	}
	
	@Test
	public void updateForm_login_noOwner() {
		ResponseEntity<String> response = basicAuthTemplate(defaultOtherUser()).getForEntity(String.format("/questions/%d/form", ANOTHER_EXISTS_QUESTION_ID), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		Question question = questionRepository.findById(ANOTHER_EXISTS_QUESTION_ID).get();
        assertThat(response.getBody().contains(question.getContents()), is(false));
	}
	
	@Test
	public void updateForm_no_login() {
		ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", EXISTS_QUESTION_ID), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void update() {
		log.debug("수정 전:"+questionRepository.findById(EXISTS_QUESTION_ID).toString());

		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
				.put()
				.addParameter("title", "질문제목수정test")
				.addParameter("contents", "질문내용수정test").build();
		ResponseEntity<String> response = basicAuthTemplate().postForEntity(String.format("/questions/%d", EXISTS_QUESTION_ID), request, String.class);
		
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is(String.format("/questions/%d", EXISTS_QUESTION_ID)));
		log.debug("수정 후:"+questionRepository.findById(EXISTS_QUESTION_ID).toString());
	}
	
	
	@Test
	public void delete_login_owner() {
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
				.delete().build();
		ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity(String.format("/questions/%d",EXISTS_QUESTION_ID), request, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(questionRepository.findById(EXISTS_QUESTION_ID), is(Optional.empty()));
	}
	
	@Test
	public void delete_login_noOwner() {
		log.debug("삭제 전:"+questionRepository.findById(EXISTS_QUESTION_ID).isPresent());
		HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().delete().build();
		ResponseEntity<String> response = basicAuthTemplate(defaultOtherUser()).postForEntity(String.format("/questions/%d",EXISTS_QUESTION_ID), request, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		log.debug("삭제 후(실패임):"+questionRepository.findById(EXISTS_QUESTION_ID).isPresent());
	}
	
	
}
