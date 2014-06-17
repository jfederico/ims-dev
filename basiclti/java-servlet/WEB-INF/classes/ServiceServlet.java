import java.util.Map;
import java.util.Properties;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.imsglobal.pox.IMSPOXRequest;

@SuppressWarnings("deprecation")
public class ServiceServlet extends HttpServlet {

	public void doError(HttpServletRequest request, HttpServletResponse response, 
                            String s, String message, Exception e)
		throws java.io.IOException
	{
		response.setContentType("application/xml");
		PrintWriter out = response.getWriter();
		String output = IMSPOXRequest.getFatalResponse(s);
  		out.println(output);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		doError(request, response,"Service only supports POST method", null, null);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		String oauth_consumer_key = "lmsng.school.edu";
		String oauth_secret = "secret";

		IMSPOXRequest pox = new IMSPOXRequest(oauth_consumer_key, oauth_secret, request);

		if ( ! pox.valid ) {
			doError(request, response, pox.errorMessage, null, null);
			return;
		}

		System.out.println("Version = "+pox.getHeaderVersion());

		Map<String,String> bodyMap = pox.getBodyMap();
		String guid = bodyMap.get("/resultRecord/sourcedGUID/sourcedId");
		System.out.println("guid="+guid);
		String grade = bodyMap.get("/resultRecord/result/resultScore/textString");
		System.out.println("grade="+grade);

		response.setContentType("application/xml");
		PrintWriter writer = response.getWriter();
		String desc = "Message received and validated operation="+pox.getOperation()+
				" guid="+guid+" grade="+grade;
		String output = pox.getResponseUnsupported(desc);
  		writer.println(output);
	}

	public void destroy() {

	}

}
