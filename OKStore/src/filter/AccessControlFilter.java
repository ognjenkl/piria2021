package filter;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bean.UserBean;


/**
 * @author ognjen
 *
 */
//TODO Uncomment web filter annotation
@WebFilter("/*")
public class AccessControlFilter implements Filter{
//	 private static final String AJAX_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//		        + "<partial-response><redirect url=\"%s\"></redirect></partial-response>";

	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) arg0;
		HttpServletResponse resp = (HttpServletResponse) arg1;
//		System.out.println("Access controler: ");
		HttpSession session = req.getSession(false);
		
//		System.out.println("uri: " + req.getRequestURI());
//		System.out.println("servlet path: " + req.getServletPath());
//		System.out.println("context path: " + req.getContextPath());
//		uri: /Piria2016ProjektniZNDFilm/rfRes/skinning.ecss.xhtml
//		servlet path: /rfRes/skinning.ecss.xhtml
//		context path: /Piria2016ProjektniZNDFilm
		UserBean userBean = (session != null) ? (UserBean)session.getAttribute("userBean") : null;
		String homeURL = req.getContextPath() + "/guest.xhtml";
		String activationURL = req.getContextPath() + "/activate.xhtml";
		
		boolean loggedIn = userBean != null && userBean.isLoggedIn();
		boolean guestRequest = req.getRequestURI().startsWith(homeURL);
		boolean activationRequest = req.getRequestURI().startsWith(activationURL); 
		boolean resourceRequest = req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER);
		if(loggedIn || guestRequest || resourceRequest || activationRequest) {
			if(!resourceRequest){ // Prevent browser from caching restricted resources.
				resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
				resp.setHeader("Pragma", "no-cache"); // HTTP 1.0.
				resp.setDateHeader("Expires", 0); // Proxies.
			}			
			
			if(req.getServletPath().startsWith("/manageUsers.xhtml") && userBean.getUser().getPrivilege() > 1){
				resp.sendRedirect(homeURL);
			} else if (req.getServletPath().startsWith("/message.xhtml") && userBean.getUser().getPrivilege() > 3) {
				resp.sendRedirect(homeURL);
//			} else if(req.getServletPath().startsWith("/buyingArticles.xhtml") && userBean.getUser().getPrivilege() > 3){
//				resp.sendRedirect(homeURL);
//			} else if(req.getServletPath().startsWith("/chat.xhtml") && userBean.getUser().getPrivilege() > 3){
//				resp.sendRedirect(homeURL);
//			} else if(req.getServletPath().startsWith("/user.xhtml") && loginBean.getUser().getPrivilege() > 3){
//				resp.sendRedirect(homeURL);
//			} else if(req.getServletPath().startsWith("/gallery.xhtml") && loginBean.getUser().getPrivilege() > 3){
//				resp.sendRedirect(homeURL);
//			} else if(req.getServletPath().startsWith("/favoriteLists.xhtml") && loginBean.getUser().getPrivilege() > 4){
//				resp.sendRedirect(homeURL);
			}//to do add add movie page
			else{
				//System.out.println("2");

				arg2.doFilter(arg0, arg1); 
			}
//		}else if (ajaxRequest) {
//            response.setContentType("text/xml");
//            response.setCharacterEncoding("UTF-8");
//            response.getWriter().printf(AJAX_REDIRECT_XML, loginURL); // So, return special XML response instructing JSF ajax to send a redirect.
        }else{
			//System.out.println("3");

			//prereacunava naknadno putanju za resp.sendRedirect("guest.xhtml");
			resp.sendRedirect(homeURL);
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
