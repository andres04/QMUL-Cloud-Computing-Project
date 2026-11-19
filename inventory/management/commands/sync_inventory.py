import requests
from django.core.management.base import BaseCommand
from inventory.models import Product


class Command(BaseCommand):
    help = 'Sync inventory from external API'

    def handle(self, *args, **kwargs):
        url = 'https://birkinbagstock.com/api/v1/inventory'
        headers = {
            'Accept': 'application/json',
            'User-Agent': 'DjangoInventorySync/1.0',
        }

        self.stdout.write('Fetching inventpry from BirkingBagStock...')
        response = requests.get(url, params={'limit':100}, headers=headers, timeout=30)
        response.raise_for_status()
        results = response.json().get('results', [])

        created_count = 0
        updated_count = 0

        for item in results:
            ext_id = item.get('id')
            if not ext_id:
                continue

            image_val = item.get('image_proxy_url') or item.get('image_url') or ''
            _, created = Product.objects.update_or_create(
                external_id=ext_id,
                defaults={
                    'title': item.get('title', ''),
                    'size': item.get('size'),
                    'color': item.get('color'),
                    'leather': item.get('leather'),
                    'condition': item.get('condition'),
                    'price': item.get('price'),
                    'image': image_val,
                }
            )
            if created:
                created_count += 1
            else:
                updated_count += 1
        self.stdout.write(
            self.style.SUCCESS(f'Successfully synced inventory: {created_count} created, {updated_count} updated'))