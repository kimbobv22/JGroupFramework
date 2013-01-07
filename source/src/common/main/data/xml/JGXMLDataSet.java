package common.main.data.xml;

import java.io.StringReader;
import java.sql.*;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import common.main.JGMainSystem;
import common.main.exception.JGException;

public class JGXMLDataSet {
	static public final String JGXML_ELEMENT_ROOT = "root";
	static public final String JGXML_ELEMENT_ROW = "row";
	static public final String JGXML_ELEMENT_COLUMN = "col";
	
	static public final String JGXML_ELEMENT_COLUMNINFO = "columninfo";
	static public final String JGXML_ELEMENT_COLUMNINFO_TYPE = "type";
	static public final String JGXML_ELEMENT_COLUMNINFO_ISKEY = "isKey";
	
	static public final String JGXML_ELEMENT_ROWDATA = "rowdata";
	static public final String JGXML_ELEMENT_ROWDATA_DELETED = "deletedRowdata";
	static public final String JGXML_ELEMENT_ROWSTATUS = "status";
	
	static public final String JGXML_ELEMENT_COLUMNMODIFY = "modify";
	
	static public final int JGXML_COLUMNTYPE_STRING = Types.VARCHAR;
	static public final int JGXML_COLUMNTYPE_INTEGER = Types.INTEGER;
	static public final int JGXML_COLUMNTYPE_LONG = Types.BIGINT;
	static public final int JGXML_COLUMNTYPE_FLOAT = Types.FLOAT;
	static public final int JGXML_COLUMNTYPE_BOOLEAN = Types.BOOLEAN;
	static public final int JGXML_COLUMNTYPE_DATE = Types.DATE;
	static public final int JGXML_COLUMNTYPE_BINARY = Types.BINARY;
	
	static public final int JGXML_ROWSTATUS_NORMAL = 0;
	static public final int JGXML_ROWSTATUS_INSERT = 1;
	static public final int JGXML_ROWSTATUS_UPDATE = 3;
	
	public ArrayList<JGXMLDataSetColumn> columnInfo = new ArrayList<JGXMLDataSetColumn>();
	public ArrayList<JGXMLDataSetRow> rowData = new ArrayList<JGXMLDataSetRow>();
	
	private ArrayList<JGXMLDataSetRow> _deletedRowData = new ArrayList<JGXMLDataSetRow>();
	public ArrayList<JGXMLDataSetRow> getDeletedRowData(){
		return _deletedRowData;
	}
	
	public int columnCount(){
		return columnInfo.size();
	}
	public int rowCount(){
		return rowData.size();
	}
	public int deletedRowCount(){
		return _deletedRowData.size();
	}
	
	public JGXMLDataSetColumn insertColumn(String columnName_, int columnIndex_, int type_){
		JGXMLDataSetColumn columnItem_ = new JGXMLDataSetColumn(columnName_, type_);
		columnInfo.add(columnIndex_, columnItem_);
		
		int rowCount_ = rowData.size();
		for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
			JGXMLDataSetRow rowItem_ = rowData.get(rowIndex_);
			rowItem_.setColumn(columnName_, null);
		}
		
