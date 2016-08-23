package com.interfaceschool.omaha.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;

import com.interfaceschool.omaha.pojo.City;



@WebServlet("/omaha")
public class OmahaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	  private DataSource ds;
		
	    public OmahaServlet() throws NamingException {
	        super();
	        String jndiName = "jdbc/omaha";
			InitialContext ctx = new InitialContext();
			try {
				ds = (DataSource)ctx.lookup("java:comp/env/"+jndiName);
			}
			catch (NamingException e) {
				ds = (DataSource)ctx.lookup(jndiName);
			}
	    }
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
			List<City> list = getCities();
			if (list != null) {
				JSONArray root = new JSONArray();
				for (City city:list) {
					JSONObject city2 = new JSONObject();
					city2.put("name", city.getName());
					city2.put("population", city.getPopulation());
					root.put(city2);
				}
				response.getWriter().write(root.toString());
			}
			else {
				getErrorMessage(null,response);
			}
		}

		private void getErrorMessage(String text, HttpServletResponse response) throws IOException {
			JSONObject msg = new JSONObject();
			if (text == null) {
				text = "We could not complete your request, please try again later";
			}
			msg.put("msg", text);
			response.getWriter().write(msg.toString());
		}
		
		private List<City> getCities() {
			List<City> list = new LinkedList<City>();
			Connection connection = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				connection = ds.getConnection();
				stmt = connection.prepareStatement("select * from omaha.city");
				rs = stmt.executeQuery();
				while (rs.next()) {  
					City city = new City();
					city.setName(rs.getString("city_name"));
					city.setPopulation(rs.getInt("city_population"));
					list.add(city);
				}
			}
			catch (SQLException e) {
				//report and deal with error here
				System.err.println(e);
				return null;
			}
			finally {
				closeResources(rs, stmt, connection);
			}
			return list;
		}
		
		private static void closeResources(ResultSet rs, PreparedStatement stmt, Connection connection) {
			//clean up resources here
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}

		}

	


protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
	doGet(request, response);
}
}