package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.CannotDeleteException;

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

    public Question show(long id) {
        return findById(id).filter(q -> !q.isDeleted()).orElseThrow(UnAuthorizedException::new);
    }

    public Question checkOwner(User loginUser, long id) {
        return findById(id).filter(q -> q.isOwner(loginUser)).orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public void update(User loginUser, long id, Question updatedQuestion) {
        Question original = findById(id).orElseThrow(UnAuthorizedException::new);
        original.update(updatedQuestion, loginUser);
        log.debug("update Success : {}", original);
    }

    @Transactional(rollbackFor = CannotDeleteException.class)
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId).get();
        List<DeleteHistory> deleteHistories = question.delete(loginUser);
        deleteHistoryService.saveAll(deleteHistories);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer findByIdAnswer(long id) {
        return answerRepository.findById(id).filter(a -> !a.isDeleted()).orElseThrow(UnAuthorizedException::new);
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        answer.toQuestion(findById(questionId).get());
        return answerRepository.save(answer);
    }

    @Transactional
    public void updateAnswer(User loginUser, long id, String updateContents) {
        Answer answer = findByIdAnswer(id);
        answer.update(loginUser, updateContents);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = findByIdAnswer(id);
        DeleteHistory deleteHistory = answer.delete(loginUser);
        deleteHistoryService.saveDeleteHistoryOfAnswer(deleteHistory);
    }
}
