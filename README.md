# Веб-сервис криптобиржи

Данный репозиторий содержит в себе исходный код RESTfull API service - биржи для проведения торгов криптовалютами.

## Использованные технологии:
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html);
-  [Maven 3.8.1](https://maven.apache.org/download.cgi);
-   [Spring Boot 3.0.2](https://spring.io/projects/spring-boot);
-  [Spring Security 6.0.1](https://spring.io/projects/spring-security);
-  [Liquibase](https://www.liquibase.org/);
- [PostgreSQL latest](https://www.postgresql.org/download/);
- [Docker](https://www.docker.com/).

## Запуск приложения
Для запуска приложения необходим Docker. В папке market в терминале ввести `docker-compose up`. Для тестирования эндпоинтов можно использовать [Postman](https://www.postman.com/downloads/).

## Дополнительные задания
> Реализовано:
- в качестве хранилища данных о пользователях, о кошельках пользователей, об операциях на бирже используется PostgreSQL;
- использование Spring Security для разграничения ролей (admin/user);
- сервис по запросу может возвращать данные в json ИЛИ xml;
- формат может быть изменен добавлением header;
- **accept:application/json** или **accept:application/xml** к HTTP-запросу.

## Изменения некоторых аспектов технического задания
>По сравнению с техническим заданием в реализации присутствуют следующие изменения:
- секретный ключ, который пользователь получает при регистрации, передается **не в теле запроса**, а в **заголовке запроса** с ключом `Authorization`;
- изменен формат ввода даты для администратора для операции 'количество операций на бирже за определенный период'. Дата должна вводиться в формате `yyyy-[m]m-[d]d`, так как для работы с датами был использован класс Data из java.sql.
- добавлено Common API. Содержит в себе один метод, являющийся общим для пользователей с ролью администратора и не администратора, - просмотр актуальных курсов валют, GET запрос (в User API этого метода, соответственно, нет).


# Функционал приложения
В качестве request приложение ожидает json файл в формате {$"parameter": "value"$} . Для всех эндпоинтов, кроме регистрации, в заголовке запроса так же ожидается ключ `Authorization`.
В качестве response приходит  json файл в формате {$"parameter": "value"$}

## База данных
> Для тестирования приложения были созданы следующие таблицы (создание происходило непосредственно при первом запуске проекта с помощью файла `db/changelog/db.changelog-master.xml`, sql исходники находятся в `db/changelog/changeset/tables` ):
- my_user 

| user_id  | user_name |   email                                         | secret_key  |
|-----|------------------|--------------------|---------------------------------|
 1 | vitalyi          | pirogov@mail.ru    | 0907E3F683FB3B4EFF96F0CF85371102
2 | arsen davletyan  | davletyan@mail.ru  | 29659FEDC5C981074E3578106EB0DB7B
   3 | sofia chernikova | chernikova@mail.ru | 0B1212D83B522DA747A6D2EF77A634CC

- role

| role_id  | role_name |
|-----|----------------------------------------------------------------------|
  1 | ADMIN
  2 | USER

- user_role

| role_id  | user_id |
|-----|---------------------------------------------------------------------|
   1 |       2
   2 |       2
   3 |       1

- wallet
>Используются следующие типы валют: **RUB, DOL, EURO**

| wallet_id  | user_id |   wallet_name                                         | total  |
|-----|------------------|--------------------|---------------------------------|
 1 |       1 | RUB         |  58626.0000
  2 |       1 | EURO        |     60.0000
 3 |       2 | DOL         |  20000.000
 4 |       2 | RUB         | 100000.0000

- my_transaction

| trans_id  | trans_date |
|-----|---------------------------------------------------------------------|
   1 | 2023-02-27
   2 | 2023-02-27
   3 | 2023-02-27



## User API
- РЕГИСТРАЦИЯ. Post запрос
`URI http://localhost:8080/api/v1/auth/register`
> Примеры запросов:

 1.  request body:
`{"username":  "maria","email":  "pirogova@mail.ru"}`
response (status: 200 OK) :
`{"key":  "BB261FE05B5E28595B6BD1703938363D"}`

2. request body:
`{"username":  "vitalyi",
"email":  "pirogov@mail.ru"}`
response (status: 400 Bad Request):
`{"error":  "Не уникальное имя пользователя!"}`

3. request body:
`{"username":  "mariapogodina","email":  "pirogova@mail.ru"}`
response (status: 400 Bad Request):
`{"error":  "Не уникальная почта!"}`

4. request body:
`{"email":  "pirogova@mail.ru"}`
response (status: 400 Bad Request):
`{"error":  "Нет необходимого параметра!"}`

5. request body:
`{"username":  "mariapovodkina"}`
response (status: 400 Bad Request):
`{"error":  "Нет необходимого параметра!"}`

______________
- ПОПОЛНЕНИЕ БАЛАНСА. Post запрос
`URI http://localhost:8080/api/v1/user/replenish`
> Примеры запросов:

1.  request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"RUB":  "1000"}`
response (status: 200 OK) :
`{"RUB_wallet":  "59626.0000"}`

2. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"RU":  "1000"}`
response (status:  400 Bad Request) :
`{"error":  "Нет запрашиваемого кошелька или тело запроса пустое!"}`

3. request header:  `Authorization 0907E3F683FB3B4EF`
request body:
`{"RUB":  "1000"}`
response (status:  403 Forbidden) 

4. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC` (ключ пользователя с ролью администратора)
request body:
`{"RUB":  "1000"}`
response (status:  403 Forbidden) 
-----
- ПЕРЕВОД НАЛИЧНЫХ. Post запрос
`URI http://localhost:8080/api/v1/user/withdraw`
> Примеры запросов:

1.  request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"currency":  "RUB", "count":  "1500","card":  "1234 5678 9012 3456"}`
> было 59626.0000 на счету

response (status: 200 OK) :
`{"RUB_wallet":  "58126.0000"}`

2. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"currency":  "RUB", "count":  "100","wallet":  "1234 5678 9012 3456"}`
response (status:  200 OK) :
`{"RUB_wallet":  "58026.0000"}`

3. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"currency":  "RUB", "count":  "150000","card":  "1234 5678 9012 3456"}`
response (status:  400 Bad Request) :
`{"error:":  "Недостаточно средств!"}`


4. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC` (ключ пользователя с ролью администратора)
request body:
`{"currency":  "RUB", "count":  "1500","card":  "1234 5678 9012 3456"}`
response (status:  403 Forbidden) 

5. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"count":  "1500","card":  "1234 5678 9012 3456"}`
response (status:  400 Bad Request) :
`{"error":  "Нет необходимого параметра!"}`
---
- ОБМЕН ВАЛЮТЫ. Post запрос
`URI http://localhost:8080/api/v1/user/exchangeMoney`
> Примеры запросов:

> было  EURO  60.0000, RUB  58626.0000

1.  request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"from":  "RUB", "to":  "EURO","amount":  "700"}`
> стало EURO  68.4000, RUB 57326.0000

response (status: 200 OK) :
`{"amount_from":  "700", "amount_to":  "8.400", "currency_from":  "RUB_wallet", "currency_to":  "EURO_wallet"}`

2. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"from":  "EURO", "to":  "RUB","amount":  "700"}`
response (status:  400 Bad Request) :
`{"error:":  "Недостаточно средств!"}`

3. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"from":  "EURO", "to":  "RUB"}`
response (status:  400 Bad Request) :
`{"error":  "Нет необходимого параметра!"}`

4. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"from":  "EURO", "to":  "TON","amount":  "700"}`
response (status:  400 Bad Request) :
`{"error":  "Нет кошелька с таким именем"}`

5. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC` (ключ пользователя с ролью администратора)
request body:
`{"from":  "RUB", "to":  "EURO","amount":  "700"}`
response (status:  403 Forbidden) 
---
- ПЕРЕВОД НАЛИЧНЫХ. Post запрос
`URI http://localhost:8080/api/v1/user/withdraw`
> Примеры запросов:

1.  request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"currency":  "RUB", "count":  "1500","card":  "1234 5678 9012 3456"}`
> было 59626.0000 на счету

response (status: 200 OK) :
`{"RUB_wallet":  "58126.0000"}`

2. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"currency":  "RUB", "count":  "100","wallet":  "1234 5678 9012 3456"}`
response (status:  200 OK) :
`{"RUB_wallet":  "58026.0000"}`

3. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"currency":  "RUB", "count":  "150000","card":  "1234 5678 9012 3456"}`
response (status:  400 Bad Request) :
`{"error:":  "Недостаточно средств!"}`


4. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC` (ключ пользователя с ролью администратора)
request body:
`{"currency":  "RUB", "count":  "1500","card":  "1234 5678 9012 3456"}`
response (status:  403 Forbidden) 

5. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"count":  "1500","card":  "1234 5678 9012 3456"}`
response (status:  400 Bad Request) :
`{"error":  "Нет необходимого параметра!"}`
---
- ПРОСМОТР ВСЕХ КОШЕЛЬКОВ. Get запрос
`URI http://localhost:8080/api/v1/user/wallets`
> Примеры запросов:

1.  request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
response (status: 200 OK) :
`{"EURO_wallet":  "68.4000","RUB_wallet":  "57326.0000"}`

2. request header:  `Authorization BB261FE05B5E28595B6BD1703938363D`
response (status:  400 Bad Request) :
`{"error":  "Нет активных кошельков!"}`

3. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC` (ключ пользователя с ролью администратора)
response (status:  403 Forbidden)
______________
- ПОПОЛНЕНИЕ БАЛАНСА. Post запрос
`URI http://localhost:8080/api/v1/user/replenish`
> Примеры запросов:

