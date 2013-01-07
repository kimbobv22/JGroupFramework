package common.main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import common.db.JGDBConnection;
import common.db.JGDBManager;
import common.main.data.JGHttpBox;
import common.main.exception.JGException;
import common.util.JGEncryptionUtil;

public class JGAction{
	public JGHttpBox inOutBox = null;
	
	private JGDBManager DBManager = new JGDBManager();
	public JGDBManager getDBManager() throws JGException{
		return DBManager;
	}
	public JGDBConnection getDBConnection(String jdbcName_) throws JGException{
		return getDBManager().getConnection(jdbcName_);
	}
	public JGDBConnection getDBConnection() throws JGException{
		return getDBManager().getConnection();
	}
	
	public JGAction allocOtherAction(String actionClassName_) throws JGException{
		try{
			JGAction action_ = (JGAction)Class.forName(actionClassName_).newInstance();
			action_.inOutBox = inOutBox;
			return action_;
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.main.JGAction.0000");
		}
	}
	
	public void process() throws JGException{
		String encryptedActionID_ = inOutBox.getParameter("actionID");
		JGMainSystem mainSystem_ = JGMainSystem.sharedSystem();
		String actionID_ = null;
		if(mainSystem_.isEnableEncryption()){
			actionID_ = JGEncryptionUtil.decodeString(encryptedActionID_); 
		}else{
			actionID_ = encryptedActionID_;
		}
		
		if(actionID_ == null){
			throw new JGException(getClass(), new NullPointerException(), "common.main.JGAction.0001");
		}
		
		try{
			Method methods_[] = getClass().getMethods();
			int methodCount_ = methods_.length; 
			for(int index_=0; index_< methodCount_;++index_){
				if(methods_[index_].getName().equals(actionID_)){
					methods_[index_].invoke(this);
					break;
				}
			}
		}catch(InvocationTargetException ex_){
			Exception targetEx_ = (JGException)ex_.getTargetException();
			if(targetEx_.getClass() == JGException.class){
				throw (JGException) targetEx_;
			}else{
				throw new JGException(getClass(), ex_, "common.main.JGAction.0002");
			}
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.main.JGAction.0002");
		}
		
		release();
	}
	
	public void release() throws JGException{
		DBManager.release();
	}
}
