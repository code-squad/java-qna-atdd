package codesquad.web;

import codesquad.domain.*;
import codesquad.service.QnaService;
import codesquad.utils.HtmlFormDataBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AnswerAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);
	
	@Autowired
	private QnaService qnaService;

	@Autowired
	private DeleteHistoryRepository deleteHistoryRepository;

	private int generateAnswerIndex;
	private long defaultQuestionId;

	private Answer newAnswer() {
		generateAnswerIndex++;
		Answer defaultAnswer = new Answer(defaultUser(), "내용테스트" + generateAnswerIndex);
		return qnaService.addAnswer(defaultUser(), defaultQuestionId, defaultAnswer.getContents());
	}
	
	@Before
	public void setUp() {
		defaultQuestionId = qnaService.createQuestion(defaultUser(), new Question("제목테스트", "내용테스트")).getId();
	}
	
	private ResponseEntity<String> updateForm(TestRestTemplate template, long id) {
		return template
				.getForEntity(String.format("/answers/%d/form", id), String.class);
	}
	
	@Test
	public void updateForm_wrong_owner() {
		assertThat(updateForm(basicAuthTemplate(newUser("answerUpdateFormUser")), newAnswer().getId()).getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void updateForm_owner() {
		Answer answer = newAnswer();
		ResponseEntity<String> response = updateForm(basicAuthTemplate(), answer.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(response.getBody().contains(answer.getContents()));
	}
	
	private ResponseEntity<String> update(TestRestTemplate template, long id) {
		return template
				.postForEntity(String.format("/answers/%d", id),
						HtmlFormDataBuilder.urlEncodedForm()
								.putForEntity()
								.addParameter("contents", "내용변경테스트")
								.build(),
						String.class);
	}
	
	@Test
	public void update_wrong_owner() {
		assertThat(update(basicAuthTemplate(newUser("answerUpdateUser")), newAnswer().getId()).getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void update() {
		Answer answer = newAnswer();
		ResponseEntity<String> response = update(basicAuthTemplate(), answer.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is(String.format("/questions/%d", answer.getQuestion().getId())));
		assertThat(qnaService.findAnswerById(answer.getId()).getContents(), is("내용변경테스트"));
	}
	
	private ResponseEntity<String> delete(TestRestTemplate template, long id) {
		return template
				.postForEntity(String.format("/answers/%d", id),
						HtmlFormDataBuilder.urlEncodedForm()
								.deleteForEntity()
								.build(),
						String.class);
	}
	
	@Test
	public void delete_wrong_owner() {
		assertThat(delete(basicAuthTemplate(newUser("answerDeleteUser")), newAnswer().getId()).getStatusCode(), is(HttpStatus.OK));
	}
	
	@Test
	public void delete() {
		Answer answer = newAnswer();
		ResponseEntity<String> response = delete(basicAuthTemplate(), answer.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is("/questions/" + answer.getQuestion().getId()));
		assertTrue(qnaService.findAnswerById(answer.getId()).isDeleted());
		assertTrue(deleteHistoryRepository.findByContentIdAndContentType(answer.getId(), ContentType.ANSWER).isPresent());
	}
}
