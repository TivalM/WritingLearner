from django.db import models


# class Character(models.Model):
#     character_id = models.IntegerField(max_length=5, null=False)
#     character = models.CharField(max_length=1, null=False)
#     strokes = models.


class User(models.Model):
    user_Id = models.AutoField(null=False, primary_key=True, auto_created=True)
    user_account = models.CharField(max_length=20, null=False)
    user_name = models.CharField(max_length=20)
    user_password = models.CharField(max_length=20, null=False)

    def __str__(self):
        return "\nName:" + self.user_name + "\nId:" + self.user_Id.__str__() \
               + "\nAccount:" + self.user_account + "\nPassword" + self.user_password
