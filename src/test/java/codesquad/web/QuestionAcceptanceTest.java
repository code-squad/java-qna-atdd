package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.service.QnaService;
import codesquad.utils.HtmlFormDataBuilder;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.regex.Pattern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class QuestionAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private QnaService qnaService;

	private int generateQuestionIndex;

	private Question newQuestion() {
		generateQuestionIndex++;
		Question defaultQuestion = new Question("제목테스트" + generateQuestionIndex, "내용테스트" + generateQuestionIndex);
		defaultQuestion.writeBy(defaultUser());
		return questionRepository.save(defaultQuestion);
	}

	@Test
	public void createForm() throws Exception {
		ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		log.debug("body : {}", response.getBody());
	}
	
	@Test
	public void create() throws Exception {
		ResponseEntity<String> response = basicAuthTemplate()
				.postForEntity("/questions",
						HtmlFormDataBuilder.urlEncodedForm()
								.addParameter("title", "제목테스트")
								.addParameter("contents", "내용테스트")
								.build(),
						String.class);
		
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(Pattern.matches("^/questions/[0-9]+$", response.getHeaders().getLocation().getPath()), is(true));
	}
	
	@Test
	public void list() throws Exception {
		Question question = newQuestion();
		ResponseEntity<String> response = template().getForEntity("/", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		log.debug("body : {}", response.getBody());
	}

	@Test
	public void show() {
		Question question = newQuestion();
		ResponseEntity<String>	response = template().getForEntity("/questions/" + question.getId(), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(response.getBody().contains(question.getTitle()));
	}

	private ResponseEntity<String> updateForm(TestRestTemplate template, long id) {
		return template
				.getForEntity(String.format("/questions/%d/form", id), String.class);
	}

	@Test
	public void updateForm_wrong_owner() {
		assertThat(updateForm(basicAuthTemplate(newUser("updateFormUser")), newQuestion().getId()).getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void updateForm_owner() {
		Question question = newQuestion();
		ResponseEntity<String> response = updateForm(basicAuthTemplate(), question.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(response.getBody().contains(question.getContents()));
	}

	private ResponseEntity<String> update(TestRestTemplate template, long id) {
		return template
				.postForEntity(String.format("/questions/%d", id),
						HtmlFormDataBuilder.urlEncodedForm()
								.putForEntity()
								.addParameter("title", "변경테스트")
								.addParameter("contents", "내용변경테스트")
								.build(),
						String.class);
	}

	@Test
	public void update_wrong_owner() {
		assertThat(update(basicAuthTemplate(newUser("updateUser")), newQuestion().getId()).getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void update() {
		Question question = newQuestion();
		ResponseEntity<String> response = update(basicAuthTemplate(), question.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is(String.format("/questions/%d", question.getId())));
		assertThat(questionRepository.findOne(question.getId()).getTitle(), is("변경테스트"));
	}

	private ResponseEntity<String> delete(TestRestTemplate template, long id) {
		return template
				.postForEntity(String.format("/questions/%d", id),
						HtmlFormDataBuilder.urlEncodedForm()
								.deleteForEntity()
								.build(),
						String.class);
	}

	@Test
	public void delete_wrong_owner() {
		assertThat(delete(basicAuthTemplate(newUser("deleteUser")), newQuestion().getId()).getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void delete() {
		Question question = newQuestion();
		ResponseEntity<String> response = delete(basicAuthTemplate(), question.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is("/"));
		assertTrue(questionRepository.findOne(question.getId()).isDeleted());
	}
}
