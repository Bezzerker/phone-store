# Магазин мобильных телефонов

Этот проект представляет собой приложение для управления магазином мобильных телефонов, 
которое позволяет выполнять CRUD операции (создание, чтение, обновление, удаление) над телефонами, 
их характеристиками, а также количеством и стоимостью.

## Инструкция по запуску приложения

Для запуска приложения есть два варианта: через Docker контейнеры или ручной запуск.

### 1. Запуск через Docker контейнеры

1. Настройка переменных окружения:
  - Установите значения переменных окружения в файле `docker.env`:
    - `APPLICATION_PORT` - порт приложения
    - `POSTGRES_USERNAME` - имя пользователя базы данных
    - `POSTGRES_PASSWORD` - пароль базы данных
    - `POSTGRES_HOSTNAME` - имя хоста базы данных
    - `POSTGRES_URL` - адрес к базе данных

2. Запуск контейнеров:
  - Перейдите в папку с исходным кодом приложения и выполните запуск Docker контейнеров с помощью команды:
      ```bash
      docker compose --env-file ./docker.env up -d
      ```

3. Остановка приложения:
  - Для остановки приложения выполните следующую команду:
      ```bash
      docker stop phone-service phone-store-app-db
      ```

После запуска приложение будет доступно по порту 8080, если конфигурационный файл docker-compose.yml не был вручную изменён.

### 2. Ручной запуск

1. Настройка соединения с базой данных и порта:
  - Укажите настройки соединения с базой данных и порт в файле `application.yml`. По умолчанию порт `8080`, имя пользователя для соединения с базой данных `postgres`, и пароль `postgres`.

2. Создание таблиц базы данных:
  - Перед запуском приложения создайте необходимые таблицы базы данных. Команды для создания таблиц находятся в файле `src/main/resources/schema.sql`.

3. Сборка приложения:
  - После создания таблиц соберите приложение с помощью команды:
      ```bash
      mvn clean package -DskipTests=true
      ```

4. Запуск приложения:
  - Перейдите в папку `target` и запустите приложение с помощью команды:
      ```bash
      java -jar sber-task-0.0.1-SNAPSHOT.jar
      ```

# Документация по REST API

## Аккумулятор

### Добавление нового аккумулятора

Метод: `POST /api/v1/batteries/`

Этот запрос создает новый аккумулятор. Параметр `batteryType` должен быть либо `LI_ION` либо `LI_POL`, что соответствует 1 и 0 соответственно. Емкость аккумулятора должна быть больше 0.

**Пример запроса: `POST /api/v1/operating-systems/`**
```json
{
    "capacity": 100,
    "batteryType": 1
}
```

**Пример ответа:**
```json
{
    "id": 1,
    "capacity": 100,
    "batteryType": "LI_POL"
}
```

### Получение списка всех аккумуляторов

Метод: `GET /api/v1/batteries/`

Этот запрос возвращает список всех аккумуляторов, отсортированный по ёмкости.

**Пример ответа на запрос: `GET /api/v1/batteries/`**
```json
[
    {
        "id": 6,
        "capacity": 100,
        "batteryType": "LI_POL"
    },
    {
        "id": 4,
        "capacity": 1000,
        "batteryType": "LI_POL"
    },
    {
        "id": 2,
        "capacity": 4352,
        "batteryType": "LI_POL"
    },
    {
        "id": 3,
        "capacity": 4500,
        "batteryType": "LI_ION"
    },
    {
        "id": 1,
        "capacity": 5000,
        "batteryType": "LI_ION"
    }
]
```

### Получение аккумулятора по ID

Метод: `GET /api/v1/batteries/{id}`

Этот запрос возвращает информацию об аккумуляторе по указанному ID.

**Пример ответа на запрос: `GET /api/v1/batteries/1`**
```json
{
    "id": 1,
    "capacity": 5000,
    "batteryType": "LI_ION"
}
```

### Полное изменение данных аккумулятора по ID

Метод: `PUT /api/v1/batteries/{id}`

Этот запрос изменяет данные об аккумуляторе и возвращает обновленные сведения.

**Пример запроса: `PUT /api/v1/operating-systems/1`**
```json
{
    "capacity": 666,
    "batteryType": 0
}
```

**Пример ответа:**
```json
{
    "id": 1,
    "capacity": 666,
    "batteryType": "LI_ION"
}
```

### Частичное изменение данных аккумулятора по ID

Метод: `PATCH /api/v1/batteries/{id}`

Этот запрос частично изменяет данные об аккумуляторе и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/batteries/1`**
```json
{
    "capacity": 1338
}
```

**Пример ответа:**
```json
{
    "id": 1,
    "capacity": 1338,
    "batteryType": "LI_ION"
}
```

### Удаление аккумулятора по ID

Метод: `DELETE /api/v1/batteries/{id}`

Этот запрос удаляет данные об аккумуляторе и возвращает удаленные данные.

**Пример ответа на запрос `DELETE /api/v1/batteries/1`:**
```json
{
    "id": 1,
    "capacity": 1338,
    "batteryType": "LI_ION"
}
```

## Операционная система

### Добавление новой операционной системы

Метод: `POST /api/v1/operating-systems/`

Этот запрос создает новый аккумулятор. Запрос должен содержать название операционной системы и версию.

**Пример запроса: `/api/v1/operating-systems/`**
```json
{
	"name": "Sailfish",
	"version": "1.0"
}
```

**Пример ответа:**
```json
{
	"id": 5,
	"name": "Sailfish",
	"version": "1.0"
}
```

### Получение списка всех операционных систем

Метод: `GET /api/v1/operating-systems/`

Этот запрос возвращает список всех операционных систем, отсортированный в алфавитном порядке по названию и версии операционной системы.

**Пример ответа на запрос: `GET /api/v1/operating-systems/`**
```json
[
	{
		"id": 3,
		"name": "Android",
		"version": "12"
	},
	{
		"id": 1,
		"name": "Android",
		"version": "13"
	},
	{
		"id": 5,
		"name": "Sailfish",
		"version": "1.0"
	},
	{
		"id": 4,
		"name": "Symbian",
		"version": "9.3"
	},
	{
		"id": 2,
		"name": "iOS",
		"version": "17.0"
	}
]
```

### Получение операционной системы по ID

Метод: `GET /api/v1/operating-systems/{id}`

Этот запрос возвращает информацию об операционной системе по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/operating-systems/1`**
```json
{
	"id": 1,
	"name": "Android",
	"version": "13"
}
```

### Полное изменение данных об операционной системе по ID

Метод: `PUT /api/v1/operating-systems/{id}`

Этот запрос изменяет данные об операционной системе и возвращает обновленные сведения.

**Пример ответа на запрос: `PUT /api/v1/operating-systems/5`**
```json
{
	"name": "Zorin OS",
	"version": "1.8"
}
```

**Пример ответа:**
```json
{
	"id": 5,
	"name": "Zorin OS",
	"version": "1.8"
}
```

### Частичное изменение данных операционной системы по ID

Метод: `PATCH /api/v1/operating-systems/{id}`

Этот запрос частично изменяет данные об операционной системе и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/operating-systems/5`**
```json
{
	"name": "Astra Linux"
}
```

**Пример ответа:**
```json
{
	"id": 5,
	"name": "Astra Linux",
	"version": "1.8"
}
```

### Удаление операционной системы по ID

Метод: `DELETE /api/v1/operating-systems/{id}`

Этот запрос удаляет данные об операционной системе и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/operating-systems/5`**
```json
{
	"id": 5,
	"name": "Astra Linux",
	"version": "1.8"
}
```

## Процессор

### Добавление нового процессора

Метод: `POST /api/v1/processors/

Этот запрос создает новый процессор. Для создания процессора обязательно необходимо указать модель процессора, техпроцесс изготовления процессора, количество ядер и максимальную частоту.

**Пример запроса: `/api/v1/processors/`**
```json
{
	"model": "Apple M3",
	"technologyNode": 3,
	"cores": 8,
	"maxFrequency": 3.7
}
```

**Пример ответа:**
```json
{
	"id": 5,
	"model": "Apple M3",
	"technologyNode": 3,
	"cores": 8,
	"maxFrequency": 3.7
}
```

### Получение списка всех процессоров

Метод: `GET /api/v1/processors/`

Этот запрос возвращает список всех процессоров, отсортированный в порядке увеличения технологического процесса изготовления процессора и модели процессора в алфавитном порядке.

**Пример ответа на запрос: `GET /api/v1/processors/`**
```json
[
	{
		"id": 2,
		"model": "Apple A17 Bionic",
		"technologyNode": 3,
		"cores": 6,
		"maxFrequency": 3.46
	},
	{
		"id": 5,
		"model": "Apple M3",
		"technologyNode": 3,
		"cores": 8,
		"maxFrequency": 3.70
	},
	{
		"id": 3,
		"model": "Qualcomm Snapdragon 8+ Gen 1",
		"technologyNode": 4,
		"cores": 8,
		"maxFrequency": 3.20
	},
	{
		"id": 1,
		"model": "Google Tensor G3",
		"technologyNode": 5,
		"cores": 8,
		"maxFrequency": 2.80
	},
	{
		"id": 4,
		"model": "Texas Instruments OMAP1510",
		"technologyNode": 5,
		"cores": 1,
		"maxFrequency": 0.20
	}
]
```

### Получение сведений о процессоре по ID

Метод: `GET /api/v1/processors/{id}`

Этот запрос возвращает информацию о процессоре по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/processors/5`**
```json
{
	"id": 5,
	"model": "Apple M3",
	"technologyNode": 3,
	"cores": 8,
	"maxFrequency": 3.70
}
```

