package common.db;

import java.util.HashMap;
import java.util.Iterator;

import common.main.JGMainSystem;
import common.main.exception.JGException;

public class JGDBManager {
	private HashMap<String, JGDBConnection>_connectionList = new HashMap<String, JGDBConnection>();
	
	public JGDBConnection getConnection(String jdbcName_) throws JGException{
		JGDBConnection connection_ = _connectionList.get(jdbcName_);
		
		if(connection_ == null){
			connection_ = new JGDBConnection(jdbcName_);
			_connectionList.put(jdbcName_, connection_);
		}
		
		return connection_;
		
	}
	public JGDBConnection getConnection() throws JGException{
		return getConnection(JGMainSystem.sharedSystem().getJDBCList().get(0));
	}
	
	public void release() throws JGException{
		if(_connectionList.size() == 0) return;

		Iterator<JGDBConnection> connections_ = _connectionList.values().iterator();
		while(connections_.hasNext()){
			connections_.next().release();
		}
	}
}
