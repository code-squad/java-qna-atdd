package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private static final Logger logger = LoggerFactory.getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@PathVariable("questionId") long questionId, @LoginUser User loginUser, @RequestBody String contents) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionId + "/answers/" + answer.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> delete(@PathVariable("answerId") long answerId, @LoginUser User loginUser) {
        try {
            qnaService.deleteAnswer(loginUser, answerId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (CannotDeleteException e) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }
    }

}
