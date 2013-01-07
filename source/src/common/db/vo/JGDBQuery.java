package common.db.vo;

import java.util.ArrayList;
import common.db.vo.JGDBParameter.JGDBParameterValue;

public class JGDBQuery {
	private String _query = null;
	public String getQuery(){
		return _query;
	}
	public void setQuery(String query_){
		_query = query_;
	}
	
	private ArrayList<Object> _parameter = new ArrayList<Object>();
	public ArrayList<Object> getParameter(){
		return _parameter;
	}
	
	public void addParameter(Object object_){
		_parameter.add(object_);
	}
	
	public Object[] parameterToArray(){
		return _parameter.toArray();
	}
	
	public void reset(){
		_query = null;
		_parameter.clear();
	}
	
	static private void _fillWHEREStatement(JGDBQuery query_,JGDBParameter parameter_){
		StringBuffer queryStr_ = new StringBuffer(query_.getQuery()+"WHERE 1=1 \n");
		
		ArrayList<JGDBParameterValue> keyList_ = parameter_.keyList;
		int count_ = keyList_.size();
		for(int index_=0;index_<count_;++index_){
			JGDBParameterValue key_ = keyList_.get(index_);
			if(key_.value != null){
				queryStr_.append("AND "+key_.column+" = ? \n");
				query_.addParameter(key_.value);
			}else{
				queryStr_.append("AND "+key_.column+" IS NULL \n");
			}
		}
		
		query_.setQuery(queryStr_.toString());
	}
	
	public void fillQueryForSELECT(JGDBParameter parameter_){
		reset();
		String tableName_ = parameter_.getTargetName();
		if(tableName_ == null){
			return;
		}
		
		StringBuffer queryStr_ = new StringBuffer("SELECT \n");
		
		ArrayList<JGDBParameterValue> values_ = parameter_.values;
		int count_ = values_.size();
		for(int index_=0;index_<count_;++index_){
			JGDBParameterValue value_ = values_.get(index_);
			queryStr_.append(value_.column);
			if(index_ < count_-1){
				queryStr_.append(",");
			}
			queryStr_.append(" \n");
		}
		
		queryStr_.append("FROM "+tableName_+" \n");
		_query = queryStr_.toString();
		_fillWHEREStatement(this, parameter_);
	}
	public void fillQueryForINSERT(JGDBParameter parameter_){
		reset();
		String tableName_ = parameter_.getTargetName();
		if(tableName_ == null){
			return;
		}
		
		StringBuffer queryStr_ = new StringBuffer("INSERT INTO "+tableName_+" (\n");
		
		ArrayList<JGDBParameterValue> values_ = parameter_.values;
		int count_ = values_.size();
		for(int index_=0;index_<count_;++index_){
			JGDBParameterValue value_ = values_.get(index_);
			queryStr_.append(value_.column);
			if(index_ < count_-1){
				queryStr_.append(",");
			}
			queryStr_.append(" \n");
		}
		queryStr_.append(")VALUES( \n");
		
		for(int index_=0;index_<count_;++index_){
			queryStr_.append("?");
			JGDBParameterValue value_ = values_.get(index_);
			addParameter(value_.value);
			if(index_ < count_-1){
				queryStr_.append(",");
			}
			queryStr_.append(" \n");
		}
		
		queryStr_.append(")\n");
		_query = queryStr_.toString();
	}
	public void fillQueryForUPDATE(JGDBParameter parameter_){
		reset();
		String tableName_ = parameter_.getTargetName();
		if(tableName_ == null){
			return;
		}
		
		StringBuffer queryStr_ = new StringBuffer("UPDATE "+tableName_+" SET \n");
		ArrayList<JGDBParameterValue> values_ = parameter_.values;
		int count_ = values_.size();
		for(int index_=0;index_<count_;++index_){
			JGDBParameterValue value_ = values_.get(index_);
			
			queryStr_.append(value_.column+" = ?");
			addParameter(value_.value);
			if(index_ < count_-1){
				queryStr_.append(",");
			}
			queryStr_.append(" \n");
		}
		_query = queryStr_.toString();
		_fillWHEREStatement(this, parameter_);
	}
	public void fillQueryForDELETE(JGDBParameter parameter_){
		reset();
		String tableName_ = parameter_.getTargetName();
		if(tableName_ == null){
			return;
		}
		
		StringBuffer queryStr_ = new StringBuffer("DELETE FROM "+tableName_+" \n");
		_query = queryStr_.toString();
		_fillWHEREStatement(this, parameter_);
	}
	
	public void fillQueryForPROCEDURE(JGDBParameter parameter_){
		reset();
		String targetName_ = parameter_.getTargetName();
		if(targetName_ == null){
			return;
		}
		
		StringBuffer queryStr_ = new StringBuffer("CALL "+targetName_+"(");
		
		ArrayList<JGDBParameterValue> values_ = parameter_.values;
		int count_ = values_.size();
		for(int index_=0;index_<count_;++index_){
			JGDBParameterValue value_ = values_.get(index_);
			queryStr_.append("?");
			addParameter(value_.value);
			if(index_ < count_-1){
				queryStr_.append(", ");
			}
		}
		
		queryStr_.append(")\n");
		_query = queryStr_.toString();
	}
	
	public String toString(){		
		StringBuffer result_ = new StringBuffer();
		result_.append("** query **\n");
		result_.append(_query+"\n");
		result_.append("** parameter **\n");
		result_.append(_parameter.toString()+"\n");
		return result_.toString();
	}
}
