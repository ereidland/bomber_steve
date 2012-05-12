package com.evanreidland.e;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;


public class Settings {
	private HashMap<String, Setting> settings;
	
	public Setting addSetting(Setting setting) {
		settings.put(setting.getName(), setting);
		return setting;
	}
	
	public Setting addSetting(String name, String value) {
		return addSetting(new Setting(name, value));
	}
	public Setting addSetting(String name, int value) {
		return addSetting(new Setting(name, String.valueOf(value)));
	}
	public Setting addSetting(String name, boolean value) {
		return addSetting(new Setting(name, String.valueOf(value)));
	}
	
	public Setting getSetting(String name) {
		Setting s = settings.get(name);
		if ( s == null ) {
			return new Setting("null", "");
		}
		return s;
	}
	
	public Setting[] getList() {
		return settings.values().toArray(new Setting[settings.values().size()]);
	}
	
	public void writeToFile(BufferedWriter output) {
		Setting[] list = getList();
		
		try {
			for ( int i = 0; i < list.length; i++ ) {
				output.write("\"" + list[i].getName() + "\" " + "\"" + list[i].value + "\"");
				output.newLine();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void writeToFile(String fname) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
			writeToFile(writer);
			writer.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void readFromFile(BufferedReader reader) {
		String str = null;
		try {
			StringBuilder builder = new StringBuilder();
			String name = null;
			boolean inQuote = false;
			while ( (str = reader.readLine()) != null ) {
				inQuote = false;
				builder = new StringBuilder();
				name = null;
				for ( int i = 0; i < str.length(); i++ ) {
					char c = str.charAt(i);
					if ( inQuote ) {
						if ( c == '"' ) {
							inQuote = false;
						} else {
							builder.append(c);
						}
					} else {
						if ( c == ' ' ) {
							if ( name == null ) {
								name = builder.toString();
								if ( name.length() == 0 ) {
									break;
								}
								builder = new StringBuilder();
							}
						} else if ( c == '"' ) {
							inQuote = true;
						} else {
							builder.append(c);
						}
					}
				}
				if ( builder.length() > 0 && name != null ) {
					addSetting(new Setting(name, builder.toString()));
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void readFromFile(String fname) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fname)); 
			readFromFile(reader);
			reader.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public Settings() {
		settings = new HashMap<String, Setting>();
	}
}
