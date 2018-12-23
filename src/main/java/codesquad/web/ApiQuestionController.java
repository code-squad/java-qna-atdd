package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.Valid;

import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

@RequestMapping("/api/questions")
@RestController
public class ApiQuestionController {
    private static final Logger logger = getLogger(ApiQuestionController.class);

    @Autowired
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question){
        Question createQuestion = qnaService.create(loginUser, question);
        logger.debug("question : {}", question);
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.setLocation(URI.create("/api/questions/" + createQuestion.getId()));
        return new ResponseEntity<>(responseHeader, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> show(@PathVariable long id){
        return new ResponseEntity<>(qnaService.findById(id).get(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question otherQuestion){
        otherQuestion.writeBy(loginUser);
        return new ResponseEntity<>(qnaService.update(loginUser, id, otherQuestion), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Question> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        return new ResponseEntity<Question>(qnaService.deleteQuestion(loginUser, id), HttpStatus.OK);
    }
}
