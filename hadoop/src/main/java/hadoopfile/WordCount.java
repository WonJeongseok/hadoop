package hadoopfile;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {	// 필요에 의해 data의 분석( 평균, 합계 등 )이 필요할 때 사용한다.
	
	//	중첩 클래스 ( Instance 중첩, Static 중첩 ) static 변수를 쓸 수 없게 해야한다. 
	private static class TokenCounterMapper extends Mapper<Object, Text, Text, IntWritable>{
		
		//	필요한 곳에서 new를 사용해서 넣을 수 있지만 메모리관리를 위해 위에서 선언해준다.
		private final IntWritable one = new IntWritable(1);
		private final Text word = new Text();
		
		@Override // class Mapper ( KEYIN, VALUEIN, KEYOUT, VALUEOUT ) KEYOUT, VALUEOUT은 Reduce class에서 사용된다.
		protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
				
				String line = value.toString();
				StringTokenizer itr = new StringTokenizer(line);
				while(itr.hasMoreTokens()){
					word.set(itr.nextToken());
					context.write(word, one);
				}
				//context.write(line, 1);			
		}		
	}
	
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		@Override // class reduce(KEY1, VAULE1, KEY2, VALUE2) KEY1, VALUE1은 class Mapper에서 넘겨준 값.
		protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
				
				
			
		}
	}

	public static void main(String[] args) throws IOException {
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Word Count");
		
		job.setJarByClass(WordCount.class);				// 	실행클래스
		job.getMapperClass();							//	매퍼클래스
		job.getReducerClass();
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[0]));
	}
	
}
