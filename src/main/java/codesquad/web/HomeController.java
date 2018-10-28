package codesquad.web;

import codesquad.domain.Question;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        List<Question> questions = qnaService.findAll(new PageRequest(0, 10));
        model.addAttribute("questions", questions);
        return "home";
    }

}
