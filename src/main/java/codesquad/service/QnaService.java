package codesquad.service;

import java.util.List;

import javax.annotation.Resource;

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

	public Question findById(long id) {
		return questionRepository.findOne(id);
	}

	public Question update(User loginUser, long id, Question updatedQuestion) throws CannotDeleteException {
		questionRepository.save(updatedQuestion);
		return updatedQuestion;
	}

	@Transactional
	public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
		Question question = questionRepository.findOne(questionId);
		if (!question.isDeleted()) {
			question.deleteQuestion();
		}
		return;
	}

	public Iterable<Question> findAll() {
		return questionRepository.findByDeleted(false);
	}

	public List<Question> findAll(Pageable pageable) {
		return questionRepository.findAll(pageable).getContent();
	}

	public Answer addAnswer(User loginUser, long questionId, String contents) {
		return null;
	}

	public Answer deleteAnswer(User loginUser, long id) {
		// TODO 답변 삭제 기능 구현 
		return null;
	}
}
