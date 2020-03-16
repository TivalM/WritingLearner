from tensorflow.keras import models

model = models.load_model('model_700_pre2.h5')
json_config = model.to_json()
with open('model_weights_pre.json', 'w') as json_file:
    json_file.write(json_config)
# Save weights to disk
model.save_weights('pre2_weights.h5')