### Полное изменение данных о процессоре по ID

Метод: `PUT /api/v1/processors/{id}`

Этот запрос изменяет данные о процессоре и возвращает обновленные сведения.

**Пример ответа на запрос: `PUT /api/v1/processors/5`**
```json
{
    "model": "Apple M1",
    "technologyNode": 5,
    "cores": 8,
    "maxFrequency": 3.2
}
```

**Пример ответа:**
```json
{
	"id": 5,
	"model": "Apple M1",
	"technologyNode": 5,
	"cores": 8,
	"maxFrequency": 3.2
}
```

### Частичное изменение данных процессора по ID

Метод: `PATCH /api/v1/processors/{id}`

Этот запрос частично изменяет данные о процессоре и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/processors/5`**
```json
{
	"model": "Intel Core i9 9900K"
}
```

**Пример ответа:**
```json
{
	"id": 5,
	"model": "Intel Core i9 9900K",
	"technologyNode": 5,
	"cores": 8,
	"maxFrequency": 3.20
}
```

### Удаление процессора по ID

Метод: `DELETE /api/v1/processors/{id}`

Этот запрос удаляет данные о процессоре и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/processors/5`**
```json
{
    "id": 5,
    "model": "Intel Core i9 9900K",
    "technologyNode": 5,
    "cores": 8,
    "maxFrequency": 3.20
}
```

## Страны

### Добавление новой страны

Метод: `POST /api/v1/countries/

Этот запрос создает новую страну и возвращает сведения о созданной стране. Для выполнения запроса обязательно необходимо указывать название страны в теле запроса.

**Пример запроса: `/api/v1/countries/`**
```json
{
	"name": "North Korea"
}
```

**Пример ответа:**
```json
{
    "id": 5,
    "name": "North Korea"
}
```

### Получение списка всех стран

Метод: `GET /api/v1/countries/`

Этот запрос возвращает список всех стран, отсортированный в алфавитном порядке по названию стран.

**Пример ответа на запрос: `GET /api/v1/countries/`**
```json
[
	{
		"id": 3,
		"name": "China"
	},
	{
		"id": 2,
		"name": "England"
	},
	{
		"id": 5,
		"name": "North Korea"
	},
	{
		"id": 1,
		"name": "USA"
	}
]
```

### Получение сведений о стране по ID

Метод: `GET /api/v1/countries/{id}`

Этот запрос возвращает информацию о стране по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/countries/3`**
```json
{
	"id": 3,
	"name": "China"
}
```

### Полное изменение данных страны по ID

Метод: `PUT /api/v1/countries/{id}`

Этот запрос изменяет данные о стране и возвращает обновленные сведения.

**Пример ответа на запрос: `PUT /api/v1/countries/5`**
```json
{
	"name": "South Korea"
}
```

**Пример ответа:**
```json
{
	"id": 5,
	"name": "South Korea"
}
```

### Частичное изменение данных страны по ID

Метод: `PATCH /api/v1/countries/{id}`

Этот запрос частично изменяет данные о стране и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/countries/5`**
```json
{
	"name": "Japan"
}
```

**Пример ответа:**
```json
{
	"id": 5,
	"name": "Japan"
}
```

### Удаление страны по ID

Метод: `DELETE /api/v1/countries/{id}`

Этот запрос удаляет данные о стране и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/countries/5`**
```json
{
	"id": 5,
	"name": "Japan"
}
```

## Разрешение экрана

### Добавление нового разрешения экрана

Метод: `POST /api/v1/screen-resolutions/

Этот запрос создает новое разрешение экрана и возвращает сведения о нем. Для выполнения запроса обязательно необходимо указывать количество пикселей по горизонтали и по вертикали.

**Пример запроса: `/api/v1/screen-resolutions/`**
```json
{
	"horizontalPixels": 1920,	
	"verticalPixels": 1080
}
```

**Пример ответа:**
```json
{
    "id": 5,
    "horizontalPixels": 1920,
    "verticalPixels": 1080
}
```

### Получение списка всех разрешений экранов

Метод: `GET /api/v1/screen-resolutions/`

Этот запрос возвращает список всех разрешений экранов, отсортированный в порядке возрастания по количеству пикселей по горизонтали и по вертикали.

**Пример ответа на запрос: `GET /api/v1/screen-resolutions/`**
```json
[
	{
		"id": 4,
		"horizontalPixels": 240,
		"verticalPixels": 320
	},
	{
		"id": 3,
		"horizontalPixels": 1080,
		"verticalPixels": 2400
	},
	{
		"id": 2,
		"horizontalPixels": 1284,
		"verticalPixels": 2778
	},
	{
		"id": 1,
		"horizontalPixels": 1440,
		"verticalPixels": 3120
	},
	{
		"id": 5,
		"horizontalPixels": 1920,
		"verticalPixels": 1080
	}
]
```

### Получение сведений о разрешении экрана по ID

Метод: `GET /api/v1/screen-resolutions/{id}`

Этот запрос возвращает информацию о разрешении экрана по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/screen-resolutions/5`**
```json
{
    "id": 5,
    "horizontalPixels": 1920,
    "verticalPixels": 1080
}
```

### Полное изменение данных о разрешении экрана по ID

Метод: `PUT /api/v1/screen-resolutions/{id}`

Этот запрос изменяет данные о разрешении экрана и возвращает обновленные сведения.

**Пример ответа на запрос: `PUT /api/v1/screen-resolutions/5`**
```json
{
	"horizontalPixels": 1440,
	"verticalPixels": 2560
}
```

**Пример ответа:**
```json
{
	"id": 5,
	"horizontalPixels": 1440,
	"verticalPixels": 2560
}
```

### Частичное изменение данных о разрешении экрана по ID

Метод: `PATCH /api/v1/screen-resolutions/{id}`

Этот запрос частично изменяет данные о разрешении экрана и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/screen-resolutions/5`**
```json
{
	"horizontalPixels": 720
}
```

**Пример ответа:**
```json
{
	"id": 5,
	"horizontalPixels": 720,
	"verticalPixels": 2560
}
```

### Удаление разрешения экрана по ID

Метод: `DELETE /api/v1/screen-resolutions/{id}`

Этот запрос удаляет данные о разрешении экрана и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/screen-resolutions/5`**
```json
{
    "id": 5,
    "horizontalPixels": 720,
    "verticalPixels": 2560
}
```

## Сенсор камеры

### Добавление нового сенсора камеры

Метод: `POST /api/v1/camera-sensors/

Этот запрос создает новый сенсор камеры и возвращает его. Для создания сенсора необходимо передать в теле запроса название сенсора, количество мегапикселей, размер матрицы, размер пикселя.

**Пример запроса: `/api/v1/camera-sensors/`**
```json
{
	"sensorName": "Sony IMX886",
	"megapixels": 12.00,
	"matrixSize": "1/2.54",
	"pixelSize": "1.21µm"
}
```

**Пример ответа:**
```json
{
    "id": 13,
    "sensorName": "Sony IMX886",
    "megapixels": 12.00,
    "matrixSize": "1/2.54",
    "pixelSize": "1.21µm"
}
```

### Получение списка всех сенсоров камер

Метод: `GET /api/v1/camera-sensors/`

Этот запрос возвращает список всех сенсоров камер мобильных телефонов, отсортированный по возрастанию количества мегапикселей.

**Пример ответа на запрос: `GET /api/v1/camera-sensors/`**
```json
[
    {
        "id": 4,
        "sensorName": "Samsung S5K3T2",
        "megapixels": 11.10,
        "matrixSize": "1/3.6",
        "pixelSize": "1.22µm"
    },
    {
        "id": 3,
        "sensorName": "Sony IMX386",
        "megapixels": 12.00,
        "matrixSize": "1/2.55",
        "pixelSize": "1.22µm"
    },
    {
        "id": 13,
        "sensorName": "Sony IMX886",
        "megapixels": 12.00,
        "matrixSize": "1/2.54",
        "pixelSize": "1.21µm"
    },
    {
        "id": 6,
        "sensorName": "Sony IMX714",
        "megapixels": 12.00,
        "matrixSize": "1/3.6",
        "pixelSize": "1µm"
    }
]
```

### Получение сведений о сенсоре камеры по ID

Метод: `GET /api/v1/camera-sensors/{id}`

Этот запрос возвращает информацию о сенсоре камеры по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/camera-sensors/5`**
```json
{
	"id": 5,
	"sensorName": "Sony IMX800",
	"megapixels": 48.00,
	"matrixSize": "1/1.33",
	"pixelSize": "1.22µm"
}
```

