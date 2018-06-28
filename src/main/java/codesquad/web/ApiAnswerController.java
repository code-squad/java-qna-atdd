package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
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
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/{answerId}")
    public Answer show(@PathVariable long answerId) {
        return qnaService.getAnswer(answerId);
    }

    @PostMapping
    public ResponseEntity<Void> addAnswer(@LoginUser User loginUser, @PathVariable long questionId, String comment) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, comment);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionId + "/answers/" + savedAnswer.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{answerId}")
    public Answer update(@LoginUser User loginUser, @PathVariable long answerId, @Valid @RequestBody Answer updateAnswer) {
        return qnaService.updateAnswer(loginUser, answerId, updateAnswer);
    }
}
