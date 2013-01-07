package common.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import common.main.exception.JGException;

import biz.source_code.base64Coder.Base64Coder;

public class JGEncryptionUtil {
	static private String _cipherAlgorithm_ = "AES";
	static private String _cipherDefaultKey_ = null;
	
	static private Cipher _getCipher(int opmode_, String key_) throws JGException{
		SecretKeySpec keySpec_ = new SecretKeySpec(key_.getBytes(), _cipherAlgorithm_);
		
		try {
			Cipher cipher_ = Cipher.getInstance(_cipherAlgorithm_);
			cipher_.init(opmode_, keySpec_);
			return cipher_;
		}catch(InvalidKeyException ex_){
			throw new JGException(JGEncryptionUtil.class, ex_, "_getCipher(int, String) : invalid key.");
		}catch(NoSuchAlgorithmException ex_){
			throw new JGException(JGEncryptionUtil.class, ex_, "_getCipher(int, String) : invalid algorithm.");
		}catch(NoSuchPaddingException ex_){
			throw new JGException(JGEncryptionUtil.class, ex_, "_getCipher(int, String) : invalid Padding.");
		}
		
	}
	
	static public void setCipherAlgorithm(String cipherAlgorithm_){
		_cipherAlgorithm_ = cipherAlgorithm_;
	}
	static public String cipherAlgorithm(){
		return _cipherAlgorithm_;
	}
	
	static public void setCipherDefaultKey(String cipherDefaultKey_){
		_cipherDefaultKey_ = cipherDefaultKey_;
	}
	static public String cipherDefaultKey(){
		return _cipherDefaultKey_;
	}
	
	static public String encodeString(String string_, String key_) throws JGException{
		Cipher cipher_ = _getCipher(Cipher.ENCRYPT_MODE, key_);
		byte[] encryptedBytes_;
		try{
			encryptedBytes_ = cipher_.doFinal(string_.getBytes());
			return new String(Base64Coder.encode(encryptedBytes_));
		}catch(IllegalBlockSizeException ex_){
			throw new JGException(JGEncryptionUtil.class, ex_, "encodeString(String, String) : invalid BlockSize.");
		}catch(BadPaddingException ex_){
			throw new JGException(JGEncryptionUtil.class, ex_, "encodeString(String, String) : invalid Padding.");
		}
	}
	static public String encodeString(String string_) throws JGException{
		return encodeString(string_, _cipherDefaultKey_);
	}
	static public String decodeString(String encryptedString_, String key_) throws JGException{
		try{
			Cipher cipher_ = _getCipher(Cipher.DECRYPT_MODE, key_);
			return new String(cipher_.doFinal(encryptedString_.getBytes()));
		}catch(IllegalBlockSizeException ex_){
			throw new JGException(JGEncryptionUtil.class, ex_, "decodeString(String, String) : invalid BlockSize.");
		}catch(BadPaddingException ex_){
			throw new JGException(JGEncryptionUtil.class, ex_, "decodeString(String, String) : invalid Padding.");
		}
	}
	static public String decodeString(String string_) throws JGException{
		return decodeString(string_, _cipherDefaultKey_);
	}
}
