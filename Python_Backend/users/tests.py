import json
from users.models import History, User

from django.core import serializers
user = User.objects.get(id=5)
histories = History.objects.filter(belongs_to_user=user) \
    .exclude(learning_state="NL")\
    .values(
    "related_to_char__id",
    "related_to_char__itself",
    "learning_state")
print(list(histories[:3]))
print(serializers.serialize("json", histories))
print(histories.__len__())
#            .exclude(learning_state="NL")\
# character_id, learning_state
