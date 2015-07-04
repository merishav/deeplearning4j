package org.deeplearning4j.ui;

import org.deeplearning4j.datasets.fetchers.MnistDataFetcher;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.layers.factory.LayerFactories;
import org.deeplearning4j.nn.layers.feedforward.autoencoder.AutoEncoder;
import org.deeplearning4j.nn.params.PretrainParamInitializer;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.renders.UpdateFilterIterationListener;
import org.deeplearning4j.ui.weights.HistogramIterationListener;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Arrays;
import java.util.Collections;


/**
 * @author Adam Gibson
 */
public class TestRenders extends BaseUiServerTest {
    @Test
    public void renderSetup() throws Exception {
        MnistDataFetcher fetcher = new MnistDataFetcher(true);
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().momentum(0.9f)
                .optimizationAlgo(OptimizationAlgorithm.GRADIENT_DESCENT)
                .corruptionLevel(0.6)
                .iterations(100)
                .lossFunction(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
                .learningRate(1e-1f).nIn(784).nOut(600)
                .layer(new org.deeplearning4j.nn.conf.layers.AutoEncoder())
                .build();


        fetcher.fetch(100);
        DataSet d2 = fetcher.next();

        INDArray input = d2.getFeatureMatrix();
        AutoEncoder da = LayerFactories.getFactory(conf.getLayer()).create(conf, Arrays.<IterationListener>asList(new ScoreIterationListener(1),new UpdateFilterIterationListener(Collections.singletonList(PretrainParamInitializer.WEIGHT_KEY),1)),0);
        da.setParams(da.params());
        da.fit(input);
    }

    @Test
    public void renderHistogram() throws Exception {
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().momentum(0.9f)
                .optimizationAlgo(OptimizationAlgorithm.ITERATION_GRADIENT_DESCENT)
                .corruptionLevel(0.6)
                .iterations(100).constrainGradientToUnitNorm(true).applySparsity(true).sparsity(0.5)
                .lossFunction(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
                .learningRate(1e-1f).nIn(4).nOut(3)
                .layer(new org.deeplearning4j.nn.conf.layers.AutoEncoder())
                .build();


        DataSet d2 = new IrisDataSetIterator(150,150).next();

        INDArray input = d2.getFeatureMatrix();
        AutoEncoder da = LayerFactories.getFactory(conf.getLayer()).create(conf, Arrays.<IterationListener>asList(new ScoreIterationListener(1),new HistogramIterationListener(1)),0);
        da.setParams(da.params());
        da.fit(input);
    }

}