package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.exception.CannotDeleteException;
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
    public Question show(@PathVariable long id) {
        return qnaService.findById(id);
    }

    @PutMapping("{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question updatedQuestion) {
        return qnaService.update(loginUser, id, updatedQuestion);
    }

    @DeleteMapping("{id}")
    public Question delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        return qnaService.deleteQuestion(loginUser, id);
    }

    //TODO : 답변 기능 구현 필요
}