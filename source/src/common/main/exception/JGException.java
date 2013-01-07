package common.main.exception;

import java.util.ResourceBundle;

import common.main.JGAction;

public class JGException extends Exception{
	
	private static final long serialVersionUID = 6847897226745000623L;
	static final ResourceBundle _exceptionMessageBundle = ResourceBundle.getBundle("exceptionMessage");
	
	private Class<?> _ariseClass = null;
	public Class<?> getAriseClass(){
		return _ariseClass;
	}
	
	private Exception _exception = null;
	public Exception getException(){
		return _exception;
	} 
	
	private String _messageID = null;
	public String getMessageID(){
		return _messageID;
	}
	
	private String _remarkMessage = null;
	public String getRemarkMessage(){
		return _remarkMessage;
	}
	
	public JGException(Class<?> ariseClass_, Exception exception_, String messageID_, String remark_){
		super(exception_);
		_ariseClass = ariseClass_;
		_exception = exception_;
		_messageID = messageID_;
		_remarkMessage = remark_;
	}
	
	public JGException(Class<?> ariseClass_, Exception exception_, String messageID_){
		this(ariseClass_,exception_,messageID_,null);
	}
	
	public void handleException(JGAction action_){
		System.out.println(getMessage());
		
		// TODO handle exception
		//targetAction_.getClass()
	}
	
	public String getMessage(){
		StringBuffer str_ = new StringBuffer();
		str_.append("EXCEPTION!!!\n");
		str_.append("araise    class : "+_ariseClass.toString()+"\n");
		str_.append("exception class : "+_exception.getClass().toString()+"\n");
		if(_exceptionMessageBundle != null && _messageID != null){
			String message_ = _exceptionMessageBundle.getString(_messageID);
			if(message_ != null){
				str_.append("message : "+message_+"\n");	
			}
		}
		if(_remarkMessage != null){
			str_.append("remark message : "+_remarkMessage+"\n");
		}
		
		String exceptionStack_ = _exception.getMessage();
		if(exceptionStack_ != null){
			str_.append("***exception stack*** \n ");
			str_.append(_exception.getMessage()+"\n");
			str_.append("********************* \n ");
		}
	
		return str_.toString();
	}
}
