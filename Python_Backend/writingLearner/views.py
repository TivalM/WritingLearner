import _thread
import base64
import io
import json

from django.http import HttpResponse, response
from PIL import Image
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST
from writingLearner.model.predict import PredictModel
from writingLearner.similarity import SimilarityThread


def index(request):
    return HttpResponse("Hello, world. You're at the writingLearner index.")


@csrf_exempt
@require_POST
def recognize_character(request, user_id=1):
    """收到Bytes图片，返回识别到的字符"""
    received_json_data = json.loads(request.body)
    img_writing_string = received_json_data['img_written']
    img_target_string = received_json_data['img_target']

    bytes_img_writing = base64.b64decode(img_writing_string)
    bytes_img_target = base64.b64decode(img_target_string)

    thread = SimilarityThread(bytes_img_target, bytes_img_writing)
    thread.start()
    # 保存图片
    _thread.start_new_thread(save_image, (bytes_img_writing, bytes_img_target))
    # 图片预测
    model = PredictModel()
    reco_char = model.predict_char(bytes_img_writing)
    thread.join()
    # 判断图片相似度
    grade = thread.get_result()
    data = {"char": reco_char, "similarity": grade}
    in_json = json.dumps(data)
    return HttpResponse(in_json)


def save_image(img_writing, img_target):
    img1 = Image.open(io.BytesIO(img_writing))
    img1.save('static\\writing.png')
    img2 = Image.open(io.BytesIO(img_target))
    img2.save('static\\target.png')
