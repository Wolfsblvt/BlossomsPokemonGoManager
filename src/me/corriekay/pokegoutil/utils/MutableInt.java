package me.corriekay.pokegoutil.utils;

public class MutableInt {
	int value = 0;
	
	public MutableInt() {}
	
	public MutableInt(int i) {
		value = i;
	}
	
	public int increment() {
		return ++value;
	}
	
	public int decrement() {
		return --value;
	}
	
	public int get() {
		return value;
	}
	
	public void set(int i) {
		value = i;
	}
	
	public void setLowest(int i) {
		if (value > i) {
			value = i;
		}
	}
	
	public void setHighest(int i) {
		if (value < i) {
			value = i;
		}
	}
	
	public String toString() { 
		return value + "";
	}
}