### Полное изменение данных сенсора камеры по ID

Метод: `PUT /api/v1/camera-sensors/{id}`

Этот запрос изменяет данные о сенсоре камеры и возвращает обновленные сведения.

**Пример ответа на запрос: `PUT /api/v1/camera-sensors/13`**
```json
{
	"sensorName": "Sony IMX777",
	"megapixels": 99.00,
	"matrixSize": "1/1.54",
	"pixelSize": "1.12µm"
}
```

**Пример ответа:**
```json
{
    "id": 13,
    "sensorName": "Sony IMX777",
    "megapixels": 99.00,
    "matrixSize": "1/1.54",
    "pixelSize": "1.12µm"
}
```

### Частичное изменение данных сенсора камеры по ID

Метод: `PATCH /api/v1/camera-sensors/{id}`

Этот запрос частично изменяет данные сенсора камеры и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/camera-sensors/13`**
```json
{
    "megapixels": 400.00
}
```

**Пример ответа:**
```json
{
    "id": 13,
    "sensorName": "Sony IMX777",
    "megapixels": 400.00,
    "matrixSize": "1/1.54",
    "pixelSize": "1.12µm"
}
```

### Удаление сенсора камеры по ID

Метод: `DELETE /api/v1/camera-sensors/{id}`

Этот запрос удаляет данные о сенсоре камеры по уникальному идентификатору и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/camera-sensors/13`**
```json
{
    "id": 13,
    "sensorName": "Sony IMX777",
    "megapixels": 400.00,
    "matrixSize": "1/1.54",
    "pixelSize": "1.12µm"
}
```

## Производитель

### Добавление нового производителя

Метод: `POST /api/v1/manufacturers/

Этот запрос создает нового производителя и возвращает его. Для создания производителя необходимо указать уникальный идентификатор страны производителя и название страны.

**Пример запроса: `/api/v1/manufacturers/`**
```json
{
	"name": "Amazon",
	"country": {
		"id": 1
	}
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "name": "Amazon",
    "country": {
        "id": 1,
        "name": "USA"
    }
}
```

### Получение списка всех производителей

Метод: `GET /api/v1/manufacturers/`

Этот запрос возвращает список всех производителей мобильных телефонов, отсортированный согласно названиям производителей по алфавитному порядку.

**Пример ответа на запрос: `GET /api/v1/manufacturers/`**
```json
[
    {
        "id": 6,
        "name": "Amazon",
        "country": {
            "id": 1,
            "name": "USA"
        }
    },
    {
        "id": 2,
        "name": "Apple",
        "country": {
            "id": 1,
            "name": "USA"
        }
    },
    {
        "id": 5,
        "name": "BQ",
        "country": {
            "id": 3,
            "name": "China"
        }
    },
    {
        "id": 1,
        "name": "Google",
        "country": {
            "id": 1,
            "name": "USA"
        }
    },
    {
        "id": 4,
        "name": "Motorola",
        "country": {
            "id": 1,
            "name": "USA"
        }
    },
    {
        "id": 3,
        "name": "Nothing",
        "country": {
            "id": 2,
            "name": "England"
        }
    }
]
```

### Получение сведений о производителе по ID

Метод: `GET /api/v1/manufacturers/{id}`

Этот запрос возвращает информацию о производителей мобильного телефона по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/manufacturers/6`**
```json
{
	"id": 6,
	"name": "Amazon",
	"country": {
		"id": 1,
		"name": "USA"
	}
}
```

### Полное изменение данных производителя по ID

Метод: `PUT /api/v1/manufacturers/{id}`

Этот запрос изменяет все данные о производителе мобильного телефона и возвращает обновленные сведения.

**Пример ответа на запрос: `PUT /api/v1/manufacturers/6`**
```json
{
	"name": "Xiaomi",
	"country": {
		"id": 3
	}
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "name": "Xiaomi",
    "country": {
        "id": 3,
        "name": "China"
    }
}
```

### Частичное изменение данных производителя по ID

Метод: `PATCH /api/v1/manufacturers/{id}`

Этот запрос частично изменяет данные производителя мобильного телефона и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/manufacturers/6`**
```json
{
	"country": {
		"id": 2
	}
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "name": "Xiaomi",
    "country": {
        "id": 2,
        "name": "England"
    }
}
```

### Удаление производителя по ID

Метод: `DELETE /api/v1/manufacturers/{id}`

Этот запрос удаляет данные о производителе по уникальному идентификатору и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/manufacturers/6`**
```json
{
    "id": 6,
    "name": "Xiaomi",
    "country": {
        "id": 2,
        "name": "England"
    }
}
```

## Дисплей

### Добавление нового дисплея

Метод: `POST /api/v1/displays/

Этот запрос создает новый дисплей телефона и возвращает сведения о нём. Для создания дисплея необходимо указать частоту обновления дисплея, диагональ дисплея, тип матрицы дисплея (может быть AMOLED, IPS или TN, а также соответственно может принимать значения 0, 1 и 2). Кроме того, необходимо указать уникальный идентификатор разрешения экрана, соответствующий данному дисплею.

**Пример запроса: `/api/v1/displays/`**
```json
{
    "refreshRate": 90,
    "diagonal": 2.40,
    "displayType": "IPS",
    "resolution": {
        "id": 4
    }
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "refreshRate": 90,
    "diagonal": 2.40,
    "displayType": "IPS",
    "resolution": {
        "id": 4,
        "horizontalPixels": 240,
        "verticalPixels": 320
    }
}
```

### Получение списка всех дисплеев

Метод: `GET /api/v1/displays/`

Этот запрос возвращает список всех дисплеев мобильных телефонов, отсортированный в порядке возрастания по диагонали экрана и частоте обновления экрана.

**Пример ответа на запрос: `GET /api/v1/displays/`**
```json
[
    {
        "id": 4,
        "refreshRate": 60,
        "diagonal": 2.40,
        "displayType": "TN",
        "resolution": {
            "id": 4,
            "horizontalPixels": 240,
            "verticalPixels": 320
        }
    },
    {
        "id": 6,
        "refreshRate": 90,
        "diagonal": 2.40,
        "displayType": "IPS",
        "resolution": {
            "id": 4,
            "horizontalPixels": 240,
            "verticalPixels": 320
        }
    },
    {
        "id": 3,
        "refreshRate": 120,
        "diagonal": 6.67,
        "displayType": "AMOLED",
        "resolution": {
            "id": 3,
            "horizontalPixels": 1080,
            "verticalPixels": 2400
        }
    },
    {
        "id": 2,
        "refreshRate": 120,
        "diagonal": 6.70,
        "displayType": "AMOLED",
        "resolution": {
            "id": 2,
            "horizontalPixels": 1284,
            "verticalPixels": 2778
        }
    },
    {
        "id": 1,
        "refreshRate": 120,
        "diagonal": 6.71,
        "displayType": "AMOLED",
        "resolution": {
            "id": 1,
            "horizontalPixels": 1440,
            "verticalPixels": 3120
        }
    }
]
```

### Получение сведений о дисплее по ID

Метод: `GET /api/v1/displays/{id}`

Этот запрос возвращает информацию о дисплее мобильного телефона по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/displays/6`**
```json
{
	"id": 6,
	"refreshRate": 90,
	"diagonal": 2.40,
	"displayType": "IPS",
	"resolution": {
		"id": 4,
		"horizontalPixels": 240,
		"verticalPixels": 320
	}
}
```

### Полное изменение данных дисплея по ID

Метод: `PUT /api/v1/displays/{id}`

Этот запрос изменяет все данные о дисплее мобильного телефона по указанному идентификатору и возвращает обновленные сведения.

**Пример ответа на запрос: `PUT /api/v1/cameras/6`**
```json
{
    "refreshRate": 75,
    "diagonal": 5,
    "displayType": "TN",
    "resolution": {
        "id": 1
    }
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "refreshRate": 75,
    "diagonal": 5,
    "displayType": "TN",
    "resolution": {
        "id": 1,
        "horizontalPixels": 1440,
        "verticalPixels": 3120
    }
}
```

### Частичное изменение данных дисплея по ID

Метод: `PATCH /api/v1/displays/{id}`

Этот запрос частично изменяет данные дисплея мобильного телефона по указанному идентификатору и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/cameras/6`**
```json
{
	"resolution": {
		"id": 4
	}
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "refreshRate": 75,
    "diagonal": 5.00,
    "displayType": "TN",
    "resolution": {
        "id": 4,
        "horizontalPixels": 240,
        "verticalPixels": 320
    }
}
```

### Удаление дисплея по ID

Метод: `DELETE /api/v1/displays/{id}`

Этот запрос удаляет данные о дисплее по уникальному идентификатору и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/displays/6`**
```json
{
	"id": 6,
	"refreshRate": 75,
	"diagonal": 5.00,
	"displayType": "TN",
	"resolution": {
		"id": 4,
		"horizontalPixels": 240,
		"verticalPixels": 32
	}
}
```

## Камера

### Добавление новой камеры

