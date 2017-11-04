import java.io.*;
import java.util.*;

public class FileReader {

	// Takes in a string (file path), and opens the file. The method then returns a 
	// Scanner that is reading the file. 
	
	public static void main(String[] args){
		try{
		for (String str : getStringList("/Users/DevinAlexander/Documents/School/312OS/programs/ide.txt")){
			System.out.println(str);
		}
		
		}catch(FileNotFoundException e){};
	}
	
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
	
}
