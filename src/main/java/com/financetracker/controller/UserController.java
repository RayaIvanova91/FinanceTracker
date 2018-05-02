package com.financetracker.controller;


import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.financetracker.exceptions.AccountException;
import com.financetracker.exceptions.UserException;
import com.financetracker.model.accounts.Account;
import com.financetracker.model.accounts.AccountDAO;
import com.financetracker.model.users.User;
import com.financetracker.model.users.UserDAO;

@Controller
@RequestMapping(value = "/")
public class UserController {

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private AccountDAO accountDao;
	
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	private String login(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		try {
			if (userDAO.login(email, password)) {
				session = request.getSession();
				User user = userDAO.getUserByEmail(email);
				HashSet<Account> userAccounts = accountDao.getAllAccountsForUser(user);
				session.setAttribute("user", user);
				user.setAccounts(userAccounts);
				//session.setAttribute("userAccounts", userAccounts);
				return "redirect:/home";
			} else {
				return "login";
			}
		} catch (UserException | AccountException e) {
			e.printStackTrace();
			return "error";
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/register")
	private String doPost(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		User user = null;
		user = new User(email, password, firstName, lastName);
		try {
			if (userDAO.register(user) > 0) {
				return "signup-login";
			} else {
				return "signup-login";
			}
		} catch (UserException e) {
			e.printStackTrace();
			return "error";
		}
	}
}
