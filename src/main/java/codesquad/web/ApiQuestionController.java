package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import support.domain.RestResponseEntityMaker;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Resource
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User user, @Valid @RequestBody QuestionDto questionDto) {
        Question question = qnaService.create(user, questionDto);
        return RestResponseEntityMaker.of(question.generateApiUrl(), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> show(@PathVariable Long id) {
        Question question = qnaService.findQuestionById(id);
        return RestResponseEntityMaker.of(question, question.generateApiUrl(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@LoginUser User loginUser, @PathVariable Long id, @Valid @RequestBody QuestionDto updateQuestionDto) {
        Question question = qnaService.updateQuestion(loginUser, id, updateQuestionDto).toQuestion();
        return RestResponseEntityMaker.of(question.generateApiUrl(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return RestResponseEntityMaker.of("/", HttpStatus.OK);
    }
}
