package com.chaoxing.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.springframework.web.util.HtmlUtils;

/**
 * @author <a href="mailto:hb562100@163.com">HeBo</a>
 * @version 2009-9-18
 */
public class StringUtil {
	public static String iso2gb(String s) {
		if (s == null)
			return "";
		try {
			return new String(s.getBytes("ISO-8859-1"), "GBK").trim();
		} catch (Exception e) {
			return s;
		}
	}
	public static String gbk2gb2312(String s){
		if (s == null)
			return "";
		try {
			return new String(s.getBytes("GBK"), "GB2312").trim();
		} catch (Exception e) {
			return s;
		}
	}
	
	public static String gb2gb(String s) {
		if (s == null)
			return "";
		try {
			return new String(s.getBytes("GBK"), "GBK").trim();
		} catch (Exception e) {
			return s;
		}
	}

	public static String gb2iso(String s) {
		if (s == null)
			return "";
		try {
			return new String(s.getBytes("GBK"), "ISO-8859-1").trim();
		} catch (Exception e) {
			return s;
		}
	}

	public static String iso2utf8(String s) {
		if (s == null)
			return "";
		try {
			return new String(s.getBytes("ISO-8859-1"), "UTF-8").trim();
		} catch (Exception e) {
			return s;
		}
	}

	public static String utf82iso(String s) {
		if (s == null)
			return "";
		try {
			return new String(s.getBytes("UTF-8"), "ISO-8859-1").trim();
		} catch (Exception e) {
			return s;
		}
	}

	public static String notNull(String s) {
		if (s == null)
			return "";
		else
			return s.trim();
	}

	/**
	 * 判断一个字符串是否为空
	 * 
	 * @author 贺博
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || s.trim().equals("");
	}
	
	/**
	 * 判断一组字符串是否为空
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String ...s) {
		for(String str:s){
			if(isEmpty(str)){
				return true;
			}
		}
		return false;
	}
	

	public static String[] split(String sourse, String spliter) {
		sourse = notNull(sourse);
		int index = 0, nextIndex = 0, spliterLen = spliter.length();
		ArrayList<String> list = new ArrayList<String>();
		while ((nextIndex = sourse.indexOf(spliter, index)) != -1) {
			list.add(sourse.substring(index, nextIndex));
			index = nextIndex + spliterLen;
		}
		if (list.size() > 0)
			list.add(sourse.substring(sourse.lastIndexOf(spliter) + spliter.length(), sourse.length()));
		String splits[] = new String[list.size()];
		list.toArray(splits);

		return splits;
	}

	/**
	 * String join
	 * 
	 * @author 贺博
	 * @date 2008-4-9
	 * 
	 * @param s
	 * @param delimiter 连接符
	 * @return
	 */
	public static String join(Collection s, String delimiter) {
		StringBuilder sb = new StringBuilder();
		Iterator iter = s.iterator();
		if (iter.hasNext()) {
			sb.append(iter.next());
			while (iter.hasNext()) {
				sb.append(delimiter);
				sb.append(iter.next());
			}
		}
		return sb.toString();
	}

	/**
	 * 使用反射，效率比较差
	 * 
	 * @author <a href="mailto:hb562100@163.com">贺博</a>
	 * @date 2008-5-28
	 * 
	 * @param array
	 * @param delimiter
	 * @return
	 */
	public static String join(Object array, String delimiter) {
		if (array.getClass().isArray()) {
			StringBuilder sb = new StringBuilder();
			int len = Array.getLength(array);

			if (len > 0) {
				sb.append(Array.get(array, 0));
			}

			for (int i = 1; i < len; i++) {
				sb.append(delimiter);
				sb.append(Array.get(array, i));
			}

			return sb.toString();
		} else {
			throw new java.lang.IllegalArgumentException(array + " is not a array.");
		}
	}

	public static String join(int[] arr, String delimiter) {
		StringBuilder sb = new StringBuilder();
		int len = arr.length;

		if (len > 0) {
			sb.append(arr[0]);
		}

		for (int i = 1; i < len; i++) {
			sb.append(delimiter);
			sb.append(arr[i]);
		}

		return sb.toString();
	}

