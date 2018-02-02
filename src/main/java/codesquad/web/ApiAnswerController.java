package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;


    @PostMapping("")
    public ResponseEntity<Void> create(@Valid @RequestBody AnswerDto answerDto, @PathVariable long questionId, @LoginUser User user) throws Exception {
        Answer saveAnswer = qnaService.addAnswer(user, questionId, answerDto.toAnswer());
        log.debug("saveAnswer : {}", saveAnswer);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(saveAnswer.generateUrl()));
        log.debug("setLocation : {}", URI.create(saveAnswer.generateUrl()));

        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public AnswerDto show(@PathVariable long questionId, @PathVariable long id) throws Exception {
        Answer answer = qnaService.findAnswerById(id);
        log.debug("answer from repository : {}", answer);
        return answer.toAnswerDto();
    }

    @DeleteMapping("{answerId}")
    public ResponseEntity<Void> delete(@PathVariable long questionId,@PathVariable long answerId, @LoginUser User user) throws Exception {
        qnaService.deleteAnswer(user ,answerId);
        log.debug("Deleted answer! {}", qnaService.findById(questionId));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(qnaService.findAnswerById(answerId).generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
    }
}
