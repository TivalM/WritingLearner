import pickle
from users.models import Character
f = open('writingLearner/model/src/char_dict', 'br')
dict = pickle.load(f)
chars_used = [v for v in sorted(dict.keys())][:600]
# ['一', '丁', '七', '万', '丈', ... ,]
for i in range(0, 600):
    char = Character(id=i + 1, itself=chars_used[i])
    char.save()
