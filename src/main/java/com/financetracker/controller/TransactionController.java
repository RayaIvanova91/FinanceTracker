package com.financetracker.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.financetracker.exceptions.AccountException;
import com.financetracker.exceptions.TransactionException;
import com.financetracker.model.accounts.Account;
import com.financetracker.model.accounts.AccountDAO;
import com.financetracker.model.categories.CategoryDAO;
import com.financetracker.model.transactions.Transaction;
import com.financetracker.model.transactions.TransactionDAO;
import com.financetracker.model.users.User;
import com.google.gson.Gson;

@Controller
@RequestMapping(value="/transactions")
public class TransactionController {
	
	@Autowired
	public TransactionDAO transactionDAO;
	@Autowired
	public AccountDAO accountDAO;
	@Autowired
	public CategoryDAO categoryDAO;


	@RequestMapping(method = RequestMethod.GET)
	protected String showTransactions(Model model, HttpSession session) {
		if ((session == null) || (session.getAttribute("user") == null)) {
			return "signup-login";
		}

		try {
			User currentUser = (User) session.getAttribute("user");
			List<Transaction> allUserTransactions = new ArrayList<>();
			HashSet<Account> currentUserAccounts = accountDAO.getAllAccountsForUser(currentUser);

			for (Account account : currentUserAccounts) {
				allUserTransactions.addAll(transactionDAO.getAllTransactions(account));
			}
			model.addAttribute("allUserTransactions", allUserTransactions);
		} catch (AccountException | TransactionException | SQLException e) {
			e.printStackTrace();
			return "error";
		}
		return "transactions";
	}
	

	
	

//	@RequestMapping(value = "/add", method = RequestMethod.GET)
//	public String addTransaction(HttpSession session, Model model) {
//		Transaction transaction = new Transaction();
//		try {
//			HashSet<Account> allAccounts=accountDAO.getAllAccountsForUser((User) session.getAttribute("user"));
//			model.addAttribute("transaction", transaction);
//			model.addAttribute("allAccounts", allAccounts);
//			return "newTransaction";
//		} catch (AccountException e) {
//			e.printStackTrace();
//			return "error";
//		}
//			
//	}
	
	@RequestMapping(value = "/add/getCategories", method = RequestMethod.GET)
	public @ResponseBody void doGet(HttpServletRequest request, HttpServletResponse response) throws TransactionException {
			
			try {
				String transactionType = request.getParameter("typeSelect");
				TreeSet<String> categories = categoryDAO.getCategories(transactionType);
				response.setContentType("application/json");
				response.getWriter().println(new Gson().toJson(categories));
				
			} catch (IOException | SQLException e) {
				e.printStackTrace();
				throw new TransactionException("Cannot get categories!");
			} 
	}
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	protected String getUserAccounts(HttpSession session) {
		User loggedUser = (User) session.getAttribute("user");
		HashSet<Account> usersAccounts;
		try {
			usersAccounts = accountDAO.getAllAccountsForUser(loggedUser);
			loggedUser.setAccounts(usersAccounts);
			return "newTransaction";
		} catch (AccountException e) {
			e.printStackTrace();
			return "error";
		}
		
	}
	
	@RequestMapping(value="/add", method=RequestMethod.POST)
	protected String addTransaction(HttpServletRequest request, HttpSession session) {
		if ((session == null) || (session.getAttribute("user") == null)) {
			return "redirect:/signup-login";
		}

		try {
			String payee = request.getParameter("payee");
			double amount = Double.parseDouble(request.getParameter("amount"));
			LocalDate date = LocalDate.parse(request.getParameter("date"));
			boolean isIncome;
			
			if (request.getParameter("typeSelect").equals("false")) {
				isIncome = false;
			}else {
				isIncome = true;
			}
			
			int category = categoryDAO.getCategoryID(request.getParameter("category"));
			String accountName=request.getParameter("accountSelect");
			Account account = accountDAO.getAccountByName(accountName);
			
			Transaction transaction = new Transaction(payee, amount, date, account, category, isIncome);
			transactionDAO.addTransaction(transaction);
			
			return "redirect:/transactions";

		} catch (TransactionException | AccountException | SQLException  e) {
			e.printStackTrace();
			return "error";
		}
	}
}