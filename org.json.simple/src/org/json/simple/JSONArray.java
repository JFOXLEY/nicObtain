/*
 * $Id: JSONArray.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package org.json.simple;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.generics.ListHolder;


/**
 * A JSON array. JSONObject supports java.util.List<Object> interface.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class JSONArray extends ArrayList<Object> implements List<Object>, JSONAware, JSONStreamAware {
	private static final long serialVersionUID = 3957988303675231981L;

    /**
     * Encode a List<Object> into JSON text and write it to out. 
     * If this List<Object> is also a JSONStreamAware or a JSONAware, JSONStreamAware and JSONAware specific behaviours will be ignored at this top level.
     * 
     * @see org.json.simple.JSONValue#writeJSONString(Object, Writer)
     * 
     * @param list
     * @param out
     */
	public static void writeJSONString(ListHolder listHolder, Writer out) throws IOException{
		List<Object> list = listHolder.list();
		if(list == null){
			out.write("null");
			return;
		}
		
		boolean first = true;
		Iterator<Object> iter=list.iterator();
		
        out.write('[');
		while(iter.hasNext()){
            if(first)
                first = false;
            else
                out.write(',');
            
			Object value=iter.next();
			if(value == null){
				out.write("null");
				continue;
			}
			
			JSONValue.writeJSONString(value, out);
		}
		out.write(']');
	}
	
	public void writeJSONString(Writer out) throws IOException{
		writeJSONString(new ListHolder(this), out);
	}
	
	/**
	 * Convert a List<Object> to JSON text. The result is a JSON array. 
	 * If this List<Object> is also a JSONAware, JSONAware specific behaviours will be omitted at this top level.
	 * 
	 * @see org.json.simple.JSONValue#toJSONString(Object)
	 * 
	 * @param list
	 * @return JSON text, or "null" if List<Object> is null.
	 */
	public static String toJSONString(ListHolder listHolder){
		List<Object> list = listHolder.list();
		if(list == null)
			return "null";
		
        boolean first = true;
        StringBuffer sb = new StringBuffer();
		Iterator<Object> iter=list.iterator();
        
        sb.append('[');
		while(iter.hasNext()){
            if(first)
                first = false;
            else
                sb.append(',');
            
			Object value=iter.next();
			if(value == null){
				sb.append("null");
				continue;
			}
			sb.append(JSONValue.toJSONString(value));
		}
        sb.append(']');
		return sb.toString();
	}

	public String toJSONString(){
		return toJSONString(new ListHolder(this));
	}
	
	public String toString() {
		return toJSONString();
	}

	
		
}
