package textClassify;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayes;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.util.ArrayList;

public class mallet_test {

    static InstanceList trainList;
    static InstanceList testList;
    static int[] feature_number = new int[]{1, 5, 10, 50, 100, 500, 1000, 10000};
    static int n = feature_number.length;
    static String train="F:/Web信息挖掘技术/reuter R8/r8-train-no-stop-id.txt";
    static String test="F:/Web信息挖掘技术/reuter R8/r8-test-no-stop-id.txt";

    static double[] exp_list=new double[n];
    static double[] counts_list=new double[n];
    static double[] info_list=new double[n];
    static double[] gradient_list=new double[n];

    public static void main(String[] args) {





//        String train = "/users/user/documents/study_in_swufe/web info/reuter R8/r8-train-no-stop-id.txt";
//        String test = "/users/user/documents/study_in_swufe/web info/reuter R8/r8-test-no-stop-id.txt";

        Pipe instancePipe = new SerialPipes(new Pipe[]{
                new Target2Label(),                                    // Target String -> class label
                new Input2CharSequence(),                              // Data File -> String containing contents
                new CharSequence2TokenSequence(),                      // Data String -> TokenSequence
                new TokenSequenceLowercase(),                          // TokenSequence words lowercased
                new TokenSequenceRemoveStopwords(),                    // Remove stopwords from sequence
                new TokenSequence2FeatureSequence(),                   // Replace each Token with a feature index
                new FeatureSequence2FeatureVector(),                   // Collapse word order into a "feature vector"
        });
        trainList = new InstanceList(instancePipe);
        testList = new InstanceList(instancePipe);

        FeatureSelector exp,counts,info,gradient;
        int num;
        for(int i=0;i<n;i++) {
            num=feature_number[i];
//            exp = new FeatureSelector(new ExpGain.Factory(new LabelVector[]{}), num);
//            exp=new FeatureSelector(new ExpGain.Factory(),num);
//            exp_list[i]=DiffSelector(exp,train,test);

            counts=new FeatureSelector(new FeatureCounts.Factory(),num);
            counts_list[i]=DiffSelector(counts);

            info=new FeatureSelector(new InfoGain.Factory(),num);
            info_list[i]=DiffSelector(info);

//            gradient=new FeatureSelector(new GradientGain.Factory().newRankedFeatureVector(trainList),num);
//            gradient_list[i]=DiffSelector(gradient,train,test);
        }


//        CategoryDataset mDataset = GetDataset(exp_list,counts_list,info_list,gradient_list);
        CategoryDataset mDataset = GetDataset();
        JFreeChart mChart= ChartFactory.createLineChart("","number of features selected","F1 measure", mDataset, PlotOrientation.VERTICAL,true,true,false);
        CategoryPlot mPlot = (CategoryPlot)mChart.getPlot();
        mPlot.setBackgroundPaint(Color.WHITE);
        mPlot.setRangeGridlinePaint(Color.WHITE);//背景底部横虚线
        mPlot.setOutlinePaint(Color.WHITE);//边界线
        ChartFrame mChartFrame = new ChartFrame("compare", mChart);
        mChartFrame.pack();
        mChartFrame.setVisible(true);

    }

    public static CategoryDataset GetDataset() {
        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
        for(int i=0;i<n;i++) {
            mDataset.addValue(info_list[i],"InfoGain",String.valueOf(feature_number[i]));
        }
        for(int i=0;i<n;i++) {
            mDataset.addValue(counts_list[i],"FeatureCounts",String.valueOf(feature_number[i]));
        }
        return mDataset;
    }

    public static Double DiffSelector(FeatureSelector fea) {

        try {
            trainList.addThruPipe(new CsvIterator(train, "(\\w+)\\s+([\\w-]+)\\s+(.*)", 3, 2, 1));
            testList.addThruPipe(new CsvIterator(test, "(\\w+)\\s+([\\w-]+)\\s+(.*)", 3, 2, 1));
            fea.selectFeaturesFor(trainList);

            ClassifierTrainer<NaiveBayes> naiveBayesTrainer = new NaiveBayesTrainer();

            FeatureVector f, d;
            int len = trainList.size();
            Instance in;
            for (int i = 0; i < len; i++) {
                in = trainList.get(i);
                f = (FeatureVector) in.getData();
                d = FeatureVector.newFeatureVector(f, f.getAlphabet(), trainList.getFeatureSelection());
                in.unLock();
                in.setData(d);
                in.lock();
            }

            Classifier classifier = naiveBayesTrainer.train(trainList);
            System.out.println(classifier.getF1(testList, 0));
            return classifier.getF1(testList, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}

