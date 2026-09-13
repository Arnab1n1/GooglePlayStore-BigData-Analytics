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

public class MaximumInstallsByCategory {

    public static class InstallMapper extends Mapper<Object, Text, Text, LongWritable> {

        private Text category = new Text();

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString();

            if (line.startsWith("App Name"))
                return;

            String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            if (fields.length > 7) {

                String cat = fields[2].replace("\"", "").trim();
                String installs = fields[7].replace("\"", "").trim();

                try {
                    long maxInstall = Long.parseLong(installs);
                    category.set(cat);
                    context.write(category, new LongWritable(maxInstall));
                } catch (Exception e) {
                }
            }
        }
    }

    public static class InstallReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

        public void reduce(Text key, Iterable<LongWritable> values, Context context)
                throws IOException, InterruptedException {

            long max = Long.MIN_VALUE;

            for (LongWritable val : values) {
                if (val.get() > max)
                    max = val.get();
            }

            context.write(key, new LongWritable(max));
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Maximum Installs By Category");

        job.setJarByClass(MaximumInstallsByCategory.class);

        job.setMapperClass(InstallMapper.class);
        job.setReducerClass(InstallReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}