Метод: `POST /api/v1/cameras/

Этот запрос создает новую камеру телефона и возвращает сведения о нём. Для создания записи о телефоне необходимо указать тип камеры (STANDARD, TELEPHOTO, MACRO, WIDE_ANGLE, ULTRA_WIDE_ANGLE, FRONT_FACING или же соответствующие числовые значения от 0 до 5), наличие системы оптической стабилизации, уникальный идентификатор сенсора камеры.

**Пример запроса: `/api/v1/cameras/`**
```json
{
    "cameraType": "ULTRA_WIDE_ANGLE",
    "hasOpticalStabilization": true,
    "sensor": {
        "id": 5
    }
}
```

**Пример ответа:**
```json
{
    "id": 13,
    "cameraType": "ULTRA_WIDE_ANGLE",
    "hasOpticalStabilization": true,
    "sensor": {
        "id": 5,
        "sensorName": "Sony IMX800",
        "megapixels": 48.00,
        "matrixSize": "1/1.33",
        "pixelSize": "1.22µm"
    }
}
```

### Получение списка всех камер

Метод: `GET /api/v1/cameras/`

Этот запрос возвращает список всех камер мобильных телефонов, отсортированный в порядке возрастания уникальных идентификаторов.

**Пример ответа на запрос: `GET /api/v1/cameras/`**
```json
[
    {
        "id": 1,
        "cameraType": "STANDARD",
        "hasOpticalStabilization": true,
        "sensor": {
            "id": 1,
            "sensorName": "Samsung GN2",
            "megapixels": 50.00,
            "matrixSize": "1/1.31",
            "pixelSize": "1.22µm"
        }
    },
    {
        "id": 2,
        "cameraType": "TELEPHOTO",
        "hasOpticalStabilization": true,
        "sensor": {
            "id": 2,
            "sensorName": "Sony IMX566",
            "megapixels": 48.00,
            "matrixSize": "1/2",
            "pixelSize": "0.8µm"
        }
    },
    {
        "id": 3,
        "cameraType": "ULTRA_WIDE_ANGLE",
        "hasOpticalStabilization": false,
        "sensor": {
            "id": 3,
            "sensorName": "Sony IMX386",
            "megapixels": 12.00,
            "matrixSize": "1/2.55",
            "pixelSize": "1.22µm"
        }
    }
]
```

### Получение сведений о камере по ID

Метод: `GET /api/v1/cameras/{id}`

Этот запрос возвращает полную информацию о камере мобильного телефона по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/cameras/13`**
```json
{
    "id": 13,
    "cameraType": "ULTRA_WIDE_ANGLE",
    "hasOpticalStabilization": true,
    "sensor": {
        "id": 5,
        "sensorName": "Sony IMX800",
        "megapixels": 48.00,
        "matrixSize": "1/1.33",
        "pixelSize": "1.22µm"
    }
}
```

### Полное изменение данных камеры по ID

Метод: `PUT /api/v1/cameras/{id}`

Этот запрос изменяет все данные о камере мобильного телефона по указанному идентификатору и возвращает обновленные сведения.

**Пример ответа на запрос: `PUT /api/v1/cameras/13`**
```json
{
    "cameraType": 5,
    "hasOpticalStabilization": false,
    "sensor": {
        "id": 2
    }
}
```

**Пример ответа:**
```json
{
    "id": 13,
    "cameraType": "FRONT_FACING",
    "hasOpticalStabilization": false,
    "sensor": {
        "id": 2,
        "sensorName": "Sony IMX566",
        "megapixels": 48.00,
        "matrixSize": "1/2",
        "pixelSize": "0.8µm"
    }
}
```

### Частичное изменение данных камеры по ID

Метод: `PATCH /api/v1/cameras/{id}`

Этот запрос частично изменяет данные камеры мобильного телефона по указанному идентификатору и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/cameras/13`**
```json
{
	"sensor": {
		"id": 1
	}
}
```

**Пример ответа:**
```json
{
    "id": 13,
    "cameraType": "FRONT_FACING",
    "hasOpticalStabilization": false,
    "sensor": {
        "id": 1,
        "sensorName": "Samsung GN2",
        "megapixels": 50.00,
        "matrixSize": "1/1.31",
        "pixelSize": "1.22µm"
    }
}
```

### Удаление камеры по ID

Метод: `DELETE /api/v1/cameras/{id}`

Этот запрос удаляет данные о камере по уникальному идентификатору и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/cameras/13`**
```json
{
    "id": 13,
    "cameraType": "FRONT_FACING",
    "hasOpticalStabilization": false,
    "sensor": {
        "id": 1,
        "sensorName": "Samsung GN2",
        "megapixels": 50.00,
        "matrixSize": "1/1.31",
        "pixelSize": "1.22µm"
    }
}
```

## Характеристика телефона

### Добавление новой характеристики

