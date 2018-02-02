package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    Logger logger = LoggerFactory.getLogger(ApiQuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto questionDto) {
        Question savedQuestion = qnaService.create(loginUser, new Question(questionDto));
        return new ResponseEntity<>(ApiHeaderGenerator.generateApiHeader(savedQuestion), HttpStatus.CREATED);
    }
    
    @GetMapping("{id}")
    public QuestionDto show(@PathVariable long id) {
        Question question = qnaService.findById(id);
        return (question == null) ? null : question.toQuestionDto();
    }

    @PutMapping("{id}")
    public void update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody QuestionDto updatedQuestionDto) {
        Question updatedQuestion = new Question(updatedQuestionDto);
        qnaService.update(loginUser, id, updatedQuestion);
    }

    @DeleteMapping("{id}")
    public void delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException e) {
            logger.error("we can not delete this, id=" + id, e);
        }
    }

}
