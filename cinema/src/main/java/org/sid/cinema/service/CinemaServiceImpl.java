package org.sid.cinema.service;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.sid.cinema.dao.CategorieRepository;
import org.sid.cinema.dao.CinemaRepository;
import org.sid.cinema.dao.FilmRepository;
import org.sid.cinema.dao.PlaceRepository;
import org.sid.cinema.dao.ProjectionRepository;
import org.sid.cinema.dao.SalleRepository;
import org.sid.cinema.dao.SeanceRepository;
import org.sid.cinema.dao.TicketRepository;
import org.sid.cinema.dao.VilleRepository;
import org.sid.cinema.entities.Categorie;
import org.sid.cinema.entities.Cinema;
import org.sid.cinema.entities.Film;
import org.sid.cinema.entities.Place;
import org.sid.cinema.entities.Projection;
import org.sid.cinema.entities.Salle;
import org.sid.cinema.entities.Seance;
import org.sid.cinema.entities.Ticket;
import org.sid.cinema.entities.Ville;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CinemaServiceImpl implements ICinemaInitService {
	@Autowired
	VilleRepository villeRepository;
	@Autowired
	CinemaRepository cinemaRepository;
	@Autowired
	SalleRepository salleRepository;
	@Autowired
	CategorieRepository categorieRepository;
	@Autowired
	PlaceRepository placeRepository;
	@Autowired
	ProjectionRepository projectionRepository;
	@Autowired
	FilmRepository filmRepository;
	@Autowired
	SeanceRepository seanceRepository;
	@Autowired
	TicketRepository ticketRepository;

	@Override
	public void initVilles() {
		Stream.of("Douala", "Yaoundé", "Bafoussam").forEach(nameVille -> {
			Ville ville = new Ville();
			ville.setName(nameVille);
			villeRepository.save(ville);

		});
	}

	@Override
	public void initCinemas() {
		villeRepository.findAll().forEach(nameVille -> {
			Stream.of("Abia", "Wouri", "Empire", "Canal Olympia").forEach(nameCinema -> {
				Cinema cinema = new Cinema();
				cinema.setName(nameCinema);
				cinema.setVille(nameVille);
				cinema.setNombreSalles(3 + (int) (Math.random() * 15));
				cinemaRepository.save(cinema);
			});
		});
	}

	@Override
	public void initSalles() {
		cinemaRepository.findAll().forEach(cinema -> {
			for (int i = 0; i < cinema.getNombreSalles(); i++) {
				Salle salle = new Salle();
				salle.setCinema(cinema);
				salle.setName("salle numero" + (i + 1));
				salle.setNombrePlace(10 + (int) (Math.random() * 30));
				salleRepository.save(salle);
			}
		});
	}

	@Override
	public void initPlaces() {
		salleRepository.findAll().forEach(salle->{
			for(int i=0;i<salle.getNombrePlace();i++) {
				Place place = new Place();
				place.setSalle(salle);
				place.setNumero(i+1);
				placeRepository.save(place);
				
			}
		});
	}

	@Override
	public void initSeances() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Stream.of("12:00","12:00","14:00","15:00","17:00","21:00").forEach(nameSeance->{
			Seance seance = new Seance();
			try {
				seance.setHeureDebut(dateFormat.parse(nameSeance));
				seanceRepository.save(seance);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void initCategories() {
		Stream.of("Horreur","Action","Romantique").forEach(nameCategorie->{
			Categorie categorie = new Categorie();
			categorie.setName(nameCategorie);
			categorieRepository.save(categorie);
		});
	}

	@Override
	public void initFilm() {
		double[] duree = new double [] {1,1.5,2,2.5,3} ;
		categorieRepository.findAll().forEach(nameCategorie->{
			Stream.of("Billion","Startup","Vickings")
			.forEach(nameFilm->{
				Film film = new Film();
				film.setTitre(nameFilm);
				//film.setCategorie(nameCategorie);
				film.setDuree(duree [new Random().nextInt(duree.length)]);
				film.setPhoto(nameFilm+".jpg");
				film.setCategorie(categorieRepository.findAll().get(new Random().nextInt(categorieRepository.findAll().size())));
				filmRepository.save(film);
			});
		});
	}

	@Override
	public void initProjection() {
		double[] price = new double[] {1500, 3500, 5500, 10500};
		villeRepository.findAll().forEach(ville->{
			ville.getCinemas().forEach(cinema->{
				cinema.getSalles().forEach(salle->{
					filmRepository.findAll().forEach(film->{
						seanceRepository.findAll().forEach(seance->{
							Projection projection = new Projection();
							projection.setDateProjection(new Date(0));
							projection.setFilm(film);
							projection.setSalle(salle);
							projection.setPrix(price[new Random().nextInt(price.length)]);
							projection.setSeance(seance);
							projectionRepository.save(projection);
						});
						
					});
				});
			});
		});
	}

	@Override
	public void initTickets() {
		projectionRepository.findAll().forEach(projection->{
			projection.getSalle().getPlaces().forEach(place->{
				Ticket ticket = new Ticket();
				ticket.setPlace(place);
				ticket.setPrix(projection.getPrix());
				ticket.setProjection(projection);
				ticket.setReserve(false);
				ticketRepository.save(ticket);
			});
		});
	}

}
