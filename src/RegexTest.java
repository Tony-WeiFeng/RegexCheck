import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.*;

public class RegexTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
	String p = "C:\\CER_Text";
	//test
	//String p = "C:\\1";
	
	
	paserAllFiles(p);
	
	}
	
	static void parserFile(String fileName) throws Exception {
	File f = new File(fileName);
	BufferedReader reader = null;
	try {
		reader = new BufferedReader(new FileReader(f));
		String tempString = reader.readLine();
		int line = 0;
		while (tempString != null && 
				!tempString.startsWith("quit:") 
				) {
			line++;
			if ((line >= 3) && (!tempString.startsWith("WARNING:")) && tempString.contains("+0x")) {
				StackEntry se = Parser.parseSymbol(tempString, 1);
				if ((se.toString().contains("no matching symbol")) && (!(tempString.split(" ")[3].startsWith("0x"))))  {

					// Write in log file
					/*
					System.out.println("===============================================");
					System.out.println("No matching symbol in line " + line + " of file " + f.getName());
					System.out.println("***********");
					System.out.println(tempString);
					System.out.println("***********");
					System.out.println(se.toString());
					*/
					String logItem = "";
					logItem = logItem + "===============================================\n";
					logItem = logItem + "No matching symbol in line " + line + " of file " + f.getName() + "\n";
					logItem = logItem + "***********\n";
					logItem = logItem + tempString + "\n";
					logItem = logItem + "***********\n";
					logItem = logItem + se.toString() + "\n";
					
					System.out.println(logItem);
						
					logWriter(logItem);
						
						
				 }
			 }
			tempString = reader.readLine();
		 }
	 } catch (IOException e) {
		 e.printStackTrace();
	 } finally {
		 if (reader != null) {
			 try {
				 reader.close();
			 } catch (IOException e1) {
				 
			 }
			 
		 }
	 }
	 
	
}
	
	static void paserAllFiles(String path) throws Exception {
		File dir = new File(path);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (int i=0; i<files.length; i++) {
			String fn = files[i].getPath();
			
			parserFile(fn);
		}
		
	}


	static void logWriter(String str) {
		FileWriter fw = null;
		
		try {
			fw = new FileWriter("C:\\Users\\fengw\\Desktop\\log.txt", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.write(str);
//			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}