package common.db.xml;

import java.io.File;
import java.util.HashMap;

import common.db.xml.data.JGDBXMLQuery;
import common.db.xml.data.JGDBXMLQuerySet;
import common.main.JGMainSystem;
import common.main.exception.JGException;

public class JGDBXMLQueryManager {
	static private JGDBXMLQueryManager _sharedQueryManager_ = null;
	
	private String _targetPath = null;
	public String getTargetPath(){
		return _targetPath;
	}
	
	private HashMap<String, JGDBXMLQuerySet> _querySetList = new HashMap<String, JGDBXMLQuerySet>();
	public HashMap<String, JGDBXMLQuerySet> getQueryList(){
		return _querySetList;
	}
	
	static public JGDBXMLQueryManager sharedManager() throws JGException{
		if(_sharedQueryManager_ == null){
			synchronized(JGDBXMLQueryManager.class){
				_sharedQueryManager_ = new JGDBXMLQueryManager(JGMainSystem.sharedSystem().getDBXMLQueryFilePath());
			}
		}
		
		return _sharedQueryManager_;
	}
		
	private void _searchXMLDirectory(String targetPath_) throws JGException{
		try{
			File targetDirectory_ = new File(targetPath_);
			File[] fileList_ = targetDirectory_.listFiles();
			int fileCount_ = fileList_.length;
			for(int index_=0;index_<fileCount_;++index_){
				File targetFile_ = fileList_[index_];
				String childPath_ = targetFile_.getCanonicalPath();
				if(targetFile_.isFile() && targetFile_.getName().endsWith(".xml")){
					addQuerySet(childPath_.substring(_targetPath.length(),childPath_.lastIndexOf(".xml")), new JGDBXMLQuerySet(childPath_));
				}else if(targetFile_.isDirectory()){
					_searchXMLDirectory(childPath_);
				}
			}
		}catch(Exception ex_){
			throw new JGException(getClass(), ex_, "common.db.xml.JGDBXMLQueryManager.0000");
		}
	}
	
	public JGDBXMLQueryManager(String targetPath_) throws JGException{
		_targetPath = targetPath_;
		reload();
	}
	
	public void reload() throws JGException{
		_querySetList.clear();
		_searchXMLDirectory(_targetPath);
	}
	
	public void addQuerySet(String key_, JGDBXMLQuerySet query_){
		_querySetList.put(key_, query_);
	}
	
	public JGDBXMLQuerySet getQuerySet(String path_){
		return _querySetList.get(path_);
	}
	
	public JGDBXMLQuery getQuery(String path_, String keyName_){
		return getQuerySet(path_).getQuery(keyName_);
	}
}
