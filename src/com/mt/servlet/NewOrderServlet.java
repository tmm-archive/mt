package com.mt.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mt.bean.Order;
import com.mt.bean.PortfolioManager;
import com.mt.manager.QueryManager;

/**
 * Servlet implementation class NewOrderServlet
 */
@WebServlet("/newOrder")
public class NewOrderServlet extends DBConnectorServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private QueryManager qm;
       
    public NewOrderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		connection = getConnection();
		qm = new QueryManager(connection);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String symbol = request.getParameter("symbol");		
		int quantity = Integer.parseInt(request.getParameter("quantity"));
		String side = request.getParameter("side");
		String type = request.getParameter("type");
		double price = Double.parseDouble(request.getParameter("price"));
		String pName = request.getParameter("portfolio");
		int pmId = Integer.parseInt(request.getParameter("pmId"));
		String trader = request.getParameter("trader");
		String notes = request.getParameter("notes");
		
		try{
			if(connection.isClosed()){
				connection = getConnection();
			}
			int id = qm.getNewOrderId();
			int portfolioId = qm.getPortfolioId(pName, pmId);
			
			
			if(connection.isClosed()){
				connection = getConnection();
			}
			String[] nameParts = trader.split(" ");
			int traderId = qm.getIdFromName(nameParts[0], nameParts[1]);
			
			if(connection.isClosed()){
				connection = getConnection();
			}
			Order order = new Order(id, symbol, quantity, side, type, price, 2, notes);
			qm.createOrder(portfolioId, order);
			
			RequestDispatcher rd = request.getRequestDispatcher("positionDisplayServlet");
			rd.forward(request, response);			
			
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