Метод: `POST /api/v1/specifications/

Для создания записи с характеристикой необходимо обязательно указать следующие параметры:
- `networkType`: тип сети (может быть `FREQUENCY_2G`, `FREQUENCY_3G`, `FREQUENCY_4G`, `FREQUENCY_5G` или принимать значение от 0 до 3)
- `simCount`: количество сим-карт
- `hasWifi`: наличие технологии WiFi (boolean)
- `hasNfc`: наличие технологии NFC (boolean)
- `hasBluetooth`: наличие технологии Bluetooth (boolean)
- `height`: высота телефона
- `width`: ширина телефона
- `thickness`: толщина телефона
- `weight`: вес телефона
- `material`: материал телефона (может быть `ALUMINUM`, `STEEL`, `TITANIUM`, `PLASTIC`, `GOLD`, `LEATHER`, `GLASS` или принимать значение от 0 до 6)
- `chargerType`: тип зарядки (может быть `USB_TYPE_C`, `MICRO_USB`, `MINI_USB`, `LIGHTNING` или принимать значение от 0 до 3)
- `operatingSystem`: идентификационный номер операционной системы
- `display`: id дисплея
- `processor`: id процессора
- `battery`: id аккумуляторной батареи
  По желанию можно также указать идентификационные номера камер.

**Пример запроса: `/api/v1/specifications/`**
```json
{
	"networkType": "FREQUENCY_4G",
	"simCount": 1,
	"hasWifi": true,
	"hasNfc": false,
	"hasBluetooth": true,
	"height": 160.90,
	"width": 72.90,
	"thickness": 9.70,
	"weight": 210.00,
	"material": "ALUMINUM",
	"chargerType": "MICRO_SB",
	"operatingSystem": {
		"id": 1
	},
	"display": {
		"id": 1
	},
	"processor": {
		"id": 1
	},
	"battery": {
		"id": 1
	},
	"cameras": [
		{
			"id": 1
		},
		{
			"id": 2
		}
	]
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "networkType": "FREQUENCY_4G",
    "simCount": 1,
    "hasWifi": true,
    "hasNfc": false,
    "hasBluetooth": true,
    "height": 160.90,
    "width": 72.90,
    "thickness": 9.70,
    "weight": 210.00,
    "material": "ALUMINUM",
    "chargerType": "MICRO_USB",
    "operatingSystem": {
        "id": 1,
        "name": "Android",
        "version": "13"
    },
    "display": {
        "id": 1,
        "refreshRate": 120,
        "diagonal": 6.71,
        "displayType": "AMOLED",
        "resolution": {
            "id": 1,
            "horizontalPixels": 1440,
            "verticalPixels": 3120
        }
    },
    "processor": {
        "id": 1,
        "model": "Google Tensor G3",
        "technologyNode": 5,
        "cores": 8,
        "maxFrequency": 2.80
    },
    "battery": {
        "id": 1,
        "capacity": 5000,
        "batteryType": "LI_ION"
    },
    "cameras": [
        {
            "id": 1,
            "cameraType": "STANDARD",
            "hasOpticalStabilization": true,
            "sensor": {
                "id": 1,
                "sensorName": "Samsung GN2",
                "megapixels": 50.00,
                "matrixSize": "1/1.31",
                "pixelSize": "1.22µm"
            }
        },
        {
            "id": 2,
            "cameraType": "TELEPHOTO",
            "hasOpticalStabilization": true,
            "sensor": {
                "id": 2,
                "sensorName": "Sony IMX566",
                "megapixels": 48.00,
                "matrixSize": "1/2",
                "pixelSize": "0.8µm"
            }
        }
    ]
}
```

### Получение списка всех характеристик

Метод: `GET /api/v1/specifications/`

Этот запрос возвращает список всех характеристик мобильных телефонов, отсортированный в порядке возрастания уникальных идентификаторов.

**Пример ответа на запрос: `GET /api/v1/specifications/`**
```json
[
    {
        "id": 1,
        "networkType": "FREQUENCY_5G",
        "simCount": 1,
        "hasWifi": true,
        "hasNfc": true,
        "hasBluetooth": true,
        "height": 163.90,
        "width": 75.90,
        "thickness": 8.70,
        "weight": 210.00,
        "material": "ALUMINUM",
        "chargerType": "USB_TYPE_C",
        "operatingSystem": {
            "id": 1,
            "name": "Android",
            "version": "13"
        },
        "display": {
            "id": 1,
            "refreshRate": 120,
            "diagonal": 6.71,
            "displayType": "AMOLED",
            "resolution": {
                "id": 1,
                "horizontalPixels": 1440,
                "verticalPixels": 3120
            }
        },
        "processor": {
            "id": 1,
            "model": "Google Tensor G3",
            "technologyNode": 5,
            "cores": 8,
            "maxFrequency": 2.80
        },
        "battery": {
            "id": 1,
            "capacity": 5000,
            "batteryType": "LI_ION"
        },
        "cameras": [
            {
                "id": 1,
                "cameraType": "STANDARD",
                "hasOpticalStabilization": true,
                "sensor": {
                    "id": 1,
                    "sensorName": "Samsung GN2",
                    "megapixels": 50.00,
                    "matrixSize": "1/1.31",
                    "pixelSize": "1.22µm"
                }
            },
            {
                "id": 2,
                "cameraType": "TELEPHOTO",
                "hasOpticalStabilization": true,
                "sensor": {
                    "id": 2,
                    "sensorName": "Sony IMX566",
                    "megapixels": 48.00,
                    "matrixSize": "1/2",
                    "pixelSize": "0.8µm"
                }
            },
            {
                "id": 3,
                "cameraType": "ULTRA_WIDE_ANGLE",
                "hasOpticalStabilization": false,
                "sensor": {
                    "id": 3,
                    "sensorName": "Sony IMX386",
                    "megapixels": 12.00,
                    "matrixSize": "1/2.55",
                    "pixelSize": "1.22µm"
                }
            },
            {
                "id": 4,
                "cameraType": "FRONT_FACING",
                "hasOpticalStabilization": false,
                "sensor": {
                    "id": 4,
                    "sensorName": "Samsung S5K3T2",
                    "megapixels": 11.10,
                    "matrixSize": "1/3.6",
                    "pixelSize": "1.22µm"
                }
            }
        ]
    },
    {
        "id": 2,
        "networkType": "FREQUENCY_5G",
        "simCount": 1,
        "hasWifi": true,
        "hasNfc": true,
        "hasBluetooth": true,
        "height": 160.80,
        "width": 78.10,
        "thickness": 7.70,
        "weight": 240.00,
        "material": "TITANIUM",
        "chargerType": "USB_TYPE_C",
        "operatingSystem": {
            "id": 2,
            "name": "iOS",
            "version": "17.0"
        },
        "display": {
            "id": 2,
            "refreshRate": 120,
            "diagonal": 6.70,
            "displayType": "AMOLED",
            "resolution": {
                "id": 2,
                "horizontalPixels": 1284,
                "verticalPixels": 2778
            }
        },
        "processor": {
            "id": 2,
            "model": "Apple A17 Bionic",
            "technologyNode": 3,
            "cores": 6,
            "maxFrequency": 3.46
        },
        "battery": {
            "id": 2,
            "capacity": 4352,
            "batteryType": "LI_POL"
        },
        "cameras": [
            {
                "id": 5,
                "cameraType": "STANDARD",
                "hasOpticalStabilization": true,
                "sensor": {
                    "id": 5,
                    "sensorName": "Sony IMX800",
                    "megapixels": 48.00,
                    "matrixSize": "1/1.33",
                    "pixelSize": "1.22µm"
                }
            },
            {
                "id": 6,
                "cameraType": "ULTRA_WIDE_ANGLE",
                "hasOpticalStabilization": false,
                "sensor": {
                    "id": 6,
                    "sensorName": "Sony IMX714",
                    "megapixels": 12.00,
                    "matrixSize": "1/3.6",
                    "pixelSize": "1µm"
                }
            },
            {
                "id": 7,
                "cameraType": "TELEPHOTO",
                "hasOpticalStabilization": true,
                "sensor": {
                    "id": 7,
                    "sensorName": "Sony IMX772",
                    "megapixels": 12.00,
                    "matrixSize": "1/3.4",
                    "pixelSize": "1µm"
                }
            },
            {
                "id": 8,
                "cameraType": "FRONT_FACING",
                "hasOpticalStabilization": false,
                "sensor": {
                    "id": 8,
                    "sensorName": "Sony IMX663",
                    "megapixels": 12.00,
                    "matrixSize": "1/3.6",
                    "pixelSize": "1.22µm"
                }
            }
        ]
    }
]
```

### Получение сведений о характеристике телефона по ID

Метод: `GET /api/v1/specifications/{id}`

Этот запрос возвращает полную информацию о характеристике мобильного телефона по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/specifications/1`**
```json
[
    {
        "id": 1,
        "networkType": "FREQUENCY_5G",
        "simCount": 1,
        "hasWifi": true,
        "hasNfc": true,
        "hasBluetooth": true,
        "height": 163.90,
        "width": 75.90,
        "thickness": 8.70,
        "weight": 210.00,
        "material": "ALUMINUM",
        "chargerType": "USB_TYPE_C",
        "operatingSystem": {
            "id": 1,
            "name": "Android",
            "version": "13"
        },
        "display": {
            "id": 1,
            "refreshRate": 120,
            "diagonal": 6.71,
            "displayType": "AMOLED",
            "resolution": {
                "id": 1,
                "horizontalPixels": 1440,
                "verticalPixels": 3120
            }
        },
        "processor": {
            "id": 1,
            "model": "Google Tensor G3",
            "technologyNode": 5,
            "cores": 8,
            "maxFrequency": 2.80
        },
        "battery": {
            "id": 1,
            "capacity": 5000,
            "batteryType": "LI_ION"
        },
        "cameras": [
            {
                "id": 1,
                "cameraType": "STANDARD",
                "hasOpticalStabilization": true,
                "sensor": {
                    "id": 1,
                    "sensorName": "Samsung GN2",
                    "megapixels": 50.00,
                    "matrixSize": "1/1.31",
                    "pixelSize": "1.22µm"
                }
            },
            {
                "id": 2,
                "cameraType": "TELEPHOTO",
                "hasOpticalStabilization": true,
                "sensor": {
                    "id": 2,
                    "sensorName": "Sony IMX566",
                    "megapixels": 48.00,
                    "matrixSize": "1/2",
                    "pixelSize": "0.8µm"
                }
            },
            {
                "id": 3,
                "cameraType": "ULTRA_WIDE_ANGLE",
                "hasOpticalStabilization": false,
                "sensor": {
                    "id": 3,
                    "sensorName": "Sony IMX386",
                    "megapixels": 12.00,
                    "matrixSize": "1/2.55",
                    "pixelSize": "1.22µm"
                }
            },
            {
                "id": 4,
                "cameraType": "FRONT_FACING",
                "hasOpticalStabilization": false,
                "sensor": {
                    "id": 4,
                    "sensorName": "Samsung S5K3T2",
                    "megapixels": 11.10,
                    "matrixSize": "1/3.6",
                    "pixelSize": "1.22µm"
                }
            }
        ]
    }
]
```

### Полное изменение данных характеристики телефона по ID

Метод: `PUT /api/v1/specifications/{id}`

Этот запрос изменяет все данные о характеристике мобильного телефона по указанному идентификатору и возвращает обновленные сведения.

**Пример ответа на запрос: `PUT /api/v1/specifications/6`**
```json
{
	"networkType": "FREQUENCY_3G",
	"simCount": 2,
	"hasWifi": false,
	"hasNfc": true,
	"hasBluetooth": false,
	"height": 169.70,
	"width": 78.80,
	"thickness": 8.90,
	"weight": 190.00,
	"material": "PLASTIC",
	"chargerType": "USB_TYPE_C",
	"operatingSystem": {
		"id": 3
	},
	"display": {
		"id": 2
	},
	"processor": {
		"id": 2
	},
	"battery": {
		"id": 3
	},
	"cameras": [
		{
			"id": 4
		},
		{
			"id": 3
		}
	]
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "networkType": "FREQUENCY_3G",
    "simCount": 2,
    "hasWifi": false,
    "hasNfc": true,
    "hasBluetooth": false,
    "height": 169.70,
    "width": 78.80,
    "thickness": 8.90,
    "weight": 190.00,
    "material": "PLASTIC",
    "chargerType": "USB_TYPE_C",
    "operatingSystem": {
        "id": 3,
        "name": "Android",
        "version": "12"
    },
    "display": {
        "id": 2,
        "refreshRate": 120,
        "diagonal": 6.70,
        "displayType": "AMOLED",
        "resolution": {
            "id": 2,
            "horizontalPixels": 1284,
            "verticalPixels": 2778
        }
    },
    "processor": {
        "id": 2,
        "model": "Apple A17 Bionic",
        "technologyNode": 3,
        "cores": 6,
        "maxFrequency": 3.46
    },
    "battery": {
        "id": 3,
        "capacity": 4500,
        "batteryType": "LI_ION"
    },
    "cameras": [
        {
            "id": 4,
            "cameraType": "FRONT_FACING",
            "hasOpticalStabilization": false,
            "sensor": {
                "id": 4,
                "sensorName": "Samsung S5K3T2",
                "megapixels": 11.10,
                "matrixSize": "1/3.6",
                "pixelSize": "1.22µm"
            }
        },
        {
            "id": 3,
            "cameraType": "ULTRA_WIDE_ANGLE",
            "hasOpticalStabilization": false,
            "sensor": {
                "id": 3,
                "sensorName": "Sony IMX386",
                "megapixels": 12.00,
                "matrixSize": "1/2.55",
                "pixelSize": "1.22µm"
            }
        }
    ]
}
```

