package com.loginpage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MainPageServlet
 */
@WebServlet("/MainPageServlet")
public class MainPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainPageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginName = LoginManagerServlets.getLoginName(request);
		if (loginName != null) { // User has already logged in.
		generateMainPage(response, loginName);
		} else { // User has not logged in yet.
		response.sendRedirect("ShowLogin");
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void generateMainPage(HttpServletResponse response,
			String loginName) throws IOException {
			response.setContentType("text/html; charset=ISO-8859-1");
			PrintWriter out = response.getWriter();
			out.println("<html><head><title>");
			out.println("Portal-1 main page");
			out.println("</title></head>");
			out.println("<body text=\"#000000\" bgcolor=\"#ffffff\" " +
			"link=\"#000ee0\" vlink=\"#551a8b\" alink=\"#000ee0\">");
			out.println("<h1>Hello " + loginName + "! </h1>");
			out.println("<br><br><br>");
			out.println("<a href=\"Index.jsp\">Servlet and JSP tutorial " +
			"main page</a><br>");
			out.println("<a href=\"ProcessLogout\">Logout</a><br>");
			out.println("</body></html>");
			out.close();
			}
}
