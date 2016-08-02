package me.corriekay.pokegoutil.utils;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public abstract class Utilities {
	private static final Random random = new Random(System.currentTimeMillis());
	
	private Utilities() {}
	
	public static void setNativeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
	}
	
	public static void deleteFile(File file, boolean deleteDir) {
		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				deleteFile(subFile, true);
			}
			if (deleteDir) {
				file.delete();
			}
		} else {
			file.delete();
		}
	}
	
	public static boolean checkFilename(String checkme, boolean warn) {
		String[] chars = new String[] { "\\", "/", ":", "*", "?", "\"", "<", ">", "|" };
		
		for (String c : chars) {
			
			if (checkme.contains(c)) {
				if (warn) JOptionPane.showMessageDialog(null, "A file name can't contain any of the following characters: \\/:*\"<>|");
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkFileExists(File file) {
		if (file.exists()) {
			JOptionPane.showMessageDialog(null, "This file already exists. Please choose another file name.");
			return true;
		}
		return false;
	}
	
	public static Image loadImage(String filename) {
		
		filename = "res/" + filename;
		try {
			return ImageIO.read(Utilities.class.getClassLoader().getResourceAsStream(filename));
		} catch (Exception e) {
			System.out.println("UNABLE TO READ IMAGE " + filename);
			return null;
		}
	}
	
	public static String readResourceFile(String res) {
		try {
			InputStream is = Utilities.class.getClassLoader().getResourceAsStream(res);
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			in.lines().forEach(s -> sb.append(s + "\n"));
			in.close();
			return sb.toString();
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public static String readFile(String url) {
		return readFile(new File(url));
	}
	
	public static String readFile(File file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			
			StringBuilder sb = new StringBuilder();
			
			String l = "";
			boolean firstline = true;
			do {
				sb.append(l);
				l = in.readLine();
				if (l != null && !firstline) {
					sb.append("\n");
					firstline = false;
				}
			} while (l != null);
			in.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace(); //TODO tagging for future exception handling/logging
		}
		return null;
	}
	
	public static void saveFile(File file, String saveme) {
		try {
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(saveme);
			out.close();
		} catch (Exception e) {
			System.out.println("Exception caught trying to save file. Path: " + file.getAbsolutePath());
			e.printStackTrace(); //TODO tagging for future exception handling/logging
		}
	}
	
	public static String joinStringArray(String[] args) {
		return joinStringArray(args, "");
	}
	
	public static String joinArrayList(ArrayList<String> args) {
		return joinArrayList(args, "");
	}
	
	public static String joinStringSet(Set<String> args) {
		return joinStringSet(args, "");
	}
	
	public static String joinStringArray(String[] args, String delimiter) {
		return joinStringArray(args, delimiter, 0);
	}
	
	public static String joinArrayList(ArrayList<String> args, String delimiter) {
		return joinArrayList(args, delimiter, 0);
	}
	
	public static String joinStringSet(Set<String> args, String delimiter) {
		return joinStringSet(args, delimiter, 0);
	}
	
	public static String joinStringArray(String[] args, String delimiter, int startingIndex) {
		StringBuilder s = new StringBuilder();
		for (int i = startingIndex; i < args.length; i++) {
			s.append(args[i]);
			if (!(i + 1 >= args.length)) {
				s.append(delimiter);
			}
		}
		return s.toString();
	}
	
	public static String joinArrayList(ArrayList<String> args, String delimiter, int startingIndex) {
		StringBuilder s = new StringBuilder();
		for (int i = startingIndex; i < args.size(); i++) {
			s.append(args.get(i));
			if (!(i + 1 >= args.size())) {
				s.append(delimiter);
			}
		}
		return s.toString();
	}
	
	public static String joinStringSet(Set<String> args, String delimiter, int startingIndex) {
		int count = startingIndex;
		StringBuilder s = new StringBuilder();
		for (String string : args) {
			if (count >= startingIndex) {
				s.append(string);
				if (count + 1 < args.size()) {
					s.append(delimiter);
				}
			}
			count++;
		}
		return s.toString();
	}
	
	public static String getStringFromIndex(int index, String[] args) {
		String s = "";
		for (int i = index; i < args.length; i++) {
			s += args[i] + " ";
		}
		return s.trim();
	}
	
	public static void setLocationMidScreen(Window frame, int screen) {
		DisplayMode monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[screen].getDisplayMode();
		frame.setLocation(monitor.getWidth() / 2 - (frame.getWidth() / 2), monitor.getHeight() / 2 - (frame.getHeight() / 2));
	}
	
	public static void setLocationMidScreen(Window frame) {
		setLocationMidScreen(frame, 0);
	}
	
	public static String getTimeStamp(String format) {
		return getTimeStamp(format, System.currentTimeMillis());
	}
	
	public static String getTimeStamp(String format, long time) {
		return new SimpleDateFormat(format).format(time);
	}
	
	public static boolean isEven(long i) {
		return i % 2 == 0;
	}
	
	public static void setBackgroundColorRecursively(Container container, Color color) {
		for (int i = 0; i < container.getComponentCount(); i++) {
			Component component = container.getComponent(i);
			if (component instanceof Container) {
				setBackgroundColorRecursively((Container) component, color);
			}
			component.setBackground(color);
		}
	}
	
	public static Color randomColor() {
		Color c = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255), 255);
		return c;
	}
	
	public static void sleep(int sleep) {
		try {
			Thread.sleep(sleep);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