### Частичное изменение данных характеристики по ID

Метод: `PATCH /api/v1/specifications/{id}`

Этот запрос частично изменяет данные характеристики мобильного телефона по указанному идентификатору и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/specifications/6`**
```json
{
	"networkType": "FREQUENCY_2G",
	"simCount": 4
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "networkType": "FREQUENCY_2G",
    "simCount": 4,
    "hasWifi": false,
    "hasNfc": true,
    "hasBluetooth": false,
    "height": 169.70,
    "width": 78.80,
    "thickness": 8.90,
    "weight": 190.00,
    "material": "PLASTIC",
    "chargerType": "USB_TYPE_C",
    "operatingSystem": {
        "id": 3,
        "name": "Android",
        "version": "12"
    },
    "display": {
        "id": 2,
        "refreshRate": 120,
        "diagonal": 6.70,
        "displayType": "AMOLED",
        "resolution": {
            "id": 2,
            "horizontalPixels": 1284,
            "verticalPixels": 2778
        }
    },
    "processor": {
        "id": 2,
        "model": "Apple A17 Bionic",
        "technologyNode": 3,
        "cores": 6,
        "maxFrequency": 3.46
    },
    "battery": {
        "id": 3,
        "capacity": 4500,
        "batteryType": "LI_ION"
    },
    "cameras": [
        {
            "id": 4,
            "cameraType": "FRONT_FACING",
            "hasOpticalStabilization": false,
            "sensor": {
                "id": 4,
                "sensorName": "Samsung S5K3T2",
                "megapixels": 11.10,
                "matrixSize": "1/3.6",
                "pixelSize": "1.22µm"
            }
        },
        {
            "id": 3,
            "cameraType": "ULTRA_WIDE_ANGLE",
            "hasOpticalStabilization": false,
            "sensor": {
                "id": 3,
                "sensorName": "Sony IMX386",
                "megapixels": 12.00,
                "matrixSize": "1/2.55",
                "pixelSize": "1.22µm"
            }
        }
    ]
}
```

### Удаление характеристики телефона по ID

Метод: `DELETE /api/v1/specifications/{id}`

Этот запрос удаляет данные о характеристике телефона по уникальному идентификатору и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/specifications/6`**
```json
{
    "id": 6,
    "networkType": "FREQUENCY_2G",
    "simCount": 4,
    "hasWifi": false,
    "hasNfc": true,
    "hasBluetooth": false,
    "height": 169.70,
    "width": 78.80,
    "thickness": 8.90,
    "weight": 190.00,
    "material": "PLASTIC",
    "chargerType": "USB_TYPE_C",
    "operatingSystem": {
        "id": 3,
        "name": "Android",
        "version": "12"
    },
    "display": {
        "id": 2,
        "refreshRate": 120,
        "diagonal": 6.70,
        "displayType": "AMOLED",
        "resolution": {
            "id": 2,
            "horizontalPixels": 1284,
            "verticalPixels": 2778
        }
    },
    "processor": {
        "id": 2,
        "model": "Apple A17 Bionic",
        "technologyNode": 3,
        "cores": 6,
        "maxFrequency": 3.46
    },
    "battery": {
        "id": 3,
        "capacity": 4500,
        "batteryType": "LI_ION"
    },
    "cameras": [
        {
            "id": 4,
            "cameraType": "FRONT_FACING",
            "hasOpticalStabilization": false,
            "sensor": {
                "id": 4,
                "sensorName": "Samsung S5K3T2",
                "megapixels": 11.10,
                "matrixSize": "1/3.6",
                "pixelSize": "1.22µm"
            }
        },
        {
            "id": 3,
            "cameraType": "ULTRA_WIDE_ANGLE",
            "hasOpticalStabilization": false,
            "sensor": {
                "id": 3,
                "sensorName": "Sony IMX386",
                "megapixels": 12.00,
                "matrixSize": "1/2.55",
                "pixelSize": "1.22µm"
            }
        }
    ]
}
```

## Вариация телефона

### Добавление новой вариации телефона

Метод: `POST /api/v1/variants/

Этот запрос создает новую вариацию телефона. Для добавления вариации в теле запроса обязательно необходимо указать размер оперативной памяти, размер постоянной памяти и цвет телефона (цвет телефона может иметь значения RED, ORANGE, YELLOW, GREEN, BLUE, DARK_BLUE, PURPLE, BLACK, WHITE, PINK, GOLD или же значения в промежутке от 0 до 10 соответственно).

**Пример запроса: `/api/v1/variants/`**
```json
{
	"romSize": 1,
	"ramSize": 1,
	"color": "BLACK"
}
```

**Пример ответа:**
```json
{
	"id": 24,
	"romSize": 6,
	"ramSize": 128,
	"color": "ORANGE"
}
```

### Получение списка всех вариаций телефонов

Метод: `GET /api/v1/variants/`

Этот запрос возвращает список всех вариаций мобильных телефонов, отсортированный в порядке возрастания размеров оперативной памяти и постоянной памяти телефона.

**Пример ответа на запрос: `GET /api/v1/variants/`**
```json
[
	{
		"id": 23,
		"romSize": 1,
		"ramSize": 1,
		"color": "BLACK"
	},
	{
		"id": 20,
		"romSize": 128,
		"ramSize": 8,
		"color": "BLACK"
	},
	{
		"id": 18,
		"romSize": 128,
		"ramSize": 8,
		"color": "WHITE"
	},
	{
		"id": 6,
		"romSize": 256,
		"ramSize": 8,
		"color": "BLUE"
	},
	{
		"id": 7,
		"romSize": 256,
		"ramSize": 8,
		"color": "DARK_BLUE"
	},
	{
		"id": 4,
		"romSize": 256,
		"ramSize": 8,
		"color": "BLACK"
	}
]
```

### Получение сведений о вариации телефона по ID

Метод: `GET /api/v1/variants/{id}`

Этот запрос возвращает полную информацию о вариации мобильного телефона по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/variants/24`**
```json
{
    "id": 24,
    "romSize": 6,
    "ramSize": 128,
    "color": "ORANGE"
}
```

### Полное изменение данных вариации телефона по ID

Метод: `PUT /api/v1/variants/{id}`

Этот запрос изменяет все данные о вариации мобильного телефона по указанному идентификатору и возвращает обновленные сведения.

**Пример ответа на запрос: `PUT /api/v1/variants/24`**
```json
{
	"romSize": 10,
	"ramSize": 100,
	"color": "GREEN"
}
```

**Пример ответа:**
```json
{
	"id": 24,
	"romSize": 10,
	"ramSize": 100,
	"color": "GREEN"
}
```

### Частичное изменение данных вариации телефона по ID

Метод: `PATCH /api/v1/variants/{id}`

Этот запрос частично изменяет вариацию мобильного телефона по указанному идентификатору и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/variants/24`**
```json
{
    "color": "WHITE"
}
```

**Пример ответа:**
```json
{
    "id": 24,
    "romSize": 10,
    "ramSize": 100,
    "color": "WHITE"
}
```

### Удаление вариации телефона по ID

Метод: `DELETE /api/v1/variants/{id}`

Этот запрос удаляет вариацию телефона по уникальному идентификатору и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/variants/13`**
```json
{
    "id": 24,
    "romSize": 10,
    "ramSize": 100,
    "color": "WHITE"
}
```

## Телефон

### Добавление нового телефона

