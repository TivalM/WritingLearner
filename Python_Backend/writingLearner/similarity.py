from io import StringIO
from threading import Thread

from PIL import Image
from numpy import average, dot, linalg
from io import BytesIO


class SimilarityThread(Thread):

    def __init__(self, target, writing):
        Thread.__init__(self)
        self.target = target
        self.writing = writing

    def run(self):
        self.result = similarity_calculator(self.target, self.writing)

    def get_result(self):
        return self.result


def similarity_calculator(target, writing):
    # 图片二值化
    img_target = Image.open(BytesIO(target))
    img_writing = Image.open(BytesIO(writing))
    img_target = img_target.convert('1')
    img_writing = img_writing.convert('1')

    images = [img_target, img_writing]
    vectors = []
    norms = []
    for image in images:
        vector = []
        # print(image.getdata())
        for pixel_tuple in image.getdata():
            vector.append(average(pixel_tuple))
        # print(vector.__len__(), vector)
        vectors.append(vector)
        norms.append(linalg.norm(vector, 2))
    a, b = vectors
    a_norm, b_norm = norms
    res = dot(a, b) / (a_norm * b_norm)

    return res
