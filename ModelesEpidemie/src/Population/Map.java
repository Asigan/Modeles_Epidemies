package Population;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
public class Map{
	protected List<List<LinkedList<Personne>>> map;
	private int colonnes= 0;
	private int cases=0;
	private Random rand = new Random();
	public Map(Population pop, int tailleMonde) {
		map = new ArrayList<List<LinkedList<Personne>>>();
		for(int i=0; i<tailleMonde; i++) {
			map.add(new ArrayList<LinkedList<Personne>>());
			for(int j=0; j<tailleMonde; j++) {
				map.get(i).add(new LinkedList<Personne>());
			}
		}
		int nbCategories = pop.getPop().size();
		for(int i=0; i<nbCategories; i++) {
			List<Personne> popTmp = pop.getPop(i);
			for(Personne p:popTmp) {
				add(p, true);
			}
		}
	}
	public int getTailleMap() {
		return map.size();
	}
	public LinkedList<Personne> getCase(int x, int y){
		// on ne veut pas permettre la modification de la liste, donc on en 
		// envoie une shallow copy
		return map.get(x).get(y);
	}
	
	public boolean remove(Personne p) {
		return getCase(p.getPosX(),p.getPosY()).remove(p);
	}
	
	public void add(Personne p) {
		getCase(p.getPosX(), p.getPosY()).add(p);
	}
	
	public void add(Personne p, boolean randomPlacement) {
		if(randomPlacement) {
			int	posX = rand.nextInt(map.size());
			int posY = rand.nextInt(map.size());
			p.setPos(posX, posY);
		}
		add(p);
	}
	public LinkedList<Personne> getNextCase(){
		if(cases>=map.get(colonnes).size()) {
			cases=0;
			colonnes++;
		}
		LinkedList<Personne> res =null;
		if(colonnes<map.size()) {
			
			res = map.get(colonnes).get(cases);
			cases++;
		}
		else {
			colonnes=0;
		}
		return res;
		
	}
}
