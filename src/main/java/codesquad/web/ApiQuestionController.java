package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("")
    public QuestionsDto list() {
        List<QuestionDto> list = StreamSupport.stream(qnaService.findAll().spliterator(), false)
                .map(Question::toQuestionDto)
                .collect(Collectors.toList());
        final QuestionsDto dto = new QuestionsDto(list);
        return dto;
    }

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto question) {
        final Question savedQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + savedQuestion.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public QuestionDto show(@PathVariable long id) {
        Question question = qnaService.findById(id);

        if (question == null)
            return null;

        return question.toQuestionDto();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody QuestionDto updateQuestion) {
        qnaService.update(loginUser, id, updateQuestion.toQuestion());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CannotDeleteException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
