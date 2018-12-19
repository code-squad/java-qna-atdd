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
    QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody String contents) {
        Answer savedAnswer = qnaService.addAnswer(loginUser,questionId,contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionId + "/answers/" + savedAnswer.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Answer show(@PathVariable long id) {
        return qnaService.findAnswerById(id).get();
    }

    @PutMapping("{id}")
    public Answer update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody String contents) {
        return qnaService.updateAnswer(loginUser, id, contents);
    }

}
