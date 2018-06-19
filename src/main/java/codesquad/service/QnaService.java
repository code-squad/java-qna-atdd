package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.QuestionDto;
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

    public Question addQuestion(User loginUser, QuestionDto questionDto) {
        if (loginUser.isGuestUser()) {
            throw new UnAuthorizedException();
        }

        Question question = questionDto.toQuestion();
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findQuestionById(long id) {
        return questionRepository.findById(id);
    }

    public Optional<Answer> findAnswerById(long id) {
        return answerRepository.findById(id);
    }

    public Question findByLoginUser(Long id, User loginUser) throws EntityNotFoundException {
        return findQuestionById(id).filter(question -> question.isOwner(loginUser)).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, QuestionDto updatedQuestion) {
        Question originalQuestion = questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return originalQuestion.update(updatedQuestion.toQuestion(), loginUser);
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
        // TODO 답변 추가 기능 구현
        Question question = questionRepository.findById(questionId).orElseThrow(EntityNotFoundException::new);
        Answer answer = new Answer(loginUser, question, contents);
        return answerRepository.save(answer);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현
        Answer savedAnswer = answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        answerRepository.delete(savedAnswer);
    }

    public Long questionCount() {
        return questionRepository.count();
    }
}
