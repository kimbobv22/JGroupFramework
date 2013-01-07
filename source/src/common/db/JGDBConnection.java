package common.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import common.db.vo.JGDBParameter;
import common.db.vo.JGDBQuery;
import common.main.JGMainSystem;
import common.main.data.xml.JGXMLDataSet;
import common.main.data.xml.JGXMLDataSetColumn;
import common.main.data.xml.JGXMLDataSetRow;
import common.main.exception.JGException;

public class JGDBConnection {
	private Connection _connection;
	private int _defaultResultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
	public int getDefaultResultSetType(){
		return _defaultResultSetType;
	}
	public void setDefaultResultSetType(int resultSetType_){
		_defaultResultSetType = resultSetType_;
	}
	
	private int _defaultResultSetConcurrency = ResultSet.CONCUR_READ_ONLY;
	public int getDefaultResultSetConcurrency(){
		return _defaultResultSetConcurrency;
	}
	public void setDefaultResultSetConcurrency(int resultSetConcurrency_){
		_defaultResultSetConcurrency = resultSetConcurrency_;
	}
	
	public JGDBConnection(String jdbcName_) throws JGException{
		try{
			Context context_ = (Context)new InitialContext().lookup("java:/comp/env");
			DataSource dataSource_ = (DataSource)context_.lookup(jdbcName_);
			_connection = dataSource_.getConnection();
			_connection.setAutoCommit(false);
			
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0000");
		}
	}
	
	public Connection connection(){
		return _connection;
	}
	
	public void commit() throws JGException{
		try{
			_connection.commit();
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0001");
		}
	}
	public void rollback() throws JGException{
		try{
			_connection.rollback();
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0002");
		}
		
	}
	
	public void release() throws JGException{
		rollback();
		try{
			_connection.close();
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0003");
		}
	}
	
	private void _printQuery(String query_, Object[] parameter_) throws JGException{
		if(JGMainSystem.sharedSystem().getDebugLevel() <= 0){
			return;
		}
		
		StringBuffer sBuffer_ = new StringBuffer();
		sBuffer_.append("*******access DB*******\n\n");
		sBuffer_.append(query_+"\n\n");
		if(parameter_ != null){
			sBuffer_.append(" * parameters \n");
			int count_ = parameter_.length;
			for(int index_=0;index_<count_;++index_){
				sBuffer_.append("["+index_+"]"+String.valueOf(parameter_[index_])+"\n");
			}
		}
		sBuffer_.append("\n***********************\n");
		System.out.println(sBuffer_);
	}
	
	private void _fillStatement(PreparedStatement statement_, Object[] parameters_) throws JGException{
		try{
			if(parameters_ != null){
				int count_ = parameters_.length;
				for(int index_=0;index_<count_;++index_){
					Object object_ = parameters_[index_];
					statement_.setObject(index_+1, object_);
				}
			}
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0004");
		}
	}
	
