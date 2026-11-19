from django.contrib import admin
from django.utils.html import format_html
from accounts.models import User
from .models import Product


@admin.register(Product)
class ProductAdmin(admin.ModelAdmin):
    list_display = (
        'id',
        'image_preview',
        'title',
        'size',
        'color',
        'leather',
        'condition',
        'formatted_price',
        'owner',
    )
    list_display_links = ('id', 'title')
    list_editable = ('owner',)  # Lets superuser reassign merchants directly from the list table
    list_filter = ('owner', 'size', 'color', 'leather', 'condition')
    search_fields = ('title', 'color', 'leather', 'owner__username')
    list_per_page = 33  # Matches your 33-per-merchant split cleanly!
    ordering = ('id',)

    def formfield_for_foreignkey(self, db_field, request, **kwargs):
        # Order owner dropdown cleanly by role and username
        if db_field.name == 'owner':
            kwargs['queryset'] = User.objects.order_by('role', 'username')
        return super().formfield_for_foreignkey(db_field, request, **kwargs)

    @admin.display(description='Photo')
    def image_preview(self, obj):
        if obj.image:
            return format_html(
                '<img src="{}" style="width:48px; height:48px; object-fit:cover; border-radius:4px;" />',
                obj.image,
            )
        return '—'

    @admin.display(description='Price', ordering='price')
    def formatted_price(self, obj):
        if obj.price is not None:
            return f"${obj.price:,.2f}"
        return 'N/A'