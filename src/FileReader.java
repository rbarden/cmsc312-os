import java.io.*;
import java.util.*;

public class FileReader {

	// Takes in a string (file path), and opens the file. The method then returns a 
	// Scanner that is reading the file. 
	
	
	public static Scanner openFile(String str) throws FileNotFoundException{
		File file = new File(str);
		return new Scanner(file);
	}
	
	// Returns an ArrayList of all strings from the file. 
	// 
	public static ArrayList<String> parseFile(Scanner s){
		ArrayList<String> list = new ArrayList<>();
		while(s.hasNextLine()){
			String str = s.nextLine().trim();
			if(str.contains(" ")){
				list.add(str.replace(' ', ','));
			}
			else{
				list.add(str);
			}	
		}
		return list;
	}
	
	public static ArrayList<String> getStringList(String str) throws FileNotFoundException{
		return parseFile(openFile(str));
	}
	
	public static Process createProcess(String str) throws FileNotFoundException{
		String pName = "rName";
		if (str.contains("/")){
			String[] spl = str.split("/");
			pName = spl[spl.length - 1];
		}
		else{
			pName = str;
		}
		ArrayList<String> arr = parseFile(openFile(str));
		int mem = Integer.parseInt(arr.get(0).trim());
		arr.remove(0);
		return new Process(State.READY, pName, 0, 1, mem, 0, arr, false);
	}
	
}
