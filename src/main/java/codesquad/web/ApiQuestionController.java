package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    private static final Logger logger = LoggerFactory.getLogger(ApiQuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("/create")
    public ResponseEntity<QuestionDto> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto questionDto) {
        Question question = qnaService.create(loginUser, questionDto);
        HttpHeaders headers = new HttpHeaders();
        logger.debug("Created Question ID: {}", question.getId());
        headers.setLocation(URI.create("/api/questions/" + question.getId()));

        return new ResponseEntity<>(question.toQuestionDto(), headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> show(@PathVariable long id) {
        QuestionDto questionDto = qnaService.findQuestionById(id).toQuestionDto();
        return new ResponseEntity<>(questionDto, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{id}")
    public void update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody QuestionDto updated) {
        qnaService.update(loginUser, id, updated);
    }
}