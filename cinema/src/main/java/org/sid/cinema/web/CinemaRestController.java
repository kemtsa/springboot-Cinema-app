package org.sid.cinema.web;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.sid.cinema.dao.FilmRepository;
import org.sid.cinema.dao.TicketRepository;
import org.sid.cinema.entities.Film;
import org.sid.cinema.entities.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
public class CinemaRestController {
	@Autowired
	private FilmRepository filmRepository;
	@Autowired
	private TicketRepository ticketRepository;

	@GetMapping(path = "imageFilm/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] image(@PathVariable(name = "id") Long id) throws Exception {
		// ici on suppose qu'on n'a deja le film qui s'affiche on le recupère
		Film f = filmRepository.findById(id).get();

		// ici on recupère la photo
		String photoName = f.getPhoto();

		/*
		 * ici on instancie le repertoire du fichier ou les photo sont dans la machine
		 * avec le repertoire par défaut qui user.home
		 */

		File file = new File(System.getProperty("user.home") + "/cinema/images/" + photoName);
		// on instancie le chemin (Paths.get c'est une méthode systeme
		Path path = Paths.get(file.toURI());

		// on retourne le repertoire (Files.readAllBytes c'est une méthode systeme)

		return Files.readAllBytes(path);

	}

	@PostMapping("/payerTicket")
	private List<Ticket> payerTicket(@RequestBody TicketUser ticketUser) {
		// on crait la liste des clients qui va être retourné pour print ses infos
		List<Ticket> listTickets = new ArrayList<Ticket>();
		
		/* on affiche la liste des tickets selectionnée par le client
		et pour chaque ticket on recupère les infos  */
		
		ticketUser.getTicketReserves().forEach(idTicket -> {
			Ticket ticket = ticketRepository.findById(idTicket).get();
			ticket.setNomClient(ticketUser.getNomClient());
			ticket.setReserve(true);
			ticket.setCodePayement(ticketUser.getCodeTicket());
			ticketRepository.save(ticket);
			
// ensuite on enregistre le ticket puis on l'ajoue a la liste des tickets selectionné
			listTickets.add(ticket);
		});
		return listTickets;

	}

}

@Data
class TicketUser {
	private int codeTicket;
	private String nomClient;
	private List<Long> ticketReserves = new ArrayList<>();
}