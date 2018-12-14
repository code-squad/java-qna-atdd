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
import java.util.List;
import java.util.Optional;

@Service("qnaService")
public class QnaService {
    private static final Logger logger = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        return questionRepository.save(question);
    }

    public Optional<Question> findQuestionById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question original = findQuestionByIdWithLoginUser(loginUser, id);
        original.update(updatedQuestion, loginUser);
        return original;
    }

    public Question findQuestionByIdWithLoginUser(User loginUser, long id) {
        return questionRepository.findById(id)
                .filter(q -> q.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    public Answer findAnswerByIdWithLoginUser(User loginUser, long id) {
        return answerRepository.findById(id)
                .filter(answer -> answer.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        try {
            Question question = findQuestionByIdWithLoginUser(loginUser, questionId);
            deleteHistoryService.saveAll(question.delete(loginUser));
        } catch (UnAuthorizedException e) {
            throw new CannotDeleteException("You do not have permission to delete.");
        }
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer addAnswer = new Answer(loginUser, contents);
        findQuestionById(questionId).map(question -> {
            question.addAnswer(addAnswer);
            return question;
        }).orElseThrow(IllegalArgumentException::new);

        return addAnswer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer deleteAnswer;
        try {
            deleteAnswer = findAnswerByIdWithLoginUser(loginUser, id);
        } catch (UnAuthorizedException e) {
            throw new CannotDeleteException("You do not have permission to delete.");
        }
        deleteHistoryService.save(deleteAnswer.delete(loginUser));
        return deleteAnswer;
    }
}
