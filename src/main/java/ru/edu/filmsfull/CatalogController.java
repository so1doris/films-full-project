package ru.edu.filmsfull;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CatalogController {
    private final JdbcTemplate db;
    CatalogController(JdbcTemplate db) { this.db = db; }

    @GetMapping("/")
    String catalog(Model model) {
        model.addAttribute("films", db.query("SELECT id,title,release_year,director,genre FROM films ORDER BY id",
            (r,n) -> new Film(r.getLong("id"), r.getString("title"), r.getInt("release_year"), r.getString("director"), r.getString("genre"))));
        model.addAttribute("characters", db.query("SELECT c.id,c.film_id,c.name,c.actor_name,c.status,f.title film_title FROM characters c JOIN films f ON f.id=c.film_id ORDER BY c.id",
            (r,n) -> new CharacterRow(r.getLong("id"), r.getLong("film_id"), r.getString("name"), r.getString("actor_name"), r.getString("status"), r.getString("film_title"))));
        return "catalog";
    }

    @PostMapping("/films")
    String addFilm(@RequestParam String title, @RequestParam int releaseYear, @RequestParam String director, @RequestParam String genre, RedirectAttributes flash) {
        if (title.isBlank() || director.isBlank() || genre.isBlank() || releaseYear < 1888 || releaseYear > 2100) {
            flash.addFlashAttribute("message", "Проверьте поля фильма: год должен быть от 1888 до 2100.");
            return "redirect:/";
        }
        db.update("INSERT INTO films(title,release_year,director,genre) VALUES(?,?,?,?)", title.trim(), releaseYear, director.trim(), genre.trim());
        flash.addFlashAttribute("message", "Фильм добавлен."); return "redirect:/";
    }

    @PostMapping("/films/{id}/update")
    String updateFilm(@PathVariable long id, @RequestParam String title, @RequestParam int releaseYear, @RequestParam String director, @RequestParam String genre, RedirectAttributes flash) {
        if (title.isBlank() || director.isBlank() || genre.isBlank() || releaseYear < 1888 || releaseYear > 2100) {
            flash.addFlashAttribute("message", "Некорректные данные фильма."); return "redirect:/";
        }
        db.update("UPDATE films SET title=?,release_year=?,director=?,genre=? WHERE id=?", title.trim(), releaseYear, director.trim(), genre.trim(), id);
        flash.addFlashAttribute("message", "Данные фильма обновлены."); return "redirect:/";
    }

    @PostMapping("/films/{id}/delete")
    String deleteFilm(@PathVariable long id, RedirectAttributes flash) {
        db.update("DELETE FROM films WHERE id=?", id); flash.addFlashAttribute("message", "Фильм и связанные персонажи удалены."); return "redirect:/";
    }

    @PostMapping("/characters")
    String addCharacter(@RequestParam long filmId, @RequestParam String name, @RequestParam String actorName, @RequestParam String status, RedirectAttributes flash) {
        if (name.isBlank() || actorName.isBlank() || status.isBlank()) {
            flash.addFlashAttribute("message", "Заполните все поля персонажа."); return "redirect:/";
        }
        db.update("INSERT INTO characters(film_id,name,actor_name,status) VALUES(?,?,?,?)", filmId, name.trim(), actorName.trim(), status.trim());
        flash.addFlashAttribute("message", "Персонаж добавлен."); return "redirect:/";
    }

    @PostMapping("/characters/{id}/update")
    String updateCharacter(@PathVariable long id, @RequestParam String name, @RequestParam String actorName, @RequestParam String status, RedirectAttributes flash) {
        if (name.isBlank() || actorName.isBlank() || status.isBlank()) {
            flash.addFlashAttribute("message", "Заполните все поля персонажа."); return "redirect:/";
        }
        db.update("UPDATE characters SET name=?,actor_name=?,status=? WHERE id=?", name.trim(), actorName.trim(), status.trim(), id);
        flash.addFlashAttribute("message", "Данные персонажа обновлены."); return "redirect:/";
    }

    @PostMapping("/characters/{id}/delete")
    String deleteCharacter(@PathVariable long id, RedirectAttributes flash) {
        db.update("DELETE FROM characters WHERE id=?", id); flash.addFlashAttribute("message", "Персонаж удален."); return "redirect:/";
    }

    record Film(long id, String title, int releaseYear, String director, String genre) {}
    record CharacterRow(long id, long filmId, String name, String actorName, String status, String filmTitle) {}
}
