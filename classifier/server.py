from flask import Flask, request, Response
import tensorflow as tf
import numpy as np
import json

LABELS_FILENAME = "labels.txt"
MODEL_FILENAME = "pothole-net.tflite"

app = Flask(__name__)


def load_labels(path):
  with open(path, 'r') as f:
    return {i: line.strip() for i, line in enumerate(f.readlines())}


interpreter = tf.lite.Interpreter(model_path=MODEL_FILENAME)
interpreter.allocate_tensors()
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()
input_shape = input_details[0]['shape']

labels = load_labels(LABELS_FILENAME)


def invoke_classifier(input_data):
    input_data = np.array(input_data, dtype=np.float32).reshape(input_shape)
    interpreter.set_tensor(input_details[0]['index'], input_data)

    interpreter.invoke()
    return interpreter.get_tensor(output_details[0]['index'])


@app.route('/classify', methods=['POST'])
def classify():
    input_data = request.json
    input_len = np.multiply.reduce(input_shape)

    if not isinstance(input_data, list) or len(input_data) != input_len:
        message = {"message": "body must be a list with length of %s" % input_len}
        return Response(json.dumps(message), status=400, mimetype='application/json')

    result = invoke_classifier(input_data)
    message = {"result": labels[result.argmax()], "values": {l: float(v) for l, v in zip(labels.values(), result[0])} }
    return Response(json.dumps(message), status=200, mimetype='application/json')


if __name__ == '__main__':
    app.run(debug=False, host='0.0.0.0', port=8080)
