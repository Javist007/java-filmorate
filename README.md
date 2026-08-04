# Схема базы данных Filmorate

![Схема базы данных Filmorate](docs/DB-Schema.png)

## Описание схемы

База данных состоит из следующих основных таблиц:

* **users** — хранит информацию о пользователях системы (ID, email, логин, имя, дата рождения).
* **films** — содержит данные о фильмах (название, описание, дата выхода, длительность, ссылка на рейтинг).
* **ratings** — справочник возрастных рейтингов фильмов.
* **genres** — справочник доступных жанров кино.
* **film_genre** — связующая таблица "многие ко многим" между фильмами и жанрами.
* **likes** — таблица для учета лайков пользователей к фильмам.
* **friends** — таблица для хранения связей между пользователями (дружба).
* **directors** — информация о режиссерах фильмов.
* **film_director** — связующая таблица "многие ко многим" между фильмами и режиссерами.
* **reviews** — отзывы пользователей о фильмах.
* **review_reactions** — система лайков и дизлайков для отзывов пользователей.
* **events** — история событий пользователей (лайки, отзывы, добавление в друзья).

## Справочник рейтингов

* **G** — у фильма нет возрастных ограничений,
* **PG** — детям рекомендуется смотреть фильм с родителями,
* **PG-13** — детям до 13 лет просмотр не желателен,
* **R** — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
* **NC-17** — лицам до 18 лет просмотр запрещён.

## Справочник жанров

* Комедия
* Драма
* Мультфильм
* Триллер
* Документальный
* Боевик

## Примеры запросов

### Фильмы

| Метод  | Путь                            |
|--------|---------------------------------|
| GET    | `/films`                        |
| GET    | `/films/{filmId}`               |
| GET    | `/films/popular`                |
| GET    | `/films/common`                 |
| GET    | `/films/director/{directorId}`  |
| GET    | `/films/search`                 |
| POST   | `/films`                        |
| PUT    | `/films`                        |
| PUT    | `/films/{filmId}/like/{userId}` |
| DELETE | `/films/{filmId}`               |
| DELETE | `/films/{filmId}/like/{userId}` |

### Пользователи и Лента событий

| Метод  | Путь                                       |
|--------|--------------------------------------------|
| GET    | `/users`                                   |
| GET    | `/users/{userId}`                          |
| GET    | `/users/{userId}/feed`                     |
| GET    | `/users/{userId}/friends`                  |
| GET    | `/users/{userId}/friends/common/{otherId}` |
| GET    | `/users/{userId}/recommendations`          |
| POST   | `/users`                                   |
| PUT    | `/users`                                   |
| PUT    | `/users/{userId}/friends/{friendId}`       |
| DELETE | `/users/{userId}/friends/{friendId}`       |
| DELETE | `/users/{userId}`                          |

### Режиссёры

| Метод  | Путь              |
|--------|-------------------|
| GET    | `/directors`      |
| GET    | `/directors/{id}` |
| POST   | `/directors`      |
| PUT    | `/directors`      |
| DELETE | `/directors/{id}` |

### Отзывы

| Метод  | Путь                             |
|--------|----------------------------------|
| POST   | `/reviews`                       |
| PUT    | `/reviews`                       |
| GET    | `/reviews/{id}`                  |
| GET    | `/reviews`                       |
| DELETE | `/reviews/{id}`                  |
| PUT    | `/reviews/{id}/like/{userId}`    |
| PUT    | `/reviews/{id}/dislike/{userId}` |
| DELETE | `/reviews/{id}/like/{userId}`    |
| DELETE | `/reviews/{id}/dislike/{userId}` |

### Дополнительные ресурсы

| Метод | Путь                |
|-------|---------------------|
| GET   | `/mpa`              |
| GET   | `/mpa/{mpaId}`      |
| GET   | `/genres`           |
| GET   | `/genres/{genreId}` |