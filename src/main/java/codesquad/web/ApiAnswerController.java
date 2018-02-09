package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private Logger logger = LoggerFactory.getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@PathVariable long questionId, @LoginUser User loginUser, @Valid @RequestBody AnswerDto answerDto) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, answerDto.getContents());
        return new ResponseEntity<>(ApiHeaderGenerator.generateApiHeader(savedAnswer), HttpStatus.CREATED);
    }

    @GetMapping("{answerId}")
    public AnswerDto show(@PathVariable long questionId, @PathVariable long answerId) {
        Question question = qnaService.findById(questionId);
        if (question == null) {
            return null;
        }
        logger.debug("question={}", question);
        logger.debug("question answer size={}", question.getAnswersCount());

        Answer answer = question.findAnswer(answerId);

        return (answer == null) ? null : answer.toAnswerDto();
    }

    @PutMapping("{answerId}")
    public void update(@PathVariable long questionId, @LoginUser User loginUser, @PathVariable long answerId, @Valid @RequestBody AnswerDto updatedAnswerDto) {
        qnaService.updateAnswer(loginUser, questionId, answerId, updatedAnswerDto.getContents());
    }

    @DeleteMapping("{answerId}")
    public void delete(@PathVariable long questionId, @LoginUser User loginUser, @PathVariable long answerId) {
        try {
            qnaService.deleteAnswer(loginUser, questionId, answerId);
        } catch (CannotDeleteException e) {
            logger.error("we can not delete this, answerId=" + answerId, e);
        }
    }

}
