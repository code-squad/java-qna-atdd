package codesquad.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import codesquad.domain.*;
import codesquad.dto.QuestionDto;
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

    public Optional<Question> findById(long id) {
        return Optional.ofNullable(questionRepository.findOne(id))
                        .filter(i -> !i.isDeleted());
    }

    @Transactional
    public void update(User loginUser, long id, Question newQuestion) throws UnAuthorizedException {
        Optional<Question> optQuestion = findById(id);
        optQuestion.ifPresent(question -> question.update(loginUser, newQuestion));
    }

    @Transactional
    public void updateAnswer(User loginUser, long id, Answer newAnswer) throws UnAuthorizedException {
        Answer answer = findOneAnswer(id);
        answer.update(loginUser, newAnswer);
    }

    @Transactional(rollbackFor = CannotDeleteException.class)
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findOne(questionId);
        List<DeleteHistory> questionDeleteHistoryList = question.delete(loginUser);

        deleteHistoryService.saveAll(questionDeleteHistoryList);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = findOneAnswer(id);
        DeleteHistory answerDeleteHistory = answer.delete(loginUser);

        deleteHistoryService.saveAll(Arrays.asList(answerDeleteHistory));
    }

    public Answer findOneAnswer(long id) {
        return answerRepository.findOne(id);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) throws EntityNotFoundException {
        Optional<Question> optQuestion = findById(questionId);
        Question question = optQuestion.orElseThrow(EntityNotFoundException::new);

        Answer answer = new Answer()
                .setContents(contents)
                .setWriter(loginUser)
                .setQuestion(question);
        question.addAnswer(answer);

        return answer;
    }


}
