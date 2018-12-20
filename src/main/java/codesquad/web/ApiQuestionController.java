package codesquad.web;

import codesquad.domain.Question;
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
@RequestMapping("/api/questions")
public class ApiQuestionController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question savedQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Question show(@PathVariable Long id) {
        return qnaService.findQuestionById(id);
    }

    @PutMapping("{id}")
    public Question update(@LoginUser User loginUser, @PathVariable Long id, @Valid @RequestBody Question updatedQuestion) {
        return qnaService.updateQuestion(loginUser, id, updatedQuestion);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable Long id) {
        qnaService.deleteQuestion(loginUser, id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
