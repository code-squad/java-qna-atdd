package codesquad.web;

import java.net.URI;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

	private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

	@Resource(name = "qnaService")
	private QnaService qnaService;

	@PostMapping("")
	public ResponseEntity<Void> create(@LoginUser User loginUser, @RequestBody QuestionDto question,
			HttpSession session) {
		Question newQuestion = qnaService.create(loginUser, question);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/api" + newQuestion.generateUrl()));
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public QuestionDto showDetail(User user, @PathVariable long id) {
		return qnaService.findById(id).toQuestionDto();
	}

	@PutMapping("/{id}")
	public void update(@LoginUser User loginUser, @PathVariable long id,
			@Valid @RequestBody QuestionDto updateQuestion) {
		qnaService.update(loginUser, id, updateQuestion);
	}

}
