package localfile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Program {

	public static void main(String[] args) throws IOException {
		
		/*try {
			File file =  new File("res/data.txt");
			FileReader fr = null;
			BufferedReader br = null;
	
			String read = null;
			
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			while((read=br.readLine()) != null) {
				System.out.println(read);
			}
			if(fr!=null)fr.close();
			if(br!=null)br.close();
		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
		}*/
		
		System.out.println(args[0]);
		System.out.println(args[1]);
		System.out.println(args[2]);
		
		
		/*FileInputStream fis = new FileInputStream("res/data.txt");
		
		Scanner fscan = new Scanner(fis);
		
		int total = 0;
		
		while(fscan.hasNext()) {
		
			String line = fscan.nextLine();
			String[] tokens = line.split("\t");
			float lateTime = Float.parseFloat(tokens[1]);
			total += lateTime;
		
		}
		
		System.out.println(total);
		
		fis.close();*/
		
		
		
	}

}
