package it.polito.tdp.yelp.model;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	private Graph<Business, DefaultWeightedEdge> grafo;
	private List<Business> vertici;
	private Map<String, Business> verticiIdMap;
	
	//variabili per ricorsione
	private List<Business> percorsoBest;
	
	public List<String> getAllCities() {
		YelpDao dao = new YelpDao();
		return dao.getAllCities();
	}
	
	public String creaGrafo(String citta, Year anno) {
		this.grafo = new SimpleDirectedWeightedGraph<Business, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		YelpDao dao = new YelpDao();
		this.vertici = dao.getBusinessByCityAndYear(citta, anno);
		this.verticiIdMap = new HashMap<String, Business>();
		for(Business b : this.vertici)
			this.verticiIdMap.put(b.getBusinessId(), b);
		
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		//IPOTESI 1: calcolare media recensioni mentre leggo Business
		/*for(Business b1 : this.vertici)
			for(Business b2 : this.vertici) {
				if(b1.getMediaRecensioni() < b2.getMediaRecensioni())
					Graphs.addEdge(this.grafo, b1, b2, (b2.getMediaRecensioni()-b1.getMediaRecensioni()));
			}*/
		
		//IPOTESI 1bis: non modifico oggetto Business, ma creo una mappa per ricordare le medie recensioni
		/*Map<Business, Double> mediaRecensioni = new HashMap<>();
		//carico mappa col DAO
		dao.medie(mediaRecensioni);
		for(Business b1 : this.vertici)
			for(Business b2 : this.vertici) {
				if(mediaRecensioni.get(b1) < mediaRecensioni.get(b2))
					Graphs.addEdge(this.grafo, b1, b2, (mediaRecensioni.get(b2)-mediaRecensioni.get(b1)));
			}*/
		
		//IPOTESI 3: faccio calcolare gli archi al db
		List<ArcoGrafo> archi = dao.calcolaArchi(citta, anno);
		for(ArcoGrafo arco : archi) {
			Graphs.addEdge(this.grafo, this.verticiIdMap.get(arco.getB1()), this.verticiIdMap.get(arco.getB2()), arco.getPeso());
		}
		
		return String.format("Grafo creato con %d vertici e %d archi\n", this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
	}
	
	public Business getLocaleMigliore() {
		double max = 0.0;
		Business result = null;
		
		for(Business b : this.grafo.vertexSet()) {
			double val = 0.0;
			for(DefaultWeightedEdge e : this.grafo.incomingEdgesOf(b))
				val += this.grafo.getEdgeWeight(e);
			for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(b))
				val -= this.grafo.getEdgeWeight(e);
			if(val > max) {
				max = val;
				result = b;
			}
		}
		return result;
	}
	
	public List<Business> getVertici() {
		return this.vertici;
	}
	
	private void cerca(List<Business> parziale, int livello, Business arrivo, double soglia) {
		Business ultimo = parziale.get(parziale.size()-1);
		
		//caso terminale: ho trovato l'arrivo
		if(parziale.get(parziale.size()-1).equals(arrivo)) {
			if(this.percorsoBest == null) {
				this.percorsoBest = new ArrayList<Business>(parziale);
				return;
			}
			else if(parziale.size() < percorsoBest.size()){
				this.percorsoBest = new ArrayList<Business>(parziale);
				return;
			}
			else {
				return;
			}
		}
		
		//generazione percorsi
		//cerca successori di 'ultimo'
		for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(ultimo)) {
			if(this.grafo.getEdgeWeight(e) > soglia) {
				//vai
				Business prossimo = Graphs.getOppositeVertex(this.grafo, e, ultimo);

				if(!parziale.contains(prossimo)) {
					parziale.add(prossimo);
					cerca(parziale, livello+1, arrivo, soglia);
					parziale.remove(parziale.size()-1);
				}
			}
		}
	}
	
	public List<Business> percorsoMigliore(Business partenza, Business arrivo, double soglia) {
		this.percorsoBest = null;
		
		List<Business> parziale = new ArrayList<Business>();
		parziale.add(partenza);
		
		this.cerca(parziale, 1, arrivo, soglia);
		
		return this.percorsoBest;
	}
	
}
