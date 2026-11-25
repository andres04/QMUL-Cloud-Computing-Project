from decimal import Decimal
from django.test import TestCase
from django.urls import reverse
from django.contrib.auth import get_user_model


from rest_framework.test import APIClient
from rest_framework import status

from inventory.models import Product, Cart, CartItem
from inventory.serializers import ProductSerializer, CartSerializer

User = get_user_model()


class ProductModelTest(TestCase):
    #  tests product mmodel

    def setUp(self):
        self.merchant = User.objects.create_user(
            username='merchant123',
            email='merchant123@example.com',
            password='password123',
            role=User.Role.MERCHANT
        )
        self.product = Product.objects.create(
            title='Leather Bag',
            owner=self.merchant,
            size=42,
            color='Black',
            leather='Genuine',
            condition='New',
            price=Decimal('67.67'),
            image='https://example.com/bag.jpg'
        )

    def test_product_creation(self):
        #  test product created with correct fields
        self.assertEqual(self.product.title, 'Leather Bag')
        self.assertEqual(self.product.owner, self.merchant)
        self.assertEqual(self.product.size, 42)
        self.assertEqual(self.product.color, 'Black')
        self.assertEqual(self.product.price, Decimal('67.67'))

    def test_product_string(self):
        # test the product string

        expected = "Leather Bag - Black (42)"
        self.assertEqual(str(self.product), expected)


    def test_product_str_with_missing_fields(self):
        # test string when missing fields
        product_no_optional = Product.objects.create(
            title='Product',
            owner=self.merchant
        )
        expected = "Product - N/A (N/A)"

        self.assertEqual(str(product_no_optional), expected)

    def test_product_owner(self):
        # test owner
        self.assertEqual(self.merchant.products.count(), 1)
        self.assertIn(self.product, self.merchant.products.all())

    def test_product_optional_fields(self):
        #  test optional fields null
        product = Product.objects.create(
            title='Product missing',
            owner=self.merchant
        )
        self.assertIsNone(product.size)
        self.assertIsNone(product.color)
        self.assertIsNone(product.leather)
        self.assertIsNone(product.condition)
        self.assertIsNone(product.price)

    def test_external_id_unique(self):
        #  test external_id unique
        Product.objects.create(
            title='Ext Product 1',
            owner=self.merchant,
            external_id='EXT-001'
        )

        with self.assertRaises(Exception):
            Product.objects.create(
                title='Ext Product 2',
                owner=self.merchant,
                external_id='EXT-001'
            )

    def test_product_timestamps(self):
        #  test timestamps
        self.assertIsNotNone(self.product.created_at)
        self.assertIsNotNone(self.product.updated_at)

    def test_product_default_image(self):
        #  test default image

        product = Product.objects.create(
            title='No Image Product',
            owner=self.merchant
        )
        self.assertEqual(product.image, '')


class ProductSerializerTest(TestCase):
    #  tests product serializer

    def setUp(self):
        self.merchant = User.objects.create_user(
            username='merchant123',
            email='merchant123@example.com',
            password='password123',
            role=User.Role.MERCHANT
        )
        self.product = Product.objects.create(
            title='Test Product',
            owner=self.merchant,
            size=38,
            color='Red',
            price=Decimal('67.67')
        )

    def test_product_serialization(self):
        #  test product serialization

        serializer = ProductSerializer(self.product)
        data = serializer.data
        self.assertEqual(data['title'], 'Test Product')
        self.assertEqual(data['size'], 38)
        self.assertEqual(data['color'], 'Red')
        self.assertEqual(data['owner_username'], 'merchant123')
        self.assertEqual(data['owner_role'], User.Role.MERCHANT)

    def test_product_creation(self):
        #  test product creation
        data = {
            'title': 'New Product',
            'size': 40,
            'color': 'Blue',
            'price': Decimal('67.67')
        }
        serializer = ProductSerializer(data=data)
        self.assertTrue(serializer.is_valid())
        product = serializer.save()
        self.assertEqual(product.title, 'New Product')

    def test_read_only_fields(self):
        # test read only not set

        data = {
            'id': 999,
            'title': 'Bad Product',
            'external_id': 'FAKE-EXT',
            'owner': self.merchant.id,
            'size': 40
        }
        serializer = ProductSerializer(data=data)
        self.assertTrue(serializer.is_valid())
        product = serializer.save()
        self.assertNotEqual(product.id, 999)
        self.assertIsNone(product.external_id)



