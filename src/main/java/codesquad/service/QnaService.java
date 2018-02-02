package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.CannotDeleteException;

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
        Optional<Question> question = questionRepository.findOne(id);

        return question.filter(q -> !q.isDeleted())
                .orElseThrow(EntityNotFoundException::new);
    }

    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question originalQuestion = findById(id);

        originalQuestion.update(loginUser, updatedQuestion);

        return questionRepository.save(originalQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId);

        List<DeleteHistory> histories = question.delete(loginUser);

        deleteHistoryService.saveAll(histories);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId);
        Answer answer = new Answer(loginUser, contents);

        question.addAnswer(answer);

        return answer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        Answer answer = findAnswerById(id);

        answer.delete(loginUser);

        return answer;
    }

    public Answer findAnswerById(long answerNo) {
        Answer answer = answerRepository.findByIdAndDeletedFalse(answerNo);

        if(answer == null)
            throw new EntityNotFoundException("Answer not exists");

        return answer;
    }

    public Answer updateAnswer(User loginUser, long answerNo, AnswerDto answerDto) {
        Answer answer = findAnswerById(answerNo);
        answer.update(loginUser, answerDto);

        return answerRepository.save(answer);
    }
}
