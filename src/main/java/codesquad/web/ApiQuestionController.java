package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;


    @GetMapping("")
    public List<Question> list(@PageableDefault(
            sort = "id",
            direction = Sort.Direction.DESC) Pageable pageable) {
        return qnaService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findById(id);
    }

    @PostMapping("")
    public ResponseEntity<String> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto questionDto) {
        Question createdQuestion = qnaService.create(loginUser, questionDto.toQuestion());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(createdQuestion.generateResourceURI()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody QuestionDto updatingQuestion) throws UnAuthorizedException {

        log.debug("param: {}", updatingQuestion);
        qnaService.update(loginUser, id, updatingQuestion.toQuestion());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@LoginUser User loginUser, @PathVariable long id) throws UnAuthorizedException {
        qnaService.deleteQuestion(loginUser, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}