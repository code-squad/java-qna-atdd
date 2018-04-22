package codesquad.web;

import codesquad.domain.CannotDeleteException;
import codesquad.domain.CannotUpdateException;
import codesquad.domain.Question;
import codesquad.domain.UnAuthorizedException;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    QnaService qnaService;

    @GetMapping
    public String list(Model model,
                       @PageableDefault(
                               size = 15,
                               sort = "id",
                               direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Question> page = qnaService.findAll(pageable);
        model.addAttribute("questions", page.getContent());
        return "home";
    }

    @GetMapping("/{id}")
    public String get(Model model, @PathVariable long id) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, QuestionDto question) {
        Question created = qnaService.create(loginUser, question);
        return String.format("redirect:%s", created.generateUrl());
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findById(id);
        if (!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto question)
            throws CannotUpdateException {
        Question updated = qnaService.update(loginUser, id, question);
        return String.format("redirect:%s", updated.generateUrl());
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}
