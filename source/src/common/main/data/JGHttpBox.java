package common.main.data;

import java.math.BigDecimal;
import java.util.Enumeration;


import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.main.exception.JGException;
import common.util.JGServletUtil;

public class JGHttpBox{
	public class JGHttpSession{
		private HttpSession _session = null;
		
		public JGHttpSession(HttpSession session_) throws JGException{
			_session = session_; 
		}
		public void setObject(String keyName_, Object value_) throws JGException{
			_session.setAttribute(keyName_, value_);
		}
		public void removeObject(String keyName_) throws JGException{
			_session.removeAttribute(keyName_);
		}
		public Object getObject(String keyName_) throws JGException{
			return _session.getAttribute(keyName_);
		}
		public Enumeration<String> keyEnumeration() throws JGException{
			return _session.getAttributeNames();
		}
		
		public HttpSession getSession() throws JGException{
			return _session;
		}
	}
	
	private HttpServletRequest _request = null;
	private HttpServletResponse _response = null;
	private JGHttpSession _session = null;
	private JGHttpResponseWriter _responseWriter = null;	

	public JGHttpBox(HttpServletRequest request_,HttpServletResponse response_) throws JGException{
		_request = request_;
		_response = response_;
	}
	
	public String getParameter(String keyName_)throws JGException{
		return JGServletUtil.convertKorString(_request .getParameter(keyName_));
	}
	public Integer getParamterAsInteger(String keyName_) throws JGException{
		return new Integer(getParameter(keyName_));
	}
	public Long getParamterAsLong(String keyName_) throws JGException{
		return new Long(getParameter(keyName_));
	}
	public BigDecimal getParamterAsDecimal(String keyName_) throws JGException{
		return new BigDecimal(getParameter(keyName_));
	}
	
	public void setAttribute(String keyName_, Object value_) throws JGException{
		_request.setAttribute(keyName_, value_);
	}
	public void removeAttribute(String keyName_) throws JGException{
		_request.removeAttribute(keyName_);
	}
	public Object getAttribute(String keyName_) throws JGException{
		return _request.getAttribute(keyName_);
	}
	
	public RequestDispatcher getRequestDispather(String target_) throws JGException{
		return _request.getRequestDispatcher(target_);
	}
	
	public HttpServletRequest getRequest() throws JGException{
		return _request;
	}
	public JGHttpSession getSession() throws JGException{
		if(_session == null){
			_session = new JGHttpSession(_request .getSession());
		}
		
		return _session;
	}
	public HttpServletResponse getResponse() throws JGException{
		return _response;
	}
	public JGHttpResponseWriter getWriter() throws JGException{
		if(_responseWriter == null){
			_responseWriter = new JGHttpResponseWriter(_response);
		}
		return _responseWriter;
	}
	
	public void forword(String target_) throws JGException{
		try{
			getRequestDispather(target_).forward(_request, _response);
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.main.data.JGHttpBox.0000");
		}
	}
}