	public PreparedStatement createStatement(String query_, Object[] parameters_, int resultSetType_, int resultSetConcurrency_) throws JGException{
		_printQuery(query_,parameters_);
		PreparedStatement statement_ = null;
		try{
			statement_ = _connection.prepareStatement(query_, resultSetType_, resultSetConcurrency_);
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0008");
		}
		
		_fillStatement(statement_, parameters_);
		return statement_;
	}
	public PreparedStatement createStatement(String query_, Object[] parameters_, int resultSetType_) throws JGException{
		return createStatement(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	public PreparedStatement createStatement(String query_, Object[] parameters_) throws JGException{
		return createStatement(query_, parameters_, _defaultResultSetType);
	}
	public PreparedStatement createStatement(String query_) throws JGException{
		return createStatement(query_, (Object[])null);
	}
	
	public CallableStatement createCallableStatement(String query_, Object[] parameters_, int resultSetType_, int resultSetConcurrency_) throws JGException{
		_printQuery(query_,parameters_);
		CallableStatement statement_ = null;
		try{
			statement_ = _connection.prepareCall(query_, resultSetType_, resultSetConcurrency_);
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0005");
		}
		_fillStatement(statement_, parameters_);
		return statement_;
		
	}
	public CallableStatement createCallableStatement(String query_, Object[] parameters_, int resultSetType_) throws JGException{
		return createCallableStatement(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	public CallableStatement createCallableStatement(String query_, Object[] parameters_) throws JGException{
		return createCallableStatement(query_, parameters_, _defaultResultSetType);
	}
	public CallableStatement createCallableStatement(String query_) throws JGException{
		return createCallableStatement(query_, (Object[])null);
	}
	
	public JGXMLDataSet executeQuery(String query_, Object[] objects_, int resultSetType_, int resultSetConcurrency_) throws JGException{
		PreparedStatement pStatement_ = createStatement(query_,objects_, resultSetType_, resultSetConcurrency_);
		
		ResultSet resultSet_ = null;
		try{
			resultSet_ = pStatement_.executeQuery();
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0006");
		}
		
		JGXMLDataSet dataSet_ = null;
		if(resultSet_ != null){
			dataSet_ = JGXMLDataSet.makeDataSet(resultSet_);
		}
		
		try{
			resultSet_.close();
			pStatement_.close();
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0007");
		}

		return dataSet_;
	}
	public JGXMLDataSet executeQuery(String query_, Object[] objects_, int resultSetType_) throws JGException{
		return executeQuery(query_,objects_, resultSetType_, _defaultResultSetConcurrency);
	}
	public JGXMLDataSet executeQuery(String query_, Object[] objects_) throws JGException{
		return executeQuery(query_,objects_, _defaultResultSetType);
	}
	public JGXMLDataSet executeQuery(String query_) throws JGException{
		return executeQuery(query_,(Object[])null);
	}
	
	public JGXMLDataSet executeQuery(JGDBQuery query_, int resultSetType_, int resultSetConcurrency_) throws JGException{
		return executeQuery(query_.getQuery(), query_.parameterToArray(), resultSetType_, resultSetConcurrency_);
	}
	public JGXMLDataSet executeQuery(JGDBQuery query_, int resultSetType_) throws JGException{
		return executeQuery(query_, resultSetType_, _defaultResultSetConcurrency);
	}
	public JGXMLDataSet executeQuery(JGDBQuery query_) throws JGException{
		return executeQuery(query_, _defaultResultSetType);
	}
	
	public int executeUpdate(String query_, Object[] objects_) throws JGException{
		PreparedStatement pStatement_ = createStatement(query_, objects_);
		try{
			int result_ = pStatement_.executeUpdate();
			pStatement_.close();
			return result_;
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0009");
		}
	}
	public int executeUpdate(String query_) throws JGException{
		return executeUpdate(query_, (Object[]) null);
	}
	
	public int executeUpdate(JGDBQuery query_) throws JGException{
		return executeUpdate(query_.getQuery(), query_.parameterToArray());
	}
	
	public int executeUpdate(JGXMLDataSet dataSet_, String tableName_) throws JGException{
		JGDBQuery query_ = new JGDBQuery();
		
		ArrayList<JGXMLDataSetColumn> columnList_ = dataSet_.columnInfo;
		int columnCount_ = columnList_.size();
		
		// execute insert & update
		int result_ = 0;
		int rowCount_ = dataSet_.rowCount();
		for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
			JGXMLDataSetRow rowItem_ = dataSet_.getRow(rowIndex_);
			
			JGDBParameter parameter_ = new JGDBParameter(tableName_);
			parameter_.setTargetName(tableName_);
			for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
				JGXMLDataSetColumn columnItem_ = columnList_.get(columnIndex_);
				String columnName_ = columnItem_.getName();
				parameter_.addValue(columnName_, rowItem_.getColumnValue(columnName_));
				if(columnItem_.isKey()){
					parameter_.addKey(columnName_,rowItem_.getColumnValue(columnName_));
				}
			}
			
			switch(rowItem_.rowStatus){
				case JGXMLDataSet.JGXML_ROWSTATUS_INSERT:{
					query_.fillQueryForINSERT(parameter_);
					break;
				}
				case JGXMLDataSet.JGXML_ROWSTATUS_UPDATE:{
					query_.fillQueryForUPDATE(parameter_);
					break;
				}
				default : break;
			}
			
			result_ = result_ + executeUpdate(query_);
		}
		
		// execute delete
		ArrayList<JGXMLDataSetColumn> keyColumnList_ = dataSet_.getKeyColumnList();
		int keyCount_ = keyColumnList_.size();
		
		ArrayList<JGXMLDataSetRow> deletedRowList_ = dataSet_.getDeletedRowData();
		rowCount_ = deletedRowList_.size();
		for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
			JGXMLDataSetRow rowItem_ = dataSet_.getDeletedRow(rowIndex_);
			
			JGDBParameter parameter_ = new JGDBParameter(tableName_);
			parameter_.setTargetName(tableName_);
			for(int columnIndex_=0;columnIndex_<keyCount_;++columnIndex_){
				JGXMLDataSetColumn columnItem_ = keyColumnList_.get(columnIndex_);
				String columnName_ = columnItem_.getName();				
				parameter_.addKey(columnName_,rowItem_.getColumnValue(columnName_));
			}
			
			query_.fillQueryForDELETE(parameter_);
			
			result_ = result_ + executeUpdate(query_);
		}
		
		return result_;
	}
	
	public boolean callProcedure(String query_, Object[] parameters_, int resultSetType_, int resultSetConcurrency_) throws JGException{
		CallableStatement statement_ = createCallableStatement(query_, parameters_, resultSetType_, resultSetConcurrency_);
		
		try{
			boolean result_ = statement_.execute();
			statement_.close();
			return result_ ;
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.JGDBConnection.0010");
		}
	}
	public boolean callProcedure(String query_, Object[] parameters_, int resultSetType_) throws JGException{
		return callProcedure(query_, parameters_, resultSetType_, _defaultResultSetConcurrency);
	}
	public boolean callProcedure(String query_, Object[] parameters_) throws JGException{
		return callProcedure(query_, parameters_, _defaultResultSetType);
	}
	public boolean callProcedure(String query_) throws JGException{
		return callProcedure(query_, (Object[])null);
	}
	
	public boolean callProcedure(JGDBQuery query_, int resultSetType_, int resultSetConcurrency_) throws JGException{
		return callProcedure(query_.getQuery(), query_.parameterToArray(), resultSetType_, resultSetConcurrency_);
	}
	public boolean callProcedure(JGDBQuery query_, int resultSetType_) throws JGException{
		return callProcedure(query_, resultSetType_, _defaultResultSetConcurrency);
	}
	public boolean callProcedure(JGDBQuery query_) throws JGException{
		return callProcedure(query_, _defaultResultSetType);
	}
}
