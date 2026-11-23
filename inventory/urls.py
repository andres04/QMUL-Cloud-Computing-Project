from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import ProductViewSet, CartViewSet


router = DefaultRouter()
router.register(r'', ProductViewSet, basename='product')
router.register(r'cart', CartViewSet, basename='cart')
urlpatterns = [
    path('', include(router.urls)),
]