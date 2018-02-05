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
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto question) {
        Question saveQuestion = qnaService.create(loginUser, question.toQuestion());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(saveQuestion.generateApiUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public QuestionDto show(@LoginUser User loginUser, @PathVariable long id) {
        return qnaService.findById(id).toQuestionDto();
    }

    @PutMapping("{id}")
    public QuestionDto update(@LoginUser User loginUser, @Valid @RequestBody QuestionDto questionDto, @PathVariable long id) {
        return qnaService.update(loginUser, questionDto).toQuestionDto();
    }

    @DeleteMapping("{id}")
    public void delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException cde) {
            log.error("user : " + loginUser + ", id : " + id + ", message : " + cde.getMessage());
        }
    }
}
