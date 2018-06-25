package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;

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
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Question userCheck(User loginUser, long id) {
        log.debug("Login User : {}", loginUser);
        return questionRepository.findById(id)
                .filter(question ->  question.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    public Answer savedUserCheck(User loginUser, long id) {
        return answerRepository.findById(id)
                .filter(answer -> answer.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, String title, String contents) {
        Question updatedQuestion = userCheck(loginUser, id);
        updatedQuestion.update(title, contents);
        return questionRepository.save(updatedQuestion);
    }

    @Transactional
    public void delete(User loginUser, long questionId) {
        Question question = userCheck(loginUser, questionId);
        log.debug("q : {}", question.toString());
        questionRepository.deleteById(questionId);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer findByAnswer(long id) {
        return answerRepository.findById(id).get();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        answer.toQuestion(findById(questionId));
        return answerRepository.save(answer);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) {
        answerRepository.delete(savedUserCheck(loginUser, id));
    }
}
