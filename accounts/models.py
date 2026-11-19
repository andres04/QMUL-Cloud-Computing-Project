from django.db import models
from django.contrib.auth.models import AbstractUser, Group
# Create your models here.

class User(AbstractUser):
    class Role(models.TextChoices):
        CUSTOMER = 'customer', 'Customer'
        MERCHANT = 'merchant', 'Merchant'
        SUPERUSER = 'superuser', 'Superuser'

    role = models.CharField(
        max_length=20,
        choices=Role.choices,
        default=Role.CUSTOMER,
    )

    def save(self, *args, **kwargs):
        if self.is_superuser:
            self.role = self.Role.SUPERUSER
        elif self.role == self.Role.SUPERUSER:
            self.is_superuser = True
            self.is_staff = True
        super().save(*args, **kwargs)


        group, _ = Group.objects.get_or_create(name=self.role)
        self.groups.set([group])


