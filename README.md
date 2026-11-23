# QMUL CloudComputing Luxury Bag Inventory

A Django REST Framework (DRF) backend service that ingests luxury bag inventory data from the [BirkinBagStock Public API](https://birkinbagstock.com/api/v1/inventory) into PostgreSQL, enforces role-based CRUD permissions across **Superusers**, **Merchants**, and **Customers**, provides persistent **Shopping Basket / Cart** endpoints, and includes a superuser-only admin dashboard for visualizing users, baskets, and catalog ownership.

---

## Table of Contents
- [Architecture & Project Structure](#architecture--project-structure)
- [User Roles & Permissions](#user-roles--permissions)
- [Tech Stack](#tech-stack)
- [Local Setup & Installation](#local-setup--installation)
- [Data Ingestion & Merchant Distribution](#data-ingestion--merchant-distribution)
- [API Endpoints Reference](#api-endpoints-reference)
  - [1. Authentication & User Management (`/api/accounts/`)](#1-authentication--user-management-apiaccounts)
  - [2. Product Inventory (`/inventory/`)](#2-product-inventory-inventory)
  - [3. Shopping Basket / Cart (`/inventory/cart/`)](#3-shopping-basket--cart-inventorycart)
- [Testing with `curl`](#testing-with-curl)
- [Superuser Admin Dashboard](#superuser-admin-dashboard)
- [Cloud Database Migration (PostgreSQL Dump & Restore)](#cloud-database-migration-postgresql-dump--restore)

---

## Architecture & Project Structure

```text
QMUL-Cloud-Computing-Project/
├── manage.py
├── requirements.txt            # Python project dependencies
├── ecommerce/                  # Main project configuration (settings.py, urls.py)
├── accounts/                   # Custom User model, role groups, JWT auth & user management API
│   ├── models.py               # User model with role choices & automatic Django Group sync
│   ├── serializers.py          # Registration & user profile serializers
│   ├── views.py                # Public registration & superuser user management endpoints
│   ├── urls.py                 # /api/accounts/* routes
│   └── admin.py                # Role-badged UserAdmin with owned product counts
└── inventory/                  # Product catalog, shopping cart, API sync command & Admin
    ├── models.py               # Product, Cart, and CartItem models
    ├── permissions.py          # Object-level role permissions (Superuser, Merchant, Customer)
    ├── serializers.py          # Product, Cart, and CartItem serializers
    ├── views.py                # ProductViewSet & CartViewSet
    ├── urls.py                 # /inventory/* and /inventory/cart/* routes
    ├── admin.py                # Visual ProductAdmin & CartAdmin for superusers
    └── management/
        └── commands/
            └── sync_inventory.py  # Custom command to pull & upsert external API data into PostgreSQL
```

---

## User Roles & Permissions

All users are automatically assigned to a matching Django `Group` (`customer`, `merchant`, or `superuser`) upon creation or update.

| Role | Product Read (`GET`) | Product Create (`POST`) | Product Update/Delete (`PATCH`/`PUT`/`DELETE`) | Shopping Cart | User Management | Django Admin (`/admin/`) |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Unauthenticated** | All products | No | No | No | Register (`customer`/`merchant` only) | No |
| **Customer** | All products | Own products | Own products only | Own cart | No | No |
| **Merchant** | All products | Own products | Own products only | Own cart | No | No |
| **Superuser** | All products | All products | **All products** | Own cart | Full CRUD on all users | **Full visual access** |

---

## Tech Stack

- **Framework:** Django & Django REST Framework (DRF)
- **Authentication:** JSON Web Tokens (`djangorestframework-simplejwt`) + Session Auth
- **Database:** PostgreSQL (`psycopg`)
- **Filtering & Search:** `django-filter`, DRF `SearchFilter`, DRF `OrderingFilter`
- **External Data Source:** `https://birkinbagstock.com/api/v1/inventory`

---

## Local Setup & Installation

### 1. Clone & Activate Virtual Environment
```bash
git clone <your-repo-url>
cd QMUL-Cloud-Computing-Project
python3 -m venv env
source env/bin/activate
```

### 2. Install Dependencies
```bash
pip install -r requirements.txt
```

### 3. Configure PostgreSQL
Create a local PostgreSQL database (default `ecommerce_db`) and ensure `DATABASES` in `ecommerce/settings.py` matches your credentials:

```python
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql',
        'NAME': 'ecommerce_db',
        'USER': 'postgres',
        'PASSWORD': 'your_password',
        'HOST': 'localhost',
        'PORT': '5432',
    }
}
```

### 4. Apply Migrations & Create Superuser
```bash
python manage.py makemigrations
python manage.py migrate
python manage.py createsuperuser
```

### 5. Start Development Server
```bash
python manage.py runserver
```
The API will be available at `http://127.0.0.1:8000/`.

---

## Data Ingestion & Merchant Distribution

### Pull Inventory from External API
Run the custom management command to fetch listings (`title`, `size`, `color`, `leather`, `condition`, `price`, `image`) and upsert them into PostgreSQL using `external_id`:

```bash
python manage.py sync_inventory
```

### Split Products Across 3 Merchants
Once 3 merchant accounts exist in the database, distribute the synced catalog evenly (e.g., 99 products split 33 per merchant):

```bash
python manage.py shell -c "
from accounts.models import User
from inventory.models import Product

merchants = list(User.objects.filter(role='merchant').order_by('id')[:3])
products = list(Product.objects.all().order_by('id'))

for i, product in enumerate(products):
    product.owner = merchants[i % len(merchants)]

Product.objects.bulk_update(products, ['owner'])
for m in merchants:
    print(f'{m.username}: {Product.objects.filter(owner=m).count()} products')
"
```

---

## API Endpoints Reference

### 1. Authentication & User Management (`/api/accounts/`)

| Method | Endpoint | Auth Required | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/accounts/register/` | Public | Register a new `customer` or `merchant` account (`superuser` self-registration is blocked). |
| `POST` | `/accounts/login/` | Public | Exchange `username` and `password` for JWT `access` and `refresh` tokens. |
| `POST` | `/accounts/token/refresh/` | Public | Obtain a new `access` token using a valid `refresh` token. |
| `GET` | `/accounts/users/` | Superuser | List all registered users across all roles. |
| `POST` | `/accounts/users/` | Superuser | Create a new user account. |
| `GET` | `/accounts/users/<id>/` | Superuser | Retrieve details for a specific user. |
| `PATCH` / `PUT` | `/accounts/users/<id>/` | Superuser | Update a user's profile, active status, or role. |
| `DELETE` | `/accounts/users/<id>/` | Superuser | Delete a user account. |

---

### 2. Product Inventory (`/inventory/`)

| Method | Endpoint | Auth / Role Required | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/inventory/` | Public | Paginated list of all products (25 per page). |
| `POST` | `/inventory/` | Authenticated (`Customer`, `Merchant`, `Superuser`) | Create a new product; automatically assigns `owner` to the authenticated user. |
| `GET` | `/inventory/<id>/` | Public | Retrieve full details for a single product by database ID. |
| `PATCH` / `PUT` | `/inventory/<id>/` | Owner or Superuser | Update product fields (or reassign `owner` if Superuser). |
| `DELETE` | `/inventory/<id>/` | Owner or Superuser | Delete a product. |
| `POST` | `/inventory/sync/` | Superuser (`IsAdminUser`) | Trigger `sync_inventory` via API to pull fresh data from BirkinBagStock. |
| `POST` | `/inventory/split-merchants/` | Superuser (`IsAdminUser`) | Evenly distribute products across the 3 registered merchants. |

#### Query Parameters for `GET /inventory/`
- **Exact Filtering:** `?size=30`, `?color=Black`, `?leather=Togo`, `?condition=Pre-owned`
- **Keyword Search:** `?search=Birkin` (searches `title`, `color`, and `leather`)
- **Ordering:** `?ordering=price`, `?ordering=-price`, `?ordering=size`, `?ordering=-created_at`
- **Pagination:** `?page=2`

---

### 3. Shopping Basket / Cart (`/inventory/cart/`)

All cart endpoints require a valid JWT `Authorization: Bearer <ACCESS_TOKEN>` header and operate on the authenticated user's persistent cart.

| Method | Endpoint | Auth Required | Request Body | Description |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/inventory/cart/` | Authenticated | — | View current user's basket, items, `total_items`, and `total_price`. |
| `POST` | `/inventory/cart/add/` | Authenticated | `{"product_id": 1, "quantity": 1}` | Add a product to the basket (increments quantity if already present). |
| `PATCH` | `/inventory/cart/update-item/` | Authenticated | `{"product_id": 1, "quantity": 2}` | Set exact item quantity (`0` removes the item from the basket). |
| `DELETE` | `/inventory/cart/remove/<product_id>/` | Authenticated | — | Remove a specific product from the basket. |
| `DELETE` | `/inventory/cart/clear/` | Authenticated | — | Remove all items and empty the basket. |

---

## Testing with `curl`

### 1. Register a Merchant or Customer
```bash
curl -X POST [http://127.0.0.1:8000/api/accounts/register/](http://127.0.0.1:8000/api/accounts/register/) \
  -H "Content-Type: application/json" \
  -d '{
    "username": "merchant1",
    "email": "merchant1@example.com",
    "password": "Password123!",
    "role": "merchant"
  }'
```

### 2. Login & Get JWT Access Token
```bash
curl -X POST [http://127.0.0.1:8000/api/accounts/login/](http://127.0.0.1:8000/api/accounts/login/) \
  -H "Content-Type: application/json" \
  -d '{
    "username": "merchant1",
    "password": "Password123!"
  }'
```

### 3. List & Filter Products
```bash
curl -s "[http://127.0.0.1:8000/inventory/?color=Black&size=30](http://127.0.0.1:8000/inventory/?color=Black&size=30)" | python -m json.tool
```

### 4. Create a Product (`POST`)
```bash
curl -X POST [http://127.0.0.1:8000/inventory/](http://127.0.0.1:8000/inventory/) \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Hermes Birkin 25",
    "size": 25,
    "color": "Etoupe",
    "leather": "Togo",
    "condition": "New",
    "price": "24500.00",
    "image": ""
  }'
```

### 5. Update a Product (`PATCH`)
```bash
curl -X PATCH [http://127.0.0.1:8000/inventory/1/](http://127.0.0.1:8000/inventory/1/) \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"price": "25900.00"}'
```

### 6. Delete a Product (`DELETE`)
```bash
curl -X DELETE [http://127.0.0.1:8000/inventory/1/](http://127.0.0.1:8000/inventory/1/) \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### 7. Add Item to Basket (`POST`)
```bash
curl -X POST [http://127.0.0.1:8000/inventory/cart/add/](http://127.0.0.1:8000/inventory/cart/add/) \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "product_id": 1,
    "quantity": 1
  }'
```

### 8. View Basket (`GET`)
```bash
curl -s [http://127.0.0.1:8000/inventory/cart/](http://127.0.0.1:8000/inventory/cart/) \
  -H "Authorization: Bearer <ACCESS_TOKEN>" | python -m json.tool
```

### 9. Update Basket Item Quantity (`PATCH`)
```bash
curl -X PATCH [http://127.0.0.1:8000/inventory/cart/update-item/](http://127.0.0.1:8000/inventory/cart/update-item/) \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "product_id": 1,
    "quantity": 3
  }'
```

### 10. Remove Item or Clear Basket (`DELETE`)
```bash
# Remove single product
curl -X DELETE [http://127.0.0.1:8000/inventory/cart/remove/1/](http://127.0.0.1:8000/inventory/cart/remove/1/) \
  -H "Authorization: Bearer <ACCESS_TOKEN>"

# Clear entire basket
curl -X DELETE [http://127.0.0.1:8000/inventory/cart/clear/](http://127.0.0.1:8000/inventory/cart/clear/) \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

---

## Superuser Admin Dashboard

Django Admin is locked exclusively to `is_superuser=True` accounts and is accessible in the browser at:

```text
[http://127.0.0.1:8000/admin/](http://127.0.0.1:8000/admin/)
```

Features included:
- **Users View:** Color-coded role badges (`Superuser`, `Merchant`, `Customer`), role filters, and live **Products Owned** counters per user.
- **Products View:** Inline image thumbnails, formatted currency pricing, sidebar filters (`owner`, `size`, `color`, `leather`, `condition`), and inline dropdowns to reassign product ownership between merchants.
- **Carts View:** Inspect active customer/user shopping carts, item quantities, and computed subtotals.

---

## Cloud Database Migration (PostgreSQL Dump & Restore)

When deploying to a cloud PostgreSQL provider (e.g., AWS RDS, Supabase, Railway, Neon, or Google Cloud SQL), export your populated local database and restore it directly into the cloud instance:

### 1. Dump Local PostgreSQL Database
```bash
pg_dump -Fc --no-owner --no-acl -U postgres -d ecommerce_db -f ecommerce_backup.dump
```

### 2. Restore into Cloud PostgreSQL
```bash
pg_restore --no-owner --no-acl -d "postgresql://<CLOUD_USER>:<CLOUD_PASSWORD>@<CLOUD_HOST>:5432/<CLOUD_DB_NAME>" ecommerce_backup.dump
```

### 3. Update `ecommerce/settings.py`
Point `DATABASES['default']` (via environment variables) to your cloud PostgreSQL host and set `DEBUG = False` with your production `ALLOWED_HOSTS`.