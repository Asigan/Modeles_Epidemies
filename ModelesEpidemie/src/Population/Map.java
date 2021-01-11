package Population;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
/**
 * Classe permettant de g�rer la position de la population
 * @author titouan
 *
 */
public class Map{
	protected List<List<LinkedList<Personne>>> map;
	private int column= 0;
	private int line=0;
	private Random rand = new Random();
	/**
	 * 
	 * @param pop La population qui se d�placera sur la carte
	 * @param tailleMonde Dimension de la carte = tailleMonde*tailleMonde
	 */
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
	/**
	 * Renvoie la largeur/longueur de la carte (largeur = longueur)
	 * @return
	 */
	public int getSizeMap() {
		return map.size();
	}
	/**
	 * 
	 * @param x Num�ro de colonne
	 * @param y Num�ro de ligne
	 * @return
	 */
	public LinkedList<Personne> getCase(int x, int y){
		return map.get(x).get(y);
	}
	/**
	 * Retire une personne de la carte
	 * @param p La personne � retirer
	 * @return
	 */
	public boolean remove(Personne p) {
		return getCase(p.getPosX(),p.getPosY()).remove(p);
	}
	/**
	 * Ajoute une personne sur la carte, aux coordonn�es
	 * stock�es dans l'objet Personne envoy�
	 * @param p La personne � ajouter
	 */
	public void add(Personne p) {
		getCase(p.getPosX(), p.getPosY()).add(p);
	}
	/**
	 * Ajoute une personne sur la carte
	 * @param p La personne � ajouter
	 * @param randomPlacement : coordonn�es al�atoires si true(change les coordonn�es
	 * de l'objet Personne), coordonn�es stock�es dans l'objet Personne si false
	 **/
	public void add(Personne p, boolean randomPlacement) {
		if(randomPlacement) {
			int	posX = rand.nextInt(map.size());
			int posY = rand.nextInt(map.size());
			p.setPos(posX, posY);
		}
		add(p);
	}
	/**
	 * Renvoie la prochaine case (correspondant � une liste de Personne)
	 * (null si la case pr�c�dente �tait la derni�re case, puis revient � 
	 * la premi�re case)
	 * @return
	 */
	public LinkedList<Personne> getCaseSuivante(){
		if(line>=map.get(column).size()) {
			line=0;
			column++;
		}
		LinkedList<Personne> res =null;
		if(column<map.size()) {
			
			res = map.get(column).get(line);
			line++;
		}
		else {
			column=0;
		}
		return res;
		
	}
}
