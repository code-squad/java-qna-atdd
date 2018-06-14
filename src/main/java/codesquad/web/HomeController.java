package codesquad.web;

import codesquad.domain.Question;
import codesquad.service.QnaService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class HomeController {
    @Resource(name = "qnaService")
    QnaService qnaService;

    @GetMapping("/")
    public String home(Model model, Pageable pageable) {
        List<Question> questions = qnaService.findAll(pageable);
        model.addAttribute("questions", questions);
        return "/home";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "/user/login";
    }
}
