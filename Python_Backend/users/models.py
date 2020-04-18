import datetime

from django.db import models


# Create your models here.
class User(models.Model):
    id = models.AutoField(null=False, primary_key=True, auto_created=True)
    account = models.CharField(max_length=20, null=False)
    name = models.CharField(max_length=20)
    password = models.CharField(max_length=20, null=False)

    def __str__(self):
        return "\nName:" + self.name + "\nId:" + self.id.__str__() \
               + "\nAccount:" + self.account + "\nPassword" + self.password


class Character(models.Model):
    id = models.AutoField(null=False, primary_key=True, auto_created=True)
    itself = models.CharField(max_length=1, null=False)

    def __str__(self):
        return "\nId:" + self.id.__str__() + " Itself:" + self.itself.__str__()


class History(models.Model):
    LEARNING = "LR"
    NOTLEARN = "NL"
    FINISHED = "FD"
    STATE_CHOICES = [
        (LEARNING, "learning"),
        (NOTLEARN, "haven't learned"),
        (FINISHED, "finished"),
    ]
    id = models.AutoField(null=False, primary_key=True, auto_created=True)
    belongs_to_user = models.ForeignKey(User, on_delete=models.CASCADE)
    related_to_char = models.ForeignKey(Character, on_delete=models.CASCADE)
    learning_state = models.CharField(
        max_length=2,
        choices=STATE_CHOICES,
        default=NOTLEARN
    )

    def __str__(self):
        return "\nHistory_Id:" + self.id.__str__() + \
               "\nUser: " + self.belongs_to_user.__str__() + \
               "\nChar: " + self.related_to_char.__str__()


class Sessions(models.Model):
    key = models.CharField(max_length=20, null=False, primary_key=True)
    data = models.CharField(max_length=20, null=False)
    updated_time = models.DateTimeField()
