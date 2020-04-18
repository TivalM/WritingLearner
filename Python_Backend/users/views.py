import datetime
import json

from django.http import HttpResponse
from django.shortcuts import render

# Create your views here.
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST, require_GET, require_http_methods
from users.models import User, Sessions, Character, History


def index(request):
    return HttpResponse("Hello, world. You're at the writingLearner index.")


@csrf_exempt
@require_POST
def register(request):
    received_json_data = json.loads(request.body)
    account = received_json_data['account']
    name = received_json_data['name']
    password = received_json_data['password']
    if User.objects.filter(account=account).exists():
        data = {"state": 1, "description": "Account already exist"}
        in_json = json.dumps(data)
    else:
        new_user = User(account=account, name=name, password=password)
        new_user.save()
        init_json = init_history(new_user.id)
        login_json = login_helper(account, password)
        data = {"state": 0, "description": "Register Success", "init state": init_json, "login state": login_json}
        in_json = json.dumps(data)
    return HttpResponse(in_json)


@csrf_exempt
@require_GET
def login(request):
    account = request.GET.get('account')
    password = request.GET.get('password')
    return HttpResponse(login_helper(account, password))


def login_helper(account, password):
    user = User.objects.filter(account=account)
    if user.exists() and user[0].password == password:
        cookie = hash(account)
        login_time = datetime.datetime.now()
        old_session = Sessions.objects.filter(key=account)
        if not old_session.exists():
            # create session
            session = Sessions(key=account, data=cookie, updated_time=login_time)
            session.save()
        else:
            # update Session
            session = Sessions.objects.get(key=account)
            session.data = cookie
            session.updated_time = login_time
            session.save()
        data = {"state": 0, "cookie": cookie, "description": "Login in Success"}
        in_json = json.dumps(data)
    elif not user.exists():
        data = {"state": 1, "description": "Account not exist"}
        in_json = json.dumps(data)
    elif not user[0].password == password:
        data = {"state": 1, "description": "Password not right"}
        in_json = json.dumps(data)
    else:
        data = {"state": 1, "description": "Error unknown"}
        in_json = json.dumps(data)
    return in_json


@csrf_exempt
@require_GET
def logout(request):
    result = authentic(request)
    if result is not True:
        return result
    account = request.GET.get('account')
    try:
        session = Sessions.objects.get(key=account)
        session.delete()
        data = {"state": 0, "description": "logout success"}
        in_json = json.dumps(data)
        return HttpResponse(in_json)
    except Sessions.DoesNotExist:
        data = {"state": 1, "description": "session doesn't exist"}
        in_json = json.dumps(data)
        return HttpResponse(in_json)


@require_http_methods(["GET", "POST"])
def authentic(request):
    if request.method == 'GET':
        account = request.GET.get('account')
        cookie = request.GET.get('cookie')
    elif request.method == 'POST':
        received_json_data = json.loads(request.body)
        print(type(received_json_data))
        account = received_json_data["account"]
        cookie = received_json_data["cookie"]
    try:
        session = Sessions.objects.get(key=account, data=cookie)
        time_now = datetime.datetime.now()
        time_last = session.updated_time.replace(tzinfo=None) + datetime.timedelta(hours=8)
        print(time_now, time_last)
        print(time_now - time_last)
        if (time_now - time_last).seconds < 3600:
            session.updated_time = time_now
            session.save()
            return True
        else:
            session.delete()
            data = {"state": 1, "description": "Login state timeout"}
            in_json = json.dumps(data)
            return HttpResponse(in_json)
    except Sessions.DoesNotExist:
        data = {"state": 1, "description": "Authentication failed"}
        in_json = json.dumps(data)
        return HttpResponse(in_json)


@require_GET
def get_history(request):
    result = authentic(request)
    if result is not True:
        return result
    received_json_data = result
    data = {"state": 0, "description": "data"}
    in_json = json.dumps(data)
    return HttpResponse(in_json)


def init_history(user_id):
    char_set = Character.objects.all().all()
    user = User.objects.all().get(id=user_id)
    for c in char_set:
        history = History(belongs_to_user=user, related_to_char=c, learning_state="NL")
        history.save()
    data = {"state": 0, "description": "Init Success"}
    in_json = json.dumps(data)
    return in_json

 