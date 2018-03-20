package codesquad.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;

@Service("qnaService")
public class QnaService {
	private static final Logger log = LoggerFactory.getLogger(QnaService.class);

	@Resource(name = "questionRepository")
	private QuestionRepository questionRepository;

	@Resource(name = "answerRepository")
	private AnswerRepository answerRepository;

	@Resource(name = "deleteHistoryService")
	private DeleteHistoryService deleteHistoryService;

	public Question create(User loginUser, QuestionDto newQuestion) {
		Question question = new Question(newQuestion.getTitle(), newQuestion.getContents());
		question.writeBy(loginUser);
		return questionRepository.save(question);
	}

	public Question findById(long id) {
		return questionRepository.findOne(id);
	}

	public Question update(User loginUser, long id, QuestionDto updatequestion) {
		Question oldQuestion = findById(id);
		oldQuestion.update(loginUser, updatequestion);
		return questionRepository.save(oldQuestion);
	}

	@Transactional
	public void deleteQuestion(User loginUser, long id) throws CannotDeleteException {
		List<DeleteHistory> deleteHistories = Optional.ofNullable(findById(id).delete(loginUser)).orElse(new ArrayList<>());
		deleteHistoryService.saveAll(deleteHistories);
	}

	public Iterable<Question> findAll() {
		return questionRepository.findByDeleted(false);
	}

	public List<Question> findAll(Pageable pageable) {
		return questionRepository.findAll(pageable).getContent();
	}

	public Answer addAnswer(User loginUser, long questionId, String contents) {
		Answer newAnswer = new Answer(loginUser, contents);
		Question question = findById(questionId);

		if(question.isDeleted())
			throw new IllegalStateException("이미 삭제되어있는 질문입니다.");
		
		findById(questionId).addAnswer(newAnswer);
		return answerRepository.save(newAnswer);
	}

	public Answer deleteAnswer(User loginUser, long id) {
		Answer deleteAnswer = answerRepository.findOne(id);
		return deleteAnswer;
	}
}
