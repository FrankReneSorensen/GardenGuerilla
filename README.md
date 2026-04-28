# Garden Guerilla Ready 🌱

> Mission Control for guerilla gardeners – powered by MET Norway / Yr weather data.

Garden Guerilla er en webapp som henter sanntids værdata og beregner hvor god timing det er for planting akkurat nå. Den gir deg en score fra 0–100 og klar anbefaling: **Perfekt kveld for planting**, **Gjør deg klar**, **Mulig, men ikke optimalt**, eller **Vent litt**.

---

## 🚀 Kom i gang lokalt (Docker)

### Krav
- Docker & Docker Compose installert
- (Valgfritt) Java 21 + Maven for lokal kjøring av backend uten Docker

### Start med Docker Compose

```bash
# Klon repo
git clone https://github.com/FrankReneSorensen/GardenGuerilla.git
cd GardenGuerilla

# Sett User-Agent (obligatorisk for MET API – se under)
export METNO_USER_AGENT="GardenGuerillaReady/1.0 your-email@example.com"

# Start alt
docker compose up --build
```

Åpne nettleseren på **http://localhost** 🎉

---

## 🔑 User-Agent for MET API

MET Norway krever at alle klienter setter en **User-Agent** med appnavn og kontaktinfo. Uten dette kan forespørsler bli blokkert.

Sett variabelen slik:
```bash
export METNO_USER_AGENT="GardenGuerillaReady/1.0 your-email@example.com"
```

Eller legg det i en `.env`-fil i rotmappa:
```env
METNO_USER_AGENT=GardenGuerillaReady/1.0 your-email@example.com
```

Docker Compose plukker opp `.env` automatisk.

---

## 🧮 Slik fungerer score-reglene

Scoren går fra 0 til 100 og beregnes basert på:

| Betingelse | Endring |
|---|---|
| Temperatur 8–20°C | +30 |
| Nedbør 0.2–5 mm neste 12t | +20 |
| Vind < 6 m/s | +15 |
| Ingen temp under 2°C neste 24t | +15 |
| Skydekke > 50% | +10 |
| Luftfuktighet > 60% | +10 |
| Temperatur under 0°C | -30 |
| Vind > 10 m/s | -20 |
| Nedbør > 10 mm neste 12t | -25 |
| Ingen nedbør + temp > 22°C | -20 |

**Status basert på score:**

| Score | Status |
|---|---|
| 80–100 | Perfekt kveld for planting 🌱 |
| 60–79 | Gjør deg klar – gode forhold snart |
| 40–59 | Mulig, men ikke optimalt |
| 0–39 | Vent litt |

Cachen er 30 minutter – MET API kalles ikke oftere enn nødvendig.

---

## 💻 Lokal utvikling (uten Docker)

### Backend

```bash
cd backend
mvn spring-boot:run
# API kjører på http://localhost:8080
```

### Frontend

```bash
cd frontend
npm install
npm run dev
# Åpner http://localhost:5173 med proxy mot :8080
```

---

## 🧪 Kjør tester

```bash
cd backend
mvn test
```

Unit-tester for score-algoritmen ligger i `GardeningReadinessServiceTest`.

---

## 🏗️ Arkitektur

```
┌─────────────────┐        ┌──────────────────────┐        ┌───────────────┐
│   React/Vite    │──────► │   Spring Boot API    │──────► │  MET/Yr API   │
│   (port 80)     │        │   (port 8080)        │        │ api.met.no    │
└─────────────────┘        └──────────────────────┘        └───────────────┘
                                    │
                            Caffeine Cache (30 min)
```

### Backend-klasser
| Klasse | Ansvar |
|---|---|
| `WeatherController` | REST-endepunkt, validering, rate limiting |
| `WeatherService` | Koordinatvalidering og rounding |
| `MetNoClient` | WebClient mot MET API + caching |
| `GardeningReadinessService` | Score-algoritme og anbefaling |
| `GardeningTipService` | Dagens guerilla-tips |
| `WeatherSnapshot` | Modell for én time-slot |
| `GardeningRecommendation` | Komplett respons-modell |

### Frontend-komponenter
| Komponent | Ansvar |
|---|---|
| `App.tsx` | Tilstand, datahenting, layout |
| `LocationSelector.tsx` | Stedsvelger (Oslo, Bergen, Trondheim, Stavanger, Kristiansand, Tromsø) |
| `ReadinessCard.tsx` | SVG-scoreringel, status, badges |
| `ForecastDetails.tsx` | Værstatus og 24t prognose |
| `GardeningTips.tsx` | Tips, fakta og planteoversikt |

