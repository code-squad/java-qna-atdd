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
import codesquad.domain.DeleteHistory;
import codesquad.domain.DeleteHistoryRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;

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
	
	public Question add(QuestionDto questionDto, User loginUser) {
		Question question = questionDto.toQuestion();
		question.writeBy(loginUser);
		return questionRepository.save(question);
	}

	public Question findById(long id) {
		return questionRepository.findOne(id);
	}

	public Question update(User loginUser, long id, Question updatedQuestion) throws CannotDeleteException {
		Question question = questionRepository.findOne(id);
		
		if (!question.isOwner(loginUser)) {
			throw new CannotDeleteException("수정 권한이 없습니다.");
		}
		question.update(loginUser, updatedQuestion.getTitle(), updatedQuestion.getContents());
		return questionRepository.save(question);
	}

	@Transactional
	public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
		Question question = questionRepository.findOne(questionId);
		List<DeleteHistory> histories = question.deleteQuestion(loginUser);
		deleteHistoryService.saveAll(histories);
		questionRepository.save(question);
		log.debug("question delete status : " + question.isDeleted());
	}

	public Iterable<Question> findAll() {
		return questionRepository.findByDeleted(false);
	}

	public List<Question> findAll(Pageable pageable) {
		return questionRepository.findAll(pageable).getContent();
	}

	public Answer addAnswer(User loginUser, long questionId, String contents) {
		Question question = questionRepository.findOne(questionId);
		Answer answer = new Answer(loginUser, contents);
		
		question.addAnswer(answer);
		answerRepository.save(answer);
		questionRepository.save(question);
		return answer;
	}
	
	public Answer updateAnswer(User loginUser, long id, String contents) {
		Answer answer = answerRepository.findOne(id);
		if (!answer.isOwner(loginUser)) {
			return answer;
		}
		answerRepository.save(answer.update(contents));
		return answer;
	}

	public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
		Answer answer = answerRepository.findOne(id);
		if (!answer.isOwner(loginUser)) {
			return answer;
		}
		answer.delete(loginUser);
		return answerRepository.save(answer);
	}
}
