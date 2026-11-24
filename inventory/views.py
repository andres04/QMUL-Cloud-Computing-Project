from django.core.management import call_command
from rest_framework import permissions, status, viewsets
from rest_framework.decorators import action
from rest_framework.response import Response
from .models import Product, Cart, CartItem
from .permissions import ProductRolePermission
from .serializers import ProductSerializer, CartSerializer
from django.shortcuts import get_object_or_404



class ProductViewSet(viewsets.ModelViewSet):
    queryset = Product.objects.all().order_by('-updated_at')
    serializer_class = ProductSerializer
    permission_classes = [ProductRolePermission]

    filterset_fields = ['size', 'color', 'leather', 'condition']
    search_fields = ['title','color','condition']   
    ordering_fields = ['price', 'size', 'created_at']

    def perform_create(self, serializer):
        serializer.save(owner=self.request.user)

    @action(detail=False, methods=['post'], permission_classes=[permissions.IsAdminUser])
    def sync(self, request):
        call_command('sync_inventory')
        return Response(
            {'status': 'synced', 'total_products': Product.objects.count()},
            status=status.HTTP_200_OK
        )

class CartViewSet(viewsets.ViewSet):
    permission_classes = [permissions.IsAuthenticated]

    def _get_cart(self, user):
        cart, _ = Cart.objects.get_or_create(user=user)
        return cart

    def list(self, request):
        cart = self._get_cart(request.user)
        serializer = CartSerializer(cart)
        return Response(serializer.data)

    @action(detail=False, methods=['post'])
    def add(self, request):
        product_id = request.data.get('product_id')
        quantity = int(request.data.get('quantity', 1))

        if not product_id or quantity < 1:
            return Response({'error': 'Valid product-id and quantity >=1 are required.'}, status=status.HTTP_400_BAD_REQUEST)

        product = get_object_or_404(Product, id=product_id)        
        cart = self._get_cart(request.user)

        item, created = CartItem.objects.get_or_create(cart=cart, product=product, defaults={'quantity': quantity})
        if not created:
            item.quantity += quantity
            item.save()
        return Response(CartSerializer(cart).data, status = status.HTTP_200_OK)


    @action(detail = False, methods = ['patch'], url_path = 'update-item')
    def update_item(self, request):
        product_id = request.data.get('product_id')
        quantity = int(request.data.get('quantity', 1))

        cart = self._get_cart(request.user)
        item = get_object_or_404(CartItem, cart=cart, product_id=product_id)

        if quantity <= 0:
            item.delete()
        else:
            item.quantity = quantity
            item.save()

        return Response(CartSerializer(cart).data, status=status.HTTP_200_OK)

    @action(detail = False, methods = ['delete'], url_path='remove/(?P<product_id>[^/.]+)')
    def remove_item(self, request, product_id=None):
        cart = self._get_cart(request.user)
        item = get_object_or_404(CartItem, cart=cart, product_id = product_id)
        item.delete()

        return Response(CartSerializer(cart).data, status=status.HTTP_200_OK)

    @action(detail=False, methods=['delete'])
    def clear(self, request):
        cart = self._get_cart(request.user)
        cart.items.all().delete()
        return Response(CartSerializer(cart).data, status=status.HTTP_200_OK)

    

