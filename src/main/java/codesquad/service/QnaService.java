package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question originQuestion = questionRepository.findById(id).get();
        originQuestion.update(loginUser, updatedQuestion);
        return originQuestion;
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question targetQuestion = questionRepository.findById(questionId).get();
        log.debug("targetQuestion : {}", targetQuestion);
        deleteHistoryService.saveAll(targetQuestion.delete(loginUser));
        return targetQuestion;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        answer.toQuestion(questionRepository.findById(questionId).get());
        return answerRepository.save(answer);
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer targetAnswer = answerRepository.findById(id).get();
        deleteHistoryService.save(targetAnswer.delete(loginUser));
        return targetAnswer;
    }
}
