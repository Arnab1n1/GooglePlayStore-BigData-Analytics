import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TotalReviewsByCategory {

    public static class ReviewsMapper extends Mapper<Object, Text, Text, LongWritable> {

        private Text category = new Text();

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString();

            if (line.startsWith("App Name")) {
                return;
            }

            String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            if (fields.length > 4) {
                String cat = fields[2].replace("\"", "").trim();
                String reviews = fields[4].replace("\"", "").trim();

                try {
                    long reviewCount = Long.parseLong(reviews);
                    category.set(cat);
                    context.write(category, new LongWritable(reviewCount));
                } catch (Exception e) {
                }
            }
        }
    }

    public static class ReviewsReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

        public void reduce(Text key, Iterable<LongWritable> values, Context context)
                throws IOException, InterruptedException {

            long total = 0;

            for (LongWritable val : values) {
                total += val.get();
            }

            context.write(key, new LongWritable(total));
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Total Reviews By Category");

        job.setJarByClass(TotalReviewsByCategory.class);

        job.setMapperClass(ReviewsMapper.class);
        job.setReducerClass(ReviewsReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}