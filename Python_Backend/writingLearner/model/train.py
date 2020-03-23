import pickle

import tensorflow as tf
from keras_preprocessing.image import ImageDataGenerator
from tensorflow.keras import layers
from tensorflow_core.python.keras.callbacks import ReduceLROnPlateau
from tensorflow_core.python.keras.layers import BatchNormalization, Activation

# 定义全局变量
from writingLearner.model.callback import DetailsControl, TrainingMonitor

trainDataPath = "G:\\HandWriting\\HWDB1\\train"
testDataPath = "G:\\HandWriting\\HWDB1\\test"
# trainDataPath = ".\\HWDB1\\train"
# testDataPath = ".\\HWDB1\\test"
jsonPath = ".\\src\\history\\MY_history.json"

batch_size = 128
epochs = 300
IMG_HEIGHT = 64
IMG_WIDTH = 64


# 定义数据生成器
train_image_generator = ImageDataGenerator(rescale=1. / 255,
                                           rotation_range=20)  # Generator for our training data
validation_image_generator = ImageDataGenerator(rescale=1. / 255)  # Generator for our validation data

train_data_gen = train_image_generator.flow_from_directory(directory=trainDataPath,
                                                           batch_size=batch_size,
                                                           color_mode='grayscale',
                                                           shuffle=True,
                                                           target_size=(IMG_WIDTH, IMG_HEIGHT),
                                                           class_mode='categorical')
val_data_gen = validation_image_generator.flow_from_directory(directory=testDataPath,
                                                              batch_size=batch_size,
                                                              color_mode='grayscale',
                                                              shuffle=True,
                                                              target_size=(IMG_WIDTH, IMG_HEIGHT),
                                                              class_mode='categorical')

# 定义两个回调
early_stop = tf.keras.callbacks.EarlyStopping(
    monitor='val_loss', min_delta=0.0001, patience=7, verbose=0, mode='auto',
    baseline=None, restore_best_weights=True
)

reduce_lr = ReduceLROnPlateau(monitor='loss', patience=2, factor=0.1, min_delta=0.0002, min_lr=0.000001)


# 定义模型
def create_model():
    # model for 600w 150
    model = tf.keras.Sequential()

    model.add(layers.Conv2D(32, (3, 3), input_shape=(IMG_WIDTH, IMG_HEIGHT, 1),
                            padding='same', strides=(1, 1), name='conv1'))
    model.add(BatchNormalization(axis=-1))
    model.add(Activation('relu'))
    model.add(layers.MaxPool2D(pool_size=(2, 2), padding='same'))

    model.add(layers.Conv2D(48, (3, 3), input_shape=(IMG_WIDTH, IMG_HEIGHT, 1),
                            padding='same', strides=(1, 1), name='conv2'))
    model.add(BatchNormalization(axis=-1))
    model.add(Activation('relu'))
    model.add(layers.MaxPool2D(pool_size=(2, 2), padding='same'))

    model.add(layers.Conv2D(64, (3, 3), input_shape=(IMG_WIDTH, IMG_HEIGHT, 1),
                            padding='same', strides=(1, 1), name='conv3'))
    model.add(BatchNormalization(axis=-1))
    model.add(Activation('relu'))
    model.add(layers.MaxPool2D(pool_size=(2, 2), padding='same'))

    model.add(layers.Flatten())
    model.add(layers.Dense(720))
    model.add(layers.Dropout(rate=0.4))
    model.add(layers.Dense(600, activation='softmax'))

    # model for 100w 64
    # model = tf.keras.Sequential([
    #     layers.Conv2D(input_shape=(64, 64, 1), filters=32, kernel_size=(3, 3), strides=(1, 1),
    #                   padding='same', activation='relu'),
    #     layers.MaxPool2D(pool_size=(2, 2), padding='same'),
    #     layers.Conv2D(filters=64, kernel_size=(3, 3), padding='same'),
    #     layers.MaxPool2D(pool_size=(2, 2), padding='same'),
    #     layers.Flatten(),
    #     layers.Dense(512, activation='relu'),
    #     layers.Dropout(0.2),
    #     layers.Dense(300, activation='softmax')
    # ])
    return model


# 编译模型
new_model = create_model()
# new_model.set_weights(weights)
Adam = tf.keras.optimizers.Adam(lr=0.001, beta_1=0.9, beta_2=0.999,
                                epsilon=None, decay=0.0, amsgrad=False)
new_model.compile(optimizer=Adam,
                  loss='categorical_crossentropy',
                  metrics=['accuracy'])

history = new_model.fit_generator(
    generator=train_data_gen,
    steps_per_epoch=142986 // batch_size,  # 142986 71459
    epochs=epochs,
    validation_data=val_data_gen,
    validation_steps=35823 // batch_size,  # 35823 17905
    verbose=1,
    callbacks=[DetailsControl(), TrainingMonitor(jsonPath=jsonPath), reduce_lr]
    # callbacks=[DetailsControl(), early_stop, TrainingMonitor(jsonPath=jsonPath), reduce_lr]
)


