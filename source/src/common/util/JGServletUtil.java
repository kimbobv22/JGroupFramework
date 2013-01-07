package common.util;

public class JGServletUtil{
	public static String convertKorString(String str_){
		try{
			return new String(str_.getBytes("8859_1"), "KSC5601");
		}catch(Exception e){
			return str_;
		}
	}
}
