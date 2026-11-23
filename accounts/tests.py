from django.test import TestCase
from django.urls import reverse
from django.contrib.auth import get_user_model


from rest_framework.test import APIClient
from rest_framework import status

from accounts.serializers import RegisterSerializer
from accounts.serializers import UserSerializer

User = get_user_model()


class UserModelTest(TestCase):
    #  tests for user model

    def setUp(self):
        self.customer = User.objects.create_user(
            username='customer123',
            email='customer123@example.com',
            password='password123',
            role=User.Role.CUSTOMER
        )
        self.merchant = User.objects.create_user(
            username='merchant123',
            email='merchant123@example.com',
            password='password123',
            role=User.Role.MERCHANT
        )

    def test_user_creation(self):
        #  test user created with correct role

        self.assertEqual(self.customer.role, User.Role.CUSTOMER)
        self.assertEqual(self.merchant.role, User.Role.MERCHANT)

    def test_default_role_is_customer(self):
        #  test that default role is customer

        user = User.objects.create_user(
            username='defaultuser',
            email='default@example.com',
            password='password123'
        )
        self.assertEqual(user.role, User.Role.CUSTOMER)

    def test_superuser_sets_role_to_superuser(self):
        #  test is_superuser sets role to superuser
        admin = User.objects.create_superuser(
            username='admin',
            email='admin@example.com',
            password='password123'
        )
        self.assertEqual(admin.role, User.Role.SUPERUSER)
        self.assertTrue(admin.is_superuser)
        self.assertTrue(admin.is_staff)


    def test_role_choices(self):
        #  test role choices
        choices = [choice[0] for choice in User.Role.choices]
        self.assertIn(User.Role.CUSTOMER, choices)
        self.assertIn(User.Role.MERCHANT, choices)
        self.assertIn(User.Role.SUPERUSER, choices)

    def test_group_assignment_on_save(self):
        #  test group assignment on save

        self.customer.refresh_from_db()
        group_names = [g.name for g in self.customer.groups.all()]
        self.assertIn('customer', group_names)


class RegisterSerializerTest(TestCase):
    #  tests RegisterSerializer

    def test_valid_registration(self):
        #  test that registration creates a user

        data = {
            'username': 'newuser',
            'email': 'newuser@example.com',
            'password': 'goodpassword123',
            'role': User.Role.CUSTOMER
        }
        serializer = RegisterSerializer(data=data)
        self.assertTrue(serializer.is_valid())
        user = serializer.save()
        self.assertIsInstance(user, User)
        self.assertEqual(user.username, 'newuser')
        self.assertEqual(user.role, User.Role.CUSTOMER)

    def test_password_min_length(self):

        #  test password min length

        data = {
            'username': 'shortpass',
            'email': 'short@example.com',
            'password': '12345',
            'role': User.Role.CUSTOMER
        }
        serializer = RegisterSerializer(data=data)
        self.assertFalse(serializer.is_valid())
        self.assertIn('password', serializer.errors)

    def test_cannot_register_as_superuser(self):
        #  test users cannot register themselves as superuser

        data = {
            'username': 'fakesuper',
            'email': 'fake@example.com',
            'password': 'password123',
            'role': User.Role.SUPERUSER
        }
        serializer = RegisterSerializer(data=data)
        self.assertFalse(serializer.is_valid())
        self.assertIn('role', serializer.errors)

    def test_duplicate_username(self):
        #  test duplicate username is rejected

        User.objects.create_user(
            username='existing',
            email='existing@example.com',
            password='password123'
        )
        data = {
            'username': 'existing',
            'email': 'another@example.com',
            'password': 'password123',
            'role': User.Role.CUSTOMER
        }
        serializer = RegisterSerializer(data=data)
        self.assertFalse(serializer.is_valid())


class UserSerializerTest(TestCase):
    #  tests UserSerializer

    def test_user_serialization(self):
        #  test user serialization

        user = User.objects.create_user(
            username='serializationuser',
            email='serial@example.com',
            password='password123',
            role=User.Role.MERCHANT
        )
        serializer = UserSerializer(user)
        data = serializer.data
        self.assertEqual(data['username'], 'serializationuser')
        self.assertEqual(data['email'], 'serial@example.com')
        self.assertEqual(data['role'], User.Role.MERCHANT)
        self.assertNotIn('password', data)


class RegisterViewTest(TestCase):
    #  tests RegisterView API

    def setUp(self):
        self.client: APIClient = APIClient()
        self.url = reverse('register')

    def test_register_success(self):
        #  test success
        data = {
            'username': 'apiuser',
            'email': 'api@example.com',
            'password': 'password123',
            'role': User.Role.CUSTOMER
        }
        response = self.client.post(self.url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertTrue(User.objects.filter(username='apiuser').exists())

    def test_register_no_password(self):
        #  test no password

        data = {
            'username': 'nopass',
            'email': 'nopass@example.com',
            'role': User.Role.CUSTOMER
        }
        response = self.client.post(self.url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_register_as_superuser_rejected(self):
        #  test superuser registration rejected
        data = {
            'username': 'hacker',
            'email': 'hacker@example.com',
            'password': 'password123',
            'role': User.Role.SUPERUSER
        }
        response = self.client.post(self.url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_register_short_password_rejected(self):
        #  test short password rejected

        data = {
            'username': 'short',
            'email': 'short@example.com',
            'password': '12345',
            'role': User.Role.CUSTOMER
        }
        response = self.client.post(self.url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)


    def test_register_anyone(self):
        #  test that anyone can register
        data = {
            'username': 'anonuser',
            'email': 'anon@example.com',
            'password': 'password123',
            'role': User.Role.CUSTOMER
        }
        response = self.client.post(self.url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)


class UserManagementViewSetTest(TestCase):
    #  test UserManagementViewSet api

    def setUp(self):
        self.client: APIClient = APIClient()
        self.url = reverse('user-management-list')
        self.admin = User.objects.create_superuser(
            username='admin',
            email='admin@example.com',
            password='adminpass123'
        )
        self.customer = User.objects.create_user(
            username='customer12345',
            email='customer12345@example.com',
            password='password123',
            role=User.Role.CUSTOMER
        )

    def test_list_users_admin(self):
        #  test admin can list users

        response = self.client.get(self.url)
        self.assertEqual(response.status_code, status.HTTP_401_UNAUTHORIZED)

    def test_admin_list_users(self):
        #  test  admin can list users
        self.client.force_authenticate(user=self.admin)
        response = self.client.get(self.url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)

    def test_customer_cannot_list_users(self):
        #  test customer cannot list users
        self.client.force_authenticate(user=self.customer)
        response = self.client.get(self.url)
        self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)

    def test_admin_can_create_user(self):
        #  test admin can create new user
        self.client.force_authenticate(user=self.admin)
        data = {
            'username': 'newbyadmin',
            'email': 'newbyadmin@example.com',
            'password': 'password123',
            'role': User.Role.MERCHANT
        }
        response = self.client.post(self.url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertTrue(User.objects.filter(username='newbyadmin').exists())

    def test_customer_cannot_create_user(self):
        #  test customer cannot create new user
        self.client.force_authenticate(user=self.customer)
        data = {
            'username': 'hacked',
            'email': 'hacked@example.com',
            'password': 'password123',
            'role': User.Role.CUSTOMER
        }
        response = self.client.post(self.url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)
