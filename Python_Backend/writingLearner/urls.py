from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('recognize_char/', views.recognize_character, name='recognize')
]
