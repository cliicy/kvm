package com.ca.arcserve.linuximaging.ui.server.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ca.arcserve.linuximaging.util.CommonUtil;

public class LocaleFilter implements Filter {

	private static final Logger logger = Logger.getLogger(GWTCacheControlFilter.class);
	private FilterConfig filterConfig;
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain arg2) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		resp.setCharacterEncoding("utf-8");
		String locale = CommonUtil.getLocaleStr();
		logger.debug("locale:" + locale);
		InputStream is =null;
		BufferedReader reader = null;
		try{
			
			String filename = "/ARCserveLinuxImagingUI.html";
			ServletContext context = filterConfig.getServletContext();
			is = context.getResourceAsStream(filename);
			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is, "utf-8");
				reader = new BufferedReader(isr);

				String text = "";
				boolean docTypeChangeFlag = need2ChangeDocType(req);
				while ((text = reader.readLine()) != null)
				{
					if (docTypeChangeFlag && text.contains("<!doctype html>")) {
						text = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">";
					} else if (text.contains("<head>")) {
						text = appendMetaDataLineForIE(req, text);
					}else if (text.contains("locale=en") )
					{
						text = text.replaceAll("locale=en", "locale="+locale);
					}
					resp.getWriter().write(text);
				}
				
				resp.getWriter().flush();
				resp.getWriter().close();
			}
				
			return;
		}catch(Exception e){
			logger.error("", e);
		}finally{
			try
			{
				if (is != null) {
					is.close();
				}
				if(reader != null) reader.close();
			}catch(Exception eclose){}
		}

	}

	private boolean need2ChangeDocType(HttpServletRequest req) {
		String userAgent = req.getHeader("User-Agent");
		logger.debug(userAgent);

		if (userAgent.indexOf("MSIE 7.0") > 0 && !userAgent.contains("Trident/4.0"))
			return true;
		else if (userAgent.indexOf("MSIE 6.0") > 0)
			return true;
		return false;
	}
	
	// //see doc http://msdn.microsoft.com/library/ms537503.aspx; prevent
	// browser automatically into compatible IE7 mode
	private String appendMetaDataLineForIE(HttpServletRequest req, String text) {
		String userAgent = req.getHeader("User-Agent");
		if (userAgent.indexOf("MSIE 7.0") > 0) {// browser mode IE7;

			if (userAgent.contains("Trident/4.0")) { // /actually browser
														// version IE8
				text += "\n <meta http-equiv=\"X-UA-Compatible\" content=\"IE=8\" /> ";
			} else if (userAgent.contains("Trident/5.0")) {
				text += "\n <meta http-equiv=\"X-UA-Compatible\" content=\"IE=9\" /> ";
			} else if (userAgent.contains("Trident/6.0")) {
				text += "\n <meta http-equiv=\"X-UA-Compatible\" content=\"IE=10\" /> ";
			}
		}

		return text;
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		this.filterConfig = arg0;
	}

}
