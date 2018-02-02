package codesquad.service;

import java.util.List;

import javax.annotation.Resource;

import codesquad.domain.DeleteHistory;
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
@Transactional
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

    public Answer findAnswerById(long id) {
        return answerRepository.findOne(id);
    }

    public Question update(User loginUser, long id, Question updatedQuestion) throws IllegalAccessException {
        Question question = findById(id);
        return question.update(loginUser, updatedQuestion);
    }

    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findOne(questionId);
        deleteHistoryService.saveAll(question.delete(loginUser, question));
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        Question question = findById(questionId);
        question.addAnswer(answer);
        return answerRepository.save(answer);
    }

    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = findAnswerById(id);
        answer.delete(loginUser);
    }

    public Answer updateAnswer(User loginUser, Long answerId, String contents) throws IllegalAccessException {
        Answer answer = findAnswerById(answerId);
        return answer.update(contents, loginUser);
    }
}
