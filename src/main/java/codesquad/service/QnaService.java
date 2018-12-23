package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.security.HttpSessionUtils;
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
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    public Question findQuestionById(long id) {
        return questionRepository.findById(id).orElseThrow(UnAuthorizedException :: new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question original = questionRepository.findById(id).filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
        original.update(updatedQuestion, loginUser);
        return questionRepository.save(original);
    }


    public Answer updateAnswer(User loginUser, long id, String newContents) {
        Answer original = answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        original.update(newContents, loginUser);
        return answerRepository.save(original);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question original = questionRepository.findById(questionId).filter(question -> question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
        original.delete(loginUser);
        deleteHistoryService.saveAll(original.delete(loginUser));
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

    @Transactional
    public void deleteAnswer(User loginUser, long id) {
        Answer answer = answerRepository.findById(id).filter(answer1 -> answer1.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
        answer.delete(loginUser);
    }

}
