import datetime

time = datetime.datetime.now()
time2 = datetime.datetime.now() + datetime.timedelta(hours=8)
print((time2 - time).seconds)
