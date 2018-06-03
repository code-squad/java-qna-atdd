package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import codesquad.UnAuthenticationException;
import codesquad.domain.*;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
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

    public Question create(User loginUser, QuestionDto questionDto) {
        Question question = questionDto.toQuestion();
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id, Question updated) {
        return questionRepository.findById(id)
                .filter(question -> question.equals(updated))
                .orElseThrow(UnAuthenticationException::new);
    }

    @Transactional
    public void update(User loginUser, long id, QuestionDto updatedQuestionDto) {
        Question updated = updatedQuestionDto.toQuestion();
        Question original = findById(id, updated);
        original.updateQuestion(updated, loginUser);
    }

    @Transactional //TODO: questionId used in get request, is this a problem?
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findById(questionId)
                .filter(q -> !q.isDeleted())
                .orElseThrow(CannotDeleteException::new);

        DeleteHistory deletedQuestion = question.deleteQuestion(loginUser);
        List<DeleteHistory> deleted = question.deleteAnswers(loginUser);
        deleted.add(deletedQuestion);
        deleteHistoryService.saveAll(deleted);
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

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
