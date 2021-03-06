package com.mt.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.mt.bean.Block;
import com.mt.bean.Order;
import com.mt.bean.Portfolio;
import com.mt.bean.Position;

public class QueryManager {
	
	private Connection connection;
	
	public QueryManager(Connection connection){
		this.connection = connection;
	}
	
	public void setConnection(Connection connection){
		this.connection = connection;
	}
	
	public ArrayList<Order> getPositionOrders(int portfolioId, String symbol, String status){		
		String sql = "SELECT * FROM orders WHERE port_id=? AND symbol =? AND status=?";
		ArrayList<Order> orders = new ArrayList<Order>();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, portfolioId);
			ps.setString(2, symbol);
			ps.setString(3, status);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if(rs.getString("symbol").equals(symbol)){
					int id = rs.getInt("id");
					int quantity = rs.getInt("quantity");
					String side = rs.getString("side");
					String type = rs.getString("type");
					double price = rs.getDouble("price");
					int trader = rs.getInt("trader");
					String notes = rs.getString("notes");	
					int block_order_id = rs.getInt("block_order_id");
					Order order = new Order(id, symbol, quantity, side, type, price, trader, notes);
					order.setBlock_order_id(block_order_id);
					order.setStatus(rs.getString("status"));
					orders.add(order);
				}				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;		
	}
	
	public ArrayList<Order> getOpenOrders(int portfolioId){		
		String sql = "SELECT * FROM orders WHERE port_id=? AND status='open' group by symbol";
		ArrayList<Order> orders = new ArrayList<Order>();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, portfolioId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				int quantity = rs.getInt("quantity");
				String symbol = rs.getString("symbol");
				String side = rs.getString("side");
				String type = rs.getString("type");
				double price = rs.getDouble("price");
				int trader = rs.getInt("trader");
				String notes = rs.getString("notes");	
				int block_order_id = rs.getInt("block_order_id");
				Order order = new Order(id, symbol, quantity, side, type, price, trader, notes);
				order.setBlock_order_id(block_order_id);
				order.setStatus(rs.getString("status"));
				orders.add(order);
			}				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;		
	}
	
	public HashMap<String, ArrayList<Order>> getOrderMap(int portfolioId, String symbol){
		HashMap<String, ArrayList<Order>> map = new HashMap<String, ArrayList<Order>>();
		String[] statuses = {"open", "sent for execution", "successful", "expired", "partially filled", "cancelled"};
		for(int i=0; i<statuses.length;i++){
			map.put(statuses[i], getPositionOrders(portfolioId, symbol, statuses[i]));
		}
		return map;
	}
	
	public ArrayList<Position> getPortfolioPositions(int portfolioId){
		String sql = "SELECT symbol, SUM(quantity) as quantity, AVG(price) as price "
				+ "FROM orders WHERE port_id=? GROUP BY symbol";
		ArrayList<Position> positions = new ArrayList<Position>();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, portfolioId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String symbol = rs.getString("symbol");
				int quantity = rs.getInt("quantity");
				double avgPrice = rs.getDouble("price");	
				Position position = new Position(symbol, quantity, avgPrice, getOrderMap(portfolioId, symbol));
				positions.add(position);
			}				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return positions;	
	}
	
	public ArrayList<Integer> getPortfolioIds(int pmId){
		String sql = "SELECT id FROM portfolio WHERE pm_id = ?";
		ArrayList<Integer> portfolioIds = new ArrayList<Integer>();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, pmId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				portfolioIds.add(rs.getInt("id"));
			}				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return portfolioIds;
	}
	
	public Portfolio getPortfolio(int portfolioId){
			
		String sql = "SELECT name FROM portfolio WHERE id = ?";
		Portfolio portfolio = new Portfolio();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, portfolioId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ArrayList<Position> positions = getPortfolioPositions(portfolioId);
				portfolio.setPositions(positions);
				portfolio.setName(rs.getString("name"));
				portfolio.setOpenOrders(getOpenOrders(portfolioId));
			}				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return portfolio;
	}
	
	public int getNewOrderId(){
		String sql = "SELECT MAX(id) as id FROM orders";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int lastId = rs.getInt("id");
				System.out.println("last id: " + lastId);
				return ++lastId;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void createOrder(int portfolioId, Order order){
		String sql = "INSERT INTO `mtdb`.`orders` (`id`, `port_id`, `symbol`, `quantity`, `side`, `type`, `price`, `trader`, `notes`,`status`,`timestamp`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
		try {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");
			String formattedDate = sdf.format(date);			
			
			
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, getNewOrderId());
			ps.setInt(2, portfolioId);
			ps.setString(3, order.getSymbol());
			ps.setInt(4, order.getQuantity());
			ps.setString(5, order.getSide());
			ps.setString(6, order.getType());
			ps.setDouble(7, order.getPrice());
			ps.setInt(8, order.getTrader());
			ps.setString(9, order.getNotes());
			ps.setString(10, "open");
			ps.setString(11, formattedDate);
			ps.execute();
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getPortfolioId(String pName, int pmId){
		System.out.println("pname: " + pName);
		System.out.println("pmId: " + pmId);
		String sql = "SELECT id FROM portfolio WHERE name = ? AND pm_id = ?";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, pName);
			ps.setInt(2, pmId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/*// Cato - need help
	public void createBlock(int block_id, ArrayList<Order> orderList) {
		String sql = "INSERT INTO `mtdb`.`block` (`block_id`, `symbol`, `status`, `open_quantity`, `total_quantity`, `exec_quantity`, `limit_price`, `stop_price`, `trader_id`,`side_order_id`, `order_type_id`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, getNewOrderId());
			ps.setInt(2, portfolioId);
			ps.setString(3, order.getSymbol());
			ps.setInt(4, order.getQuantity());
			ps.setString(5, order.getSide());
			ps.setString(6, order.getType());
			ps.setDouble(7, order.getPrice());
			ps.setString(8, order.getTrader());
			ps.setString(9, order.getNotes());
			ps.setString(10, "open");
			ps.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}*/
	
	public ArrayList<Integer> getBlockIDs(int traderID) {
		System.out.println("traderID: " + traderID);
		String sql = "SELECT block_id FROM block WHERE trader_id = ?";
		ArrayList<Integer> blockIds = new ArrayList<Integer>();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, traderID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				blockIds.add(rs.getInt("block_id"));
			}				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return blockIds;
	}
	
	public ArrayList<Block> getBlocks(int traderID) {
		
		String sql = "SELECT * FROM block WHERE trader_id = ?";
		ArrayList<Block> blockList = new ArrayList<Block>();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, traderID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int block_id = rs.getInt("block_id");
				int side_id = rs.getInt("side_order_id");
				String symbol = rs.getString("symbol");
				String status= rs.getString("status");
				int open_quantity = rs.getInt("open_quantity");
				int total_quantity = rs.getInt("total_quantity");
				int exec_quantity = rs.getInt("exec_quantity");
				double limit_price = rs.getDouble("limit_price");
				double stop_price = rs.getDouble("stop_price");
				Block block = new Block(block_id );
				block.setExec_quantity(exec_quantity);
				block.setLimit_price(limit_price);
				block.setStop_price(stop_price);
				block.setOpen_quantity(open_quantity);
				block.setTotal_quantity(total_quantity);
				block.setSymbol(symbol);
				block.setStatus(status);
				block.setSide_id(side_id);
				block.setBlockOrders(getBlockOrdersForId(block_id));
				blockList.add(block);					
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return blockList;
	}
	
	public ArrayList<Order> getTraderOrdersForId(int userId){		
		String sql = 
				 "SELECT * FROM orders WHERE trader=?";
		ArrayList<Order> orders = new ArrayList<Order>();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, userId);
	
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
					int id = rs.getInt("id");
										
				String symbol=rs.getString("symbol");
				int quantity = rs.getInt("quantity");
				String side = rs.getString("side");
				String type = rs.getString("type");
				double price = rs.getDouble("price");
				int traderId = rs.getInt("trader");
				String status = rs.getString("status");
				String notes = rs.getString("notes");
				String timeStamp = rs.getString("timeStamp");
				String pmName = getPmNameForPortId(rs.getInt("port_Id"));
				Order order = new Order(id, symbol, quantity, side, type, price, traderId, notes);
				order.setStatus(status);
				order.setPmName(pmName);
				order.setTimeStamp(timeStamp);
				orders.add(order);			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;		
	}
	
	
	public ArrayList<Order> getOrdersUnderlyingBlock(int block_id) {
		String sql = "SELECT * FROM orders WHERE block_order_id = ? ";
		ArrayList<Order> underlyingOrders = new ArrayList<Order>();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, block_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Order o = new Order(rs.getInt("id"),
						   rs.getString("symbol"),
						   rs.getInt("quantity"),
						   rs.getString("side"),
						   rs.getString("type"),
						   rs.getDouble("price"),
						   rs.getInt("trader"),
						   rs.getString("notes"));
				o.setBlock_order_id(rs.getInt("block_order_id"));
				o.setStatus(rs.getString("status"));
				underlyingOrders.add(o);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return underlyingOrders;
	}
	
	public ArrayList<Order> getBlockOrdersForId(int blockId){		
		String sql = 
				 "SELECT * FROM orders WHERE block_order_id=?";
		ArrayList<Order> orders = new ArrayList<Order>();
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, blockId);
	
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
					int id = rs.getInt("id");
										
				String symbol=rs.getString("symbol");
				int quantity = rs.getInt("quantity");
				String side = rs.getString("side");
				String type = rs.getString("type");
				double price = rs.getDouble("price");
				int traderId = rs.getInt("trader");
				String status = rs.getString("status");
				String notes = rs.getString("notes");
				String timeStamp = rs.getString("timeStamp");
				String pmName = getPmNameForPortId(rs.getInt("port_Id"));
				Order order = new Order(id, symbol, quantity, side, type, price, traderId, notes);
				order.setStatus(status);
				order.setPmName(pmName);
				order.setTimeStamp(timeStamp);
				orders.add(order);			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;		
	}
	
	
	
	

	private String getPmNameForPortId(int id) {
		String sql = 
				 "SELECT id,fname, lname, userid, pm_id"
				 + " FROM mtdb.portfolio p, mtdb.users "
				 + "WHERE p.pm_id = ?";
		String returnString = "Sarah O'Reilly";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
	
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {										
			String firstname =rs.getString("fname");
			returnString = firstname;
			String lastname =rs.getString("lname");
			firstname = firstname.concat(" ");
			returnString = firstname.concat(lastname);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnString;
	}
	
	public int getIdFromName(String fname, String lname){
		String sql = "SELECT userid FROM users WHERE fname=? AND lname=?";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, fname);
			ps.setString(2, lname);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getInt("userid");		
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void exportToExcel(int traderId, ArrayList<Order> traderOrders) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Trader Info");
		
		System.out.println("I am running!"+ traderOrders);
		int rows = 0;
		
		for (Order order : traderOrders) {
			Row row = sheet.createRow(rows++);
			Object[] cellArray = returnArrayWithCellData(order);
			int cellnum = 0;
			for (Object obj : cellArray) {
				Cell cell = row.createCell(cellnum++);
				setCellForAppropriateClassInstance(cell, obj);
			}
			
		}
		
		try {
			FileOutputStream out = 
					new FileOutputStream(new File("D:\\traderOrders.xls"));
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private Object[] returnArrayWithCellData(Order order) {
		return new Object [] { order.getId(), order.getSymbol(), order.getSide(),  order.getType(), order.getPrice(), order.getTrader(), order.getStatus(), order.getNotes()};
	}

	

	private void setCellForAppropriateClassInstance(Cell cell, Object obj) {
		if(obj instanceof Date) 
			cell.setCellValue((Date)obj);
		else if(obj instanceof Boolean)
			cell.setCellValue((Boolean)obj);
		else if(obj instanceof String)
			cell.setCellValue((String)obj);
		else if(obj instanceof Double)
			cell.setCellValue((Double)obj);
		else if(obj instanceof Integer)
			cell.setCellValue((Integer)obj);
	}
	
}
