package common.db.xml.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Text;

import common.db.vo.JGDBQuery;
import common.db.xml.data.JGDBXMLQuerySet;
import common.main.data.xml.JGXMLDataSet;
import common.main.exception.JGException;

public class JGDBXMLQuery{
	
	static public final String JGDBXML_FORMAT_CONDITION_STATEMENT = "####%d####";
	static public final String JGDBXML_FORMAT_CONDITION_COLUMN = "##%@##";
	
	static public final String JGDBXML_FORMAT_CONDITION_STATEMENT_REGULAREX  = "####.\\p(Digit)####";
	static public final String JGDBXML_FORMAT_CONDITION_COLUMN_REGULAREX  = "##.*##";
	
	private Element _queryElement = null;
	public Element getQueryElement(){
		return _queryElement;
	}
	
	private ArrayList<Element> _conditionList = new ArrayList<Element>();
	public ArrayList<Element> getConditionList(){
		return _conditionList;
	}
	
	public JGDBXMLQuery(Element queryElement_) throws JGException{
		try{
			_queryElement = queryElement_;
			
			if(_queryElement == null){
				throw new NullPointerException("queryElement is null");
			}
			
			//add condition statement
			List<Element> condElementList_ = _queryElement.getChildren();
			int condElementCount_ = condElementList_.size();
			for(int index_=0;index_<condElementCount_;++index_){
				Element condElement_ = condElementList_.get(index_);
				_conditionList.add((Element)condElement_.clone());
				condElement_.setContent(new Text(String.format(JGDBXML_FORMAT_CONDITION_STATEMENT, index_)));
			}
	
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.xml.data.JGDBXMLQuery.0000");
		}
	}
	
	private boolean _getAttributeIsReverse(Attribute attr_){
		boolean isReverse_ = false;
		try{
			isReverse_ = Boolean.valueOf(attr_.getValue()).booleanValue();
		}catch(Exception ex_){
			isReverse_ = false;
		}
		return isReverse_;
	}
	
	private String _getConditionStatementAsISNOTNULL(Element conditionElement_, JGXMLDataSet dataSet_, int rowIndex_){
		Attribute attrColumnName_ = conditionElement_.getAttribute(JGDBXMLQuerySet.JGDBXML_CONDITION_ATTR_COLUMNNAME);
		Attribute attrIsReverse_ = conditionElement_.getAttribute(JGDBXMLQuerySet.JGDBXML_CONDITION_ATTR_ISREVERSE);
		
		String conditionStatement_ = "";
		try{
			Object columnValue_ = dataSet_.getColumnValue(attrColumnName_.getValue(), rowIndex_);
			if((columnValue_ != null) != _getAttributeIsReverse(attrIsReverse_)){
				conditionStatement_ = conditionElement_.getValue();
			}
		}catch(Exception ex_){
			conditionStatement_ = "";
		}
		
		return conditionStatement_;
	}
	
	private String _getConditionStatementAsISEQUALS(Element conditionElement_, JGXMLDataSet dataSet_, int rowIndex_){
		Attribute attrColumnName_ = conditionElement_.getAttribute(JGDBXMLQuerySet.JGDBXML_CONDITION_ATTR_COLUMNNAME);
		Attribute attrColumnValue_ = conditionElement_.getAttribute(JGDBXMLQuerySet.JGDBXML_CONDITION_ATTR_COLUMNVALUE);
		Attribute attrIsReverse_ = conditionElement_.getAttribute(JGDBXMLQuerySet.JGDBXML_CONDITION_ATTR_ISREVERSE);
		
		String conditionStatement_ = "";
		try{
			String columnValue_ = dataSet_.getStringFromColumnValue(attrColumnName_.getValue(), rowIndex_);
			
			if(columnValue_.equals(attrColumnValue_.getValue()) != _getAttributeIsReverse(attrIsReverse_)){
				conditionStatement_ = conditionElement_.getValue();
			}
			
		}catch(Exception ex_){
			ex_.printStackTrace();
			conditionStatement_ = "";
		}
		
		return conditionStatement_;
	}
	
	public JGDBQuery createQuery(JGXMLDataSet dataSet_, int rowIndex_) throws JGException{
		JGDBQuery query_ = new JGDBQuery();
		
		String queryStr_ = _queryElement.getValue().trim();
		
		int count_ = _conditionList.size();
		for(int index_=0;index_<count_;++index_){
			Element conditionElement_ = _conditionList.get(index_);
			
			String conditionName_ = conditionElement_.getName();
			String conditionStatement_ = "";
			
			if(conditionName_.equalsIgnoreCase(JGDBXMLQuerySet.JGDBXML_CONDITION_ISNOTNULL)){
				conditionStatement_ = _getConditionStatementAsISNOTNULL(conditionElement_, dataSet_, rowIndex_);
			}else if(conditionName_.equalsIgnoreCase(JGDBXMLQuerySet.JGDBXML_CONDITION_ISEQUALS)){
				conditionStatement_ = _getConditionStatementAsISEQUALS(conditionElement_, dataSet_, rowIndex_);
			}
			
			queryStr_ = queryStr_.replaceFirst(String.format(JGDBXML_FORMAT_CONDITION_STATEMENT, index_), conditionStatement_.trim());
		}
		
		try{
			Pattern conditionColumnPattern_ = Pattern.compile(JGDBXML_FORMAT_CONDITION_COLUMN_REGULAREX);
			Matcher conditionColumnMatcher_ = conditionColumnPattern_.matcher(queryStr_);
			
			while(conditionColumnMatcher_.find()){
				String matchedStr_ = conditionColumnMatcher_.group(); 
				String columnName_ = matchedStr_.replaceAll("#", "");
				queryStr_ = queryStr_.replaceFirst(matchedStr_, "?");
				query_.addParameter(dataSet_.getColumnValue(columnName_, rowIndex_));
			}
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.xml.data.JGDBXMLQuery.0001", queryStr_);
		}
		
		query_.setQuery(queryStr_);
		
		return query_;
	}
	
	public JGDBQuery createQuery(JGXMLDataSet dataSet_) throws JGException{
		return createQuery(dataSet_, 0);
	}
	
	public JGDBQuery createQuery() throws JGException{
		return createQuery(null);
	}
}
