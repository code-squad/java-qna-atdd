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
    private QuestionRepository questionRepo;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepo;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, QuestionDto questionDto) {
        Question question = questionDto.toQuestion();
        question.writeBy(loginUser);
        return questionRepo.save(question);
    }

    public Question findById(Long id) {
        return questionRepo.findById(id).filter(question -> !question.isDeleted()).orElseThrow(EntityNotFoundException::new);
    }

    public Question findById(User loginUser, Long id) {
        Question question = findById(id);
        if (!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        return question;
    }

    public QuestionDto update(User loginUser, Long id, QuestionDto updatedQuestionDto) {
        Optional<Question> maybeQuestion = questionRepo.findById(id);
        QuestionDto questionDto = maybeQuestion.map(question -> question.update(loginUser, updatedQuestionDto)).orElseThrow(EntityNotFoundException::new);
        questionRepo.save(maybeQuestion.get());
        return questionDto;
    }

    @Transactional
    public void deleteQuestion(User loginUser, Long id) throws CannotDeleteException {
        deleteHistoryService.saveAll(findById(id).delete(loginUser));
    }

    public List<Question> findAll() {
        return questionRepo.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepo.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        return null;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }
}
