from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from django.db.models import Count
from .models import User


@admin.register(User)
class UserAdmin(BaseUserAdmin):
    list_display = ('username', 'email', 'role_badge', 'product_count', 'is_superuser', 'is_active')
    list_filter = ('role', 'is_superuser', 'is_active')
    search_fields = ('username', 'email')
    ordering = ('role', 'id')

    # Add the custom 'role' field to the user edit & creation forms in Admin
    fieldsets = BaseUserAdmin.fieldsets + (
        ('Role & Permissions', {'fields': ('role',)}),
    )
    add_fieldsets = BaseUserAdmin.add_fieldsets + (
        ('Role & Permissions', {'fields': ('role',)}),
    )

    def get_queryset(self, request):
        return super().get_queryset(request).annotate(_product_count=Count('products'))

    @admin.display(description='Products Owned', ordering='_product_count')
    def product_count(self, obj):
        return obj._product_count

    @admin.display(description='Role', ordering='role')
    def role_badge(self, obj):
        colors = {
            'superuser': '#dc2626',  # Red
            'merchant': '#2563eb',   # Blue
            'customer': '#16a34a',   # Green
        }
        color = colors.get(obj.role, '#6b7280')
        from django.utils.html import format_html
        return format_html(
            '<span style="background:{}; color:white; padding:3px 8px; border-radius:4px; font-weight:600;">{}</span>',
            color,
            obj.get_role_display(),
        )