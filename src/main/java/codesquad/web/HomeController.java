package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("questions", qnaService.findAll());
        return "home";
    }
}
