package hadoopfile;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class BehindTimeAvg {	// 필요에 의해 data의 분석( 평균, 합계 등 )이 필요할 때 사용한다.
	
	//	중첩 클래스 ( Instance 중첩, Static 중첩 ) static 변수를 쓸 수 없게 해야한다. 
	private static class BehindTimeMapper extends Mapper<Object, Text, Text, FloatWritable>{
		
		//	필요한 곳에서 new를 사용해서 선언할 수 있지만 메모리관리를 위해 위에서 선언해준다.
		private FloatWritable timeWritable = new FloatWritable();
		private Text idWritable = new Text();
		
		@Override // class Mapper ( KEYIN, VALUEIN, KEYOUT, VALUEOUT ) KEYOUT, VALUEOUT은 Reduce class에서 사용된다.
		protected void map(Object key, Text value, Mapper<Object, Text, Text, FloatWritable>.Context context)
			throws IOException, InterruptedException {
				
				//	white space => space bar, tab
				String line = value.toString();
				String[] tokens = line.split("\t");
				String id = tokens[0];
				float time = Float.parseFloat(tokens[1]);
				
				idWritable.set(id);
				timeWritable.set(time);
				context.write(idWritable, timeWritable);
				
				//context.write(line, 1);			
		}		
	}
	
	public static class AvgReducer extends Reducer<Text, FloatWritable, Text, FloatWritable>{
		
		private FloatWritable avgWritable = new FloatWritable();
		
		@Override // class reduce(KEY1, VAULE1, KEY2, VALUE2) KEY1, VALUE1은 class Mapper에서 넘겨준 값.
		protected void reduce(Text key, Iterable<FloatWritable> times,
			Reducer<Text, FloatWritable, Text, FloatWritable>.Context context) throws IOException, InterruptedException {
				
				float total = 0;
				float avg = 0;
				int count = 0;
				
				for(FloatWritable val : times) {
					total += val.get();
					count++;
				}
					
				avg = total / count;
				
				avgWritable.set(avg);
				context.write(key, avgWritable);
			
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException  {
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Word Count");
		
		job.setJarByClass(BehindTimeAvg.class);									// 	실행클래스
		job.setMapperClass(BehindTimeMapper.class);						//	매퍼클래스
		job.setReducerClass(AvgReducer.class);							//	리듀서클래스
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FloatWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true)?0:1);
	}
	
}
