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
import java.util.List;

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
        return questionRepository.findOne(id);
    }

    @Transactional
    public Question update(User loginUser, QuestionDto questionDto) {
        Question question = questionRepository.findOne(questionDto.getId());
        Question updatedQuestion = questionDto.toQuestion();
        updatedQuestion.writeBy(loginUser);
        question.update(updatedQuestion);

        return question;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId);
        if (!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        if(question.notEmptyAnswer()) {
            throw new CannotDeleteException("답글이 있는 질문은 삭제할 수 없습니다.");
        }

        if (question.isDeleted()) {
            throw new CannotDeleteException("삭제된 글입니다.");
        }

        if (!questionRepository.exists(questionId)) {
            throw new CannotDeleteException("없는 글입니다.");
        }
        question.delete();
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
