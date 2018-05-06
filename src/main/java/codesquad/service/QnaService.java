package codesquad.service;

import codesquad.UnAuthorizedException;
import codesquad.domain.*;
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
    public void update(User loginUser, long id, Question updatedQuestion) throws UnAuthorizedException {
        Question question = findById(id);
        question.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws UnAuthorizedException {
        Question question = questionRepository.findOne(questionId);
        question.delete(loginUser);
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
        return answerRepository.save(answer);
    }

    public void deleteAnswer(User loginUser, long id) throws UnAuthorizedException {
        Answer deletingAnswer = answerRepository.findOne(id);
        deletingAnswer.delete(loginUser);
    }

    @Transactional
    public void updateAnswer(User loginUser, long answerId, String updatingContents) throws UnAuthorizedException{
        Answer answer = answerRepository.findOne(answerId);
        answer.update(loginUser, updatingContents);
    }
}