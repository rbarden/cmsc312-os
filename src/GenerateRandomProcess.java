import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateRandomProcess {

	public static Process generate(String processName)
			throws FileNotFoundException, UnsupportedEncodingException, IOException {

		File file = new File(processName);
		PrintWriter pw = new PrintWriter(new FileWriter(file));

		int numOfLines = ThreadLocalRandom.current().nextInt(50, 101);
		int firstLine = ThreadLocalRandom.current().nextInt(50, 1001);
		pw.println(String.valueOf(firstLine));
		int randLine;
		for (int i = 1; i < numOfLines - 1; i++) {
			randLine = ThreadLocalRandom.current().nextInt(1, 7);
			switch (randLine) {
			case 1:
				pw.println("out Output From Process " + processName);
				break;
			case 2:
				pw.println("io");
				break;
			case 3:
				pw.println("yield");
				break;
			case 4:
				int numCalc = ThreadLocalRandom.current().nextInt(5, 76);
				pw.println("calculate " + numCalc);
				break;
			}
		}
		pw.println("exe " + processName);
		pw.close();
		return FileReader.createProcess(file.getAbsolutePath());
	}

	/*
	 * public static void main(String[] args){ ArrayList<String> list =
	 * generate("tester.txt");
	 * 
	 * for(String s : list){ System.out.println(s); } }
	 */

}
