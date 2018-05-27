package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import codesquad.domain.AnswerRepository;
import codesquad.domain.ContentType;
import codesquad.domain.DeleteHistory;
import codesquad.domain.DeleteHistoryRepository;
import codesquad.domain.QuestionRepository;
import codesquad.dto.QuestionDto;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

	@Autowired
	private QuestionRepository questionRepository;
	
	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private DeleteHistoryRepository deleteHistoryRepository;

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
	public void delete_댓글없음() {
		assertThat(questionRepository.findById(2L).get().isDeleted(), is(false));
		basicAuthTemplate(defaultAnotherUser()).delete("/api/questions/2");
		assertThat(questionRepository.findById(2L).get().isDeleted(), is(true));
		assertThat(deleteHistoryRepository.findById(1L).get() , is(new DeleteHistory(1L,ContentType.QUESTION, 2L, defaultAnotherUser())));
	}
	
	@Test
	public void delete_댓글존재_모든댓글로그인유저꺼() {
		createResource(basicAuthTemplate(), String.format("/api/answers/%d", 3L), "내용은5자이상");
		assertThat(questionRepository.findById(3L).get().isDeleted(), is(false));
		basicAuthTemplate().delete("/api/questions/3");
		assertThat(questionRepository.findById(3L).get().isDeleted(), is(true));
		assertThat(answerRepository.findById(4L).get().isDeleted(), is(true));
		
		assertThat(deleteHistoryRepository.findById(2L).get() , is(new DeleteHistory(2L, ContentType.ANSWER, 4L, defaultUser())));
		assertThat(deleteHistoryRepository.findById(3L).get() , is(new DeleteHistory(3L, ContentType.QUESTION, 3L, defaultUser())));
	}
	
	@Test
	public void delete_댓글존재_다른유저의댓글있음() {
		assertThat(questionRepository.findById(1L).get().isDeleted(), is(false));
		basicAuthTemplate().delete("/api/questions/1");
		assertThat(questionRepository.findById(1L).get().isDeleted(), is(false));
		assertThat(answerRepository.findById(1L).get().isDeleted(), is(false));
		assertThat(answerRepository.findById(2L).get().isDeleted(), is(false));
		assertThat(answerRepository.findById(3L).get().isDeleted(), is(false));
	}
	
	public QuestionDto createQuestionDto(Long id) {
		return new QuestionDto(id, "제목제목", "내용내용");
	}


}