1.  request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"RUB":  "1000"}`
response (status: 200 OK) :
`{"RUB_wallet":  "59626.0000"}`

2. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102`
request body:
`{"RU":  "1000"}`
response (status:  400 Bad Request) :
`{"error":  "Нет запрашиваемого кошелька или тело запроса пустое!"}`

3. request header:  `Authorization 0907E3F683FB3B4EF`
request body:
`{"RUB":  "1000"}`
response (status:  403 Forbidden) 

4. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC` (ключ пользователя с ролью администратора)
request body:
`{"RUB":  "1000"}`
response (status:  403 Forbidden) 


## Admin API
- ИЗМЕНИТЬ КУРС ВАЛЮТЫ. Post запрос
`URI http://localhost:8080/api/v1/admin/changeExchangeRate`
> Примеры запросов:

 1. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{"currency":  "RUB", "DOL":  "0.000096","EURO":  "184"}`
  > было DOL 0.013,  EURO 0.012
  
response (status: 200 OK) :
`{"DOL":  "0.000096","EURO":  "184"}`

2. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{"currency":  "RUB", "OL":  "0.000096","EURO":  "184"}`
response (status: 400 Bad Request):
`{"error":  "Нет запрашиваемого кошелька!"}`

3. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{ "DOL":  "0.000096","EURO":  "184"}`
response (status: 400 Bad Request):
`{"error":  "Нет необходимого параметра!"}`

4. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102` (ключ пользователя с ролью не администратора)
request body: `{"currency":  "RUB", "DOL":  "0.000096","EURO":  "184"}`
response (status:  403 Forbidden) 
-------------
- ПРОСМОТР ОБЩЕЙ СУММЫ КОШЕЛЬКОВ. Get запрос
`URI http://localhost:8080/api/v1/admin/totalAmount`
> Примеры запросов:

 1. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{"currency":  "RUB"}` 
response (status: 200 OK) : `{"RUB":  "157326.0000"}`

2. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{"currency":  "TON"}` 
response (status: 400 Bad Request) : `{"error":  "Нет активных кошельков!"}`

3. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{}` 
response (status: 400 Bad Request) : `{"error":  "Нет необходимого параметра!"}`

4. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102` (ключ пользователя с ролью не администратора)
request body: `{"currency":  "RUB"}`
response (status:  403 Forbidden) 
---
- ПРОСМОТР КОЛИЧЕСТВА ОПЕРАЦИЙ ЗА ОПРЕДЕЛЕННЫЙ ПЕРИОД. Get запрос
`URI http://localhost:8080/api/v1/admin/totalTransactions`
> Примеры запросов:

 1. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{"from": "2023-02-25","to": "2023-02-28"}` 
response (status: 200 OK) : `{"total_transactions":  "13"}`

2. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{"from": "2023-02-25","to": "2023-02.28"}`
response (status: 400 Bad Request) : `{"error":  "Формат даты должен быть yyyy-[m]m-[d]d"}`

3. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{"to": "2023-02-28"}`
response (status: 400 Bad Request) : `{"error":  "Нет необходимого параметра!"}`

4. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{"from": "2022-02-25","to": "2022-02-28"}` 
 response (status: 400 Bad Request) : `{"error":  "Нет транзакций за данный период!"}`

5. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102` (ключ пользователя с ролью не администратора)
request body: `{"from": "2023-02-25","to": "2023-02-28"}`
response (status:  403 Forbidden) 

## Common API
- ПРОСМОТР КУРСА ВАЛЮТ. Get запрос
`URI http://localhost:8080/api/v1/common/exchangeRate`
> Примеры запросов:

 1. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC` (ключ пользователя с ролью администратора) 
 request body: `{"currency":  "RUB"}` 
response (status: 200 OK) : `{"DOL":  "0.013","EURO":  "0.012"}`

 2. request header:  `Authorization 0907E3F683FB3B4EFF96F0CF85371102` (ключ пользователя с ролью не администратора) 
 request body: `{"currency":  "RUB"}` 
response (status: 200 OK) : `{"DOL":  "0.013","EURO":  "0.012"}`

3. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC` 
 request body: `{"currency":  "RU"}` 
response response (status: 400 Bad Request): `{"error":  "Нет кошелька с таким именем"}`

4. request header:  `Authorization 0B1212D83B522DA747A6D2EF77A634CC`  
 request body: `{}` 
response (status: 400 Bad Request) : `{"error":  "Нет необходимого параметра!"}`
