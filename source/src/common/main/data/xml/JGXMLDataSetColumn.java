package common.main.data.xml;

public class JGXMLDataSetColumn{
	private String _name;
	public void setName(String name_){
		_name = name_.toUpperCase();
	}
	public String getName(){
		return _name;
	}
	
	public int type;
	
	private boolean isKey;
	public void setKey(boolean key_){
		isKey = key_;
	}
	public boolean isKey(){
		return isKey;
	}
	
	public JGXMLDataSetColumn(String name_, int type_){
		setName(name_);
		type = type_;
		isKey = false;
	}
}
