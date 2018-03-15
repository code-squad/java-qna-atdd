package codesquad.web;

import java.net.URI;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.User;
import codesquad.dto.UserDto;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
	private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

	@Autowired
	private AnswerRepository answerRepository;
	
	@Autowired
	private QnaService qnaService;
	
	@PostMapping("")
	public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody Answer answer, HttpSession session) {
		Answer newAnswer = qnaService.addAnswer(loginUser, questionId, answer.getContents());

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/api" + newAnswer.generateUrl()));
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);	
	}
	
	  @GetMapping("/{id}")
	    public Answer show(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id) {
	        return answerRepository.findOne(id);
	    }
	
	public String checkAuth(@LoginUser User loginUser, HttpSession session) {
		if (!HttpSessionUtils.isLoginUser(session))
			return "/user/login";
		if (!loginUser.equals(session.getAttribute(HttpSessionUtils.USER_SESSION_KEY)))
			throw new IllegalStateException("자신의 게시물만 수정/삭제 가능합니다");
		return "success";
	}

}
