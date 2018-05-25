package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import codesquad.domain.QuestionRepository;
import codesquad.dto.QuestionDto;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

	@Autowired
	private QuestionRepository questionRepository;

	private static final Long EXIST_QUESTION = 1L;

	@Test
	public void create() {
		QuestionDto question = createQuestionDto(3L);
		String location = createResource(basicAuthTemplate(), "/api/questions", question);
		assertThat(getResource(location, QuestionDto.class, defaultUser()), is(question));
	}

	@Test
	public void show() {
		assertThat(questionRepository.findById(EXIST_QUESTION).get().toQuestionDto(), is(getResource(String.format("/api/questions/%d", EXIST_QUESTION), QuestionDto.class, defaultUser())));
	}

	@Test
	public void update_owner() {
		update(basicAuthTemplate());
		assertThat(getResource(location(EXIST_QUESTION), QuestionDto.class, defaultUser()), is(createQuestionDto(EXIST_QUESTION)));
	}
	
	@Test
	public void update_noOwner() {
		update(basicAuthTemplate(defaultOtherUser()));
		assertNotEquals(getResource(location(EXIST_QUESTION), QuestionDto.class, defaultOtherUser()), is(createQuestionDto(EXIST_QUESTION)));
	}
	
	public void update(TestRestTemplate template) {
		String location = String.format("/api/questions/%d", EXIST_QUESTION);
		template.put(location, createQuestionDto(EXIST_QUESTION));
	}
	
	public String location(Long id) {
		return  String.format("/api/questions/%d", id);
	}

	@Test
	public void delete() {
		createResource(basicAuthTemplate(), "/api/questions",  createQuestionDto(4L));
		assertThat(questionRepository.findById(4L).get().isDeleted(), is(false));
		basicAuthTemplate().delete("/api/questions/4");
		assertThat(questionRepository.findById(4L).get().isDeleted(), is(true));
	}
	
	public QuestionDto createQuestionDto(Long id) {
		return new QuestionDto(id, "제목제목", "내용내용");
	}


}
