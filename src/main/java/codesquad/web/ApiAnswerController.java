package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    @Resource(name = "qnaService")
    private QnaService qnaService;


    @PostMapping("")
    public ResponseEntity<String> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody String answerContents) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, answerContents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(answer.getResoureURI()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{answerId}")
    public ResponseEntity<String> update(@LoginUser User loginUser, @PathVariable long answerId,
                                         @RequestBody String updatingAnswer) throws UnAuthorizedException {

        qnaService.updateAnswer(loginUser, answerId, updatingAnswer);
        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<String> delete(@LoginUser User loginUser, @PathVariable long answerId) throws UnAuthorizedException {

        qnaService.deleteAnswer(loginUser, answerId);
        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.NO_CONTENT);
    }
}