package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
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
        if (loginUser.isGuestUser()) {
            throw new UnAuthorizedException();
        }

        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    public Question findQuestion(Long id, User loginUser) throws EntityNotFoundException {
        return findById(id).filter(question -> question.isOwner(loginUser)).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void update(User loginUser, long id, Question updatedQuestion) {
        Question originalQuestion = questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        originalQuestion.update(updatedQuestion, loginUser);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question originalQuestion = questionRepository.findById(questionId).orElseThrow(EntityNotFoundException::new);

        if (!originalQuestion.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        questionRepository.delete(originalQuestion);
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

    public Long questionCount() {
        return questionRepository.count();
    }
}
