package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import support.utils.PagingUtils;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
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
    public void deleteQuestion(User loginUser, long id) {
        try {
            deleteHistoryService.saveAll(findQuestionById(id).delete(loginUser));
        } catch (CannotDeleteException e) {
            throw new UnAuthorizedException();
        }
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public Page<Question> findAll(Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(1) - 1, PagingUtils.DEFAULT_PAGE_QUESTION_COUNT, Sort.Direction.DESC, "createdAt", "id");
        return questionRepository.findByDeleted(false, pageable);
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        findQuestionById(questionId).addAnswer(answer);
        return answer;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        try {
            Answer answer = findAnswerById(id);
            deleteHistoryService.save(answer.delete(loginUser));
            return answer;
        } catch(CannotDeleteException e) {
            throw new UnAuthorizedException();
        }
    }
}
