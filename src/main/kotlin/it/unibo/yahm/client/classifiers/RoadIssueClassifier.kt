package it.unibo.yahm.client.classifiers

import it.unibo.yahm.client.entities.ObstacleType
/*
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
*/


class RoadIssueClassifier() {

    /*private val interpreter: Interpreter
    private val inputBuffer: TensorBuffer
    private val outputBuffer: TensorBuffer
    private val labels: List<String>
    private val probabilityProcessor: TensorProcessor

    init {

        val options = Interpreter.Option()
        options.setNumThreads(1)

        val tfliteModel = FileUtil.loadMappedFile()
        interpreter = Interpreter(tfliteModel)

        val inputTensor = interpreter.getInputTensor(0)
        val outputTensor = interpreter.getOutputTensor(0)
        inputBuffer = TensorBuffer.createFixedSize(inputTensor.shape(), inputTensor.dataType())
        outputBuffer = TensorBuffer.createFixedSize(outputTensor.shape(), outputTensor.dataType())

        labels = FileUtil.loadLabels(context, LABELS_FILENAME)

        val op = NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD)
        probabilityProcessor = TensorProcessor.Builder().add(op).build()
    }
     */

    fun classify(input: FloatArray): ObstacleType {
        /*
        inputBuffer.loadArray(input)
        interpreter.run(inputBuffer.buffer, outputBuffer.buffer.rewind())

        val labeledProbability =
            TensorLabel(labels, probabilityProcessor.process(outputBuffer)).mapWithFloatValue

        println("RoadIssueClassifier: Results: $labeledProbability")

        return ObstacleType.valueOf(labeledProbability.maxBy { it.value }!!.key)
         */
        return ObstacleType.NOTHING
    }

    companion object {
        private const val MODEL_FILENAME = "pothole-net.tflite"
        private const val LABELS_FILENAME = "labels.txt"
        private const val PROBABILITY_MEAN = 0.0f
        private const val PROBABILITY_STD = 1.0f
    }

}
