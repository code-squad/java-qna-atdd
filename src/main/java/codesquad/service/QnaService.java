package codesquad.service;

import java.util.List;

import javax.annotation.Resource;

import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
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

	public Question create(User loginUser, QuestionDto questionDto) {
		return create(loginUser, questionDto.toQuestion());
	}

	public Question create(User loginUser, Question question) {
		question.writeBy(loginUser);
		log.debug("question : {}", question);
		return questionRepository.save(question);
	}

	public Question findById(long id) {
		return questionRepository.findOne(id);
	}

	@Transactional
	public Question update(User loginUser, long id, QuestionDto updatedQuestionDto) {
		log.debug("before: {}", questionRepository.findAll());
		Question question = questionRepository.findOne(id);
		question.update(loginUser, updatedQuestionDto);
		log.debug("after: {}", questionRepository.findAll());
		return question;
	}

	@Transactional
	public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
		Question question = questionRepository.findOne(questionId);
		question.delete(loginUser);
	}

	public Iterable<Question> findAll() {
		return questionRepository.findByDeleted(false);
	}

	public List<Question> findAll(Pageable pageable) {
		return questionRepository.findAll(pageable).getContent();
	}

	@Transactional
	public Answer addAnswer(User loginUser, long questionId, Answer answer) {
		answer.writeBy(loginUser);

		Question question = findById(questionId);
		question.addAnswer(answer);

		return answer;
	}

	public Answer findAnswerById(long id) {
		return answerRepository.findOne(id);
	}

	@Transactional
	public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
		Answer answer = answerRepository.findOne(id);
		answer.delete(loginUser);
	}

}
