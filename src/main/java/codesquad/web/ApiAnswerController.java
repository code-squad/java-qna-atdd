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


    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody String contents, @PathVariable long questionId) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(String.format("/api/questions/%d/answers/%d", questionId, answer.getId())));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public Answer get(@PathVariable long id) {
        Answer answer = qnaService.findByAnswerId(id);
        return answer;
    }

    @PutMapping("/{id}")
    public Answer update(@LoginUser User loginUser, @PathVariable long id,
                       @Valid @RequestBody Answer updateAnswer) {
        return qnaService.updateAnswer(loginUser, id, updateAnswer);
    }
}