	/**
	 * String join
	 * 
	 * @author 贺博
	 * @date 2008-4-10
	 * 
	 * @param sb
	 * @param delimiter 连接符
	 * @return
	 */
	public static String join(String[] arr, String delimiter) {
		StringBuilder sb = new StringBuilder();
		int len = arr.length;

		if (len > 0) {
			sb.append(arr[0]);
		}

		for (int i = 1; i < len; i++) {
			sb.append(delimiter);
			sb.append(arr[i]);
		}

		return sb.toString();
	}
	
	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}
	
	/**
	 * 将str转成url地址
	 * @param str
	 * @return
	 */
	public static String utf8Url(String str){
		if(isEmpty(str)){
			return "";
		}		
		try {
			return URLEncoder.encode(str,"UTF-8");
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 将str转成gb2312地址
	 * @param str
	 * @return
	 */
	public static String gbUrl(String str){
		try {
			return URLEncoder.encode(str,"GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	

	/**
	 * 格式化字符串
	 * 
	 * <code><pre>
	 * format("ab{1}d{2}_{1}" , "c","e") // out put : abcde_c
	 * </pre></code>
	 * 
	 * 使用{ldelim}, {rdelim} 表示"{","}"
	 * 
	 * @param str 使用{1},{2},...{n}占位符的字符串
	 * @param args 填充参数数组
	 * @return
	 */
	public static String format(String str , Object ...args){		
		if(isEmpty(str)){
			return "";
		}
		for(int i= 0 ; i<args.length ; i++){
			if(args[i]!=null)
			str = str.replace("{"+(i+1)+"}", args[i].toString());
		}
		return str.replaceAll("\\{\\d\\}","").replaceAll("\\{ldelim\\}", "{").replaceAll("\\{rdelim\\}", "}");
	}
	
	private static final Pattern FORMAT_STRING = Pattern.compile("\\{([^\\s]+?)\\}");
	/**
	 * <code><pre>
	 * format("{name}:{sex}", Map {
	 * 		name:"Tom",
	 * 		sex:"boy"}) // out put : Tom:boy
	 * </pre></code>
	 * 
	 * 使用{ldelim}, {rdelim} 表示"{","}"
	 * 
	 * @param str 使用{key}占位符的字符串
	 * @param args 填充Map
	 * @return
	 */
	public static String format(String str , Map<String,?>args){
		if(isEmpty(str)){
			return "";
		}		
		str = str.replaceAll("\\{ldelim\\}", "{").replaceAll("\\{rdelim\\}", "}");		
		Matcher matcher = FORMAT_STRING.matcher(str);		
		while(matcher.find()){
			String k = matcher.group(1);
			Object val = args.get(k);
			if(val!=null){
				String s = val.toString();
				str = str.replaceAll("\\{"+k+"\\}", s);
			}			
			else{
				str =str.replaceAll("\\{"+k+"\\}", "");
			}
		}
	
		return str;
	}
	
	/**
	 * 赋值初始值
	 * @param s
	 * @param defaultv
	 * @return
	 */
	public static String forDefault(String s , String defaultv){
		return isEmpty(s)?defaultv:s;
	}
	
	/**
	 * Match regex , not global match ,return the first match.
	 * 
	 * @author <a href="mailto:hb562100@163.com">HeBo</a>
	 * @param s input string
	 * @param regex regex
	 * @return No match return null,or
	 * 		index [0] store full input , index[1]...index[n] store group 1-n; 
	 */
	public static String [] match(CharSequence s , String regex){
		Pattern p = Pattern.compile(regex);	
		return match(s, p);
	}
	
	
	/**
	 * Match regex , not global match ,return the first match.
	 * 
	 * @author <a href="mailto:hb562100@163.com">HeBo</a>
	 * @param s input string
	 * @param regex regex
	 * @return No match return null,or
	 * 		index [0] store full input , index[1]...index[n] store group 1-n; 
	 */
	public static String [] match(CharSequence s , Pattern regex){
		Matcher m = regex.matcher(s);
		
		String [] l=null;
		
		if(m.find()){		
			l =new String [m.groupCount()+1]; 		
			
			for(int i=0;i<l.length;i++)
				l[i]=m.group(i);
		}
		
		return l;
	}
	
	/**
	 * bytes to hex string
	 * @param bytes
	 * @return
	 */
	public static String bytes2Hex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for(byte b:bytes){
			int i = b & 0xFF;
			if(i<=0xF){
				sb.append("0");
			}

			sb.append(Integer.toHexString(i));
		}

		return sb.toString();
	}
	
	public static final List <Character>HEX_CHAR_LIST;
	static {
		HEX_CHAR_LIST = new ArrayList<Character>();
		HEX_CHAR_LIST.add(new Character('0'));
		HEX_CHAR_LIST.add(new Character('1'));
		HEX_CHAR_LIST.add(new Character('2'));
		HEX_CHAR_LIST.add(new Character('3'));
		HEX_CHAR_LIST.add(new Character('4'));
		HEX_CHAR_LIST.add(new Character('5'));
		HEX_CHAR_LIST.add(new Character('6'));
		HEX_CHAR_LIST.add(new Character('7'));
		HEX_CHAR_LIST.add(new Character('8'));
		HEX_CHAR_LIST.add(new Character('9'));
		HEX_CHAR_LIST.add(new Character('a'));
		HEX_CHAR_LIST.add(new Character('b'));
		HEX_CHAR_LIST.add(new Character('c'));
		HEX_CHAR_LIST.add(new Character('d'));
		HEX_CHAR_LIST.add(new Character('e'));
		HEX_CHAR_LIST.add(new Character('f'));
	}
	private static byte hex2Byte(String s) {
		int high = HEX_CHAR_LIST.indexOf(new Character(s.charAt(0))) << 4;
		int low = HEX_CHAR_LIST.indexOf(new Character(s.charAt(1)));

		return (byte) (high + low);
	}

	/**
	 *  hex string to bytes
	 * @param bytes
	 * @return
	 */
	public static byte[] hex2Bytes(String input){
		int len = input.length() / 2;
		byte[] rtn = new byte[len];

		for (int i = 0; i < len; i++) {
			rtn[i] = hex2Byte(input.substring(i * 2, i * 2 + 2));
		}
		return rtn;
	}

	
	/**
	 * Parses the string argument as a signed decimal integer.Default 0;
	 * @param o
	 * @return 
	 */
	public static int parseInt(Object o){
		return parseInt(o, 0);
	}
	
	/**
	 * Parses the string argument as a signed decimal integer.
	 * @param o
	 * @param defaultV
	 * @return
	 */
	public static int parseInt(Object o,int defaultV){
		if(o==null)
			return defaultV;
		try{
			return	Integer.parseInt(o.toString());
		}
		catch (NumberFormatException e) {
			return defaultV;
		}
	}
	
	/**
	 * Decorate a string, return  <i>decorator + s + decorator</i>
	 * 
	 * @param s
	 * @param decorator
	 * @return
	 */
	public static String wrap(Object s , String decorator){
		return decorator+s+decorator;
	}
	
	/**
	 * Return a integer
	 * @param number
	 * @param defaultV
	 * @return
	 */
	public static int intValue(Number number ,int defaultV){
		if(number==null){
			return defaultV;
		}
		
		return number.intValue();
	}
	
	/**
	 * Return a integer , default 0
	 * @param number
	 * @param defaultV
	 * @return
	 */
	public static int intValue(Number number){
		return intValue(number, 0);
	}
	
	/**
	 * Return a long
	 * @param number
	 * @param defaultV
	 * @return
	 */
	public static long longValue(Number number ,long defaultV){
		if(number==null){
			return defaultV;
		}
		
		return number.longValue();
	}
	
	/**
	 * Return a long , default 0
	 * @param number
	 * @param defaultV
	 * @return
	 */
	public static long longValue(Number number){
		return longValue(number, 0);
	}
	
	
	/**
	 * Specailed for length ,return start...end.
	 * <code><pre>
	 * www.duxiu.com/readPage.jsp?dxid=123456789
	 * if length = 20 get 'www.duxiu.com/r...56789'
	 * </pre></code>
	 * 
	 * @param length
	 * @param str
	 * @return
	 */
	public static String pad(int _length, String _str) {
		if (_str == null)
			return "";
		else {
			if (_str.length() > _length) {
				return _str.substring(0, _length *3/4) + "..." + _str.substring(_str.length() - _length / 4);
			} else {
				return _str;
			}
		}
	}
	
	
	/**
	 * Specailed for length ,return start...end.
	 * <code><pre>
	 * www.duxiu.com/readPage.jsp?dxid=123456789
	 * if length = 20 get 'www.duxiu.com/r...56789'
	 * </pre></code>
	 * 
	 * @param length
	 * @param str
	 * @return
	 */
	public static String pad(int _length, CharSequence _str) {
		if (_str == null)
			return "";
		else {
			if (_str.length() > _length) {
				return _str.subSequence(0, _length *3/4) + "..." + _str.subSequence(_str.length() - _length / 4,_str.length());
			} else {
				return _str.toString();
			}
		}
	}
	
//	/**
//	 * Escape html tag to entity reference.
//	 * @param s
//	 * @return string
//	 */
//	public static String escapeHtml(String s){
//		return HtmlUtils.htmlEscape(s).replaceAll("\r\n", "<BR/>").replaceAll("\n", "<BR/>").replace(" ","&nbsp;");
//	}
//	
//	/**
//	 * 1 Escape html tag to entity reference.
//	 * 2 Wrap url into anchor.
//	 * @param s
//	 * @return string
//	 */
//	public static String escapeContent(String s){
//		return escapeHtml(s).replaceAll("((?:http|https|ftp)://(?:[\\w-]+\\.)+[\\w-]+(?:/[\\w- ./?%&=]*)?)", "<A href=\"$1\" target=_blank>$1</A>");
//	}
}