---

## 🔒 Sikkerhet og begrensninger

- **Rate limiting**: Maks 10 forespørsler per minutt per IP (Bucket4j in-memory)
- **Input-validering**: lat -90..90, lon -180..180
- **Caching**: 30 minutter Caffeine-cache på MET-kall
- **Ingen eksponering**: API-feil fra MET eksponeres ikke til frontend
- **Audit-logging**: Score-vurderinger logges uten persondata
- **CORS**: Tillatte opphav styres via `CORS_ALLOWED_ORIGINS` – aldri hardkodet `*` i produksjon

### Miljøvariabler og hemmeligheter

Ingen hemmeligheter eller infrastrukturdetaljer er hardkodet i kildekoden. Alle sensitive verdier settes som miljøvariabler:

| Variabel | Beskrivelse | Standard (kun lokal utvikling) |
|---|---|---|
| `METNO_USER_AGENT` | User-Agent for MET API | `GardenGuerillaReady/1.0 contact@example.com` |
| `CORS_ALLOWED_ORIGINS` | Tillatte frontend-opphav | `http://localhost,http://localhost:5173` |

> **Merk**: Ikke legg faktiske verdier for disse variablene i `.env`-filer som commites til repoet.  
> Bruk Azure Container Apps secrets / GitHub Actions secrets for produksjonsverdier.

---

## 📦 API-referanse

### `GET /api/gardening-readiness`

**Query-parametre:**
| Parameter | Type | Eksempel | Beskrivelse |
|---|---|---|---|
| `lat` | double | `59.9139` | Breddegrad (-90 til 90) |
| `lon` | double | `10.7522` | Lengdegrad (-180 til 180) |

**Eksempel-respons:**
```json
{
  "recommendation": {
    "score": 75,
    "status": "GOOD",
    "label": "Gjør deg klar – gode forhold snart",
    "explanation": "Temperatur: 14.2°C. Nedbør neste 12t: 1.2 mm. ...",
    "bestTimeStart": "2024-05-01T18:00:00Z",
    "hoursUntilBestTime": 4,
    "missionModeText": "Neste grønne vindu åpner om 4 timer."
  },
  "currentWeather": { ... },
  "forecast24h": [ ... ],
  "badges": ["Regn på vei 🌧️", "Lav vind 💨"],
  "guerillaTip": "Rødkløver er robust og perfekt for norsk klima...",
  "attribution": "Værdata levert av MET Norway / Yr (api.met.no). Fri bruk under NLOD / CC BY 4.0."
}
```

---

## ☁️ Azure Container Apps (fremtidig deploy)

For å deploye til Azure:

1. Push Docker-images til Azure Container Registry (ACR):
   ```bash
   az acr build --registry <acr-name> --image garden-guerilla-backend:latest ./backend
   az acr build --registry <acr-name> --image garden-guerilla-frontend:latest ./frontend
   ```

2. Opprett Container Apps:
   ```bash
   az containerapp create --name garden-guerilla-backend --resource-group <rg> ...
   az containerapp create --name garden-guerilla-frontend --resource-group <rg> ...
   ```

3. Sett følgende som Container App secrets/environment variables – **aldri i kildekoden**:
   - `METNO_USER_AGENT` – din kontaktepost
   - `CORS_ALLOWED_ORIGINS` – din faktiske frontend-URL (f.eks. `https://min-app.azurecontainerapps.io`)

> Bruk **Azure Container Apps secrets** eller **GitHub Actions secrets** for disse verdiene.  
> Ikke commit faktiske verdier til repoet.

Se [Azure Container Apps docs](https://learn.microsoft.com/en-us/azure/container-apps/) for full guide.

---

## 🌿 Videre utvikling

- **Brukerposisjon**: Bruk browser Geolocation API for automatisk lat/lon
- **Manuell lat/lon**: Bonus-input felt allerede planlagt i spec
- **PostgreSQL**: Logg historiske score-vurderinger per lokasjon
- **Push-varsler**: Varsle brukeren når et "grønt vindu" åpner
- **Mer data**: Vindretning, UV-indeks, jordfuktighet
- **I18n**: Engelsk oversettelse av appen
- **PWA**: Gjør appen installerbar på mobil

---

## 📄 Attribusjon

Værdata levert av **[MET Norway / Yr](https://api.met.no)** under Norsk Lisens for Offentlige Data (NLOD) og Creative Commons CC BY 4.0.
