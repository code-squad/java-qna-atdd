package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private static final Logger log = getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody String contents) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionId + "/answers/" + savedAnswer.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{answerId}")
    public Answer show(@PathVariable long questionId, @PathVariable long answerId) {
        return qnaService.findAnswerById(answerId);
    }

    @PutMapping("{answerId}")
    public Answer update(@LoginUser User loginUser, @PathVariable long answerId,  @RequestBody String contents) {
        return qnaService.updateAnswer(loginUser, answerId, contents);
    }

    @DeleteMapping("{answerId}")
    public Answer delete(@LoginUser User loginUser, @PathVariable long answerId) {
        return qnaService.deleteAnswer(loginUser, answerId);
    }
}
