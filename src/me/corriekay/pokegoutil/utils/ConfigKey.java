package me.corriekay.pokegoutil.utils;

public enum ConfigKey {
	WINDOW_WIDTH("options.window.width", 800, Integer.class),
	WINDOW_HEIGHT("options.window.height", 650, Integer.class),
	WINDOW_POS_X("options.window.posx", 0, Integer.class),
	WINDOW_POS_Y("options.window.posy", 0, Integer.class),
	
	TRANSFER_AFTER_EVOLVE("transfer.afterEvolve", false, Boolean.class),
	SHOW_BULK_POPUP("popup.afterBulk", true, Boolean.class),
	
	LANGUAGE("options.lang", "en", String.class),
	FONT_SIZE("options.fontsize", 0, Integer.class),
	
	DELAY_RENAME_MIN("delay.rename.min", 1000, Integer.class),
	DELAY_RENAME_MAX("delay.rename.max", 5000, Integer.class),
	DELAY_TRANSFER_MIN("delay.transfer.min", 1000, Integer.class),
	DELAY_TRANSFER_MAX("delay.transfer.max", 5000, Integer.class),
	DELAY_EVOLVE_MIN("delay.evolve.min", 3000, Integer.class),
	DELAY_EVOLVE_MAX("delay.evolve.max", 12000, Integer.class),
	DELAY_POWERUP_MIN("delay.powerUp.min", 1000, Integer.class),
	DELAY_POWERUP_MAX("delay.powerUp.max", 5000, Integer.class),
	DELAY_FAVORITE_MIN("delay.favorite.min", 1000, Integer.class),
	DELAY_FAVORITE_MAX("delay.favorite.max", 3000, Integer.class),
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
