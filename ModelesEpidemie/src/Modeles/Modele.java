package Modeles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import Population.*;
/**
 * Modèle épidémiologique : base
 * @author titouan
 *
 */
abstract public class Modele {
	
	private final int SUR_PLACE = 0;
	private final int OUEST = 1;
	private final int EST = 2;
	private final int NORD = 3;
	private final int SUD = 4;
	
	private Parametres param;
	protected Population population;
	protected HashMap<String, Double> residus;
	
	/**
	 * 
	 * @param popsQte Le nombre de personen pour chaque catégorie
	 */
	public Modele(int[] popsQte) {
		param = Parametres.getInstance();
		int tailleMonde = param.getParam("Spatialisation")==1?(int)param.getParam("Taille du monde"):1;
		population = new Population(popsQte, tailleMonde);
		residus = new HashMap<String, Double>();
		for(int i=0; i<popsQte.length; i++) {
			residus.put("morts"+((Integer)i).toString(), 0.0);
		}
		residus.put("naissances", 0.0);
	}
	/**
	 * 
	 * @param pop La population a utiliser
	 */
	public Modele(Population pop) {
		this.population = pop;
		residus = new HashMap<String, Double>();
		for(int i=0; i<pop.getPop().size(); i++) {
			residus.put("morts"+((Integer)i).toString(), 0.0);
		}
		residus.put("naissances", 0.0);

	}
	/**
	 * Déplacement de la population
	 */
	protected void deplacement() {
		List<List<Personne>> pops  = population.getPop();
		Random rand = new Random();
		int popTotale = 0;
		for(List<Personne> pop: pops) {
			popTotale += pop.size();
		}
		for(List<Personne> pop: pops) {
			for(Personne p: pop) {
				boolean personneSeDeplace = true;
				if(rand.nextDouble()*100<param.getParam("Confinement")) {
					personneSeDeplace=false;
				}

				if(personneSeDeplace) {
					int direction = rand.nextInt(5);
					int newPosX=p.getPosX();
					int newPosY=p.getPosY();
					switch(direction) {
					case SUR_PLACE:
						break;
					case OUEST:
						newPosX-=1;
						if(newPosX<0)
							newPosX = (int)param.getParam("Taille du monde")-1;
						break;
					case EST:
						newPosX+=1;
						if(newPosX>=param.getParam("Taille du monde"))
							newPosX=0;
						break;
					case NORD:
						newPosY-=1;
						if(newPosY<0)
							newPosY = (int)param.getParam("Taille du monde")-1;
						break;
					case SUD:
						newPosY+=1;
						if(newPosY>=param.getParam("Taille du monde"))
							newPosY=0;
						
					}
					population.deplace(p, newPosX, newPosY);
					
				}
			}
		}
	}
	/**
	 * Ce qu'il se passe en un jour pour la population (déplacement, morts, et naissances)
	 * qui n'est pas dépendant du modèle mathématique, en se servant des autres fonctions
	 */
	public void unJour() {
		if(param.getParam("Spatialisation") ==1) {
			deplacement();
		}
		if(param.getParam("Dynamiques de population") ==1) {
			naissances();
			morts();
		}
	}
	
	// ces fonctions existent pour ne pas avoir à réécrire tout unJour si un utilisateur
	// du package voulait modifier le fonctionnement des dynamiques de population.
	/**
	 * Gère les naissances de la population
	 */
	protected void naissances() {
		double nbNaissances = population.getNbPersonnesTotal()*param.getParam("Proportion de Naissances")+residus.get("naissances");
		residus.replace("naissances", nbNaissances%1);
		nbNaissances-=nbNaissances%1;
		for(int i=0; i<(int)nbNaissances; i++) {
			population.nait(0);
		}
	}
	/**
	 * Gère les morts de la population
	 */
	protected void morts() {
		int nbPopulations = population.getPop().size(); 
		
		for(int i=0; i<nbPopulations; i++) {
			double nbMorts = population.getNbPersonnes(i)*param.getParam("Proportion de morts naturelles")+residus.get("morts"+i);
			residus.replace("morts"+i, nbMorts%1);
			nbMorts-=nbMorts%1;
			for(int j=0; j<(int)nbMorts; j++) {
				population.meurt(i, population.getNbPersonnes(i)-1);
			}
		}
	}
	
	// Deux fonctions utiles pour la transmission dans les modèles SIR et SEIR, mis ici
	// pour réutiliser le code 
	/**
	 * Renvoie un nombre entier correspondant à la partie entier 
	 * avec une probabilité égale à la partie décimale qu'on 
	 * y ajoute 1
	 * @param nombreDouble Le nombre décimal
	 * @param nbMax Le nombre maximal que peut atteindre le nombre entier (utile dans le contexte du modèle 
	 * pour ne pas réécrire trop de code...)
	 * @return
	 */
	protected int getNbEntier(double nombreDouble, int nbMax) {
		Random rand = new Random();
		double residu = nombreDouble%1;
		int res = (int)(nombreDouble-residu); 
		// Plutôt que de laisser le résidu de côté, on s'en sert comme d'une probabilité qu'une
		// personne supplémentaire soit infectée
		if(rand.nextDouble()<residu) res++;
		if(res>nbMax) res=nbMax;
		return res;
	}
	/**
	 * Transfert un certain nombre de personnes d'une catégorie déterminée présents sur
	 * une case dans une autre catégorie cible 
	 * @param caseij Case concernée
	 * @param nbPersonnes Nombre de personnes à transférer
	 * @param popSource Catégorie des personnes à transférer
	 * @param popCible Catégorie dans laquelle on veut transférer les personnes
	 */
	protected void transfertCategoriePersonnesSurCase(LinkedList<Personne> caseij, int nbPersonnes, int popSource, int popCible) {
		
		Iterator<Personne> personnesSurCase = caseij.iterator();
		for(int k=0; k<nbPersonnes; k++) {
			boolean unePersonneTransferee=false;
			while(!unePersonneTransferee) {
				if(personnesSurCase.hasNext()) {
					Personne personne = personnesSurCase.next();
					if(personne.getCategorie()==popSource) {
						unePersonneTransferee=true;
						population.changePop(personne, popCible);
					}
				}
				else {
					//pour éviter les boucles infinies si on arrive en fin d'iterator... Aurait aussi pu throw une erreur, à voir plus tard
					unePersonneTransferee=true;
				}
			}
		}	
	}
	/**
	 * Mécaniques de transmissions d'une population à l'autre
	 * dépendantes du modèle (SIR, SEIR..)
	 */
	abstract protected void transmission();
	/**
	 * 
	 * @return Le nombre de personne dans chaque catégorie de population
	 */
	abstract public int[] getPopsNumbers();
	/**
	 * 
	 * @return Le nom de chaque catégorie de population
	 */
	abstract public String[] getPopsName();
	/**
	 * 
	 * @return Une nouvelle instance du modèle
	 */
	abstract public Modele reinit();
}
