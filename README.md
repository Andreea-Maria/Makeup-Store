# Makeup Store App

Makeup Store App este o aplicație Android de tip e-commerce pentru produse cosmetice, realizată în Kotlin cu Jetpack Compose și Firebase.

## Funcționalități principale

- autentificare și înregistrare utilizatori
- salvarea numelui și emailului utilizatorului
- afișare produse din Firebase Firestore
- căutare produse
- filtrare după categorii, subcategorii și branduri
- pagină detalii produs
- variante de nuanțe pentru produse
- produse favorite persistente
- coș de cumpărături
- verificare stoc disponibil
- checkout cu adresă de livrare
- simulare plată cu cardul
- aplicare coduri de reducere prin QR sau manual
- istoric comenzi
- profil utilizator
- dark mode
- cont admin
- adăugare produse din admin
- editare produse existente
- modificare stoc
- adăugare coduri de reducere

## Tehnologii folosite

- Kotlin
- Jetpack Compose
- Firebase Authentication
- Firebase Firestore
- Firebase Storage / Image URL
- CameraX
- ML Kit Barcode Scanning
- Material 3
- Coil pentru încărcarea imaginilor

## Structura aplicației

Aplicația este împărțită în mai multe ecrane:

- LoginScreen
- RegisterScreen
- HomeScreen
- ProductDetailsScreen
- CartScreen
- CheckoutScreen
- OrderConfirmationScreen
- OrdersHistoryScreen
- FavoritesScreen
- ProfileScreen
- QRScreen
- AdminScreen
- AddProductScreen
- EditProductScreen
- AddDiscountScreen

## Roluri utilizatori

Aplicația folosește două tipuri de utilizatori:

- client
- admin

Rolul este salvat în Firestore în colecția `users`, prin câmpul:

```text
role: "client"
```

sau

```text
role: "admin"
```

Adminul are acces la funcții suplimentare pentru gestionarea produselor, stocului și codurilor de reducere.

## Firebase

Aplicația folosește următoarele colecții principale:

```text
users
products
orders
discountCodes
```

## Instalare și rulare

1. Clonează repository-ul:

```bash
git clone https://github.com/username/MakeupStoreApp.git
```

2. Deschide proiectul în Android Studio.

3. Adaugă fișierul Firebase:

```text
app/google-services.json
```

4. Rulează aplicația pe emulator sau dispozitiv Android.

## Observații

Pentru funcționalitatea QR este necesară permisiunea camerei.

Pentru testarea codurilor de reducere, se poate folosi fie introducerea manuală a codului, fie scanarea unui cod QR care conține textul codului de reducere.

## Autor

Proiect realizat pentru lucrarea de licență.
