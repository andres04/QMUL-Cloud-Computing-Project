from rest_framework import serializers
from .models import Product
from .models import Cart, CartItem

class ProductSerializer(serializers.ModelSerializer):
    owner_username = serializers.ReadOnlyField(source='owner.username')
    owner_role = serializers.ReadOnlyField(source='owner.role')

    class Meta:
        model = Product
        fields = ['id', 
                  'external_id', 
                  'owner', 
                  'owner_username', 
                  'owner_role',
                  'title',
                  'size',
                  'color',
                  'leather',
                  'condition',
                  'price',
                  'image',
                  'created_at',
                  'updated_at',
                  ]
        read_only_fields = ['id', 'external_id', 'owner', 'created_at', 'updated_at']


class CartItemSerializer(serializers.ModelSerializer):
    product_title = serializers.ReadOnlyField(source='product.title')
    product_price = serializers.ReadOnlyField(source='product.price')
    product_image = serializers.ReadOnlyField(source='product.image')
    subtotal = serializers.ReadOnlyField()

    class Meta:
        model = CartItem
        fields = ['id', 'product', 'product_title', 'product_price', 'product_image', 'quantity', 'subtotal']

class CartSerializer(serializers.ModelSerializer):
    items = CartItemSerializer(many=True, read_only=True)
    total_price = serializers.ReadOnlyField()
    total_items = serializers.ReadOnlyField()

    class Meta:
        model = Cart
        fields = ['id', 'user', 'total_items', 'total_price', 'items', 'updated_at']

        read_only_fields = ['id', 'user', 'updated_at']
