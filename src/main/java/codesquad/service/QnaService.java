package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.List;

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

    public Question findQuestionById(long id) {
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Answer findAnswerById(long id) {
        return answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question updateQuestion(User loginUser, long id, Question updatedQuestion) {
        return findQuestionById(id).update(loginUser, updatedQuestion);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, String updatedContents) {
        return findAnswerById(id).update(loginUser, updatedContents);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long id) throws CannotDeleteException {
        findQuestionById(id).delete(loginUser);
        // todo 답변 삭제 기능 구현
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
        findQuestionById(questionId).addAnswer(answer);
        return answer;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
