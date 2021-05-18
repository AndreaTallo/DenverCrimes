package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.LinkedList;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.sun.tools.javac.util.List;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	private SimpleWeightedGraph<String,DefaultWeightedEdge> grafo;
	private EventsDao dao;
	private LinkedList<String> percorsoMigliore;
	
	public Model() {
		dao=new EventsDao();
	}
	
	public List<String> getCategorie(){
		return (List<String>) dao.getCategorie();
	}
	
	public void creaGrafo(String categoria,int mese) {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.getVertici(categoria, mese));
		
		//ARchi se reati sono avvenuti nello stesso quatrtiere, il peso è il numero di volte 
		
		for(Adiacenza a: dao.getAdiacenze(categoria, mese)) {
			if(this.grafo.getEdge(a.getV1(), a.getV2())==null) {
				Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2(),a.getPeso());
			}
		}
	}
	
	public ArrayList<Adiacenza> getArchi(){
		double pesoMedio=0.0;
		for(DefaultWeightedEdge e:this.grafo.edgeSet()) {
			pesoMedio += this.grafo.getEdgeWeight(e);
		}
		pesoMedio=pesoMedio/this.grafo.edgeSet().size();
		ArrayList<Adiacenza> result=new ArrayList<Adiacenza>();
		for(DefaultWeightedEdge e:this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>pesoMedio) {
				result.add(new Adiacenza(this.grafo.getEdgeSource(e),this.grafo.getEdgeTarget(e),this.grafo.getEdgeWeight(e)));
			}
		}
		
		
		return result;
		
	}
	public LinkedList<String> trovaPercorso(String sorgente, String destinazione){
		this.percorsoMigliore=new LinkedList<String>();
		LinkedList<String> parziale=new LinkedList<String>();
		
		parziale.add(sorgente);
		cerca(destinazione,parziale,0);
		return this.percorsoMigliore;
		
	}

	private void cerca(String destinazione, LinkedList<String> parziale, int livello) {
		//caso terminale, ultimo elemento di parziale è la nostra destinazione
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			if(parziale.size()>percorsoMigliore.size()) {
				this.percorsoMigliore=new LinkedList<>(parziale);
				return;
			}
		}
		//aggiungiamo vertice per proseguire percorso
		for(String vicino:Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(vicino)) {
				parziale.add(vicino);
				cerca(destinazione,parziale,livello+1);
				parziale.remove(parziale.size()-1);
			}
		}
		
		
	}
	
}
