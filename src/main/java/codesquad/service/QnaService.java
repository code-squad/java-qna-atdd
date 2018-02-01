package codesquad.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import codesquad.domain.*;
import codesquad.etc.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.etc.CannotDeleteException;

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
        return Optional.of(questionRepository.findOne(id))
                        .filter(i -> !i.isDeleted())
                        .orElse(null);
    }

    @Transactional
    public void update(User loginUser, long id, Question updatedQuestion) throws UnAuthorizedException {
        Question question = findById(id);
        question.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        if (loginUser == null)
            throw new CannotDeleteException("No login user.");

        Question question = questionRepository.findOne(questionId);
        if (!loginUser.equals(question.getWriter()))
            throw new CannotDeleteException("No authentication on this question.");

        question.setDeleted(true);

        DeleteHistory questionDeleteHistory = new DeleteHistory(ContentType.QUESTION,
                question.getId(),
                loginUser,
                LocalDateTime.now());

        List<Answer> answers = question.getAnswers();

        for (Answer answer: answers) {
            deleteAnswer(loginUser, answer.getId());
        }

        deleteHistoryService.saveAll(Arrays.asList(questionDeleteHistory));
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        DeleteHistory answerDeleteHistory = new DeleteHistory(ContentType.ANSWER,
                id,
                loginUser,
                LocalDateTime.now());

        deleteHistoryService.saveAll(Arrays.asList(answerDeleteHistory));
        Answer answer = answerRepository.findOne(id);
        answer.delete(loginUser);
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
}
