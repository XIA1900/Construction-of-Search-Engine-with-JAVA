package textClassify;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayes;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.*;

public class Train {

    public double train(int num){
//        int num=2000;				// 特征选择时选择的特征数
        String trainfile="F:/Web信息挖掘技术/reuter R8/r8-train-no-stop-id.txt";
        String testfile="F:/Web信息挖掘技术/reuter R8/r8-test-no-stop-id.txt";
//		String trainfile="c:/qjt/data/reuter R8/1.txt";
//		String testfile="c:/qjt/data/reuter R8/1-test.txt";
        Pipe instancePipe = new SerialPipes (new Pipe[] {
                new Target2Label (),							  // Target String -> class label
                new Input2CharSequence (),				  // Data File -> String containing contents
                new CharSequence2TokenSequence (),  // Data String -> TokenSequence
                new TokenSequenceLowercase (),		  // TokenSequence words lowercased
                new TokenSequenceRemoveStopwords (),// Remove stopwords from sequence
                new TokenSequence2FeatureSequence(),// Replace each Token with a feature index
                new FeatureSequence2FeatureVector(),// Collapse word order into a "feature vector"
        });


        InstanceList trainList = new InstanceList (instancePipe);
        InstanceList testList = new InstanceList (instancePipe);

        try{
            trainList.addThruPipe(new CsvIterator(trainfile,"(\\w+)\\s+([\\w-]+)\\s+(.*)", 3, 2, 1));

            testList.addThruPipe(new CsvIterator(testfile,"(\\w+)\\s+([\\w-]+)\\s+(.*)", 3, 2, 1));
            System.out.println(trainList.get(1).getLabeling());
            System.out.println(testList);

//            RankedFeatureVector.Factory[] factories = {new FeatureCounts.Factory(), new ExpGain.Factory()};
//            LabelVector[] labelVectors = {trainList.targetLabelDistribution(), testList.targetLabelDistribution()};
//            FeatureSelector fselector=new FeatureSelector(new GradientGain.Factory(new LabelVector[]{}));

//            FeatureSelector fselector=new FeatureSelector(new ExpGain.Factory(),num);
//            FeatureSelector fselector=new FeatureSelector(new FeatureCounts.Factory(),num);
//            FeatureSelector fselector=new FeatureSelector(new InfoGain.Factory(),num);
//            fselector.selectFeaturesFor(trainList);


//            创建分类器
            ClassifierTrainer<NaiveBayes> naiveBayesTrainer = new NaiveBayesTrainer ();

            FeatureVector f,d;
            LabelVector[] labelVectors = new LabelVector[]{};
            int len=trainList.size();
            Instance in;
            for(int i=0;i<len;i++){
                in=trainList.get(i);
                labelVectors[i] = (LabelVector)in.getData();
                System.out.println(i);
//                d=FeatureVector.newFeatureVector(f, f.getAlphabet(), trainList.getFeatureSelection());
            }

            FeatureSelector fselector=new FeatureSelector(new ExpGain.Factory(labelVectors),num);
//            FeatureSelector fselector=new FeatureSelector(new FeatureCounts.Factory(),num);
//            FeatureSelector fselector=new FeatureSelector(new InfoGain.Factory(),num);
            fselector.selectFeaturesFor(trainList);


//            Instance in;
//            for(int i=0;i<len;i++){
//                in=trainList.get(i);
//                f=(FeatureVector)in.getData();
//                d=FeatureVector.newFeatureVector(f, f.getAlphabet(), trainList.getFeatureSelection());
//                in.unLock();
//                in.setData(d);
//                in.lock();
//            }




            Classifier classifier=naiveBayesTrainer.train(trainList);

//            查看有哪些类别的分类器
//            LabelAlphabet labelAlphabet = classifier.getLabelAlphabet();
//            System.out.println(labelAlphabet.toString());

//            System.out.println ("The testing accuracy is "+ classifier.getF1(testList, 0));
//            System.out.println ("The testing accuracy is "+ classifier.getAccuracy(testList));

            return classifier.getF1(testList, 0);
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0.0;
    }
}
