package common.main;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JGHttpServlet extends HttpServlet{
	
	private static final long serialVersionUID = 6001599666716741982L;
	
	public void doGet(HttpServletRequest req_, HttpServletResponse res_) throws ServletException, IOException{
		process(req_, res_);
	}
	
	public void doPost(HttpServletRequest req_, HttpServletResponse res_) throws ServletException, IOException{
		process(req_, res_);
	}
	
	public void process(HttpServletRequest req_, HttpServletResponse res_) throws ServletException, IOException{
		JGMainSystem.sharedSystem().handleRequest(req_, res_);
	}
}