Метод: `POST /api/v1/phones/

Этот запрос создает новую запись о телефоне и возвращает ее в теле ответа.

**Пример запроса: `/api/v1/phones/`**
```json
{
    "manufacturer": {
        "id": 3
    },
    "model": "Nothing Phone Zero",
    "specification": {
        "id": 2
    },
    "releaseDate": "2021-06-24T00:00:00",
    "phoneVariants": [
        {
            "variant": {
                "id": 23
            },
            "quantity": 230,
            "price": 3000.00
        }
    ]
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "manufacturer": {
        "id": 3,
        "name": "Nothing",
        "country": {
            "id": 2,
            "name": "England"
        }
    },
    "model": "Nothing Phone Zero",
    "specification": {
        "id": 2,
        "networkType": "FREQUENCY_5G",
        "simCount": 1,
        "hasWifi": true,
        "hasNfc": true,
        "hasBluetooth": true,
        "height": 160.80,
        "width": 78.10,
        "thickness": 7.70,
        "weight": 240.00,
        "material": "TITANIUM",
        "chargerType": "USB_TYPE_C",
        "operatingSystem": {
            "id": 2,
            "name": "iOS",
            "version": "17.0"
        },
        "display": {
            "id": 2,
            "refreshRate": 120,
            "diagonal": 6.70,
            "displayType": "AMOLED",
            "resolution": {
                "id": 2,
                "horizontalPixels": 1284,
                "verticalPixels": 2778
            }
        },
        "processor": {
            "id": 2,
            "model": "Apple A17 Bionic",
            "technologyNode": 3,
            "cores": 6,
            "maxFrequency": 3.46
        },
        "battery": {
            "id": 2,
            "capacity": 4352,
            "batteryType": "LI_POL"
        },
        "cameras": [
            {
                "id": 5,
                "cameraType": "STANDARD",
                "hasOpticalStabilization": true,
                "sensor": {
                    "id": 5,
                    "sensorName": "Sony IMX800",
                    "megapixels": 48.00,
                    "matrixSize": "1/1.33",
                    "pixelSize": "1.22µm"
                }
            },
            {
                "id": 6,
                "cameraType": "ULTRA_WIDE_ANGLE",
                "hasOpticalStabilization": false,
                "sensor": {
                    "id": 6,
                    "sensorName": "Sony IMX714",
                    "megapixels": 12.00,
                    "matrixSize": "1/3.6",
                    "pixelSize": "1µm"
                }
            },
            {
                "id": 7,
                "cameraType": "TELEPHOTO",
                "hasOpticalStabilization": true,
                "sensor": {
                    "id": 7,
                    "sensorName": "Sony IMX772",
                    "megapixels": 12.00,
                    "matrixSize": "1/3.4",
                    "pixelSize": "1µm"
                }
            },
            {
                "id": 8,
                "cameraType": "FRONT_FACING",
                "hasOpticalStabilization": false,
                "sensor": {
                    "id": 8,
                    "sensorName": "Sony IMX663",
                    "megapixels": 12.00,
                    "matrixSize": "1/3.6",
                    "pixelSize": "1.22µm"
                }
            }
        ]
    },
    "releaseDate": "2021-06-24T00:00:00",
    "phoneVariants": [
        {
            "variant": {
                "id": 23,
                "romSize": 1,
                "ramSize": 1,
                "color": "BLACK"
            },
            "quantity": 230,
            "price": 3000.00
        }
    ]
}
```

### Получение списка всех телефонов

Метод: `GET /api/v1/phones/`

Этот запрос возвращает список всех мобильных телефонов, отсортированный по модели телефона в алфавитном порядке.

**Пример ответа на запрос: `GET /api/v1/phones/`**
```json
[
    {
        "id": 6,
        "manufacturer": {
            "id": 3,
            "name": "Nothing",
            "country": {
                "id": 2,
                "name": "England"
            }
        },
        "model": "Nothing Phone Zero",
        "specification": {
            "id": 2,
            "networkType": "FREQUENCY_5G",
            "simCount": 1,
            "hasWifi": true,
            "hasNfc": true,
            "hasBluetooth": true,
            "height": 160.80,
            "width": 78.10,
            "thickness": 7.70,
            "weight": 240.00,
            "material": "TITANIUM",
            "chargerType": "USB_TYPE_C",
            "operatingSystem": {
                "id": 2,
                "name": "iOS",
                "version": "17.0"
            },
            "display": {
                "id": 2,
                "refreshRate": 120,
                "diagonal": 6.70,
                "displayType": "AMOLED",
                "resolution": {
                    "id": 2,
                    "horizontalPixels": 1284,
                    "verticalPixels": 2778
                }
            },
            "processor": {
                "id": 2,
                "model": "Apple A17 Bionic",
                "technologyNode": 3,
                "cores": 6,
                "maxFrequency": 3.46
            },
            "battery": {
                "id": 2,
                "capacity": 4352,
                "batteryType": "LI_POL"
            },
            "cameras": [
                {
                    "id": 5,
                    "cameraType": "STANDARD",
                    "hasOpticalStabilization": true,
                    "sensor": {
                        "id": 5,
                        "sensorName": "Sony IMX800",
                        "megapixels": 48.00,
                        "matrixSize": "1/1.33",
                        "pixelSize": "1.22µm"
                    }
                },
                {
                    "id": 6,
                    "cameraType": "ULTRA_WIDE_ANGLE",
                    "hasOpticalStabilization": false,
                    "sensor": {
                        "id": 6,
                        "sensorName": "Sony IMX714",
                        "megapixels": 12.00,
                        "matrixSize": "1/3.6",
                        "pixelSize": "1µm"
                    }
                },
                {
                    "id": 7,
                    "cameraType": "TELEPHOTO",
                    "hasOpticalStabilization": true,
                    "sensor": {
                        "id": 7,
                        "sensorName": "Sony IMX772",
                        "megapixels": 12.00,
                        "matrixSize": "1/3.4",
                        "pixelSize": "1µm"
                    }
                },
                {
                    "id": 8,
                    "cameraType": "FRONT_FACING",
                    "hasOpticalStabilization": false,
                    "sensor": {
                        "id": 8,
                        "sensorName": "Sony IMX663",
                        "megapixels": 12.00,
                        "matrixSize": "1/3.6",
                        "pixelSize": "1.22µm"
                    }
                }
            ]
        },
        "releaseDate": "2021-06-24T00:00:00",
		"phoneVariants": [
			{
				"variant": {
					"id": 23,
					"romSize": 1,
					"ramSize": 1,
					"color": "BLACK"
				},
				"quantity": 230,
				"price": 3000.00
			}
		]
    },
    {
        "id": 1,
        "manufacturer": {
            "id": 1,
            "name": "Google",
            "country": {
                "id": 1,
                "name": "USA"
            }
        },
        "model": "Pixel 8 Pro",
        "specification": {
            "id": 1,
            "networkType": "FREQUENCY_5G",
            "simCount": 1,
            "hasWifi": true,
            "hasNfc": true,
            "hasBluetooth": true,
            "height": 163.90,
            "width": 75.90,
            "thickness": 8.70,
            "weight": 210.00,
            "material": "ALUMINUM",
            "chargerType": "USB_TYPE_C",
            "operatingSystem": {
                "id": 1,
                "name": "Android",
                "version": "13"
            },
            "display": {
                "id": 1,
                "refreshRate": 120,
                "diagonal": 6.71,
                "displayType": "AMOLED",
                "resolution": {
                    "id": 1,
                    "horizontalPixels": 1440,
                    "verticalPixels": 3120
                }
            },
            "processor": {
                "id": 1,
                "model": "Google Tensor G3",
                "technologyNode": 5,
                "cores": 8,
                "maxFrequency": 2.80
            },
            "battery": {
                "id": 1,
                "capacity": 5000,
                "batteryType": "LI_ION"
            },
            "cameras": [
                {
                    "id": 1,
                    "cameraType": "STANDARD",
                    "hasOpticalStabilization": true,
                    "sensor": {
                        "id": 1,
                        "sensorName": "Samsung GN2",
                        "megapixels": 50.00,
                        "matrixSize": "1/1.31",
                        "pixelSize": "1.22µm"
                    }
                },
                {
                    "id": 2,
                    "cameraType": "TELEPHOTO",
                    "hasOpticalStabilization": true,
                    "sensor": {
                        "id": 2,
                        "sensorName": "Sony IMX566",
                        "megapixels": 48.00,
                        "matrixSize": "1/2",
                        "pixelSize": "0.8µm"
                    }
                },
                {
                    "id": 3,
                    "cameraType": "ULTRA_WIDE_ANGLE",
                    "hasOpticalStabilization": false,
                    "sensor": {
                        "id": 3,
                        "sensorName": "Sony IMX386",
                        "megapixels": 12.00,
                        "matrixSize": "1/2.55",
                        "pixelSize": "1.22µm"
                    }
                },
                {
                    "id": 4,
                    "cameraType": "FRONT_FACING",
                    "hasOpticalStabilization": false,
                    "sensor": {
                        "id": 4,
                        "sensorName": "Samsung S5K3T2",
                        "megapixels": 11.10,
                        "matrixSize": "1/3.6",
                        "pixelSize": "1.22µm"
                    }
                }
            ]
        },
        "releaseDate": "2023-10-04T00:00:00",
        "phoneVariants": [
            {
                "variant": {
                    "id": 1,
                    "romSize": 256,
                    "ramSize": 12,
                    "color": "WHITE"
                },
                "quantity": 12,
                "price": 89990.00
            },
            {
                "variant": {
                    "id": 2,
                    "romSize": 512,
                    "ramSize": 12,
                    "color": "BLUE"
                },
                "quantity": 78,
                "price": 92990.00
            }
        ]
    }
]
```

### Получение сведений о телефоне по ID

Метод: `GET /api/v1/phones/{id}`

Этот запрос возвращает полную информацию о мобильном телефоне по указанному уникальному идентификатору.

**Пример ответа на запрос: `GET /api/v1/phones/6`**
```json
{
    "id": 6,
    "manufacturer": {
        "id": 3,
        "name": "Nothing",
        "country": {
            "id": 2,
            "name": "England"
        }
    },
    "model": "Nothing Phone Zero",
    "specification": {
        "id": 2,
        "networkType": "FREQUENCY_5G",
        "simCount": 1,
        "hasWifi": true,
        "hasNfc": true,
        "hasBluetooth": true,
        "height": 160.80,
        "width": 78.10,
        "thickness": 7.70,
        "weight": 240.00,
        "material": "TITANIUM",
        "chargerType": "USB_TYPE_C",
        "operatingSystem": {
            "id": 2,
            "name": "iOS",
            "version": "17.0"
        },
        "display": {
            "id": 2,
            "refreshRate": 120,
            "diagonal": 6.70,
            "displayType": "AMOLED",
            "resolution": {
                "id": 2,
                "horizontalPixels": 1284,
                "verticalPixels": 2778
            }
        },
        "processor": {
            "id": 2,
            "model": "Apple A17 Bionic",
            "technologyNode": 3,
            "cores": 6,
            "maxFrequency": 3.46
        },
        "battery": {
            "id": 2,
            "capacity": 4352,
            "batteryType": "LI_POL"
        },
        "cameras": [
            {
                "id": 5,
                "cameraType": "STANDARD",
                "hasOpticalStabilization": true,
                "sensor": {
                    "id": 5,
                    "sensorName": "Sony IMX800",
                    "megapixels": 48.00,
                    "matrixSize": "1/1.33",
                    "pixelSize": "1.22µm"
                }
            },
            {
                "id": 6,
                "cameraType": "ULTRA_WIDE_ANGLE",
                "hasOpticalStabilization": false,
                "sensor": {
                    "id": 6,
                    "sensorName": "Sony IMX714",
                    "megapixels": 12.00,
                    "matrixSize": "1/3.6",
                    "pixelSize": "1µm"
                }
            },
            {
                "id": 7,
                "cameraType": "TELEPHOTO",
                "hasOpticalStabilization": true,
                "sensor": {
                    "id": 7,
                    "sensorName": "Sony IMX772",
                    "megapixels": 12.00,
                    "matrixSize": "1/3.4",
                    "pixelSize": "1µm"
                }
            },
            {
                "id": 8,
                "cameraType": "FRONT_FACING",
                "hasOpticalStabilization": false,
                "sensor": {
                    "id": 8,
                    "sensorName": "Sony IMX663",
                    "megapixels": 12.00,
                    "matrixSize": "1/3.6",
                    "pixelSize": "1.22µm"
                }
            }
        ]
    },
    "releaseDate": "2021-06-24T00:00:00",
    "phoneVariants": [
        {
            "variant": {
                "id": 23,
                "romSize": 1,
                "ramSize": 1,
                "color": "BLACK"
            },
            "quantity": 230,
            "price": 3000.00
        }
    ]
}
```

### Полное изменение данных телефона по ID

Метод: `PUT /api/v1/phones/{id}`

Этот запрос изменяет все данные о мобильном телефона по указанному идентификатору и возвращает обновленные сведения. Для обновления сведений необходимо указать id производителя, название модели телефона, id характеристики телефона, год выпуска.

**Пример ответа на запрос: `PUT /api/v1/phones/6`**
```json
{
    "manufacturer": {
        "id": 4
    },
    "model": "Nothing Sber Zero",
    "specification": {
        "id": 5
    },
    "releaseDate": "2021-06-24T00:00:00",
    "phoneVariants": [
        {
            "variant": {
                "id": 1
            },
            "quantity": 2320,
            "price": 30002.00
        }
    ]
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "manufacturer": {
        "id": 4,
        "name": "Motorola",
        "country": {
            "id": 1,
            "name": "USA"
        }
    },
    "model": "Nothing Sber Zero",
    "specification": {
        "id": 5,
        "networkType": "FREQUENCY_2G",
        "simCount": 3,
        "hasWifi": true,
        "hasNfc": true,
        "hasBluetooth": true,
        "height": 124.50,
        "width": 53.50,
        "thickness": 10.50,
        "weight": 83.00,
        "material": "PLASTIC",
        "chargerType": "MICRO_USB",
        "operatingSystem": {
            "id": 4,
            "name": "Symbian",
            "version": "9.3"
        },
        "display": {
            "id": 4,
            "refreshRate": 60,
            "diagonal": 2.40,
            "displayType": "TN",
            "resolution": {
                "id": 4,
                "horizontalPixels": 240,
                "verticalPixels": 320
            }
        },
        "processor": {
            "id": 4,
            "model": "Texas Instruments OMAP1510",
            "technologyNode": 5,
            "cores": 1,
            "maxFrequency": 0.20
        },
        "battery": {
            "id": 4,
            "capacity": 1000,
            "batteryType": "LI_POL"
        },
        "cameras": []
    },
    "releaseDate": "2021-06-24T00:00:00",
    "phoneVariants": [
        {
            "variant": {
                "id": 1,
                "romSize": 256,
                "ramSize": 12,
                "color": "WHITE"
            },
            "quantity": 2320,
            "price": 30002.00
        }
    ]
}
```

### Частичное изменение данных телефона по ID

Метод: `PATCH /api/v1/phones/{id}`

Этот запрос частично изменяет данные мобильного телефона по указанному идентификатору и возвращает обновленные сведения.

**Пример запроса: `PATCH /api/v1/phones/6`**
```json
{
    "manufacturer": {
        "id": 5
    },
    "model": "Nokia 3120 Classic"
}
```

**Пример ответа:**
```json
{
    "id": 6,
    "manufacturer": {
        "id": 5,
        "name": "BQ",
        "country": {
            "id": 3,
            "name": "China"
        }
    },
    "model": "Nokia 3120 Classic",
    "specification": {
        "id": 5,
        "networkType": "FREQUENCY_2G",
        "simCount": 3,
        "hasWifi": true,
        "hasNfc": true,
        "hasBluetooth": true,
        "height": 124.50,
        "width": 53.50,
        "thickness": 10.50,
        "weight": 83.00,
        "material": "PLASTIC",
        "chargerType": "MICRO_USB",
        "operatingSystem": {
            "id": 4,
            "name": "Symbian",
            "version": "9.3"
        },
        "display": {
            "id": 4,
            "refreshRate": 60,
            "diagonal": 2.40,
            "displayType": "TN",
            "resolution": {
                "id": 4,
                "horizontalPixels": 240,
                "verticalPixels": 320
            }
        },
        "processor": {
            "id": 4,
            "model": "Texas Instruments OMAP1510",
            "technologyNode": 5,
            "cores": 1,
            "maxFrequency": 0.20
        },
        "battery": {
            "id": 4,
            "capacity": 1000,
            "batteryType": "LI_POL"
        },
        "cameras": []
    },
    "releaseDate": "2021-06-24T00:00:00",
    "phoneVariants": [
        {
            "variant": {
                "id": 23,
                "romSize": 1,
                "ramSize": 1,
                "color": "BLACK"
            },
            "quantity": 230,
            "price": 3000.00
        },
        {
            "variant": {
                "id": 1,
                "romSize": 256,
                "ramSize": 12,
                "color": "WHITE"
            },
            "quantity": 2320,
            "price": 30002.00
        }
    ]
}
```

### Удаление телефона по ID

Метод: `DELETE /api/v1/phones/{id}`

Этот запрос удаляет данные о мобильном телефоне по уникальному идентификатору и возвращает удаленные данные.

**Пример ответа на запрос: `DELETE /api/v1/phones/6`**
```json
{
    "id": 6,
    "manufacturer": {
        "id": 5,
        "name": "BQ",
        "country": {
            "id": 3,
            "name": "China"
        }
    },
    "model": "Nokia 3120 Classic",
    "specification": {
        "id": 5,
        "networkType": "FREQUENCY_2G",
        "simCount": 3,
        "hasWifi": true,
        "hasNfc": true,
        "hasBluetooth": true,
        "height": 124.50,
        "width": 53.50,
        "thickness": 10.50,
        "weight": 83.00,
        "material": "PLASTIC",
        "chargerType": "MICRO_USB",
        "operatingSystem": {
            "id": 4,
            "name": "Symbian",
            "version": "9.3"
        },
        "display": {
            "id": 4,
            "refreshRate": 60,
            "diagonal": 2.40,
            "displayType": "TN",
            "resolution": {
                "id": 4,
                "horizontalPixels": 240,
                "verticalPixels": 320
            }
        },
        "processor": {
            "id": 4,
            "model": "Texas Instruments OMAP1510",
            "technologyNode": 5,
            "cores": 1,
            "maxFrequency": 0.20
        },
        "battery": {
            "id": 4,
            "capacity": 1000,
            "batteryType": "LI_POL"
        },
        "cameras": []
    },
    "releaseDate": "2021-06-24T00:00:00",
    "phoneVariants": [
        {
            "variant": {
                "id": 23,
                "romSize": 1,
                "ramSize": 1,
                "color": "BLACK"
            },
            "quantity": 230,
            "price": 3000.00
        },
        {
            "variant": {
                "id": 1,
                "romSize": 256,
                "ramSize": 12,
                "color": "WHITE"
            },
            "quantity": 2320,
            "price": 30002.00
        }
    ]
}
```
