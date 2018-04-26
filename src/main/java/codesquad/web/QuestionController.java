package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("/create")
    public String create(@LoginUser User loginUser ,String contents, String title ) {
        qnaService.create(loginUser, new Question(title,contents));
        return "redirect:/questions/create";
    }

    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("questions", qnaService.findAll());
        return "home";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, String title, String contents) {
        qnaService.update(loginUser, id, new QuestionDto(title, contents));
        return "redirect:/home";

    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/home";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id , Model model) {
        model.addAttribute("question" , qnaService.findById(id));
        return "/qna/show";
    }

}
