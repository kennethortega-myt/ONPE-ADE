from . import efficient_capsnet_graph_mnist
import os
import json
import threading
__MEMOIZED_MODEL = None
__MEMOIZED_MODEL_LOCK = threading.Lock()

class Model(object):
  """
  A class used to share common model functions and attributes.

  ...

  Attributes
  ----------
  model_name: str
      name of the model (Ex. 'MNIST')
  mode: str
      model modality (Ex. 'test')
  config_path: str
      path configuration file
  verbose: bool

  Methods
  -------
  load_config():
      load configuration file
  load_graph_weights():
      load network weights
  predict(dataset_test):
      use the model to predict dataset_test
  evaluate(X_test, y_test):
      comute accuracy and test error with the given dataset (X_test, y_test)
  save_graph_weights():
      save model weights
  """

  def __init__(self, config_path='config.json', custom_path=None, verbose=True):
    self.model_name = "MNIST"
    self.mode = "test"
    self.modelo = None
    self.config_path = config_path
    self.config = None
    self.verbose = verbose
    self.load_config()

    if custom_path != None:
      self.model_path = custom_path
    else:
      self.model_path = os.path.join(self.config['saved_model_dir'], f"efficient_capsnet_{self.model_name}.h5")
    self.load_graph()
    self.load_graph_weights()

  def load_config(self):
    """
    Load config file
    """
    with open(self.config_path) as json_data_file:
      self.config = json.load(json_data_file)

  def load_graph_weights(self):
    try:
      self.modelo.load_weights(self.model_path)
    except Exception:
      print("[ERRROR] Graph Weights not found")

  def predict(self, dataset_test):
    return self.modelo.predict(dataset_test, verbose="auto" if self.verbose else 0)

  def save_graph_weights(self):
    self.modelo.save_weights(self.model_path)

  def load_graph(self):
    self.modelo = efficient_capsnet_graph_mnist.build_graph(self.config['MNIST_INPUT_SHAPE'], self.mode, self.verbose)


CONFIG_FILE = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'config.json')
MODEL_PATH = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'bin', 'efficient_capsnet_MNIST_last_train.h5')

def load_model():
    global __MEMOIZED_MODEL
    if __MEMOIZED_MODEL is None:
        with __MEMOIZED_MODEL_LOCK:
            if __MEMOIZED_MODEL is None:
                __MEMOIZED_MODEL = Model(
                    config_path=CONFIG_FILE,
                    custom_path=MODEL_PATH,
                    verbose=False
                )
    return __MEMOIZED_MODEL
