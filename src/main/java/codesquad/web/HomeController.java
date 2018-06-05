package codesquad.web;

import codesquad.domain.Question;
import codesquad.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

import static support.domain.EntityName.QUESTION;
import static support.domain.EntityName.getModelNameOfMulti;

@Controller
public class HomeController {

    @Resource
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        List<Question> questions = qnaService.findAll();
        if (!questions.isEmpty()) {
            model.addAttribute(getModelNameOfMulti(QUESTION), questions);
        }
        return "home";
    }
}
