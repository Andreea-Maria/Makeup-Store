# Makeup Store App

## Descriere

Makeup Store App este o aplicație Android de tip e-commerce pentru produse cosmetice, dezvoltată în limbajul Kotlin folosind Jetpack Compose și Firebase. Aplicația permite utilizatorilor să își creeze un cont, să vizualizeze produsele disponibile, să le adauge în lista de favorite și în coșul de cumpărături, să plaseze comenzi și să beneficieze de reduceri prin scanarea codurilor QR.

De asemenea, aplicația include o funcționalitate de tip Try-On, care permite aplicarea virtuală a produselor cosmetice pe o fotografie selectată de utilizator, precum și un panou dedicat administratorului pentru gestionarea produselor, stocurilor și codurilor de reducere.

---

## Livrabilele proiectului

Repository-ul conține următoarele livrabile:

- codul sursă complet al aplicației Android;
- fișierele de configurare Gradle;
- fișierul `README.md`;
- fișierul `google-services.json` necesar configurării serviciilor Firebase.

Repository-ul conține exclusiv codul sursă și fișierele necesare compilării aplicației, fără fișiere binare generate în urma procesului de compilare (APK-uri, directoare `build`, fișiere `.class`, `.dex` etc.).

---

## Repository

Adresa repository-ului proiectului:

```text
https://github.com/Andreea-Maria/Makeup-Store.git
```

---

## Funcționalități principale

- autentificare și înregistrare utilizatori;
- salvarea numelui și adresei de e-mail;
- afișarea produselor din Firebase Firestore;
- căutare produse;
- filtrare după categorii, subcategorii și branduri;
- pagină de detalii pentru fiecare produs;
- selectarea nuanțelor produselor;
- listă de produse favorite;
- coș de cumpărături;
- verificarea stocului disponibil;
- checkout cu adresă de livrare;
- simularea plății cu cardul;
- aplicarea codurilor de reducere manual sau prin scanarea unui cod QR;
- istoricul comenzilor;
- profil utilizator;
- suport pentru Dark Mode;
- funcționalitate Try-On pentru aplicarea virtuală a produselor cosmetice pe o fotografie;
- detectarea reperelor faciale cu MediaPipe Face Landmarker;
- aplicarea virtuală a rujului, gloss-ului și creionului de buze;
- aplicarea virtuală a fondului de ten și concealerului;
- aplicarea virtuală a produselor pentru contouring și iluminator;
- cont de administrator;
- adăugarea produselor;
- editarea produselor existente;
- modificarea stocului;
- administrarea codurilor de reducere.

---

## Funcționalitatea Try-On

Aplicația include o funcționalitate de tip Try-On, care permite utilizatorului să vizualizeze efectul unor produse cosmetice direct pe o fotografie.

Utilizatorul poate selecta o imagine, iar aplicația utilizează MediaPipe Face Landmarker pentru detectarea reperelor faciale. Pe baza coordonatelor obținute sunt identificate diferite regiuni ale feței, necesare pentru poziționarea efectelor cosmetice.

Funcționalitatea permite aplicarea virtuală a următoarelor tipuri de produse:

- ruj;
- gloss;
- creion de buze;
- fond de ten;
- concealer;
- produse pentru contouring;
- iluminator.

Culorile și efectele sunt aplicate peste fotografia selectată în funcție de reperele faciale detectate.

---

## Tehnologii utilizate

- Kotlin
- Jetpack Compose
- Firebase Authentication
- Cloud Firestore
- Firebase Storage
- CameraX
- Google ML Kit Barcode Scanning
- MediaPipe Face Landmarker
- Material Design 3
- Coil

---

## Structura aplicației

Aplicația este alcătuită din următoarele ecrane principale:

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
- TryOnScreen
- AdminScreen
- AddProductScreen
- EditProductScreen
- AddDiscountScreen

Pentru funcționalitatea Try-On este utilizată și clasa `FaceLandmarkerHelper`, responsabilă de procesarea imaginii și detectarea reperelor faciale cu ajutorul MediaPipe Face Landmarker.

---

## Roluri utilizatori

Aplicația utilizează două tipuri de utilizatori:

- client;
- administrator.

Rolul fiecărui utilizator este stocat în colecția `users` din Cloud Firestore, prin câmpul:

```text
role: "client"
```

sau

```text
role: "admin"
```

Administratorul are acces la funcții suplimentare pentru gestionarea produselor, stocului și codurilor de reducere.

---

## Structura bazei de date Firebase

Aplicația utilizează următoarele colecții principale:

- users
- products
- orders
- discountCodes

---

## Compilarea aplicației

### Cerințe

Pentru compilarea aplicației sunt necesare:

- Android Studio;
- JDK 17;
- Android SDK;
- conexiune la Internet;
- fișierul `app/google-services.json`.

### Pașii de compilare

1. Se clonează repository-ul:

```bash
git clone https://github.com/Andreea-Maria/Makeup-Store.git
```

2. Se deschide proiectul în Android Studio.

3. Se așteaptă sincronizarea proiectului cu Gradle.

4. Dacă este necesar, se selectează opțiunea **Sync Project with Gradle Files**.

5. Se verifică existența fișierului:

```text
app/google-services.json
```

6. Se compilează aplicația folosind una dintre următoarele variante:

- **Build → Make Project**

sau

- **Run → Run 'app'**

---

## Instalarea și lansarea aplicației

1. Se conectează un dispozitiv Android sau se pornește un emulator Android.

2. Din Android Studio se selectează dispozitivul pe care se dorește rularea aplicației.

3. Se apasă butonul **Run**.

4. Android Studio compilează și instalează automat aplicația pe dispozitiv.

5. După finalizarea instalării, aplicația poate fi lansată din meniul dispozitivului.

---

## Observații

- Pentru funcționalitatea de scanare QR este necesară acordarea permisiunii de utilizare a camerei.
- Pentru testarea reducerilor pot fi utilizate atât introducerea manuală a codului, cât și scanarea unui cod QR care conține textul codului de reducere.
- Funcționalitatea Try-On necesită selectarea unei fotografii în care fața utilizatorului este vizibilă.
- Precizia poziționării efectelor cosmetice depinde de reperele faciale detectate în fotografia selectată.
- Pentru funcționarea aplicației este necesară existența unei conexiuni la Internet, deoarece autentificarea și stocarea datelor se realizează prin serviciile Firebase.

---

## Cont de administrator

Pentru testarea funcționalităților de administrare este disponibil următorul cont:

**E-mail:** `admin@gmail.com`

**Parolă:** `admin0`

Acest cont permite accesul la panoul de administrare, de unde pot fi adăugate și editate produse, actualizat stocul și gestionate codurile de reducere.

---

## Autor

Slănoiu Andreea-Maria

Proiect realizat în cadrul lucrării de licență.
