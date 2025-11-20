# <Real Estate PDF>

<Generates filled pdf based on API call>

---

## Table of Contents

- [Overview](#overview)
- [Architecture & Tech Stack](#architecture--tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Running the Application](#running-the-application)
- [Build & Test](#build--test)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Database & Migrations](#database--migrations)
- [Profiles & Environments](#profiles--environments)
- [Docker](#docker)
- [Logging & Monitoring](#logging--monitoring)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

`<PROJECT_NAME>` is a Spring Boot-based application that <briefly describe core functionality: e.g., "exposes REST APIs for managing orders and customers">.

Key features:

- http://localhost:8080/swagger-ui
- Sample for API call to generate pdf

  ```json
  {  
  "buyerInfoDto": {
    "var_1_parties_the_parties_to_this_contract_are": "Nana Ta",
    "var_seller_and": "Hello Wr", 
    "var_a_land_lot": "9999 sq",
    "var_block": "234231",
    "var_undefined": "XXYY",
    "var_b_sum_of_all_financing_described_in_the_attached": "Y"
  },

  "brokerInfoDto": {
    "var_other_broker_firm": "blah blah blah"
  },
  
  "receiptInfoDto":{
    "var_is_acknowledged": "XYZ"
  }
}
`
- <Feature 3>

---

## Architecture & Tech Stack

- **Language:** Java <version> / Kotlin <version>
- **Framework:** Spring Boot <version>
- **Core Spring Modules:**
  - Spring Web (REST APIs)
  - Spring Data JPA / Spring Data Mongo / etc.
  - Spring Security (if applicable)
  - Spring Validation
- **Build Tool:** Maven / Gradle
- **Database:** <PostgreSQL / MySQL / H2 / MongoDB / etc.>
- **Migrations:** Flyway / Liquibase (if used)
- **API Docs:** Springdoc OpenAPI / Swagger UI
- **Testing:** JUnit 5, Mockito, Testcontainers (if applicable)

---

## Getting Started

### Prerequisites

Make sure you have:

- **Java:** `>= <version>`  
- **Build Tool:** `<Maven/Gradle>` installed (or use wrapper)
- **Database:** `<PostgreSQL/MySQL/etc.>` running (if not using in-memory)
- **Git:** (optional, for cloning the repo)

```bash
java -version
mvn -v          # if using Maven
gradle -v       # if using Gradle
