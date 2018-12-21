package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, String contents, @PathVariable long questionId) {
        qnaService.addReply(loginUser, questionId, contents);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }
}
