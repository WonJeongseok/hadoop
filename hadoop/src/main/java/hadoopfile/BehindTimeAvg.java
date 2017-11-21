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
	// class Mapper ( KEYIN, VALUEIN, KEYOUT, VALUEOUT ) KEYOUT, VALUEOUT은 Reduce class에서 사용된다.
	private static class BehindTimeMapper extends Mapper<Object, Text, Text, FloatWritable>{
		
		//	Writable : 인터페이스 - DataInput,DataOutput을 Serialize(직렬화) 하기위해 필요한 객체 (커스텀도 가능하다)
		private FloatWritable timeWritable = new FloatWritable();
		private Text idWritable = new Text();
		
		@Override 
		protected void map(Object key, Text value, Mapper<Object, Text, Text, FloatWritable>.Context context)
			throws IOException, InterruptedException {
				
				//	white space => space bar, tab
				String line = value.toString();					//	줄 읽어오기
				String[] tokens = line.split("\t");				//	"\t" 단위로 끊어서 ( 탭 ) --> tonkens배열에 저장
				String id = tokens[0];							
				float time = Float.parseFloat(tokens[1]);		//	지각시간이 Float형이기 때문에 parseFloat으로 형번환
				
				idWritable.set(id);								//	Writable에 id값 Set
				timeWritable.set(time);							//	Writable에 time값 Set
				context.write(idWritable, timeWritable);		//	Set한 값 출력
				
				//context.write(line, 1);			
		}		
	}
	
	// class reduce(KEY1, VAULE1, KEY2, VALUE2) KEY1, VALUE1은 class Mapper에서 넘겨준 값.
	public static class AvgReducer extends Reducer<Text, FloatWritable, Text, FloatWritable>{
		
		private float avgTotal = 0;				//	반전체평균
		private float countTotal = 0;			//	반전체평균을 구하기 위한 카운트
		
		private FloatWritable avgWritable = new FloatWritable();
		
		//	작업하기전에 호출된다
		@Override
		protected void setup(Reducer<Text, FloatWritable, Text, FloatWritable>.Context context)
				throws IOException, InterruptedException {
			
			super.setup(context);
		}
		
		@Override 
		protected void reduce(Text key, Iterable<FloatWritable> times,
			Reducer<Text, FloatWritable, Text, FloatWritable>.Context context) throws IOException, InterruptedException {
				
				countTotal++;
				float total = 0;
				float avg = 0;
				int count = 0;
				
				for(FloatWritable val : times) {
					total += val.get();
					count++;
				}
					
				avg = total / count;
				avgTotal += avg;
				avgWritable.set(avg);
				context.write(key, avgWritable);
			
		}
		
		//	마무리작업을 할 때 호출된다
		@Override
		protected void cleanup(Reducer<Text, FloatWritable, Text, FloatWritable>.Context context)
				throws IOException, InterruptedException {
			
			FloatWritable avgTotalWritable = new FloatWritable(avgTotal / countTotal);
			Text key = new Text("total");
			context.write(key, avgTotalWritable);
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException  {
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Word Count");
		
		job.setJarByClass(BehindTimeAvg.class);							// 	실행클래스
		job.setMapperClass(BehindTimeMapper.class);						//	매퍼클래스
		job.setCombinerClass(AvgReducer.class);							//	Merge & sort를 하기전에 리듀싱을 하는게 더 효율적일때가 있다.
																		//	버그가 있을 수 있지만 버그가 없다는게 확실한 상황이면 사용하는게 좋다.
																		//	마지막 리듀싱을 하기 전에 중간결과를 보고싶을때 리듀싱을 해주는게 컴바이너이다.

		job.setReducerClass(AvgReducer.class);							//	리듀서클래스
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FloatWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true)?0:1);
	}
	
}
