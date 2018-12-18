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

    @Transactional(readOnly = true)
    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) throws UnAuthenticationException {
        // TODO 수정 기능 구현
        Question originQuestion = findById(id)
                .filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthenticationException::new);
        originQuestion.update(updatedQuestion, loginUser);
        return questionRepository.save(originQuestion);
    }

    @Transactional
    public void delete(User loginUser, long questionId) throws CannotDeleteException {
        // TODO 삭제 기능 구현
        try {
            Question originQuestion = findById(questionId)
                    .filter(question -> question.isOwner(loginUser))
                    .orElseThrow(UnAuthenticationException::new);
            originQuestion.delete(loginUser);
        } catch (UnAuthenticationException e) {
            throw new CannotDeleteException("삭제 안됨");
        }
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        return null;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
