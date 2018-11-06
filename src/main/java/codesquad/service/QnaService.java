package codesquad.service;

import java.util.List;

import javax.annotation.Resource;

import codesquad.ResourceNotFoundException;
import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.DeleteHistory;
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

    public Question create(User loginUser, QuestionDto questionDto) {
        Question question = questionDto.toQuestion();
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public Question update(User loginUser, long id, QuestionDto updatedQuestion) {
        Question question = findById(id);
        question.update(loginUser, updatedQuestion.toQuestion());
        return questionRepository.save(question);
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

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId);
        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);
        questionRepository.save(question);
        return answer;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        Answer answer = answerRepository.findById(id).get();
        answer.deletedBy(loginUser);
        answerRepository.delete(answer);
        return answer;
    }
}
