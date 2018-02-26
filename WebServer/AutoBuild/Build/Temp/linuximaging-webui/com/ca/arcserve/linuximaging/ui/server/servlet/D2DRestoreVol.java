package com.ca.arcserve.linuximaging.ui.server.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;

/**
 * Servlet implementation class D2DRestoreVol
 */
public class D2DRestoreVol extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(D2DRestoreVol.class);
	private static final String D2D_Restore_Vol_Script = "d2drestorevol";
	private static final String Package_Name_X64 = "LinuxImage-volrestore-client-x64.tar.gz";
	private static final String Package_Name_X86 = "LinuxImage-volrestore-client-x86.tar.gz";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public D2DRestoreVol() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		logger.debug("D2DRestoreVol start");
		String scriptFile = CommonUtil.PATH_D2D_SERVER_HOME+java.io.File.separator + "bin" + java.io.File.separator + D2D_Restore_Vol_Script;
		File file = new File(scriptFile);
		
		if (file.exists()) {
			String fileContent = readFileAsString(scriptFile);
			fileContent = changeHostname(fileContent);
			fileContent = changeMD5(fileContent);
			fileContent = changeServerLang(fileContent);
			resp.setContentType("application/octet-stream");
			resp.setContentLength(fileContent.length());
			resp.addHeader("Content-Disposition", "attachment; filename=" + D2D_Restore_Vol_Script);
            
			ServletOutputStream servletOutputStream=resp.getOutputStream();
			InputStream fileInputStream= new ByteArrayInputStream(fileContent.getBytes("UTF-8"));
			BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream);
			int size=0;
			byte[] b=new byte[4096];
			while ((size=bufferedInputStream.read(b))!=-1) {
				servletOutputStream.write(b, 0, size);
			}
            servletOutputStream.flush();
            servletOutputStream.close();
            bufferedInputStream.close();
		}else {
			logger.error("script file does not exist");
		}
		logger.debug("D2DRestoreVol end");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	private String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
	
	private String getFileMD5(String fileName){
		String md5 = "";
		try {
			logger.info(fileName);
			Process p = Runtime.getRuntime().exec("md5sum " + fileName);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			bw.newLine();
			bw.close();
			int ret = p.waitFor();
			if(ret == 0){
				InputStream in = p.getInputStream();
				InputStreamReader inr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(inr);
				String line = "";
			    while((line = br.readLine()) != null){
			    	md5 = line.split(" ")[0];
			    }
			}else{
				logger.error("getFileMD5 failed ret:" + ret);
			}
		}catch (IOException e) {
			logger.error("getFileMD5 failed:",e);
		} catch (InterruptedException e) {
			logger.error("getFileMD5 failed",e);
		}
		logger.debug("md5:" + md5);
		return md5;
	}
	
	
	
	private String changeHostname(String content){
		String oldStr = "https://127.0.0.1:8014/";
		String newStr = CommonUtil.getServiceProtocol() + "://" + CommonUtil.getLocalHostName() + ":" + CommonUtil.getServicePort() +"/";
		logger.debug("new hostname" + newStr);
		return content.replaceAll(oldStr, newStr);
	}
	
	private String changeMD5(String content){
		String oldMD5_X64 = "X64_PACKAGE_MD5";
		String packageFileName_X64 = CommonUtil.PATH_D2D_SERVER_HOME + java.io.File.separator + "packages" + java.io.File.separator + Package_Name_X64;
		String newMD5_X64 = getFileMD5(packageFileName_X64);
		content = content.replaceAll(oldMD5_X64, newMD5_X64);
		
		String oldMD5_X86 = "X86_PACKAGE_MD5";
		String packageFileName_X86 = CommonUtil.PATH_D2D_SERVER_HOME + java.io.File.separator + "packages" + java.io.File.separator + Package_Name_X86;
		String newMD5_X86 = getFileMD5(packageFileName_X86);
		return content.replaceAll(oldMD5_X86, newMD5_X86);
	}
	
	private String changeServerLang(String content){
		String oldStr = "LOCALE_DIR=\"C\"";
		String newStr = "LOCALE_DIR=\"" + getLang() + "\"";
		return content.replaceAll(oldStr, newStr);
	}
	
	private String getLang(){
		String lang = com.ca.arcserve.linuximaging.util.CommonUtil.getLocaleStr();
		if(lang == null || lang.equalsIgnoreCase("en")){
			return "C";
		}
		return lang;
	}
}
