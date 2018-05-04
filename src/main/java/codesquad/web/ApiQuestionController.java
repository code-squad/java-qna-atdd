package codesquad.web;


import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create( @LoginUser User loginUser,QuestionDto questionDto) {

        Question question = qnaService.create(loginUser,new Question(questionDto.getTitle(), questionDto.getContents()));

        return new ResponseEntity<Void>(getURIHeader(question.getId()), HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<Void> show(@LoginUser User loginUser, @PathVariable long id) {
        Question questions = qnaService.findById(id);

        return new ResponseEntity<Void>(getURIHeader(id), HttpStatus.OK);
    }

    @GetMapping("/info/{id}")
    public Question showInfo(@LoginUser User loginUser, @PathVariable long id) {
        Question questions = qnaService.findById(id);
        return questions;
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@LoginUser User loginUser, @PathVariable long id, QuestionDto questionDto) {

        qnaService.update(loginUser, id, new Question(questionDto.getTitle(),questionDto.getContents()));

        return new ResponseEntity<Void>(getURIHeader(id), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch(Exception ex) {
            log.debug(ex.getClass()+":"+ex.getMessage());
        }

        return new ResponseEntity<Void>(getURIHeader(id), HttpStatus.OK);
    }

    private HttpHeaders getURIHeader(long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + id));
        return headers;
    }
}
