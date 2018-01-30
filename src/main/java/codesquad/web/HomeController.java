package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/users/logout";
    }

    @GetMapping("/questions")
    public String question(@PageableDefault Pageable pageable, Model model) {
        List<Question> questions = qnaService.findAll(pageable);
        log.debug("question size : {}", questions.size());
        model.addAttribute("questions", questions);
        return "home";
    }
}