class ProductViewSetTest(TestCase):
    # test ProductViewSet api

    def setUp(self):
        self.client: APIClient = APIClient()
        self.url = reverse('product-list')
        self.merchant = User.objects.create_user(
            username='merchant123',
            email='merchant123@example.com',
            password='password123',
            role=User.Role.MERCHANT
        )
        self.customer = User.objects.create_user(
            username='customer123',
            email='customer123@example.com',
            password='password123',
            role=User.Role.CUSTOMER
        )
        self.admin = User.objects.create_superuser(
            username='admin',
            email='admin@example.com',
            password='adminpassword123'
        )
        self.product = Product.objects.create(
            title='Merchant Product',
            owner=self.merchant,
            size=42,
            color='Black',
            price=Decimal('67.67')
        )

    def test_list_products_without_authent(self):
        #  test list products without authentication
        response = self.client.get(self.url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_list_products_customer(self):
        #  test list products as a customer
        self.client.force_authenticate(user=self.customer)
        response = self.client.get(self.url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_create_product_merchant(self):
        # test creating product as merchant
        self.client.force_authenticate(user=self.merchant)
        data = {
            'title': 'New Merchant Product',
            'size': 38,
            'color': 'Red',
            'price': Decimal('67.67')
        }
        response = self.client.post(self.url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(Product.objects.filter(title='New Merchant Product').count(), 1)

    def test_create_product_sets_owner(self):
        #  test owner set
        self.client.force_authenticate(user=self.customer)
        data = {
            'title': 'Customer Product',
            'size': 40
        }
        response = self.client.post(self.url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        product = Product.objects.get(title='Customer Product')
        self.assertEqual(product.owner, self.customer)

    def test_create_product_without_authent(self):
        #  test need to be authenticated to create product
        data = {
            'title': 'Anonymous Product',
            'size': 40
        }
        response = self.client.post(self.url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    def test_retrieve_product_detail(self):
        #  test retrieving  single product by ID
        url = reverse('product-detail', kwargs={'pk': self.product.pk})
        response = self.client.get(url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['title'], 'Merchant Product')

    def test_update_product_owner(self):
        #  test product owner can update
        url = reverse('product-detail', kwargs={'pk': self.product.pk})
        self.client.force_authenticate(user=self.merchant)
        data = {
            'title': 'Updated Product',
            'size': 44,
            'color': 'Blue',
            'price': Decimal('69.99')
        }
        response = self.client.put(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.product.refresh_from_db()
        self.assertEqual(self.product.title, 'Updated Product')

    def test_cannot_update_other_users_product(self):
        #  test user cannot update anothers product
        url = reverse('product-detail', kwargs={'pk': self.product.pk})
        self.client.force_authenticate(user=self.customer)
        data = {
            'title': 'Hacked Product',
            'size': 44,
            'color': 'Blue',
            'price': Decimal('69.99')
        }
        response = self.client.put(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)

    def test_delete_product_owner(self):
        #  test product owner can delete there product
        url = reverse('product-detail', kwargs={'pk': self.product.pk})
        self.client.force_authenticate(user=self.merchant)
        response = self.client.delete(url)
        self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)
        self.assertFalse(Product.objects.filter(pk=self.product.pk).exists())

    def test_cannot_delete_other_users_product(self):
        #  test user cannot delete anothers product
        url = reverse('product-detail', kwargs={'pk': self.product.pk})
        self.client.force_authenticate(user=self.customer)
        response = self.client.delete(url)
        self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)

    def test_admin_can_update_any_product(self):
        #  test admin can update any product

        url = reverse('product-detail', kwargs={'pk': self.product.pk})
        self.client.force_authenticate(user=self.admin)
        data = {
            'title': 'Admin Updated',
            'size': 44,
            'color': 'Blue',
            'price': Decimal('67.67')
        }
        response = self.client.put(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_admin_can_delete_any_product(self):
        #  test admin can delete any product
        url = reverse('product-detail', kwargs={'pk': self.product.pk})
        self.client.force_authenticate(user=self.admin)
        response = self.client.delete(url)
        self.assertEqual(response.status_code, status.HTTP_204_NO_CONTENT)

    def test_search_products(self):
        #  test searching products by title
        Product.objects.create(
            title='Red Shoes',
            owner=self.merchant,
            color='Red'
        )
        response = self.client.get(self.url, {'search': 'Shoes'})
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertGreaterEqual(len(response.data['results']), 1)

    def test_filter_products_by_color(self):
        #  test filtering products by color
        Product.objects.create(
            title='Blue Item',
            owner=self.merchant,
            color='Blue'
        )
        response = self.client.get(self.url, {'color': 'Blue'})
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertGreaterEqual(len(response.data['results']), 1)

    def test_ordering_products_by_price(self):
        #  test ordering products by price
        response = self.client.get(self.url, {'ordering': 'price'})
        self.assertEqual(response.status_code, status.HTTP_200_OK)


    def test_sync_action_admin(self):
        #  test sync action as admin
        sync_url = reverse('product-sync')
        self.client.force_authenticate(user=self.admin)
        response = self.client.post(sync_url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['status'], 'synced')

# here !!!


class ProductRolePermissionsTest(TestCase):
    #  test product role permissions

    def setUp(self):
        self.client: APIClient = APIClient()
        self.merchant = User.objects.create_user(
            username='merchant123',
            email='merchant123@example.com',
            password='password123',
            role=User.Role.MERCHANT
        )
        self.customer = User.objects.create_user(
            username='customer123',
            email='customer123@example.com',
            password='password123',
            role=User.Role.CUSTOMER
        )
        self.product = Product.objects.create(
            title='Test Product',
            owner=self.merchant,
            size=42,
            color='Black',
            price=Decimal('67.67')
        )

    def test_get_requests(self):
        #  test anyone can do get requests
        url = reverse('product-list')
        response = self.client.get(url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_unauthenticated_cannot_create(self):
        #  test unauthenticated users cannot create products

        url = reverse('product-list')
        data = {'title': 'No Authenticated Product'}
        response = self.client.post(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)


    def test_merchant_create(self):
        #  test that merchant create products
        self.client.force_authenticate(user=self.merchant)
        url = reverse('product-list')
        data = {'title': 'Merchant Created'}
        response = self.client.post(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)

    def test_customer_create(self):
        #  test customer create products
        self.client.force_authenticate(user=self.customer)
        url = reverse('product-list')
        data = {'title': 'Customer Created'}
        response = self.client.post(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)

    def test_modify_own_product(self):
        #  test product owner modify their own product
        self.client.force_authenticate(user=self.merchant)
        url = reverse('product-detail', kwargs={'pk': self.product.pk})
        data = {'title': 'Modified by Owner', 'size': 42, 'color': 'Black', 'price': Decimal('67.67')}
        response = self.client.put(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_non_owner_cannot_modify(self):
        #  test non owner cannot modify product

        self.client.force_authenticate(user=self.customer)
        url = reverse('product-detail', kwargs={'pk': self.product.pk})
        data = {'title': 'Hacked', 'size': 42, 'color': 'Black', 'price': Decimal('67.67')}
        response = self.client.put(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)



class CartTest(TestCase):
    # tests the cart

    def setUp(self):
        self.customer = User.objects.create_user(
            username='customer123',
            email='customer123@example.com',
            password='password123',
            role=User.Role.CUSTOMER
        )
        self.product = Product.objects.create(
            title='Leather Bag',
            price=Decimal('67.67')
        )
        self.cart = Cart.objects.create(user=self.customer)

    def test_cart_totals(self):
        # test price and total items

        CartItem.objects.create(cart=self.cart, product=self.product, quantity=2)

        self.assertEqual(self.cart.total_items, 2)
        self.assertEqual(self.cart.total_price, Decimal('135.34'))

    def test_cart_string(self):
        # test cart string
        self.assertEqual(str(self.cart), 'Cart of customer123')


    def test_cart_subtotal(self):
        # test item subtotals

        item = CartItem.objects.create(cart=self.cart, product=self.product, quantity=3)

        self.assertEqual(item.subtotal, Decimal('203.01'))
        self.assertEqual(str(item), '3 x Leather Bag')



class CartSerializerTest(TestCase):
    # tests cart serializer

    def setUp(self):
        self.customer = User.objects.create_user(
            username='customer123',
            email='customer123@example.com',
            password='password123',
            role=User.Role.CUSTOMER
        )
        self.product = Product.objects.create(
            title='Leather Bag',
            price=Decimal('67.67'),
            image='https://example.com/bag.jpg'
        )
        self.cart = Cart.objects.create(user=self.customer)
        CartItem.objects.create(cart=self.cart, product=self.product, quantity=2)


    def test_cart_serialization(self):
        # test cart serialization

        data = CartSerializer(self.cart).data

        self.assertEqual(data['user'], self.customer.pk)
        self.assertEqual(data['total_items'], 2)
        self.assertEqual(data['total_price'], Decimal('135.34'))
        self.assertEqual(data['items'][0]['product'], self.product.pk)
        self.assertEqual(data['items'][0]['product_title'], 'Leather Bag')
        self.assertEqual(data['items'][0]['product_price'], Decimal('67.67'))
        self.assertEqual(data['items'][0]['product_image'], 'https://example.com/bag.jpg')
        self.assertEqual(data['items'][0]['quantity'], 2)
        self.assertEqual(data['items'][0]['subtotal'], Decimal('135.34'))


class CartViewTest(TestCase):
    # tests cart view set api

    def setUp(self):
        self.client: APIClient = APIClient()
        self.customer = User.objects.create_user(
            username='customer123',
            email='customer123@example.com',
            password='password123',
            role=User.Role.CUSTOMER
        )
        self.other_customer = User.objects.create_user(
            username='customer456',
            email='customer456@example.com',
            password='password123',
            role=User.Role.CUSTOMER
        )
        self.product = Product.objects.create(
            title='Leather Bag',
            price=Decimal('67.67')
        )
        self.second_product = Product.objects.create(
            title='Red Leather Bag',
            price=Decimal('76.76')
        )
        self.url = reverse('cart-list')


    def test_cart_authentication(self):
        # test need auth to access cart
        response = self.client.get(self.url)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    def test_add_product_to_cart(self):
        # test adding a product creates a cart item
        self.client.force_authenticate(user=self.customer)
        response = self.client.post(
            reverse('cart-add'),
            {'product_id': self.product.pk, 'quantity': 2},
            format='json'
        )
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['total_items'], 2)
        self.assertEqual(response.data['total_price'], Decimal('135.34'))

        self.assertEqual(CartItem.objects.get(cart__user=self.customer, product=self.product).quantity, 2)

    def test_adding_product_multiple_times(self):
        # test adding a product twice
        self.client.force_authenticate(user=self.customer)
        url = reverse('cart-add')
        self.client.post(url, {'product_id': self.product.pk, 'quantity': 2}, format='json')
        response = self.client.post(url, {'product_id': self.product.pk, 'quantity': 3}, format='json')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['total_items'], 5)

        self.assertEqual(CartItem.objects.filter(cart__user=self.customer).count(), 1)


    def test_add_non_product(self):
        # test adding a non existing product returns erorr
        self.client.force_authenticate(user=self.customer)
        response = self.client.post(
            reverse('cart-add'),
            {'product_id': 99999, 'quantity': 1},
            format='json'
        )

        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

    def test_update_cart_item_quantity(self):
        # test updating a cart item quantity
        self.client.force_authenticate(user=self.customer)
        self.client.post(
            reverse('cart-add'),
            {'product_id': self.product.pk, 'quantity': 2},
            format='json'
        )
        response = self.client.patch(
            reverse('cart-update-item'),
            {'product_id': self.product.pk, 'quantity': 4},
            format='json'
        )

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['total_items'], 4)

        self.assertEqual(response.data['total_price'], Decimal('270.68'))

    def test_set_cart_item_to_zero_removes_it(self):
        # test setting cart item to zero removes it
        self.client.force_authenticate(user=self.customer)
        self.client.post(reverse('cart-add'), {'product_id': self.product.pk}, format='json')
        response = self.client.patch(
            reverse('cart-update-item'),
            {'product_id': self.product.pk, 'quantity': 0},
            format='json'
        )
        self.assertEqual(response.status_code, status.HTTP_200_OK)

        self.assertEqual(response.data['total_items'], 0)
        self.assertFalse(CartItem.objects.filter(cart__user=self.customer).exists())


    def test_remove_cart_item(self):
        # tests removing cart item
        self.client.force_authenticate(user=self.customer)
        self.client.post(reverse('cart-add'), {'product_id': self.product.pk}, format='json')
        response = self.client.delete(
            reverse('cart-remove-item', kwargs={'product_id': self.product.pk})
        )
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['total_items'], 0)
        self.assertFalse(CartItem.objects.filter(cart__user=self.customer).exists())


    def test_users_only_access_their_cart(self):
        # test cart items linked to user
        cart = Cart.objects.create(user=self.customer)
        CartItem.objects.create(cart=cart, product=self.product)
        self.client.force_authenticate(user=self.other_customer)
        response = self.client.delete(
            reverse('cart-remove-item', kwargs={'product_id': self.product.pk})
        )

        self.assertEqual(response.status_code, status.HTTP_404_NOT_FOUND)

        self.assertTrue(CartItem.objects.filter(cart=cart, product=self.product).exists())

    def test_clear_cart(self):
        # test clearing cart
        self.client.force_authenticate(user=self.customer)
        self.client.post(reverse('cart-add'), {'product_id': self.product.pk}, format='json')
        self.client.post(reverse('cart-add'), {'product_id': self.second_product.pk}, format='json')

        response = self.client.delete(reverse('cart-clear'))

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['total_items'], 0)
        self.assertEqual(response.data['items'], [])

        self.assertFalse(CartItem.objects.filter(cart__user=self.customer).exists())


