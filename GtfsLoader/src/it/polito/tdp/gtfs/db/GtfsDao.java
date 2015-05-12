package it.polito.tdp.gtfs.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Stop;

public class GtfsDao {

	/****
	 * Agency Methods
	 ****/

	public Collection<Agency> getAllAgencies() {
		String sql = "SELECT * FROM gtfs_agencies";

		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);

			List<Agency> agencies = new ArrayList<>();

			ResultSet res = st.executeQuery();

			while (res.next()) {

				Agency agency = buildAgency(res);
				agencies.add(agency);

			}

			st.close();
			conn.close();

			return agencies;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}

	}

	public Agency getAgencyForId(String id) {
		String sql = "SELECT * FROM gtfs_agencies WHERE id=?";

		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);

			st.setString(1, id);
			ResultSet res = st.executeQuery();

			Agency agency = null;
			if (res.next()) {

				agency = buildAgency(res);

			}

			st.close();
			conn.close();

			return agency;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}

	}

	/*-- Helper methods for Agencies --*/

	private Agency buildAgency(ResultSet res) throws SQLException {
		Agency agency = new Agency();

		agency.setId(res.getString("id"));
		agency.setName(res.getString("name"));
		agency.setLang(res.getString("lang"));
		// TODO add other fields

		return agency;
	}

	/****
	 * {@link Stop} Methods
	 ****/

	public Collection<Stop> getAllStops(Agency agency) {
		String sql = "SELECT * FROM gtfs_stops WHERE agencyId=?";

		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);

			st.setString(1, agency.getId());
			List<Stop> stops = new ArrayList<>();

			ResultSet res = st.executeQuery();

			while (res.next()) {

				Stop stop = buildStop(res);
				stops.add(stop);

			}

			st.close();
			conn.close();

			return stops;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}

	}

	private Stop getStopForId(AgencyAndId id) {
		return null;
	}
	
	/****
	 * High Level Methods
	 ****/
	public boolean isConnected(Stop stop1, Stop stop2) {
		String sql = "SELECT * \r\n" + 
				"FROM gtfs_stops AS stop1, gtfs_stop_times AS stoptimes1, gtfs_trips,\r\n" + 
				"gtfs_stops AS stop2, gtfs_stop_times AS stoptimes2\r\n" + 
				"WHERE \r\n" + 
				"\r\n" + 
				"stop1.agencyId=? AND stop1.id=?\r\n" + 
				"AND stop2.agencyId=? AND stop2.id=?\r\n" + 
				"\r\n" + 
				"AND stoptimes2.stopSequence = stoptimes1.stopSequence+1\r\n" + 
				"\r\n" + 
				"AND stop1.agencyId=stoptimes1.stop_agencyId\r\n" + 
				"AND stop1.id=stoptimes1.stop_id\r\n" + 
				"AND stoptimes1.trip_agencyId=gtfs_trips.agencyId\r\n" + 
				"AND stoptimes1.trip_id=gtfs_trips.id\r\n" + 
				"\r\n" + 
				"AND stop2.agencyId=stoptimes2.stop_agencyId\r\n" + 
				"AND stop2.id=stoptimes2.stop_id\r\n" + 
				"AND stoptimes2.trip_agencyId=gtfs_trips.agencyId\r\n" + 
				"AND stoptimes2.trip_id=gtfs_trips.id";
		
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);

			st.setString(1, stop1.getId().getAgencyId());
			st.setString(2, stop1.getId().getId());
			st.setString(3, stop2.getId().getAgencyId());
			st.setString(4, stop2.getId().getId());
			
			ResultSet res = st.executeQuery();

			boolean found = false ;
			if (res.next()) 
				found = true ;
			
			st.close();
			conn.close();

			return found ;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database error", e);
		}
	}


	/*-- Helper methods for Stop --*/

	private Stop buildStop(ResultSet res) throws SQLException {
		Stop stop = new Stop();

		stop.setId(new AgencyAndId(res.getString("agencyId"), res.getString("id")));
		stop.setName(res.getString("name"));
		// TODO add other fields

		return stop;
	}
	
	// Testing method
	
	public static void main(String [] args) {
		
		GtfsDao dao = new GtfsDao() ;
		
		Collection<Agency> agencies = dao.getAllAgencies() ;
		for( Agency a : agencies ) {
			System.out.format("Agency %s: %s\n", a.getId(), a.getName()) ;
			
			Collection<Stop> stops = dao.getAllStops(a) ;
			
			for( Stop s: stops) {
				System.out.format("\tStop %s: %s\n", s.getId(), s.getName()) ;
			}
		}
	
		
	}
}
