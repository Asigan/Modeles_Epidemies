package Population;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 * Gère les différentes catégories de population
 * @author titouan
 *
 */
public class Population {
	private List<List<Personne>> population= new ArrayList<List<Personne>>();
	private Map map;
	/**
	 * 
	 * @param popQte Le nombre de personnes dans chaque partie de la population
	 * @param tailleMonde Longueur/largeur du monde (longueur = largeur)
	 */
	
	public Population(int[] popQte, int tailleMonde) {
		for(int i=0; i<popQte.length;i++) {
			population.add(new ArrayList<Personne>());
			for(int j=0; j<popQte[i]; j++) {
				population.get(i).add(new Personne(i));
			}
		}
		map = new Map(this, tailleMonde);
	}
	/**
	 * Renvoie le nombre de personnes total stockées dans l'objet
	 * @return
	 */
	public int getNbPersonnesTotal() {
		int nb=0;
		for(List<Personne> l: population) {
			nb+=l.size();
		}
		return nb;
	}
	/**
	 * Renvoie le nombre de personnes de la catégorie numéro i
	 * @param i Numéro de la catégorie
	 * @return
	 */
	public int getNbPersonnes(int i) {
		try {
			return getPop(i).size();
		}catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
	}
	/**
	 * Passe une personne dans la catégorie suivante
	 * @param categorieSourceIndex catégorie dans laquelle est la personne à l'origine
	 * @param personneIndex index de la personne au sein de sa catégorie actuelle
	 * @throws CustomPopException
	 */
	public void changePop(int categorieSourceIndex, int personneIndex) throws CustomPopException {
		
		try {
			changePop(categorieSourceIndex, categorieSourceIndex+1, personneIndex);
		}
		
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
	}
	/**
	 * Passe une personne dans une autre catégorie
	 * @param categorieSourceIndex catégorie dans laquelle est la personne à l'origine
	 * @param categorieCibleIndex catégorie dans laquelle on veut transférer la personne
	 * @param personneIndex index de la personne au sein de sa catégorie actuelle
	 * @throws CustomPopException
	 */
	public void changePop(int categorieSourceIndex, int categorieCibleIndex, int personneIndex) throws CustomPopException {
		
		try {
			List<Personne> pop = this.getPop(categorieSourceIndex);
			Personne p = pop.get(personneIndex);
			this.getPop(categorieCibleIndex).add(p);
			p.setCategorie(categorieCibleIndex);
			pop.remove(personneIndex);
		}
		
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
		catch(Exception e) {
			throw new CustomPopException("Index "+ personneIndex +" doesn't match with a person in category "+ categorieSourceIndex, e);
		}
		
	}
	/**
	 * Passe une personne dans une autre catégorie
	 * @param personne la personne à changer de catégorie
	 * @param categorieCibleIndex la catégorie dans laquelle on veut transférer la personne
	 */
	public void changePop(Personne personne, int categorieCibleIndex) {
		try {
			List<Personne> pop = this.getPop(personne.getCategorie());
			getPop(categorieCibleIndex).add(personne);
			personne.setCategorie(categorieCibleIndex);
			pop.remove(personne);
		}
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
		catch(Exception e) {
			throw new CustomPopException("Person couldn't be found in its category", e);
		}
	}
	/**
	 * Retire une personne des listes des populations et de la carte
	 * @param categorieIndex Catégorie à laquelle appartient la personne
	 * @param personneIndex Index de la personne dans sa catégorie
	 * @throws CustomPopException
	 */
	public void meurt(int categorieIndex, int personneIndex) throws CustomPopException{
		
		try {
			List<Personne> pop = this.getPop(categorieIndex);
			Personne p = pop.remove(personneIndex);
			map.remove(p);
		}
		
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
		catch(Exception e) {
			throw new CustomPopException("Index "+ personneIndex +" doesn't match with a person in category "+categorieIndex, e);
		}
		
	}
	/**
	 * Ajoute une personne dans la catégorie indiquée
	 * @param numCat Numéro de la catégorie dans laquelle ajouter la personne
	 * @throws CustomPopException
	 */
	
	public void nait(int numCat) throws CustomPopException{
		
		try {
			Personne p = new Personne(numCat);
			population.get(numCat).add(p);
			map.add(p, true);
		}
		
		catch(Exception err) {
			throw new CustomPopException("Index "+numCat+" is out of bounds", err);
		}
		
	}
	/**
	 * Retourne toute la population
	 * @return
	 */
	public List<List<Personne>> getPop(){
		
		return population;
		
	}
	/**
	 * Renvoie la population correspondant à la catégorie voulue
	 * @param numero Numéro de la catégorie désirée
	 * @return
	 * @throws CustomPopException
	 */
	public List<Personne> getPop(int numero) throws CustomPopException{
		
		try {
			return getPop().get(numero);
		}
		
		catch(Exception err) {
			throw new CustomPopException("Index out of range: category number "+numero+" doesn't exist", err);
		}
		
	}
	/**
	 * Déplace une personne aux coordonnées indiquées
	 * @param p La personne à déplacer
	 * @param newPosX La nouvelle coordonnée (colonne)
	 * @param newPosY La nouvelle coordonnée (ligne)
	 * @return
	 */
	public boolean deplace(Personne p, int newPosX, int newPosY) {
		boolean res = map.remove(p);
		if(res) {
			p.setPos(newPosX, newPosY);
			try {
				map.add(p);
			}
			catch(IndexOutOfBoundsException e) {
				e.printStackTrace();
				System.out.println("L'index ("+newPosX+", "+newPosY+") ne semble par correspondre à une case de la carte");
			}
		}
		return res;
	}
	
	/**
	 * Renvoie la prochaine case de la carte occupée par au moins une personne
	 * @return
	 */
	public LinkedList<Personne> getCaseOccupeeSuivante() {
		LinkedList<Personne> res=null;
		do {
			res = map.getCaseSuivante();
		}while(res!=null && res.isEmpty());
		return res;
	}

	
}
