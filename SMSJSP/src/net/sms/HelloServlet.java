package net.sms;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.smslib.InboundMessage;

/**
 * Servlet implementation class HelloServlet
 */


@WebServlet("/HelloServlet")
public class HelloServlet extends HttpServlet {
	
	MainSMS app = new MainSMS();


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
	 
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		if ( request.getParameterMap().containsKey( "incoming" ) ) {
			
			handleReceiveRequest( request, response, out );
		}
		
		
		else if ( request.getParameterMap().containsKey( "phone" ) ) {
			
			System.out.println( "you want to SEND message" );
			handleSendRequest( request, response, out );
		}
		
	}
	
	private void handleReceiveRequest( HttpServletRequest request, HttpServletResponse response, PrintWriter out ) {
		
		String msg_heap = "";
		
		for ( InboundMessage msg : app.getSmsMsgList() ) {
			
			msg_heap += msg +"\n";
		}
		
		out.println( msg_heap );
	}
	
	private void handleSendRequest( HttpServletRequest request, HttpServletResponse response, PrintWriter out ) {
		
		String all_phone = request.getParameter( "phone" );
		String message = request.getParameter( "message" );
				
		String[] phone = all_phone.split(",");
		
		if ( phone.length == 0 ) {
			
			return;
		}
		else if ( phone.length == 1 ) {
			
			app.sendSMS( all_phone, message );
		}
		else if ( phone.length > 1 ) {
			
			HashMap<String, String> m = new HashMap<String, String>();
			
			for ( int i=0; i<phone.length; i++ ) 
				m.put( phone[i], message );
			
			app.multiSendSMS( m );
		}
		
		out.println( "send" );
	}
	
	
}
	

