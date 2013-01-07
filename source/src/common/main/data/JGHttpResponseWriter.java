package common.main.data;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import common.main.JGMainSystem;
import common.main.exception.JGException;
import common.util.JGEncryptionUtil;

public class JGHttpResponseWriter {
	private HttpServletResponse _response = null;
	private PrintWriter _writer = null;
	public StringBuffer stringBuffer = new StringBuffer();
	
	public JGHttpResponseWriter(HttpServletResponse response_) throws JGException{
		try{
			_response = response_;
			_writer = _response.getWriter();
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.main.data.JGHttpResponseWriter.0000");
		}
	}
	
	public void print(boolean doEncrypt_) throws JGException{
		 _response.setContentType("text/html");
		 String printString_ = stringBuffer.toString();
		 if(doEncrypt_){
			 printString_ = JGEncryptionUtil.encodeString(printString_);
		 }
		 _writer.print(printString_);
		flush();
	}
	public void print() throws JGException{
		print(JGMainSystem.sharedSystem().isEnableEncryption());
	}
	public void flush(){
		stringBuffer = new StringBuffer();
	}
	
	public PrintWriter getWriter(){
		return _writer;
	}
}
