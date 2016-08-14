package me.corriekay.pokegoutil.utils;

public enum ConfigKey {
	WINDOW_WIDTH("options.window.width", 800, Integer.class),
	WINDOW_HEIGHT("options.window.height", 650, Integer.class),
	WINDOW_POS_X("options.window.posx", 0, Integer.class),
	WINDOW_POS_Y("options.window.posy", 0, Integer.class),
	
	;
	
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
