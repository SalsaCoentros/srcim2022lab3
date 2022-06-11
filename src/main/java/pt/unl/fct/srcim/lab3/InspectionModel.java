package pt.unl.fct.srcim.lab3;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.common.io.ClassPathResource;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;



/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class InspectionModel {

    private MultiLayerNetwork model;

    // TODO: Initialize the model in the constructor using loadModel method
    public InspectionModel(String modelPath) {
        this.model = loadModel(modelPath);
    }

    // TODO: Load the sequential model using KerasModelImport
    // Relevant classes: MultiLayerNetwork, KerasModelImport
    public MultiLayerNetwork loadModel(String filepath) {
        String simpleMlp = null;
        try {
            simpleMlp = new ClassPathResource(filepath).getFile().getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MultiLayerNetwork model = null;
        try {
            model = KerasModelImport.importKerasSequentialModelAndWeights(simpleMlp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKerasConfigurationException e) {
            e.printStackTrace();
        } catch (UnsupportedKerasConfigurationException e) {
            e.printStackTrace();
        }
        return model;
    }

    // TODO: Load the image and return the corresponding array. The input shape should match the one from training
    // Relevant classes: NativeImageLoader, INDArray
    public INDArray loadImage(String filepath, int height, int width, int channels) {
        INDArray image = null;

        // Load the image file
        File f = new File(filepath, ""); // ex.: drawn_image.jpg

        //Use the nativeImageLoader to convert to numerical matrix
        NativeImageLoader loader = new NativeImageLoader(height, width, channels);

        //put image into INDArray
        try {
            image = loader.asMatrix(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Conversion from NCHW to NHWC
        // https://github.com/eclipse/deeplearning4j/issues/8975
        image = image.permute(0, 2, 3, 1);

        return image;
    }

    // TODO: Classify the image by feeding the array from the loadImage method to the model
    // Relevant classes: INDArray, Nd4j
    public int predict(INDArray imageInput) {
        int pred = -1;

        INDArray output = model.output(imageInput);

        pred = output.getInt(0);

        return pred;
    }

}
