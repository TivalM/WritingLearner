import _thread
import io
import os

from django.shortcuts import render

from django.http import HttpResponse, response
from PIL import Image
from io import BytesIO
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST

from writingLearner.model.predict import PredictModel


def index(request):
    return HttpResponse("Hello, world. You're at the writingLearner index.")


@csrf_exempt
@require_POST
def recognize_character(request, user_id=1):
    """收到Bytes图片，返回识别到的字符"""
    bytes_stream = request.body
    # 保存图片
    _thread.start_new_thread(save_image, (bytes_stream,))
    model = PredictModel()
    response = model.predict_char(bytes_stream)

    return HttpResponse(response)


def save_image(bytes_stream):
    img = Image.open(io.BytesIO(bytes_stream))
    img.save('static\\test.png')
