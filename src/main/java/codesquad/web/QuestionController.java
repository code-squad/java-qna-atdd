package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LogManager.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, Question question) {
        qnaService.create(loginUser,question);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        Question question = qnaService.findById(id).get();
        model.addAttribute("question",question);
        return "/qna/show";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question updateQuestion) {
        qnaService.update(loginUser,id,updateQuestion);
        return "redirect:/questions/{id}";
    }

    @DeleteMapping("/{id}")
    public String deleted(@LoginUser User loginUser, @PathVariable long id) {
        qnaService.deleteQuestion(loginUser,id);
        return "redirect:/";
    }


    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findById(id).filter(q -> q.isOwner(loginUser)).orElseThrow(UnAuthorizedException::new);
        model.addAttribute("question",question);
        return "/qna/updateForm";
    }


}
