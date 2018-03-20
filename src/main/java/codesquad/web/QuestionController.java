package codesquad.web;

import javax.annotation.Resource;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.BasicAuthInterceptor;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

/*질문 삭제 기능을 구현한다. 질문 삭제 기능의 요구사항은 다음과 같다.
(기능)
o	질문 데이터를 완전히 삭제하는 것이 아니라 데이터의 상태를 삭제 상태(deleted - boolean type)로 변경한다. 
	질문을 삭제할 때 답변 또한 삭제해야 하며, 답변의 삭제 또한 삭제 상태(deleted)를 변경한다.
(조건)
o	답변이 없는 경우 삭제가 가능하다.
o	질문자와 답변 글의 모든 답변자 같은 경우 삭제가 가능하다.
o	질문자와 답변자가 다른 경우 답변을 삭제할 수 없다. 
o	로그인 사용자와 질문한 사람이 같은 경우 삭제 가능하다.
(추가 기능)
	질문과 답변 삭제 이력에 대한 정보를 DeleteHistory를 활용해 남긴다.
(장소)
o	핵심 비지니스 로직을 QnaService에 구현하지 말고, User, Question, Answer 3 객체가 협력해 구현한다.
*/

@Controller
@RequestMapping("/questions")
public class QuestionController {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

	@Resource(name = "qnaService")
	private QnaService qnaService;

	@PostMapping("")
	public String create(@LoginUser User loginUser, QuestionDto question, Model model, HttpSession session) {
		checkAuth(loginUser, session);
		qnaService.create(loginUser, question);
		
		return "redirect:/";
	}

	@GetMapping("/{id}")
	public String showDetail(User user,@PathVariable long id, Model model) {
		model.addAttribute("question", qnaService.findById(id));
		return "/qna/show";
	}

	@GetMapping("/{id}/form")
	public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model, HttpSession session) {
		checkAuth(loginUser, session);
		Question question = qnaService.findById(id);
		model.addAttribute("question", question);
		return "/qna/updateForm";
	}

	@PutMapping("/{id}")
	public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto updatequestion,
			HttpSession session) {
		checkAuth(loginUser, session);
		qnaService.update(loginUser, id, updatequestion);
		return String.format("redirect:/questions/%d", id);
	}

	@DeleteMapping("/{id}")
	public String delete(@LoginUser User loginUser, @PathVariable long id, HttpSession session)
			throws CannotDeleteException {
		checkAuth(loginUser, session);
		qnaService.deleteQuestion(loginUser, id);
		return "redirect:/";
	}

	public String checkAuth(@LoginUser User loginUser, HttpSession session) {
		if (!HttpSessionUtils.isLoginUser(session))
			return "/user/login";
		if (!loginUser.equals(session.getAttribute(HttpSessionUtils.USER_SESSION_KEY)))
			throw new IllegalStateException("자신의 게시물만 수정/삭제 가능합니다");
		return "success";
	}

}
