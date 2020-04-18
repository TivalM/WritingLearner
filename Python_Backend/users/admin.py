from django.contrib import admin

# Register your models here.
from users.models import User, Character, History

admin.site.register(User)
admin.site.register(Character)
admin.site.register(History)