package com.joserodriguez.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.joserodriguez.authentication.models.User;
import com.joserodriguez.authentication.services.UserService;

@Controller
public class UserController {
	private final UserService userService;


	public UserController(UserService userService) { 
		this.userService = userService;

	}
	
	@RequestMapping("/registration")
	public String registration(@ModelAttribute("userObject") User user) {
		return "registration.jsp";
	}
	
	@RequestMapping("/login")
	public String login() {
		return "login.jsp";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@Valid @ModelAttribute("userObject") User user, BindingResult result, Model model, HttpSession session) {
		if(result.hasErrors()) {
			return "registration.jsp";
		}
		User userInSession = userService.registerUser(user);
		session.setAttribute("userId", userInSession.getId());
		return "redirect:/home";
	}
	
	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	public String signIn(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
		boolean isAuthenticated = userService.authenticateUser(email, password);
		if(isAuthenticated) {
			User user = userService.findByEmail(email);
			session.setAttribute("userId", user.getId());
			return "redirect:/home";
		}
		else {
			model.addAttribute("error", "Invalid Credentials! Please try again with the correct user information!");
			return "login.jsp";	
		}
	}
	
	@RequestMapping("/home")
	public String home(HttpSession session, Model model) {
		Long userId = (Long) session.getAttribute("userId");
		User user = userService.findUserById(userId);
		model.addAttribute("user", user);
		return "home.jsp";
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
}