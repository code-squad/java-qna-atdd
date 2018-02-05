package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.CannotFindException;
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

        if (question.notEmptyAnswer()) {
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

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) throws CannotFindException {
        Question question = questionRepository.findOne(questionId);
        if (question == null) {
            throw new CannotFindException("글이 없습니다.");
        }
        Answer answer = new Answer(loginUser, question, contents);
        question.addAnswer(answer);

        return answer;
    }

    public Answer findAnswer(long questionId, long answerId) throws CannotFindException {
        if (!answerRepository.exists(answerId)) {
            throw new CannotFindException("없는 답글입니다.");
        }
        Answer answer = answerRepository.findOne(answerId);
        if (!answer.isQuestion(questionId)) {
            throw new IllegalArgumentException();
        }
        return answer;
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long questionId, long answerId, String updateContents) throws CannotFindException {
        if (!answerRepository.exists(answerId)) {
            throw new CannotFindException("없는 답글입니다.");
        }
        Answer answer = answerRepository.findOne(answerId);

        return answer.update(loginUser, questionId, updateContents);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) throws CannotFindException {
        if (!answerRepository.exists(id)) {
            throw new CannotFindException("없는 답글입니다.");
        }
        Answer answer = answerRepository.findOne(id);

        answer.delete(loginUser);
    }
}
