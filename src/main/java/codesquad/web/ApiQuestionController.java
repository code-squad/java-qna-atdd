package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.etc.CannotDeleteException;
import codesquad.etc.UnAuthorizedException;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

    @Autowired
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> createQuestion(@LoginUser User loginUser, @Valid @RequestBody QuestionDto questionDto) {
        Question question = qnaService.create(loginUser,
                new Question(questionDto.getTitle(), questionDto.getContents()));

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(question.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public QuestionDto showQuestionDetail(@PathVariable long id) {
        Optional<Question> optQuestion = qnaService.findById(id);

        return optQuestion
                .map(Question::toQuestionDto)
                .orElse(null);
    }

    @PutMapping("/{id}")
    public void updateQuestion(@PathVariable long id, @LoginUser User loginUser, @Valid @RequestBody QuestionDto question) {
        try {
            qnaService.update(loginUser, id, new Question(question.getTitle(), question.getContents()));
        } catch (UnAuthorizedException e) {
            log.debug(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable long id, @LoginUser User loginUser) {
        try {
            qnaService.deleteQuestion(loginUser, id);
            log.debug("delete success");
        } catch (CannotDeleteException e) {
            log.debug(e.getMessage());
            log.debug("delete failed");
        }
    }
}
