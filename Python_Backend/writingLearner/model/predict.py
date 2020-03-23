# 预测
import tensorflow as tf
import numpy as np
import pickle

HEIGHT = 64
WIDTH = 64
CHANNELS = 1


class Singleton(object):
    def __init__(self, cls):
        self._cls = cls
        self._instance = {}

    def __call__(self):
        if self._cls not in self._instance:
            self._instance[self._cls] = self._cls()
        return self._instance[self._cls]


@Singleton
class PredictModel():
    def __init__(self):
        self.model = tf.keras.models.load_model('.\\writingLearner\\model\\src\\model_600w_epoch150.h5')
        self.dict = pickle.load(open('.\\writingLearner\\model\\src\\char_dict_reversed', 'br'))

    def predict_char(self, image_str):
        def decode_and_resize(image_str):
            """Decodes png string, resizes it and returns a tensor."""
            image = tf.image.decode_png(image_str, channels=CHANNELS)
            image = tf.image.resize(image, [64, 64])
            image = image / 255.0
            image = (np.expand_dims(image, 0))
            return image

        images_tensor = decode_and_resize(image_str)
        predictions_single = self.model.predict(images_tensor)
        predict_label = np.argmax(predictions_single[0])
        return self.dict[predict_label]


def serving_input_receiver_fn():
    pass

# Optional; currently necessary for batch prediction.


# new_model = tf.keras.models.load_model('.\\src\\model_600w_epoch150.h5')
# image_string = tf.io.read_file("G:\\HandWriting\\HWDB1\\test\\00013\\12444.png")
# print(type(image_string))
# image_decode = tf.image.decode_png(image_string, channels=1)
# # image_resize = tf.squeeze(tf.image.resize(image_decode, [64, 64]), axis=2)
# image_resize = tf.image.resize(image_decode, [64, 64])
# image_resize = image_resize / 255.0
# img = (np.expand_dims(image_resize, 0))
#
# label_list = list(range(0, 599))
#
# predictions_single = new_model.predict(img)
# print(np.argmax(predictions_single[0]))

# # 读取字典文件
# f = open('src/char_dict_reversed', 'br')
# dict = pickle.load(f)
# print(dict)
# {'怀': 1126, '挂': 1337, '耀': 2669, '涉': 1906,... ,}
