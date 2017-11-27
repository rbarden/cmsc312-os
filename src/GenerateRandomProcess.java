import process.Process;

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

		int numOfLines = ThreadLocalRandom.current().nextInt(50, 51);
		int firstLineMemory = ThreadLocalRandom.current().nextInt(50, 101);
		int secondLinePriority = ThreadLocalRandom.current().nextInt(1, 11);
		pw.println(String.valueOf(firstLineMemory));
		pw.println(String.valueOf(secondLinePriority));
		int randLine;
		for (int i = 1; i < numOfLines - 1; i++) {
			randLine = ThreadLocalRandom.current().nextInt(1, 101);
			if (randLine >=1 && randLine < 25){
				pw.println("out from process " + processName);
			}
			else if (randLine >= 25 && randLine < 50){
				pw.println("io");
			}
			else if (randLine >= 50 && randLine < 75){
				pw.println("yield");
			}
			else if (randLine >= 75 && randLine < 97){
				int numCalc = ThreadLocalRandom.current().nextInt(5, 76);
				pw.println("calculate " + numCalc);
			}
			else if (randLine >= 98 && randLine < 101){
				int numCalc = ThreadLocalRandom.current().nextInt(5, 51);
				pw.println("criticalsection " + numCalc);
			}
		}
		pw.println("exe " + processName);
		pw.close();
		return FileReader.createProcess(file.getAbsolutePath());
	}

}
