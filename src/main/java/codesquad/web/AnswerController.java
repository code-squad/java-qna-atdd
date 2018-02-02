package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/questions/{questionId}/answers")
public class AnswerController {

    private final QnaService qnaService;

    public AnswerController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/{id}/form")
    public String showForm(@LoginUser User loginUser,
                           @PathVariable long id,
                           Model model) {
        Answer answer = qnaService.findAnswerByIdAndNotDeleted(id);
        answer.checkAuthority(loginUser);
        model.addAttribute("answer", answer);

        return "/qna/answerUpdateForm";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser,
                         @PathVariable long questionId,
                         @Valid AnswerDto answerDto) {
        qnaService.addAnswer(answerDto.toAnswer(loginUser), questionId);
        return "redirect:/questions/" + questionId;
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser,
                         @PathVariable long questionId,
                         @PathVariable long id,
                         @Valid AnswerDto answerDto) {
        qnaService.updateAnswer(id, answerDto.toAnswer(loginUser));
        return "redirect:/questions/" + questionId;
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser,
                         @PathVariable long questionId,
                         @PathVariable long id) {
        qnaService.deleteAnswer(loginUser, id);
        return "redirect:/questions/" + questionId;
    }
}
