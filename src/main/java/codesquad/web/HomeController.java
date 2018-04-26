package codesquad.web;

import codesquad.domain.Question;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model,Pageable pageable) {

        List<Question> questions = qnaService.findAll(pageable);
        log.debug("questions size : {}", questions.size());
        model.addAttribute("questions", questions);

        return "home";
    }
}
