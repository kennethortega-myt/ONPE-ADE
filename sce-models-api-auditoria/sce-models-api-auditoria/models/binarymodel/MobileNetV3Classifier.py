# Modelo con técnicas anti-overfitting mejoradas
import torch.nn as nn
from torchvision import models


class MobileNetV3Classifier(nn.Module):
    def __init__(self, num_classes=1, dropout_rate=0.6):
        super(MobileNetV3Classifier, self).__init__()
        
        self.num_classes = num_classes
        self.backbone = models.mobilenet_v3_large(pretrained = False)
        
        # Congelar las capas del backbone
        for param in self.backbone.parameters():
            param.requires_grad = False
        
        # Clasificador mejorado con más regularización
        in_features = self.backbone.classifier[0].in_features
        
        if num_classes == 1:
            # Clasificación binaria - usar sigmoid
            self.backbone.classifier = nn.Sequential(
                nn.BatchNorm1d(in_features),
                nn.Dropout(0.3),
                nn.Linear(in_features, 256),
                nn.BatchNorm1d(256),
                nn.ReLU(inplace=True),
                nn.Dropout(dropout_rate),
                nn.Linear(256, 128),
                nn.BatchNorm1d(128),
                nn.ReLU(inplace=True),
                nn.Dropout(dropout_rate),
                nn.Linear(128, num_classes),
                nn.Sigmoid()
            )
        else:
            # Clasificación multiclase - usar softmax
            self.backbone.classifier = nn.Sequential(
                nn.BatchNorm1d(in_features),
                nn.Dropout(0.3),
                nn.Linear(in_features, 256),
                nn.BatchNorm1d(256),
                nn.ReLU(inplace=True),
                nn.Dropout(dropout_rate),
                nn.Linear(256, 128),
                nn.BatchNorm1d(128),
                nn.ReLU(inplace=True),
                nn.Dropout(dropout_rate),
                nn.Linear(128, num_classes)
                # No aplicar activación aquí, se aplicará en la función de pérdida
            )
    
    def forward(self, x):
        return self.backbone(x)