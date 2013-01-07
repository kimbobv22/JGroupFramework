package common.db.xml.data;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.jdom.*;
import org.jdom.input.SAXBuilder;

import common.main.exception.JGException;

public class JGDBXMLQuerySet {
	
	static public final String JGDBXML_ELEMENT_QUERYSET = "queryset";
	static public final String JGDBXML_ELEMENT_QUERY = "query";
	static public final String JGDBXML_ELEMENT_QUERY_ATTR_KEYNAME = "keyName";
	
	static public final String JGDBXML_CONDITION_ISNOTNULL = "isnotnull";
	static public final String JGDBXML_CONDITION_ISEQUALS = "isequals";
	
	static public final String JGDBXML_CONDITION_ATTR_COLUMNNAME = "columnName";
	static public final String JGDBXML_CONDITION_ATTR_COLUMNVALUE = "columnValue";
	static public final String JGDBXML_CONDITION_ATTR_ISREVERSE = "isReverse";
	
	private String _targetPath = null;
	public String getTargetPath(){
		return _targetPath;
	}
	
	private HashMap<String, JGDBXMLQuery> _queryList = new HashMap<String, JGDBXMLQuery>();
	public HashMap<String, JGDBXMLQuery> getQueryList(){
		return _queryList;
	}
	
	private JGDBXMLQuerySet(){}
	public JGDBXMLQuerySet(String targetPath_) throws JGException{
		this();

		_targetPath = targetPath_;
		
		try{
			Document rootDocument_ = new SAXBuilder().build(new File(targetPath_));
			Element querySetElement_ = rootDocument_.getRootElement();
			List<Element> queryList_ = querySetElement_.getChildren(JGDBXML_ELEMENT_QUERY);
			int queryCount_ = queryList_.size();
			for(int index_=0;index_<queryCount_;++index_){
				Element queryElement_ = queryList_.get(index_);
				String keyName_ = queryElement_.getAttributeValue(JGDBXML_ELEMENT_QUERY_ATTR_KEYNAME);
				addQuery(keyName_, new JGDBXMLQuery(queryElement_));
			}
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.xml.data.JGDBXMLQuerySet.0000");
		}
	}

	public void addQuery(String key_, JGDBXMLQuery query_){
		_queryList.put(key_, query_);
	}
	public void removeQuery(String key_){
		_queryList.remove(key_);
	}
	public JGDBXMLQuery getQuery(String key_){
		return _queryList.get(key_);
	}
}
