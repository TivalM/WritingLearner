from django.http import HttpResponse
from django.urls import path

from . import views

urlpatterns = {
    path('', views.index, name='index'),
    path('register/', views.register, name='register'),
    path('login/', views.login, name='login'),
    path('logout/', views.logout, name='logout'),
    path('get_history/', views.get_history, name='get_history'),
    path('init_history/', views.init_history, name='init_history')
}
