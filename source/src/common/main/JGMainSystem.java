package common.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.db.xml.JGDBXMLQueryManager;
import common.main.data.JGHttpBox;
import common.main.data.filter.JGActionFilter;
import common.main.exception.JGException;
import common.util.JGEncryptionUtil;

public class JGMainSystem {
	static private JGMainSystem _sharedMainSystem_ = sharedSystem();
	
	private ResourceBundle _mainBundle = null;
	
	private ArrayList<String> _jdbcList = new ArrayList<String>();
	public ArrayList<String> getJDBCList(){
		return _jdbcList;
	}
	
	private String _dbXMLQueryFilePath = null;
	public String getDBXMLQueryFilePath(){
		return _dbXMLQueryFilePath;
	}
	
	private JGActionFilter _filter = new JGActionFilter();
	
	private int _debugLevel = 0;
	public int getDebugLevel(){
		return _debugLevel;
	}
	
	/**
	 * encryption
	 */
	private boolean _enableEncryption = false;
	private String _encryptionAlgorithm = "AES";
	private String _encryptionKey = null;
	
	public void setEnableEncryption(boolean isEnable_){
		_enableEncryption = isEnable_;
	}
	public boolean isEnableEncryption(){
		return _enableEncryption;
	}
	public String getEncryptionAlgorithm(){
		return _encryptionAlgorithm;
	}
	private void setEncryptionAlgorithm(String algorithm_){
		_encryptionAlgorithm = algorithm_;
		JGEncryptionUtil.setCipherAlgorithm(_encryptionAlgorithm);
	}
	public String getEncryptionKey(){
		return _encryptionKey;
	}
	private void setEncryptionKey(String key_){
		_encryptionKey = key_;
		JGEncryptionUtil.setCipherDefaultKey(key_);
	}
	
	/**
	 * default date format
	 */
	SimpleDateFormat _dateFormat_DEFAULT = null;
	SimpleDateFormat _dateFormat_YMD = null;
	SimpleDateFormat _dateFormat_YMDShort = null;
	SimpleDateFormat _dateFormat_HMS = null;
	
	public void setDateFormat_DEFAULT(String format_){
		_dateFormat_DEFAULT.applyPattern(format_);
	}
	public SimpleDateFormat getDateFormat_DEFAULT(){
		return _dateFormat_DEFAULT;
	}
	public void setDateFormat_YMD(String format_){
		_dateFormat_YMD.applyPattern(format_);
	}
	public SimpleDateFormat getDateFormat_YMD(){
		return _dateFormat_YMD;
	}
	public void setDateFormat_YMDShort(String format_){
		_dateFormat_YMDShort.applyPattern(format_);
	}
	public SimpleDateFormat getDateFormat_YMDShort(){
		return _dateFormat_YMDShort;
	}
	public void setDateFormat_HMS(String format_){
		_dateFormat_HMS.applyPattern(format_);
	}
	public SimpleDateFormat getDateFormat_HMS(){
		return _dateFormat_HMS;
	}
	
	public Object log;
	
	static public JGMainSystem sharedSystem(){
		if(_sharedMainSystem_ == null){
			synchronized(JGMainSystem.class){
				_sharedMainSystem_ = new JGMainSystem();
				_sharedMainSystem_.preloadModule();
			}
		}
		return _sharedMainSystem_;
	}
	
	public JGMainSystem() {
		try{
			getMainBundle();
			
			//get jdbc names
			String jdbcFormatter_ = "db.jdbc.name.%03d";
			for(int index_=0;index_<50;++index_){
				try{
					String jdbcName_ = _mainBundle.getString(String.format(jdbcFormatter_, index_));
					if(jdbcName_ == null){
						break;
					}
					_jdbcList.add(jdbcName_);
				}catch(Exception ex_){
					break;
				}
			}
			
			//get db query xml path
			_dbXMLQueryFilePath = getClass().getResource("/").getPath()+_mainBundle.getString("db.XMLQueryFilePath");
			
			//get debug level
			_debugLevel = Integer.valueOf(_mainBundle.getString("common.debugLevel")).intValue();
			
			//set default encryption info
			_enableEncryption = Boolean.valueOf(_mainBundle.getString("common.enableEncryption"));
			setEncryptionAlgorithm(_mainBundle.getString("common.encryptionAlgorithm"));
			setEncryptionKey(_mainBundle.getString("common.encryptionKey"));
			
			_dateFormat_DEFAULT = new SimpleDateFormat(_mainBundle.getString("common.dateFormat_DEFAULT"));
			_dateFormat_YMD = new SimpleDateFormat(_mainBundle.getString("common.dateFormat_YMD"));
			_dateFormat_YMDShort= new SimpleDateFormat(_mainBundle.getString("common.dateFormat_YMDShort"));
			_dateFormat_HMS = new SimpleDateFormat(_mainBundle.getString("common.dateFormat_HMS"));
		}catch(Exception ex_){
			new JGException(getClass(),ex_,"common.main.JGMainSystem.0000").handleException(null);
		}
	}
	
	public void preloadModule(){
		try{
			JGDBXMLQueryManager.sharedManager();
		}catch(Exception ex_){
			new JGException(getClass(),ex_,"common.main.JGMainSystem.0000").handleException(null);
		}
	}
	
	public void handleRequest(HttpServletRequest req_, HttpServletResponse res_){
		String encryptedSrvID_ = req_.getParameter("srvID");
		JGAction action_ = null;
		
		try{
			String srvID_ = null;
			if(_enableEncryption){
				srvID_ = JGEncryptionUtil.decodeString(encryptedSrvID_); 
			}else{
				srvID_ = encryptedSrvID_;
			}

			try{
				action_ = (JGAction)Class.forName(srvID_).newInstance();
			}catch(Exception ex_){
				throw new JGException(getClass(), ex_, "common.main.JGMainSystem.0001");
			}
			
			action_.inOutBox = new JGHttpBox(req_, res_);
			
			doActionFilterF(action_);
			action_.process();
			doActionFilterL(action_);
		}catch(JGException ex_){
			ex_.handleException(action_);
		}
	}
	
	public ResourceBundle getMainBundle() throws JGException{
		if(_mainBundle == null){
			_mainBundle = ResourceBundle.getBundle("common");
		}
		return _mainBundle;
	}
	
	public void doActionFilterF(JGAction action_) throws JGException{
		_filter.doFilterF(action_);
	}
	public void doActionFilterL(JGAction action_) throws JGException{
		_filter.doFilterL(action_);
	}
}
