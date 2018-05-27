package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.naming.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;

@Service("qnaService")
public class QnaService {
	private static final Logger log = LoggerFactory.getLogger(QnaService.class);

	@Resource(name = "questionRepository")
	private QuestionRepository questionRepository;

	@Resource(name = "answerRepository")
	private AnswerRepository answerRepository;

	@Resource(name = "deleteHistoryService")
	private DeleteHistoryService deleteHistoryService;

	public Question create(User loginUser, Question question) {
		question.writeBy(loginUser);
		log.debug("question : {}", question);
		return questionRepository.save(question);
	}

	public Optional<Question> findById(long id) {
		return questionRepository.findById(id);
	}

	
	@Transactional
	public Question update(User loginUser, long id, Question updatedQuestion) throws AuthenticationException {
		Question question = questionRepository.findById(id).orElseThrow(NullPointerException::new);
		question.update(updatedQuestion, loginUser);
		return question;
	}

	@Transactional
	public void deleteQuestion(User loginUser, long id) throws CannotDeleteException, AuthenticationException {
		Question question = questionRepository.findById(id).orElseThrow(NullPointerException::new);
		deleteHistoryService.saveAll(question.delete(loginUser));
		/*
		 * 이거들어가니까 먼가로직적오류가나긴합니다.
		DeleteHistory deleteHistory = new DeleteHistory(ContentType.QUESTION, id, loginUser);
		List<DeleteHistory> deleteHistories = null;
		deleteHistoryService.saveAll(deleteHistories);
		*/
		
	}

	public Iterable<Question> findAll() {
		return questionRepository.findByDeleted(false);
	}

	public List<Question> findAll(Pageable pageable) {
		return questionRepository.findAll(pageable).getContent();
	}

	public Answer addAnswer(User loginUser, long questionId, String contents) {
		Question question = questionRepository.findById(questionId).orElseThrow(NullPointerException::new);
		return answerRepository.save(new Answer(loginUser, contents, question));
	}

	@Transactional
	public void deleteAnswer(User loginUser, long id) throws CannotDeleteException, AuthenticationException{
		Answer answer = answerRepository.findById(id).orElseThrow(NullPointerException::new);
		answer.delete(loginUser);
	}
}
