import tensorflow as tf
import matplotlib.pyplot as plt

# 读取字典文件
# f = open('G:\\HandWriting\\HWDB1\\char_dict', 'br')
# dict = pickle.load(f)
# print(dict['丑'])
# {'怀': 1126, '挂': 1337, '耀': 2669, '涉': 1906,... ,}
#
# # 读取图片内容
image_string = tf.io.read_file(".\\HWDB1\\test\\00037\\80468.png")
image_decode = tf.image.decode_png(image_string, channels=1)
image_resize = tf.squeeze(tf.image.resize(image_decode, [64, 64]), axis=2)
# image_resize = tf.image.resize(image_decode, [64, 64])
image_resize = image_resize / 255.0
print(image_resize)
# image_string = tf.io.read_file(".\\HWDB1\\test\\00037\\80468.png")
# image_decode = tf.image.decode_png(image_string, channels=1)
# tf.image.resize(image_decode, [64, 64, 1])
# print(image_decode)
plt.figure(1)
plt.imshow(image_resize, cmap='Greys')
plt.show()
