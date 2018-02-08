package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

/**
 * Created by hoon on 2018. 2. 7..
 */
@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    private final QnaService qnaService;

    @Autowired
    public ApiQuestionController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping
    public Iterable<Question> findAllQuestion() {
        return this.qnaService.findAll();
    }

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question savedQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{idx}")
    public Question show(@PathVariable Long idx) {
        return qnaService.findById(idx);
    }

    @PutMapping("{idx}")
    public Question update(@LoginUser User loginUser, @PathVariable Long idx, @RequestBody QuestionDto questionDto) {
        return qnaService.updateQuestion(idx, loginUser, questionDto);
    }

    @DeleteMapping("{idx}")
    public Question delete(@LoginUser User loginUser, @PathVariable Long idx) {
        return qnaService.deleteQuestion(loginUser, idx);
    }
}
