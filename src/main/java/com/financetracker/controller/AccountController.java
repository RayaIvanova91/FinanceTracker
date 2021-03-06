package com.financetracker.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.financetracker.exceptions.AccountException;
import com.financetracker.exceptions.CurrencyException;
import com.financetracker.model.accountType.IAccountTypeDAO;
import com.financetracker.model.accounts.Account;
import com.financetracker.model.accounts.AccountDAO;
import com.financetracker.model.currencies.ICurrencyDAO;
import com.financetracker.model.users.User;

@Controller
@RequestMapping(value = "/accounts")
public class AccountController {

	@Autowired
	private AccountDAO accountDao;
	@Autowired
	private ICurrencyDAO currencyDAO;
	@Autowired
	private IAccountTypeDAO accountTypeDAO;

	@RequestMapping(method = RequestMethod.GET)
	public String addAccounts(
			@RequestParam(required = false, defaultValue = "Account name", value = "criteria") String criteria,
			Model model, HttpSession session, HttpServletRequest request) {
		if ((session == null) || (session.getAttribute("user") == null)) {
			return "redirect:/";
		}
		try {
			User loggedUser = (User) session.getAttribute("user");
			Set<Account> usersAccounts = new TreeSet<Account>();
			usersAccounts = accountDao.getSortedAccounts(loggedUser, criteria);
			loggedUser.setAccounts(usersAccounts);
			return "accounts";

		} catch (AccountException e) {
			e.printStackTrace();
			return "error";
		}
	}

	@RequestMapping(value = "/del", method = RequestMethod.GET)
	public String deleteAccount(HttpSession session, HttpServletRequest request) {
		int accountId = Integer.parseInt(request.getParameter("accId"));
		return "deleteAccount";
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String confirmDelete(HttpSession session, HttpServletRequest request) {
		int accountId = Integer.parseInt(request.getParameter("accId"));
		accountDao.deleteAccount(accountId);
		return "redirect:/accounts";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addAccount(Model model) {
		Account account = new Account();
		model.addAttribute("account", account);
		List<String> currencies;
		List<String> accountTypes;
		try {
			currencies = currencyDAO.getCurrenciesFromDB();
			accountTypes = accountTypeDAO.getAccountTypesFromDB();
			model.addAttribute("allCurrencies", currencies);
			model.addAttribute("allTypes", accountTypes);
			return "addNewAccount";
		} catch (ClassNotFoundException | SQLException | CurrencyException | AccountException e) {
			e.printStackTrace();

			return "redirect:home";
		}
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addAccount(@Valid Account readyAccount, BindingResult bindingResult, HttpServletRequest request,
			HttpSession session) {
		try {
			if (bindingResult.hasErrors()) {
				return "redirect:./add";
			}
			if (accountDao.addNewAccount(readyAccount, session) > 0) {
				return "redirect:/accounts";
			} else {
				request.setAttribute("error", "The account couldn't be added!");
				return "redirect:/accounts";
			}
		} catch (AccountException e) {
			e.printStackTrace();
			return "error";
		}
	}

	@RequestMapping(value = "/acc/{account_id}", method = RequestMethod.GET)
	public String editAccount(Model model, @PathVariable("account_id") Integer accountId) {
		try {
			Account currentAccount = accountDao.getAccountById(accountId);
			model.addAttribute("currentAccount", currentAccount);

			List<String> currencies = currencyDAO.getCurrenciesFromDB();
			List<String> accountTypes = accountTypeDAO.getAccountTypesFromDB();
			model.addAttribute("allCurrencies", currencies);
			model.addAttribute("allTypes", accountTypes);
			return "account";
		} catch (AccountException | ClassNotFoundException | SQLException | CurrencyException e) {
			e.printStackTrace();
			return "error";
		}
	}

	@RequestMapping(value = "/acc/{account_id}", method = RequestMethod.POST)
	public String editAccount(@Valid Account updatedAccount,BindingResult bindingResult, @PathVariable("account_id") Integer accountId) {
		try {	
			if (bindingResult.hasErrors()) {
				return "redirect:/accounts/acc/{account_id}";
			}
			accountDao.updateAccount(updatedAccount);
			return "redirect:/accounts";

		} catch (AccountException e) {
			e.printStackTrace();
			return "error";
		}
	}
}
