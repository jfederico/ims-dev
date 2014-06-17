import java.io.IOException;
import java.io.PrintWriter;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.signature.OAuthSignatureMethod;
import net.oauth.server.HttpRequestMessage;
import net.oauth.server.OAuthServlet;
import net.oauth.signature.OAuthSignatureMethod;

import org.imsglobal.basiclti.BasicLTIUtil;

public class ProviderServlet extends HttpServlet {

	public void doError(HttpServletRequest request, HttpServletResponse response, 
                            String s, String message, Exception e)
		throws java.io.IOException
	{
		System.out.println(s);
		String return_url = request.getParameter("launch_presentation_return_url");
                if ( return_url != null && return_url.length() > 1 ) {
			if ( return_url.indexOf('?') > 1 ) {
				return_url += "&lti_msg=" + URLEncoder.encode(s);
			} else {
				return_url += "?lti_msg=" + URLEncoder.encode(s);
			}
			response.sendRedirect(return_url);
			return;
                }
		PrintWriter out = response.getWriter();
  		out.println(s);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		String ipAddress = request.getRemoteAddr();

		System.out.println("Basic LTI Provider request from IP="+ipAddress);

		String oauth_consumer_key = request.getParameter("oauth_consumer_key");
		String user_id = request.getParameter("user_id");
		String context_id = request.getParameter("context_id");
		String resource_link_id = request.getParameter("resource_link_id");
		if ( ! "basic-lti-launch-request".equals(request.getParameter("lti_message_type")) ||
		    ! "LTI-1p0".equals(request.getParameter("lti_version")) ||
		    oauth_consumer_key == null || resource_link_id == null ) {
			doError(request, response, "Missing required parameter.", null, null);
			return;
		}

		// Lookup the secret that corresponds to the oauth_consumer_key
		String oauth_secret = "secret";

		OAuthMessage oam = OAuthServlet.getMessage(request, null);
		OAuthValidator oav = new SimpleOAuthValidator();
		OAuthConsumer cons = new OAuthConsumer("about:blank#OAuth+CallBack+NotUsed", oauth_consumer_key, oauth_secret, null);

		OAuthAccessor acc = new OAuthAccessor(cons);

		String base_string = null;
		try {
			base_string = OAuthSignatureMethod.getBaseString(oam);
		} catch (Exception e) {
			base_string = null;
		}

		try {
			oav.validateMessage(oam,acc);
		} catch(Exception e) {
			System.out.println("Provider failed to validate message");
			System.out.println(e.getMessage());
			if ( base_string != null ) System.out.println(base_string);
			doError(request, response,"Launch data does not validate", context_id, null);
			return;
		}

		String userrole = request.getParameter("roles");
		if ( userrole == null ) userrole = "";
		userrole = userrole.toLowerCase();
        boolean isInstructor = userrole.indexOf("instructor") >= 0 ;

		String userKey = null;
		if ( user_id != null ) {
			userKey = oauth_consumer_key + ":" + user_id;
		}

		String courseKey = null;
		if ( context_id != null ) {
			courseKey = oauth_consumer_key + ":" + context_id;
		}

		String courseName = context_id;
                if ( request.getParameter("context_title") != null ) courseName = request.getParameter("context_title");
                if ( request.getParameter("context_label") != null ) courseName = request.getParameter("context_label");

		String name_full = request.getParameter("lis_person_name_full");
		String name_given = request.getParameter("lis_person_name_given");
		String name_family = request.getParameter("lis_person_name_family");
		String email = request.getParameter("lis_person_contact_email_primary");

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
                out.println("<h1>Basic LTI Provider Servlet Security Passed</h1>\n");
                out.println("<pre>\n");
                out.println("email="+email+"\n");
                out.println("name_given="+name_given+"\n");
                out.println("name_family="+name_family+"\n");
                out.println("name_full="+name_full+"\n");
                out.println("userKey="+userKey+"\n");
                out.println("courseName="+courseName+"\n");
                out.println("courseKey="+courseKey+"\n");
                out.println("</pre>\n");

	}

	public void destroy() {

	}

}
