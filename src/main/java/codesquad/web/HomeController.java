package codesquad.web;

import codesquad.domain.Question;
import codesquad.service.QnaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class HomeController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        List<Question> questions = qnaService.findListNotDeleted(new PageRequest(0, 10));
        model.addAttribute("questions", questions);
        return "home";
    }
}
