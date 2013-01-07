package common.main.data.xml;

import java.util.HashMap;

public class JGXMLDataSetRow{
	private JGXMLDataSet _dataSet = null;
	public JGXMLDataSet getDataSet(){
		return _dataSet;
	}
	
	public HashMap<String, Object> columns = new HashMap<String, Object>();
	private HashMap<String, Object> _orgColumns = new HashMap<String, Object>();
	
	private HashMap<String, Boolean> _columnStatus = new HashMap<String, Boolean>();
	
	public int rowStatus = JGXMLDataSet.JGXML_ROWSTATUS_NORMAL;
	
	private JGXMLDataSetRow(){};
	public JGXMLDataSetRow(JGXMLDataSet dataSet_){
		this();
		_dataSet = dataSet_;
	}
	
	public void setColumn(String columnName_, Object value_, boolean isModify_){
		String cColumnName_ = columnName_.toUpperCase();
		columns.put(cColumnName_, value_);
		_columnStatus.put(cColumnName_, Boolean.valueOf(isModify_));
	}
	public void setColumn(String columnName_, Object value_){
		Object orgValue_ = _orgColumns.get(columnName_.toUpperCase());
		boolean isModify_ = isColumnModified(columnName_);
		boolean isNullOrgValue_ = orgValue_ == null;
		boolean isNullValue_ = value_ == null;
		
		if(isModify_){
			if((isNullOrgValue_ && isNullValue_)
				|| ((!(isNullOrgValue_ || isNullValue_)) && (String.valueOf(orgValue_).equals(String.valueOf(value_))))){
				isModify_ = false;
			}
		}else{
			if((!(isNullOrgValue_ && isNullValue_))
				|| String.valueOf(orgValue_).equals(String.valueOf(value_))){
				isModify_ = true;
			}
		}
		setColumn(columnName_, value_, isModify_);
	}	
	public void removeColumn(String columnName_){
		columns.remove(columnName_.toUpperCase());
	}
	public Object getColumnValue(String columnName_){
		return columns.get(columnName_.toUpperCase());
	}
	
	public boolean isColumnModified(String columnName_){
		Boolean result_ = _columnStatus.get(columnName_.toUpperCase());
		if(result_ == null){
			return false;
		}
		return result_.booleanValue();
	}
	public void setColumnModification(String columnName_, Boolean bool_){
		_columnStatus.put(columnName_.toUpperCase(), new Boolean(bool_));
	}
	
	public void apply(){
		_orgColumns.clear();
		_orgColumns.putAll(columns);
		rowStatus = JGXMLDataSet.JGXML_ROWSTATUS_NORMAL;
		_columnStatus.clear();
	}
}