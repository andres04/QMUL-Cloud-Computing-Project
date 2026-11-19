from django.db import models
from django.conf import settings

# Create your models here.

class Product(models.Model):
    external_id = models.CharField(
        max_length=100, unique=True, null=True, blank=True
    )
    owner = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='products',
    )


    title = models.CharField(max_length=500)
    size = models.IntegerField(null=True, blank=True)
    color = models.CharField(max_length=255, null=True, blank=True)
    leather =  models.CharField(max_length=255, null=True, blank=True)
    condition = models.CharField(max_length=255, null=True, blank=True)
    price = models.DecimalField(max_digits=12, decimal_places=2, null=True, blank=True)
    image = models.URLField(max_length=1000, blank=True, default='')

    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)


    def __str__(self):
        return f"{self.title} - {self.color or 'N/A'} ({self.size or 'N/A'})"
    