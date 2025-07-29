💱 Currency Exchange REST API

A simple currency exchange backend built with Java 21. This REST API allows for managing currencies, exchange rates, and converting amounts between currencies.


📦 Features

    🔍 Get a list of all currencies and exchange rates

    ➕ Add new currencies and rates

    ✏️ Update existing exchange rates

    🔁 Convert money between currencies in real-time

🚀 API Endpoints
🔹 Currencies
GET /currencies

Returns all available currencies.

Response:

[
  {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  },
  {
    "id": 1,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
  }
]

GET /currency/{code}

Returns a currency by its code (e.g. USD).

Example: GET /currency/USD
POST /currencies

Add a new currency.
Content-Type: application/x-www-form-urlencoded

Body:

name=Czech Koruna
code=CZK
sign=Kč

Response:

[
  {
    "id": 2,
    "name": "Czech Koruna",
    "code": "CZK",
    "sign": "Kč"
  }
]

🔹 Exchange Rates
GET /exchangeRates

Returns all exchange rates.

Response:

[
  {
    "id": 0,
    "baseCurrency": { ... },
    "targetCurrency": { ... },
    "rate": 0.93
  }
]

GET /exchangeRate/{BASE}{TARGET}

Returns a specific exchange rate.

Example: GET /exchangeRate/USDEUR
POST /exchangeRates

Add a new exchange rate.
Content-Type: application/x-www-form-urlencoded

Body:

baseCurrencyCode=EUR
targetCurrencyCode=CZK
rate=23.75

Response:

[
  {
    "id": 2,
    "baseCurrency": { ... },
    "targetCurrency": { ... },
    "rate": 23.75
  }
]

PATCH /exchangeRate/{BASE}{TARGET}

Update an existing exchange rate.
Content-Type: application/x-www-form-urlencoded

Body:

rate=22.24

Example: PATCH /exchangeRate/USDCZK
🔹 Currency Conversion
GET /exchange?from=BASE&to=TARGET&amount=VALUE

Converts the given amount from one currency to another.

Example:
GET /exchange?from=USD&to=CZK&amount=100

Response:

{
  "baseCurrency": { ... },
  "targetCurrency": { ... },
  "rate": 22.24,
  "amount": 100.00,
  "convertedAmount": 2224.00
}






   
