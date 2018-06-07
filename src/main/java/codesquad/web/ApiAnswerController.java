package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api/questions/{questionId}/answers")
@RestController
public class ApiAnswerController {

    private final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable Long questionId, @RequestBody String contents) {
        log.debug("contents is : {}", contents);
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, contents);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(String.format("/api/questions/%d/answers/%d", questionId, savedAnswer.getId())));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return ResponseEntity.ok(qnaService.findAnswerById(id));
    }

    @PutMapping("/{id}")
    public Answer update(@LoginUser User loginUser, @PathVariable Long id, @RequestBody String contents) {
        return qnaService.update(loginUser, id, contents);
    }

    @DeleteMapping("/{id}")
    public Answer delete(@LoginUser User loginUser, @PathVariable Long id) {
        return qnaService.deleteAnswer(loginUser, id);
    }

}


