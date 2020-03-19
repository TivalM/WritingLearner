import json
import os

import matplotlib.pyplot as plt
import numpy as np
import tensorflow as tf
from tensorflow_core.python.keras.callbacks import BaseLogger


class DetailsControl(tf.keras.callbacks.Callback):

    def __init__(self):
        super(DetailsControl, self).__init__()
        self.stopped_epoch = 0

    def on_train_begin(self, logs=None):
        pass

    def on_epoch_begin(self, epoch, logs=None):
        self.stopped_epoch = self.stopped_epoch + 1
        lr = float(tf.keras.backend.get_value(self.model.optimizer.lr))
        print('learning rate now is{}'.format(lr))

    def on_epoch_end(self, epoch, logs=None):
        # if epoch % 5 is 0 and epoch is not 0:
        #     self.model.save('.\\src\\models\\model_epoch{}.h5'.format(epoch))
        self.model.save('.\\src\\models\\model_epoch{}.h5'.format(epoch))

    def on_train_end(self, logs=None):
        if self.stopped_epoch > 0:
            print("Epoch %05d: stopping" % self.stopped_epoch)


class NpEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.integer):
            return int(obj)
        elif isinstance(obj, np.floating):
            return float(obj)
        elif isinstance(obj, np.ndarray):
            return obj.tolist()
        else:
            return super(NpEncoder, self).default(obj)


class TrainingMonitor(BaseLogger):
    def __init__(self, jsonPath=None, startAt=0):
        # 保存loss图片到指定路径，同时也保存json文件
        super(TrainingMonitor, self).__init__()
        self.jsonPath = jsonPath
        # 开始模型开始保存的开始epoch
        self.startAt = startAt

    def on_train_begin(self, logs=None):
        # 初始化保存文件的目录dict
        self.H = {}
        # 判断是否存在文件和该目录
        if self.jsonPath is not None:
            if os.path.exists(self.jsonPath):
                self.H = json.loads(open(self.jsonPath).read())
                # 开始保存的epoch是否提供
                if self.startAt > 0:
                    for k in self.H.keys():
                        # 循环保存历史记录，从startAt开始
                        self.H[k] = self.H[k][:self.startAt]

    def on_epoch_end(self, epoch, logs=None):
        # 不断更新logs和loss accuracy等等
        for (k, v) in logs.items():
            l = self.H.get(k, [])
            l.append(v)
            self.H[k] = l
        # 查看训练参数记录是否应该保存
        # 主要是看jsonPath是否提供
        if self.jsonPath is not None:
            f = open(self.jsonPath, 'w')
            f.write(json.dumps(self.H, cls=NpEncoder))
            f.close()
        # 保存loss acc等成图片
        if len(self.H["loss"]) > 1:
            # if len(self.H["loss"]) > 1 and epoch % 5 is 0:
            N = np.arange(0, len(self.H["loss"]))
            acc = self.H["accuracy"]
            val_acc = self.H["val_accuracy"]

            loss = self.H["loss"]
            val_loss = self.H["val_loss"]

            plt.figure()

            plt.subplot(1, 2, 1)
            plt.plot(N, acc, label='Training Accuracy')
            plt.plot(N, val_acc, label='Validation Accuracy')
            plt.legend(loc='lower right')
            plt.title('Training and Validation Accuracy')
            plt.savefig(".\\src\\images\\accuracy_{}.png".format(epoch))
            plt.close()

            plt.subplot(1, 2, 2)
            plt.plot(N, loss, label='Training Loss')
            plt.plot(N, val_loss, label='Validation Loss')
            plt.legend(loc='upper right')
            plt.title('Training and Validation Loss')
            plt.savefig(".\\src\\images\\loss_{}.png".format(epoch))
            plt.close()
