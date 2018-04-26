package codesquad.web;


import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/{id}")
    public String show(Model model, @PathVariable long id) {
        Question question = qnaService.findById(id);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/updateForm";
    }

    @GetMapping("")
    public String list(Model model, Pageable pageable) {
        List<Question> questions = qnaService.findAll(pageable);
        log.debug("user size : {}", questions.size());
        model.addAttribute("users", questions);
        return "/user/list";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, QuestionDto question) {
        qnaService.create(loginUser, new Question(question.getTitle(), question.getContents()));
        return "redirect:/";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto question) {
        qnaService.update(loginUser, id, new Question(question.getTitle(),question.getContents()));
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch(Exception ex) {
            log.debug(ex.getClass()+":"+ex.getMessage());
        }

        return "redirect:/";
    }

}
