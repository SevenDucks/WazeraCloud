package eu.wauz.wazera.controller.auth;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ping")
public class SessionPingServlet extends HttpServlet {

	private static final long serialVersionUID = 5161149851957595149L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession();
		response.setContentType("text/plain");
		final PrintWriter writer = response.getWriter();
		writer.println("pong");
		writer.close();
	}

}
