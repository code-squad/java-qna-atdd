package codesquad.web;

import java.net.URI;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.service.UserService;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
	
	@Resource(name = "qnaService")
	private QnaService qnaService;

	@PostMapping("")
	public ResponseEntity<Void> create(@Valid @RequestBody QuestionDto question, @LoginUser User loginUser) {
		Question savedQuestion = qnaService.add(question, loginUser);
		log.info("COMPLETE TO MAKE QUESTION." + savedQuestion);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public QuestionDto show(@PathVariable long id) {
		Question question = qnaService.findById(id);
		return question.toQuestionDto();
	}

	@PutMapping("/{id}")
	public void update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody QuestionDto updatedQuestion) throws CannotDeleteException {
		Question question = qnaService.findById(id);
		
		if (!question.isOwner(loginUser)) {
			throw new CannotDeleteException("수정 권한이 없습니다.");
		}
		if (loginUser.equals(null)) {
			throw new CannotDeleteException("로그인후에 이용할 수 있습니다.");
		}
		qnaService.update(loginUser, id, updatedQuestion.toQuestion());
	}
	
	@DeleteMapping("/{id}")
	public void delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
		try {
			log.debug("delete in");
			qnaService.deleteQuestion(loginUser, id);
		} catch (NullPointerException e) {
			throw new CannotDeleteException("질문이 없습니다.");
		}
	}
}
