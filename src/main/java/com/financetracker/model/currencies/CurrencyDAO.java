package com.financetracker.model.currencies;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.financetracker.database.DBConnection;
import com.financetracker.exceptions.AccountException;
import com.financetracker.exceptions.CurrencyException;

@Component
public class CurrencyDAO implements ICurrencyDAO {
	private static final String GET_CURR_ID = "SELECT id from currencies where currency_type like ?;";
	private static final String GET_ALL_CURRENCIES = "SELECT currency_type FROM currencies;";
	
	@Autowired
	private DBConnection DBConnection;

	@Override
	public int getCurrencyId(String currencyName) throws SQLException, ClassNotFoundException, AccountException {
		PreparedStatement pst = DBConnection.getConnection().prepareStatement(GET_CURR_ID,
				Statement.RETURN_GENERATED_KEYS);
		pst.setString(1, currencyName);
		ResultSet rs = pst.executeQuery();
		if (rs.next()) {
			return rs.getInt(1);
		} else {
			throw new AccountException("Not such currency in DB!");
		}
	}
	@Override
	public List<String> getCurrenciesFromDB() throws ClassNotFoundException, SQLException, CurrencyException {
		List<String> allCurrencies = new ArrayList<String>();
		Statement st = DBConnection.getConnection().createStatement();
		ResultSet rs = st.executeQuery(GET_ALL_CURRENCIES);
		while (rs.next()) {
			allCurrencies.add((String) rs.getString("currency_type"));
		}
		return allCurrencies;
	}

}
