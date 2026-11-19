from rest_framework import permissions
from accounts.models import User

class ProductRolePermission(permissions.BasePermission):
    def has_permission(self, request, view):
        if request.method in permissions.SAFE_METHODS:
            return True

        if not request.user or not request.user.is_authenticated:
            return False

        return request.user.is_superuser or request.user.role in [
            User.Role.SUPERUSER,
            User.Role.MERCHANT,
            User.Role.CUSTOMER,
        ]


    def has_object_permission(self, request, view, obj):
        if request.method in permissions.SAFE_METHODS:
            return True

        if request.user.is_superuser or request.user.role == User.Role.SUPERUSER:
            return True

        return obj.owner == request.user


    
