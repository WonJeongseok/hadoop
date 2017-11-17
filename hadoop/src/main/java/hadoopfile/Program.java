package hadoopfile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

// $ hadoop.jar
public class Program {

	public static void main(String[] args) throws IOException {

		Configuration conf = new Configuration();
		FileSystem hdfs = FileSystem.get(conf);

		// Path filePath = new Path("/input/data.txt");
		Path filePath = new Path(args[0]);

		if (!hdfs.exists(filePath)) {
			System.err.println("입력오류 : " + filePath + "가 존재하지 않습니다.");
			System.exit(2);
		}

		// FileInputStream fis = new FileInputStream("res/data.txt");
		FSDataInputStream fis = hdfs.open(filePath);

		Scanner fscan = new Scanner(fis);

		int total = 0;

		while (fscan.hasNext()) {

			String line = fscan.nextLine();
			String[] tokens = line.split("\t");
			float lateTime = Float.parseFloat(tokens[1]);
			total += lateTime;

		}

		System.out.println(total);

		fis.close();

	}

}
