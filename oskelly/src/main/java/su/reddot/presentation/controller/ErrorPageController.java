package su.reddot.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

	@GetMapping("/fail")
	public String showError(Model model) {
		return "oops";
	}
}