		rowCount_ = _deletedRowData.size();
		for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
			JGXMLDataSetRow rowItem_ = _deletedRowData.get(rowIndex_);
			rowItem_.setColumn(columnName_, null);
		}
		
		return columnItem_;
	}
	public JGXMLDataSetColumn insertColumn(String columnName_, int columnIndex_){
		return insertColumn(columnName_, columnIndex_, JGXML_COLUMNTYPE_STRING);
	}
	public JGXMLDataSetColumn addColumn(String columnName_, int type_){
		return insertColumn(columnName_, columnInfo.size(), type_);
	}
	public JGXMLDataSetColumn addColumn(String columnName_){
		return insertColumn(columnName_, columnInfo.size());
	}
	
	public void removeColumn(int columnIndex_){
		JGXMLDataSetColumn columnItem_ = getColumn(columnIndex_);
		int rowCount_ = rowData.size();
		for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
			rowData.get(rowIndex_).removeColumn(columnItem_.getName());
		}
		
		rowCount_ = _deletedRowData.size();
		for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
			_deletedRowData.get(rowIndex_).removeColumn(columnItem_.getName());
		}
		
		columnInfo.remove(columnIndex_);
	}
	public void removeColumn(JGXMLDataSetColumn column_){
		removeColumn(indexOfColumn(column_));
	}
	public void removeColumn(String columnName_){
		removeColumn(indexOfColumn(columnName_));
	}
	
	public JGXMLDataSetColumn getColumn(int columnIndex_){
		return columnInfo.get(columnIndex_);
	}
	public JGXMLDataSetColumn getColumn(String columnName_){
		return getColumn(indexOfColumn(columnName_));
	}
	public int indexOfColumn(JGXMLDataSetColumn column_){
		return columnInfo.indexOf(column_);
	}
	public int indexOfColumn(String columnName_){
		int columnCount_ = columnInfo.size();
		for(int index_=0;index_<columnCount_;++index_){
			if(columnInfo.get(index_).getName().equalsIgnoreCase(columnName_)){
				return index_;
			}
		}
		
		return -1;
	}
	
	public void setKeyColumn(int columnIndex_, boolean isKey_){
		JGXMLDataSetColumn columnItem_ = getColumn(columnIndex_);
		columnItem_.setKey(isKey_);
	}
	public void setKeyColumn(String columnName_, boolean isKey_){
		setKeyColumn(indexOfColumn(columnName_), isKey_);
	}
	public ArrayList<JGXMLDataSetColumn> getKeyColumnList(){
		ArrayList<JGXMLDataSetColumn> columnList_ = new ArrayList<JGXMLDataSetColumn>();
		int columnCount_ = columnInfo.size();
		for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
			JGXMLDataSetColumn columnItem_ = getColumn(columnIndex_);
			if(columnItem_.isKey()){
				columnList_.add(columnItem_);
			}
		}
		
		return columnList_;
	}
	
	public void insertRow(int rowIndex_){
		JGXMLDataSetRow rowItem_ = new JGXMLDataSetRow(this);
		rowItem_.rowStatus = JGXML_ROWSTATUS_INSERT;
		int columnCount_ = columnInfo.size();
		for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
			JGXMLDataSetColumn columnItem_ = columnInfo.get(columnIndex_);
			rowItem_.setColumn(columnItem_.getName(), null);
		}
		
		rowData.add(rowIndex_, rowItem_);
	}
	public int addRow(){
		int rowIndex_ = rowData.size();
		insertRow(rowIndex_);
		return rowIndex_;
	}
	public void removeRow(int rowIndex_){
		JGXMLDataSetRow rowItem_ = getRow(rowIndex_);
		
		switch(rowItem_.rowStatus){
			case JGXML_ROWSTATUS_NORMAL:
			case JGXML_ROWSTATUS_UPDATE:{
				_deletedRowData.add(rowItem_);
				break;
			}
			case JGXML_ROWSTATUS_INSERT:
			default:{
				break;
			}
		}
		
		rowData.remove(rowIndex_);
	}
	public void removeRow(JGXMLDataSetRow rowItem_){
		removeRow(indexOfRow(rowItem_));
	}
	public JGXMLDataSetRow getRow(int rowIndex_){
		return rowData.get(rowIndex_);
	}
	public int indexOfRow(JGXMLDataSetRow rowItem_){
		return rowData.indexOf(rowItem_);
	}
	
	public void setColumnValue(String columnName_, int rowIndex_, Object value_){
		int columnIndex_ = indexOfColumn(columnName_);
		if(columnIndex_ < 0){
			throw new NullPointerException("not exists column");
		}
		JGXMLDataSetRow rowItem_ = getRow(rowIndex_);
		rowItem_.setColumn(columnName_, value_);
	}
	public void setColumnValue(int columnIndex_, int rowIndex_, Object value_){
		JGXMLDataSetColumn columnItem_ = getColumn(columnIndex_);
		setColumnValue(columnItem_.getName(), rowIndex_, value_);
	}
	public Object getColumnValue(String columnName_, int rowIndex_){
		return getRow(rowIndex_).getColumnValue(columnName_);
	}
	public Object getColumnValue(int columnIndex_, int rowIndex_){
		return getColumnValue(getColumn(columnIndex_).getName(), rowIndex_);
	}
	
	public String getStringFromColumnValue(int columnIndex_, int rowIndex_){
		return convertObjectToString(getColumnValue(columnIndex_, rowIndex_), getColumn(columnIndex_).type);
	}
	public String getStringFromColumnValue(String columnName_, int rowIndex_){
		return getStringFromColumnValue(indexOfColumn(columnName_), rowIndex_);
	}
	
	public JGXMLDataSetRow getDeletedRow(int rowIndex_){
		return _deletedRowData.get(rowIndex_);
	}
	public int indexOfDeletedRow(JGXMLDataSetRow rowItem_){
		return _deletedRowData.indexOf(rowItem_);
	}
	public Object getDeletedColumnValue(String columnName_, int rowIndex_){
		return getDeletedRow(rowIndex_).getColumnValue(columnName_);
	}
	
	public void apply(){
		int rowCount_ = rowData.size();
		for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
			JGXMLDataSetRow rowItem_ = getRow(rowIndex_);
			rowItem_.apply();
		}
		_deletedRowData.clear();
	}
	public void setRowStatus(int rowIndex_, int rowStatus_){
		getRow(rowIndex_).rowStatus = rowStatus_;
	}
	
	static public Object convertStringToObject(String value_, int type_) throws JGException{
		switch(type_){
		case JGXML_COLUMNTYPE_INTEGER:
			return Integer.valueOf(value_);
		case JGXML_COLUMNTYPE_LONG:
			return Long.valueOf(value_);
		case JGXML_COLUMNTYPE_FLOAT:
			return Float.valueOf(value_);
		case JGXML_COLUMNTYPE_BOOLEAN:
			return Boolean.valueOf(value_);
		case JGXML_COLUMNTYPE_DATE:
			try{
				SimpleDateFormat dateFormat_ = JGMainSystem.sharedSystem().getDateFormat_DEFAULT();
				return dateFormat_.parse(value_);
			}catch(Exception ex_){
				throw new JGException(JGXMLDataSet.class, ex_, "common.main.data.xml.JGXMLDataSet.0000");
			}
		case JGXML_COLUMNTYPE_BINARY:
			try{
				return value_.getBytes("UTF-8");
			}catch(Exception ex_){
				throw new JGException(JGXMLDataSet.class, ex_, "common.main.data.xml.JGXMLDataSet.0001");
			}
			
		case JGXML_COLUMNTYPE_STRING:
		default :
			return value_;
		}
	}
	static public String convertObjectToString(Object value_, int type_){
		switch(type_){ // TODO check handler for data type 
		case JGXML_COLUMNTYPE_DATE:
			SimpleDateFormat dateFormat_ = JGMainSystem.sharedSystem().getDateFormat_DEFAULT();
			return dateFormat_.format((Date)value_);
		default :
			return String.valueOf(value_);
		}
	}
	
	public String toXML(){
		try{
			
			Document document_ = new Document();
			
			//make root node
			Element rootNode_ = new Element(JGXML_ELEMENT_ROOT);
			
			//make column info node
			Element columnInfoNode_ = new Element(JGXML_ELEMENT_COLUMNINFO);
			int columnCount_ = columnInfo.size();
			for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
				JGXMLDataSetColumn columnItem_ = columnInfo.get(columnIndex_);
				
				Element columnNode_ = new Element(JGXML_ELEMENT_COLUMN);
				columnNode_.setAttribute(JGXML_ELEMENT_COLUMNINFO_TYPE, String.valueOf(columnItem_.type));
				if(columnItem_.isKey()){
					columnNode_.setAttribute(JGXML_ELEMENT_COLUMNINFO_ISKEY, String.valueOf(true));
				}
				
				columnNode_.setText(columnItem_.getName());
				columnInfoNode_.addContent(columnNode_);
			}
			
			//make row data node
			Element rowDataNode_ = new Element(JGXML_ELEMENT_ROWDATA);
			int rowCount_ = rowData.size();
			for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
				JGXMLDataSetRow rowItem_ = rowData.get(rowIndex_);
				Element rowNode_ = new Element(JGXML_ELEMENT_ROW);
				Attribute rowStatusAttr_ = new Attribute(JGXML_ELEMENT_ROWSTATUS, String.valueOf(rowItem_.rowStatus));

				rowNode_.setAttribute(rowStatusAttr_);
				
				for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
					JGXMLDataSetColumn columnItem_ = columnInfo.get(columnIndex_);
					Object columnValue_ = getColumnValue(columnItem_.getName(), rowIndex_);
					Element columnNode_ = new Element(columnItem_.getName());
					
					columnNode_.setText(convertObjectToString(columnValue_, columnItem_.type));
					
					if(rowItem_.isColumnModified(columnItem_.getName())){
						Attribute columnStatusAttr_ = new Attribute(JGXML_ELEMENT_COLUMNMODIFY, String.valueOf(true));
						columnNode_.setAttribute(columnStatusAttr_);
					}
					
					rowNode_.addContent(columnNode_);
				}
				
				rowDataNode_.addContent(rowNode_ );
			}
			
			//make deleted row data node
			Element deletedRowDataNode_ = new Element(JGXML_ELEMENT_ROWDATA_DELETED);
			rowCount_ = _deletedRowData.size();
			for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
				Element rowNode_ = new Element(JGXML_ELEMENT_ROW);
				
				for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
					JGXMLDataSetColumn columnItem_ = columnInfo.get(columnIndex_);
					Object columnValue_ = getDeletedColumnValue(columnItem_.getName(), rowIndex_);
					Element columnNode_ = new Element(columnItem_.getName());
					columnNode_.setText(convertObjectToString(columnValue_, columnItem_.type));
					rowNode_.addContent(columnNode_);
				}
				
				deletedRowDataNode_.addContent(rowNode_);
			}
			
			rootNode_.addContent(columnInfoNode_);
			rootNode_.addContent(rowDataNode_);
			rootNode_.addContent(deletedRowDataNode_);
			
			document_.addContent(rootNode_);
			
			XMLOutputter outputter_ = new XMLOutputter();
			outputter_.setFormat(Format.getPrettyFormat());

			return outputter_.outputString(document_);
		}catch(Exception ex_){
			return "error : "+ex_.getClass().toString();
		}
	}
	
	static public JGXMLDataSet makeDataSet(String xmlString_) throws JGException{
		try{
			JGXMLDataSet dataSet_ = new JGXMLDataSet();
			
			SAXBuilder saxBuilder_ = new SAXBuilder();
			StringReader stringReader_ = new StringReader(xmlString_);
			Document rootDocument_ = saxBuilder_.build(stringReader_);
			
			Element rootNode_ = rootDocument_.getRootElement();
			Element columnInfoNode_ = rootNode_.getChild(JGXML_ELEMENT_COLUMNINFO);
			Element rowData_ = rootNode_.getChild(JGXML_ELEMENT_ROWDATA);
			Element deletedRowData_ = rootNode_.getChild(JGXML_ELEMENT_ROWDATA_DELETED);
			
			//add column info
			List<Element> columnInfoList_ = columnInfoNode_.getChildren();
			int columnCount_ = columnInfoList_.size();
			for(int columnIndex_ = 0; columnIndex_ < columnCount_;++columnIndex_){
				Element columnNode_ = columnInfoList_.get(columnIndex_);
				JGXMLDataSetColumn columnItem_ = dataSet_.addColumn(columnNode_.getTextTrim(), Integer.valueOf(columnNode_.getAttributeValue(JGXML_ELEMENT_COLUMNINFO_TYPE)).intValue());
				String isKey_ = columnNode_.getAttributeValue(JGXML_ELEMENT_COLUMNINFO_ISKEY);
				if(isKey_ != null){
					columnItem_.setKey(Boolean.valueOf(isKey_).booleanValue());
				}
			}
			
			//add row data
			List<Element> rowDataList_ = rowData_.getChildren();
			int rowCount_ = rowDataList_.size();
			for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
				JGXMLDataSetRow rowItem_ = new JGXMLDataSetRow(dataSet_);
				Element rowNode_ = rowDataList_.get(rowIndex_);
				
				List<Element> rColumnList_ = rowNode_.getChildren();
				int rColumnCount_ = rColumnList_.size();
				for(int columnIndex_=0;columnIndex_<rColumnCount_;++columnIndex_){
					JGXMLDataSetColumn columnItem_ = dataSet_.getColumn(columnIndex_);
					Element rColumnNode_ = rColumnList_.get(columnIndex_);
					String isModifyo_ = rColumnNode_.getAttributeValue(JGXML_ELEMENT_COLUMNMODIFY);
					boolean isModify_ = false;
					if(isModifyo_ != null){
						isModify_ = Boolean.valueOf(isModifyo_).booleanValue();
					}
				
					rowItem_.setColumn(rColumnNode_.getName(), convertStringToObject(rColumnNode_.getText(), columnItem_.type), isModify_);
				}
				
				dataSet_.rowData.add(rowItem_);
			}
			
			//add deleted row data
			ArrayList<JGXMLDataSetRow> deleteRowData_ = dataSet_.getDeletedRowData();
			List<Element> deltedRowDataList_ = deletedRowData_.getChildren();
			rowCount_ = deltedRowDataList_.size();
			for(int rowIndex_=0;rowIndex_<rowCount_;++rowIndex_){
				JGXMLDataSetRow rowItem_ = new JGXMLDataSetRow(dataSet_);
				Element rowNode_ = deltedRowDataList_.get(rowIndex_);
				
				List<Element> rColumnList_ = rowNode_.getChildren();
				int rColumnCount_ = rColumnList_.size();
				for(int columnIndex_=0;columnIndex_<rColumnCount_;++columnIndex_){
					JGXMLDataSetColumn columnItem_ = dataSet_.getColumn(columnIndex_);
					Element rColumnNode_ = rColumnList_.get(columnIndex_);
					String isModifyo_ = rColumnNode_.getAttributeValue(JGXML_ELEMENT_COLUMNMODIFY);
					boolean isModify_ = false;
					if(isModifyo_ != null){
						isModify_ = Boolean.valueOf(isModifyo_).booleanValue();
					}
					
					rowItem_.setColumn(rColumnNode_.getName(), convertStringToObject(rColumnNode_.getText(), columnItem_.type), isModify_);
				}
				
				deleteRowData_.add(rowItem_);
			}
			
			return dataSet_;
		}catch(Exception ex_){
			throw new JGException(JGXMLDataSet.class, ex_, "common.main.data.xml.JGXMLDataSet.0004");
		}
	}
	
	static public JGXMLDataSet makeDataSet(ResultSet resultSet_) throws JGException{
		JGXMLDataSet dataSet_ = new JGXMLDataSet();
		ResultSetMetaData resultSetMetaData_ = null;
		int columnCount_ = 0;
		try{
			resultSetMetaData_ = resultSet_.getMetaData();
			columnCount_ = resultSetMetaData_.getColumnCount();
			for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
				int rColumnIndex_ = columnIndex_+1;
				int columnType_ = resultSetMetaData_.getColumnType(rColumnIndex_);
				switch(columnType_){
					case Types.INTEGER:
						columnType_ = JGXMLDataSet.JGXML_COLUMNTYPE_INTEGER;
						break;
					case Types.BIGINT:
						columnType_ = JGXMLDataSet.JGXML_COLUMNTYPE_LONG;
						break;
					case Types.NUMERIC:
					case Types.FLOAT:
					case Types.DOUBLE:
					case Types.DECIMAL:
						columnType_ = JGXMLDataSet.JGXML_COLUMNTYPE_FLOAT;
						break;
					case Types.BOOLEAN:
						columnType_ = JGXMLDataSet.JGXML_COLUMNTYPE_BOOLEAN;
						break;
					case Types.TIME:
					case Types.TIMESTAMP:
					case Types.DATE:
						columnType_ = JGXMLDataSet.JGXML_COLUMNTYPE_DATE;
						break;
					case Types.LONGNVARCHAR:
					case Types.LONGVARBINARY:
					case Types.LONGVARCHAR:
					case Types.CLOB:
					case Types.BLOB:
					case Types.BIT:
					case Types.BINARY:
						columnType_ = JGXMLDataSet.JGXML_COLUMNTYPE_BINARY;
						break;
					case Types.CHAR:
					case Types.VARCHAR:
					default :
						columnType_ = JGXMLDataSet.JGXML_COLUMNTYPE_STRING;
						break;
				}
				
				dataSet_.addColumn(resultSetMetaData_.getColumnName(rColumnIndex_), columnType_);
			}
		}catch(Exception ex_){
			throw new JGException(JGXMLDataSet.class, ex_, "common.main.data.xml.JGXMLDataSet.0002");
		}
		
		try{
			while(resultSet_.next()){
				int rowIndex_ = dataSet_.addRow();
				
				for(int columnIndex_=0;columnIndex_<columnCount_;++columnIndex_){
					int rColumnIndex_ = columnIndex_+1;
					int dbColumnType_ = resultSetMetaData_.getColumnType(rColumnIndex_);
					
					switch(dbColumnType_){
						case Types.NUMERIC:
						case Types.INTEGER:
						case Types.BIGINT:
						case Types.FLOAT:
						case Types.DOUBLE:
						case Types.DECIMAL:
						case Types.CHAR:
						case Types.VARCHAR:
						case Types.BOOLEAN:
							dataSet_.setColumnValue(columnIndex_, rowIndex_, resultSet_.getObject(rColumnIndex_));
							break;
						case Types.DATE:
						case Types.TIMESTAMP:
						case Types.TIME:
							dataSet_.setColumnValue(columnIndex_, rowIndex_, resultSet_.getDate(rColumnIndex_));
							break;
						case Types.LONGNVARCHAR:
						case Types.LONGVARBINARY:
						case Types.LONGVARCHAR:
						case Types.CLOB:
						case Types.BLOB:
						case Types.BINARY:
						case Types.BIT:
							dataSet_.setColumnValue(columnIndex_, rowIndex_, new String(resultSet_.getBytes(rColumnIndex_),"UTF-8"));
							break;
						default : break;
					}
				}
			}
		}catch(Exception ex_){
			throw new JGException(JGXMLDataSet.class, ex_, "common.main.data.xml.JGXMLDataSet.0003");
		}
		
		dataSet_.apply();
		return dataSet_;
	}
	
}
