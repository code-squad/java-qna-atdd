package codesquad.service;

import codesquad.domain.Answer;
import codesquad.domain.AnswerNotFoundException;
import codesquad.domain.AnswerRepository;
import codesquad.domain.CannotDeleteException;
import codesquad.domain.CannotUpdateException;
import codesquad.domain.Question;
import codesquad.domain.QuestionNotFoundException;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, QuestionDto questionDto) {
        Question question = new Question(loginUser, questionDto);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findOne(id)
                .orElseThrow(QuestionNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long questionId, QuestionDto questionDto) throws CannotUpdateException {
        Question question = findById(questionId);
        return question.update(loginUser, questionDto);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId);
        deleteHistoryService.saveAll(question.delete(loginUser));
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId);
        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);

        return answerRepository.save(answer);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, String content) throws CannotUpdateException {
        Answer answer = findAnswerById(id);
        return answer.update(loginUser, content);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = findAnswerById(id);
        deleteHistoryService.save(answer.delete(loginUser));
    }

    private Answer findAnswerById(long id) {
        return answerRepository.findOne(id)
                .orElseThrow(AnswerNotFoundException::new);
    }
}
