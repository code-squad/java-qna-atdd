package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

//REST CONTROLLER를 쓰는 이유 JSON이나 XML형식의 데이터를 응답하려고 쓴다

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @RequestBody QuestionDto question) {
        Question savedQuestion = qnaService.create(loginUser, question.toQuestion());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public QuestionDto show(@PathVariable long id) {
        Question question = qnaService.findById(id);
        return question.toQuestionDto();
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id, @LoginUser User loginUser, @Valid @RequestBody QuestionDto question) {
        qnaService.update(loginUser, id, question.getTitle(), question.getContents());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id, @LoginUser User loginUser) {
        qnaService.delete(loginUser, id);
    }
}
