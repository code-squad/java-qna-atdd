package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import codesquad.dto.UserDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
	public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto questionDto) {
		Question savedQuestion = qnaService.create(loginUser, questionDto.toQuestion());

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(savedQuestion.generateApiUrl()));
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@GetMapping("")
	public QuestionsDto list() {
		List<QuestionDto> questionDtoList = new ArrayList<QuestionDto>();
		for (Question question: qnaService.findAll()) {
			questionDtoList.add(question.toQuestionDto());
		}

		QuestionsDto questionsDto = new QuestionsDto(questionDtoList);
		log.debug(questionsDto.toString());

		return new QuestionsDto(questionDtoList);
	}

	@GetMapping("/{id}")
	public QuestionDto show(@PathVariable long id) {
		Question question = qnaService.findById(id);
		return question.toQuestionDto();
	}

	@PutMapping("/{id}")
	public void update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody QuestionDto updatedQuestionDto) {
		qnaService.update(loginUser, id, updatedQuestionDto);
	}

	@DeleteMapping("/{id}")
	public void delete(@LoginUser User loginUser, @PathVariable long id) {
		try {
			qnaService.deleteQuestion(loginUser, id);
		} catch (CannotDeleteException e) {
			log.info(e.getMessage());
		}
	}
}
