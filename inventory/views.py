from django.core.management import call_command
from rest_framework import permissions, status, viewsets
from rest_framework.decorators import action
from rest_framework.response import Response
from .models import Product
from .permissions import ProductRolePermission
from .serializers import ProductSerializer


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