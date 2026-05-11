# -*- coding: utf-8 -*-
"""
This Script contains the default and Spinal VGG code for MNIST.

This code trains both NNs as two different models.

This code randomly changes the learning rate to get a good result.

@author: Dipu
"""


import torch
import torch.nn as nn
import torch.nn.functional as F

from models.binarymodel.base_binary_model import BaseVGG

num_epochs = 25
batch_size_train = 100
batch_size_test = 1000
learning_rate = 0.005
momentum = 0.5
    
    
Half_width =128
layer_width =128
    
class SpinalVGG(BaseVGG):
    def __init__(self, num_classes=1):
        super().__init__()
        self.num_classes = num_classes
        self.l1 = self.two_conv_pool(3, 64, 64)  # Cambiado de 1 a 3 canales
        self.l2 = self.two_conv_pool(64, 128, 128)
        self.l3 = self.three_conv_pool(128, 256, 256, 256)
        self.l4 = self.three_conv_pool(256, 256, 256, 256)

        self.fc_spinal_layer1 = self._spinal_layer(Half_width, layer_width)
        self.fc_spinal_layer2 = self._spinal_layer(Half_width + layer_width, layer_width)
        self.fc_spinal_layer3 = self._spinal_layer(Half_width + layer_width, layer_width)
        self.fc_spinal_layer4 = self._spinal_layer(Half_width + layer_width, layer_width)
        self.fc_out = nn.Sequential(nn.Dropout(0.5), nn.Linear(layer_width * 4, num_classes))

    def _spinal_layer(self, in_features, out_features):
        return nn.Sequential(
            nn.Dropout(0.5),
            nn.Linear(in_features, out_features),
            nn.BatchNorm1d(out_features),
            nn.ReLU(inplace=True)
        )

    def forward(self, x):
        x = self.l1(x)
        x = self.l2(x)
        x = self.l3(x)
        x = self.l4(x)
        x = x.view(x.size(0), -1)

        x1 = self.fc_spinal_layer1(x[:, 0:Half_width])
        x2 = self.fc_spinal_layer2(torch.cat([x[:, Half_width:2*Half_width], x1], dim=1))
        x3 = self.fc_spinal_layer3(torch.cat([x[:, 0:Half_width], x2], dim=1))
        x4 = self.fc_spinal_layer4(torch.cat([x[:, Half_width:2*Half_width], x3], dim=1))

        x = torch.cat([x1, x2, x3, x4], dim=1)
        x = self.fc_out(x)

        if self.num_classes == 1:
            # Clasificación binaria - usar sigmoid
            return torch.sigmoid(x)
        else:
            # Clasificación multiclase - retornar logits sin activación
            # La activación softmax se aplicará en la función de pérdida
            return x