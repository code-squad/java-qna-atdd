package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.service.QnaService;
import codesquad.utils.HtmlFormDataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.regex.Pattern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class QuestionAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

	private Question defaultQuestion;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QnaService qnaService;

	private User newUser(String userId) {
		return userRepository.save(new User(userId, "password", "name", "email@domain.com"));
	}

	@Before
	public void setUp() throws Exception {
		defaultQuestion = new Question("제목테스트", "내용테스트");
		defaultQuestion.writeBy(defaultUser());
		defaultQuestion = questionRepository.save(defaultQuestion);
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
		ResponseEntity<String> response = template().getForEntity("/", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		log.debug("body : {}", response.getBody());
		assertTrue(response.getBody().contains(defaultQuestion.getTitle()));
	}

	@Test
	public void list_not_contain_deleted() throws Exception {
		qnaService.deleteQuestion(defaultUser(), defaultQuestion.getId());
		ResponseEntity<String> response = template().getForEntity("/", String.class);
		assertFalse(response.getBody().contains(defaultQuestion.getTitle()));
	}

	@Test
	public void show() {
		ResponseEntity<String>	response = template().getForEntity("/questions/" + defaultQuestion.getId(), String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(response.getBody().contains(defaultQuestion.getTitle()));
	}

	private ResponseEntity<String> updateForm(TestRestTemplate template) {
		return template
				.getForEntity(String.format("/questions/%d/form", defaultQuestion.getId()), String.class);
	}

	@Test
	public void updateForm_wrong_owner() {
		assertThat(updateForm(basicAuthTemplate(newUser("updateFormUser"))).getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void updateForm_owner() {
		ResponseEntity<String> response = updateForm(basicAuthTemplate());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(response.getBody().contains(defaultQuestion.getContents()));
	}

	private ResponseEntity<String> update(TestRestTemplate template) {
		return template
				.postForEntity(String.format("/questions/%d", defaultQuestion.getId()),
						HtmlFormDataBuilder.urlEncodedForm()
								.putForEntity()
								.addParameter("title", "변경테스트")
								.addParameter("contents", "내용변경테스트")
								.build(),
						String.class);
	}

	@Test
	public void update_wrong_owner() {
		assertThat(update(basicAuthTemplate(newUser("updateUser"))).getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void update() {
		ResponseEntity<String> response = update(basicAuthTemplate());
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is(String.format("/questions/%d", defaultQuestion.getId())));
		assertThat(questionRepository.findOne(defaultQuestion.getId()).getTitle(), is("변경테스트"));
	}

	private ResponseEntity<String> delete(TestRestTemplate template) {
		return template
				.postForEntity(String.format("/questions/%d", defaultQuestion.getId()),
						HtmlFormDataBuilder.urlEncodedForm()
								.deleteForEntity()
								.build(),
						String.class);
	}

	@Test
	public void delete_wrong_owner() {
		assertThat(delete(basicAuthTemplate(newUser("deleteUser"))).getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void delete() {
		ResponseEntity<String> response = delete(basicAuthTemplate());
		assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().getPath(), is("/"));
		assertTrue(questionRepository.findOne(defaultQuestion.getId()).isDeleted());
	}
}
