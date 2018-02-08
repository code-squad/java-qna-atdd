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
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User user, @RequestBody QuestionDto questionDto) {
        Question question = qnaService.create(user, questionDto.toQuestion());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/question/" + question.getId()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public QuestionDto showQuestion(@PathVariable long id) {
        Question question = qnaService.findById(id);
        return question.toQuestionDto();
    }

    @PutMapping("{id}")
    public void update(@LoginUser User loginUser, @PathVariable long id, @RequestBody QuestionDto questionDto) {
        qnaService.update(loginUser, id, questionDto.toQuestion());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CannotDeleteException e) {
            log.error("삭제할 수 없는 질문입니다.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
