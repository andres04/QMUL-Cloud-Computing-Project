from rest_framework import serializers
from .models import Product

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