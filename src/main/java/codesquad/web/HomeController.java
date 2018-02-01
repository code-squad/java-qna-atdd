package codesquad.web;

import codesquad.domain.Question;
import codesquad.service.QnaService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

@Controller
public class HomeController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        Iterable<Question> questions = qnaService.findAll();
        model.addAttribute("questions", questions);
        return "home";
    }
}
