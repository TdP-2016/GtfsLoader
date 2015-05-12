package it.polito.tdp.gtfs.model;

import it.polito.tdp.gtfs.db.GtfsDao;

import java.util.ArrayList;
import java.util.Collection;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Stop;

public class Model {

	private Collection<Agency> agencies = null;
	private Collection<Stop> stops = null;

	private SimpleDirectedGraph<Stop, DefaultEdge> graph = null;

	public Model() {
	}

	public Collection<Stop> getAllStops() {
		if (stops != null)
			return stops;

		GtfsDao dao = new GtfsDao();

		agencies = dao.getAllAgencies();

		stops = new ArrayList<>();

		for (Agency a : agencies) {
			Collection<Stop> agencyStops = dao.getAllStops(a);
			stops.addAll(agencyStops);
		}

		return stops;
	}

	public void buildGraph() {
		graph = new SimpleDirectedGraph<>(DefaultEdge.class);

		Graphs.addAllVertices(graph, getAllStops());

		GtfsDao dao = new GtfsDao();

		for (Stop s1 : graph.vertexSet()) {
			for (Stop s2 : graph.vertexSet()) {
				if (dao.isConnected(s1, s2)) {
					graph.addEdge(s1, s2);
					System.out.format("%20s - %20s\n", s1.getId(), s2.getId());
				}
			}
		}

	}

	/*-- Testing methods --*/
	public static void main(String[] args) {
		Model m = new Model();

		m.buildGraph();
		
		System.out.println(m.graph) ;

	}

}
