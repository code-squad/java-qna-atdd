package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

	@Resource(name = "qnaService")
	private QnaService qnaService;

	@PostMapping("")
	public ResponseEntity<Void> create(@Valid @RequestBody Question question, @LoginUser User user) {
		Question saveQuestion = qnaService.create(user, question);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/api/questions/" + saveQuestion.getId()));
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@GetMapping("")
	public QuestionsDto showAll() {
		List<QuestionDto> questionsDto = new ArrayList<>();
		for (Question question : qnaService.findAll()) {
			questionsDto.add(question.toQuestionDto());
		}
		return new QuestionsDto(questionsDto);
	}

	@GetMapping("{id}")
	public QuestionDto show(@PathVariable long id) {
		Question question = qnaService.findById(id);
		return question.toQuestionDto();
	}

	@PutMapping("{id}")
	public void update(@LoginUser User user, @PathVariable long id, @Valid @RequestBody Question question) {
		qnaService.update(user, id, question);
	}

	@DeleteMapping("{id}")
	public void delete(@LoginUser User user, @PathVariable long id) {
		try {
			qnaService.deleteQuestion(user, id);
		} catch (CannotDeleteException e) {
			e.printStackTrace();
		}
	}

}
