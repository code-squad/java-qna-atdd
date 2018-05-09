package codesquad.web;

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
    QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto questionDto) {
        QuestionDto savedQuestion = qnaService.create(loginUser, new Question(questionDto.getTitle(), questionDto.getContents())).toQuestionDto();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public QuestionDto get(@PathVariable Long id){
        return qnaService.findById(id).toQuestionDto();
    }

    @PutMapping("{id}")
    public void update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody QuestionDto questionDto){
        log.info("Question : {}",questionDto.getTitle());
        qnaService.update(loginUser, id, questionDto);
    }
}
