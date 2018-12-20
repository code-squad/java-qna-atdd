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

    public Optional<Answer> findByIdAnswer(long id) {
        return answerRepository.findById(id);
    }

    @Transactional
    public Question update(User loginUser, Long id, Question updatedQuestion) throws UnAuthenticationException {
        Question question = questionRepository.findById(id).orElse(null).updateQuestion(updatedQuestion);
        return questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException, UnAuthenticationException {
        questionRepository.deleteById(questionId);
    }

    public void isOneSelfQuestion(User user, Long id) throws UnAuthenticationException {
        if(!findById((id)).orElse(null).isOneSelf(user)) {
            throw new UnAuthenticationException();
        }
    }

    public void isOneSelfAnswer(User user, Answer answer) throws UnAuthenticationException {
        if(!answer.isOneSelf(user)) {
            throw new UnAuthenticationException();
        }
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Question addAnswer(long questionId, Answer answer) {
        log.debug("Call addAnswer Method!");
        Question question = questionRepository.getOne(questionId);
        question.addAnswer(answer);
        return question;
    }

    @Transactional
    public Question deleteAnswer(long questionId, Answer answer) {
        Question question = questionRepository.getOne(questionId);
        question.deleteAnswer(answer);
        return question;
    }
}
