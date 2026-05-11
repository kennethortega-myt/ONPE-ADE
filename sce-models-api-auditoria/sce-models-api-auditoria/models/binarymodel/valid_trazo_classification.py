import os, sys
import torch
from torchvision import transforms
import numpy as np
import cv2
from PIL import Image
import time
from logger_config import logger
from models.binarymodel.MNIST_VGG_and_SpinalVGG import SpinalVGG
from models.binarymodel.MobileNetV3Classifier import MobileNetV3Classifier
from safetensors.torch import load_file
import threading
__MULTICLASS_CLASSIFIER_INSTANCE = None
__MULTICLASS_CLASSIFIER_INSTANCE_LOCK = threading.Lock()

SPINALVGG_PATH = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'bin', 'best_model_balanced_spinalvgg_291025.safetensors')
MOBILENETV3_PATH = os.path.join(os.path.dirname(os.path.realpath(__file__)), 'bin', 'best_model_balanced_mobilenetv3_291025.safetensors')

class MultiClassModelClassifier:
    """
    Administra los modelos SpinalVGG y MobileNetV3 para clasificación multiclase (3 clases: 0, 1, 2).
    """

    def __init__(self, spinal_path=SPINALVGG_PATH, mobilenet_path=MOBILENETV3_PATH, device="cpu", num_classes=3):
        self.device = device
        self.spinal_path = spinal_path
        self.mobilenet_path = mobilenet_path 
        self.num_classes = num_classes
        self.spinal_model = None
        self.mobilenet_model = None
        self.class_names = ['0', '1', '2']

        self.transform = transforms.Compose([
            transforms.Resize((32, 32)),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
        ])

        self.load_models()

    def load_models(self):
        self.spinal_model = SpinalVGG(num_classes=self.num_classes).to(self.device)
        self.mobilenet_model = MobileNetV3Classifier(num_classes=self.num_classes).to(self.device)

        try:
            # Cargar pesos desde SafeTensors
            spinal_weights = load_file(self.spinal_path)
            self.spinal_model.load_state_dict(spinal_weights)
            #logger.info(f"✓ Modelo SpinalVGG cargado desde: {self.spinal_path}")
        except Exception as e:
            logger.exception(f"Error al cargar el modelo SpinalVGG: {e}")
            raise

        try:
            # Cargar pesos desde SafeTensors
            mobilenet_weights = load_file(self.mobilenet_path)
            self.mobilenet_model.load_state_dict(mobilenet_weights)
            #logger.info(f"✓ Modelo MobileNetV3 cargado desde: {self.mobilenet_path}")
        except Exception as e:
            logger.exception(f"Error al cargar el modelo MobileNetV3: {e}")
            raise

        self.spinal_model.eval()
        self.mobilenet_model.eval()

    def predict_single_image(self, img: np.ndarray):
        """Predecir una sola imagen para clasificación multiclase"""
        # Convertir numpy array a PIL Image
        if img.ndim == 2:  # Si es grayscale
            pil_img = Image.fromarray(img, mode='L').convert('RGB')
        else:  # Si ya es RGB
            pil_img = Image.fromarray(img)
        
        input_tensor = self.transform(pil_img).unsqueeze(0).to(self.device)
        
        # Predicción
        with torch.no_grad():
            spinal_output = self.spinal_model(input_tensor)
            mobilenet_output = self.mobilenet_model(input_tensor)
            
            # Aplicar softmax para obtener probabilidades
            spinal_probs = torch.softmax(spinal_output, dim=1).squeeze().cpu().numpy()
            mobilenet_probs = torch.softmax(mobilenet_output, dim=1).squeeze().cpu().numpy()

        # --- Spinal ---
        predicted_class_spinal = np.argmax(spinal_probs)
        confidence_spinal = float(spinal_probs[predicted_class_spinal])

        # --- MobileNet ---
        predicted_class_mobilenet = np.argmax(mobilenet_probs)
        confidence_mobilenet = float(mobilenet_probs[predicted_class_mobilenet])
        
        return {
            'predicted_class_spinal': self.class_names[predicted_class_spinal],
            'predicted_class_mobilenet': self.class_names[predicted_class_mobilenet],
            'confidence_spinal': confidence_spinal,
            'confidence_mobilenet': confidence_mobilenet,
            'probabilities_spinal': spinal_probs.tolist(),
            'probabilities_mobilenet': mobilenet_probs.tolist()
        }

def load_multiclass_model():
    global __MULTICLASS_CLASSIFIER_INSTANCE
    if __MULTICLASS_CLASSIFIER_INSTANCE is None:
        with __MULTICLASS_CLASSIFIER_INSTANCE_LOCK:
            if __MULTICLASS_CLASSIFIER_INSTANCE is None:
                __MULTICLASS_CLASSIFIER_INSTANCE = MultiClassModelClassifier()
    return __MULTICLASS_CLASSIFIER_INSTANCE
