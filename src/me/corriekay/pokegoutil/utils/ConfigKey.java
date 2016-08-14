package me.corriekay.pokegoutil.utils;

public enum ConfigKey {
	WindowHeight("options.window.width", 800, Integer.class);
	
	public final String keyName;
	private Object defaultValue;
	private Class<?> cls;
	
	private <T> ConfigKey(String keyName, Object defaultValue, Class<T> cls){
		this.keyName = keyName;
		this.defaultValue = defaultValue;
		this.cls = cls;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getDefaultValue(){
		try{
			return (T) cls.cast(defaultValue);
		}catch(ClassCastException e){
			System.err.println("Error casting: " + e.getMessage());
			return null;
		}		
	}
}
