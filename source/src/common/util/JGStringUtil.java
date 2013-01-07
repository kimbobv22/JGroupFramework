package common.util;

import java.util.List;

public class JGStringUtil {
	
	static String getStringWithStringArray(String[] stringArray_, String connectCharacter_){
		StringBuffer result_ = new StringBuffer();
		int count_ = stringArray_.length;
		for(int index_=0;index_<count_;++index_){
			result_.append(stringArray_[index_]);
			if(index_ < count_-1){
				result_.append(connectCharacter_);
			}
		}
		
		return result_.toString();
	}
	static String getStringWithStringArray(List<String> stringArray_, String connectCharacter_){
		return getStringWithStringArray((String[])stringArray_.toArray(), connectCharacter_);
	}

}
