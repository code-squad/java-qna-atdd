package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.validate.ValidationErrorsResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto questionDto) {
        Question question = qnaService.create(loginUser, questionDto.toQuestion());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(question.generateRestUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<Question> read(@PathVariable long questionId) {
        Question question = qnaService.findQuestionById(questionId);
        return new ResponseEntity<>(question, HttpStatus.OK);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity update(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody QuestionDto questionDto) {
        Question updatedQuestion = qnaService.update(loginUser, questionId, questionDto);
        return new ResponseEntity<>(updatedQuestion, HttpStatus.OK);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity delete(@LoginUser User loginUser, @PathVariable long questionId) {
        qnaService.deleteQuestion(loginUser, questionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
