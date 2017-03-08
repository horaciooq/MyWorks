package com.loginpage;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HelloUSerServlet
 */
@WebServlet("/HelloUSerServlet")
public class HelloUSerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HelloUSerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* Get value of parameter "userName". */
		
		String userName = request.getParameter("userName");
		String ab="sss";
		/* Generate response. */
		response.setContentType("text/html; charset=ISO-8859-1"); 
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>");
		out.println("HelloUser response");
		out.println("</title></head>");
		out.println("<body text=\"#000000\" bgcolor=\"#ffffff\">");
		out.println("<h1>Hello " +userName + "! </h1>");
		out.println("<h1>Hello " + ab+ "! </h1>");
		out.println("</body></html>");
		out.close(); 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
