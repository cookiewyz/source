package com.chaoxing.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class IniReader {
	private final String TAG = "IniReader";
    protected HashMap<String, Properties> sections = new HashMap<String, Properties>();
    private transient String currentSecion;
    private transient Properties current;

    public IniReader(String filename) throws IOException {
    	File file = new File(filename);
    	Charset charset = Charset.forName("gb2312");
    	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
//                BufferedReader reader = new BufferedReader(new FileReader(filename));
//        	Log.i(TAG, Charset.defaultCharset().toString());
            read(reader);
            reader.close();
    }

    protected void read(BufferedReader reader) throws IOException {
            String line;
            while ((line = reader.readLine()) != null) {
//                		Log.i(TAG, line);
                    parseLine(line);
            }
    }

    protected void parseLine(String line) {
            line = line.trim();
            if (line.matches("\\[.*\\]")) {
                    currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
                    current = new Properties();
                    sections.put(currentSecion, current);
            } else if (line.matches(".*=.*")) {
                    if (current != null) {
                            int i = line.indexOf('=');
                            String name = line.substring(0, i);
                            String value = line.substring(i + 1);
                            current.setProperty(name, value);
                    }
            }
    }

    public String getValue(String section, String name) {
            Properties p = (Properties) sections.get(section);

            if (p == null) {
                    return null;
            }

            String value = p.getProperty(name);
            return value;
    }
    
    public int getSectionCount(){
    	return sections.size();
    }
    
    public Set<String> getSections(){
    	return sections.keySet();
    }

}