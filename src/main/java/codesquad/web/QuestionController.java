package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    public UserService userService;

    @Autowired
    private QnaService qnaService;

    @GetMapping("/form")
    public String question(@LoginUser User user) {
        return "qna/form";
    }

    @GetMapping("/{id}")
    public String questionShow(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).orElseThrow(UnAuthorizedException::new));
        return "qna/show";
    }

    @GetMapping("/{id}/form")
    public String questionUpdate(@LoginUser User user, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id).filter(q -> q.isOwner(user))
                .orElseThrow(UnAuthorizedException::new));
        return "qna/updateForm";
    }

    @PostMapping("")
    public String createQuestion(@LoginUser User user, String title, String contents) {
        qnaService.create(user, new Question(title, contents));
        return "redirect:/";
    }

    @PutMapping("/{id}")
    public String updateQuestion(@LoginUser User loginUser, @PathVariable long id, Question target) {
        qnaService.update(loginUser, id, target);
        return String.format("redirect:/questions/%d", id);
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User user, @PathVariable long id) {
        qnaService.deleteQuestion(user, id);
        return "redirect:/";
    }
}
