CREATE TABLE IF NOT EXISTS films (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(120) NOT NULL,
  release_year INTEGER NOT NULL CHECK (release_year BETWEEN 1888 AND 2100),
  director VARCHAR(100) NOT NULL,
  genre VARCHAR(60) NOT NULL
);
CREATE TABLE IF NOT EXISTS characters (
  id BIGSERIAL PRIMARY KEY,
  film_id BIGINT NOT NULL REFERENCES films(id) ON DELETE CASCADE,
  name VARCHAR(100) NOT NULL,
  actor_name VARCHAR(100) NOT NULL,
  status VARCHAR(40) NOT NULL
);
INSERT INTO films(title, release_year, director, genre)
SELECT 'Интерстеллар', 2014, 'Кристофер Нолан', 'фантастика'
WHERE NOT EXISTS (SELECT 1 FROM films);
INSERT INTO characters(film_id, name, actor_name, status)
SELECT f.id, 'Купер', 'Мэттью Макконахи', 'главный герой'
FROM films f WHERE f.title='Интерстеллар'
AND NOT EXISTS (SELECT 1 FROM characters WHERE name='Купер');
