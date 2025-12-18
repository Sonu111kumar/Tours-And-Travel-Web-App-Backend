package com.org.service;

import java.util.Arrays;
import java.util.List;

import com.org.dao.HotelDao;
import com.org.dao.LocationDao;
import com.org.model.Hotel;
import com.org.model.Location;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;



@Service
public class LocationService implements CommandLineRunner {
	@Autowired
	private LocationDao locationDao;
	@Autowired
	private HotelDao hotelDao;
	
	public Location addLocation( Location location) {
		return locationDao.save(location);
	}
	
	public List<Location> fetchAllLocations() {
		return locationDao.findAll();
	}
	
	public Location getLocationById(int id) {
		return locationDao.findById(id).get();
	}

     @Override
	public void run(String... args) throws  Exception{
//		if (locationDao.count() == 0) {
//			List<Hotel> delhiHotels = Arrays.asList(
//					createHotel("Taj Palace", "Luxury 5-star hotel in the heart of Delhi.",
//							"Chanakyapuri", "110021", "tajdelhi@example.com",
//							8000.0, 150, 101),
//
//					createHotel("Budget Inn Delhi", "Affordable stay for students & solo travelers.",
//							"Karol Bagh", "110005", "budgetinn@example.com",
//							2000.0, 50, 104)
//
//			);
//		List<Hotel> mumbaiHotels = 	Arrays.asList(createHotel("Oberoi Mumbai", "Premium seafront hotel with luxury amenities.",
//					"Nariman Point", "400021", "oberoi.mumbai@example.com",
//					10000.0, 200, 102));
//
//			List<Hotel> goaHotels = Arrays.asList(createHotel("Leela Goa", "Beachside resort with stunning views.",
//							"Mobor Beach", "403731", "leela.goa@example.com",
//							7000.0, 120, 103));
//
//
//
//
//
//
//
//					locationDao.save(addLocation(0, "Delhi", "Capital of India, known for its heritage and modernity.", delhiHotels));
//			locationDao.save(addLocation(0, "Mumbai", "Financial capital of India and home of Bollywood.", mumbaiHotels));
//			locationDao.save(addLocation(0, "Goa", "Famous for beaches and tourism.", goaHotels));
//			locationDao.save(addLocation(0, "Bangalore", "IT hub of India, known as Silicon Valley of India.", null));
//			locationDao.save(addLocation(0, "Chennai", "Known for temples and Marina Beach.", null));
//			locationDao.save(addLocation(0, "Hyderabad", "City of pearls, famous for biryani.", null));
//			locationDao.save(addLocation(0, "Pune", "Educational hub with pleasant weather.", null));
//			locationDao.save(addLocation(0, "Kolkata", "Cultural capital of India.", null));
//			locationDao.save(addLocation(0, "Jaipur", "Pink City, famous for forts and palaces.", null));
//			locationDao.save(addLocation(0, "Ahmedabad", "Known for Sabarmati Ashram and textiles.", null));
//
//
//
//
//			System.out.println("✅ Default Locations and Hotels added to DB");
//		}
	}

	private Location addLocation(int id,String city,String description,List<Hotel> hotels){
		Location location  = new Location();
		location.setId(id);
		location.setCity(city);
		location.setDescription(description);
		if (hotels != null) {
			hotels.forEach(h -> h.setLocation(location)); // ✅ set back-reference here
			location.setHotels(hotels);
		}
		return location;
	}

	private Hotel createHotel(String name, String description,
							  String street, String pincode, String email,
							  double pricePerDay, int totalRooms, int userId) {
		Hotel hotel = new Hotel();
		hotel.setName(name);
		hotel.setDescription(description);


		hotel.setStreet(street);
		hotel.setPincode(pincode);
		hotel.setEmailId(email);
		hotel.setPricePerDay(pricePerDay);
		hotel.setTotalRoom(totalRooms);
		hotel.setUserId(userId);
		return hotel;
	}

}
