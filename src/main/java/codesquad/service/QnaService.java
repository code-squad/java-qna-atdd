package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
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

    public Question findById(long id) {
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        // TODO 수정 기능 구현
        Question question = questionRepository.findById(id).orElseThrow(UnAuthorizedException::new);
        return question.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        // TODO 삭제 기능 구현
        Question question = questionRepository.findById(questionId).orElseThrow(UnAuthorizedException::new);
        deleteHistoryService.saveAll(question.delete(loginUser));
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        Answer answer = new Answer(loginUser, contents);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(UnAuthorizedException::new);
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        // TODO 답변 삭제 기능 구현
        Answer answer = answerRepository.findById(id)
                .filter(a -> a.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
        deleteHistoryService.save(answer.delete(loginUser));
        return answer;
    }

    public void oneSelf(User loginUser, long id) {
        questionRepository.findById(id)
                .filter(q -> q.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }
}
