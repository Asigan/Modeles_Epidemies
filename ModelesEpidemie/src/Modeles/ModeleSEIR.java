package Modeles;

import java.util.LinkedList;

import Population.Personne;
/**
 * Modèle épidémiologique SEIR
 * @author titouan
 *
 */
public class ModeleSEIR extends Modele {
	/**
	 * Catégories de population du modèle épidémiologique SEIR
	 * @author titouan
	 *
	 */
	private enum CategoriePopulationSEIR{
		Susceptible(0),
		Exposed(1),
		Infectious(2),
		Recovered(3);
		
		private int numero;
		private CategoriePopulationSEIR(int numero) {
			this.numero = numero;
		}
		/**
		 * 
		 * @return Le numéro correspondant à la catégorie de population
		 */
		public int getNumero() {
			return this.numero;
		}
	};
	static Parametres param = Parametres.getInstance();
	/**
	 * 
	 */
	public ModeleSEIR() {
		super(new int[] {(int)param.getParam("S0"), (int)param.getParam("E0"), (int)param.getParam("I0"), (int)param.getParam("R0")});
	}
	/**
	 * Mécaniques de transmissions d'une population à l'autre
	 * dépendantes du modèle SEIR (Expositions, Infections, récupérations, vaccinations)
	 */
	@Override
	protected void transmission() {
		LinkedList<Personne> caseij = super.population.getCaseOccupeeSuivante();
		while(caseij!=null) {
			
			int susceptiblesCounter=0;
			int exposedCounter = 0;
			int infectiousCounter=0;
			
			for(Personne p: caseij) {
				if(p.getCategorie()==CategoriePopulationSEIR.Susceptible.getNumero()) {
					susceptiblesCounter++;
				}
				else if(p.getCategorie()==CategoriePopulationSEIR.Exposed.getNumero()) {
					exposedCounter++;
				}
				else if(p.getCategorie()==CategoriePopulationSEIR.Infectious.getNumero()) {
					infectiousCounter++;
				}
			}
			int vaccinatedNb = vaccinatedPeople(susceptiblesCounter);
			transfertCategoriePersonnesSurCase(caseij, vaccinatedNb,
					CategoriePopulationSEIR.Susceptible.getNumero(), CategoriePopulationSEIR.Recovered.getNumero());
			
			int recoveredNb = recoveredPeople(infectiousCounter);
			transfertCategoriePersonnesSurCase(caseij, recoveredNb,
					CategoriePopulationSEIR.Infectious.getNumero(), CategoriePopulationSEIR.Recovered.getNumero()); 
			
			int infectedNb = infectiousPeople(exposedCounter);
			transfertCategoriePersonnesSurCase(caseij, infectedNb,
					CategoriePopulationSEIR.Exposed.getNumero(), CategoriePopulationSEIR.Infectious.getNumero()); 
			
			int exposedNb = exposedPeople(susceptiblesCounter, infectiousCounter);
			transfertCategoriePersonnesSurCase(caseij, exposedNb,
					CategoriePopulationSEIR.Susceptible.getNumero(), CategoriePopulationSEIR.Exposed.getNumero());
			
			caseij = super.population.getCaseOccupeeSuivante();
		}
	}
	/**
	 * Calcule le nombre de personnes infectées dans la journée dans le cluster
	 * @param susceptiblesNb Nombre de personnes susceptibles d'être contaminées
	 * @param infectiousNb Nombre de personnes déjà infectées
	 * @return Nombre de personnes exposées
	 */
	protected int exposedPeople(int susceptiblesNb, int infectiousNb) {
		int exposedNb = 0;
		if(susceptiblesNb>0 && infectiousNb>0 && param.getParam("Quarantaine")==0.0) {
			double beta = param.getParam("Beta");
			double  maskEfficacity = 0.95;
			beta -= beta*maskEfficacity*param.getParam("Port du masque")/100;			
			exposedNb = getNbEntier(beta*susceptiblesNb*infectiousNb, susceptiblesNb);
		}
		return exposedNb;
	}
	/**
	 * Calcule le nombre de personnes terminant leur période d'incubation dans le cluster
	 * @param exposedNb Nombre de personnes en période d'incubation
	 * @return Nombre de personnes ayant fini leur période d'incubation
	 */
	protected int infectiousPeople(int exposedNb) {
		int infectedNb = 0;
		if(exposedNb>0) {
			double alpha = param.getParam("Alpha");
			infectedNb = getNbEntier(alpha*exposedNb, exposedNb);
		}
		return infectedNb;
	}
	/**
	 * Calcule le nombre de personnes ayant récupéré de l'infection dans le cluster
	 * @param infectiousNb Nombre de personnes actuellement infectées dans le cluster
	 * @return Nombre de personnes venant de récupérer de l'infection
	 */
	protected int recoveredPeople(int infectiousNb) {
		int recoveredNb = 0;
		if(infectiousNb>0) {
			double gamma = param.getParam("Gamma");
			recoveredNb = getNbEntier(gamma*infectiousNb, infectiousNb);
		}
		return recoveredNb;
	}
	/**
	 * Calcule le nombre de personnes allant se faire vacciner dans la journée parmi la population
	 * @param susceptiblesNb Nombre de personnes susceptibles d'être infectées dans le cluster
	 * @return Le nombre de personnes se faisant vacciner dans la journée parmi les personnes susceptibles du cluster
	 */
	protected int vaccinatedPeople(int susceptiblesNb) {
		int vaccinatedNb = 0;
		if(susceptiblesNb>0 && param.getParam("Vaccination")>0.0) {
			double vaccinationRate = param.getParam("Vaccination")/100;
			vaccinatedNb = getNbEntier(vaccinationRate*susceptiblesNb, susceptiblesNb);
		}
		return vaccinatedNb;
	}
	
	/**
	 * Simulation d'un jour pour la population (à appeler pour faire une simulation)
	 */
	@Override
	public void unJour() {
		super.unJour();
		transmission();
	}
	/**
	 * 
	 * @return Le nombre de personne dans chaque catégorie de population
	 */
	@Override
	public int[] getPopsNumbers() {
		int[] res = new int[super.population.getPop().size()];
		for(int i=0; i<res.length; i++) {
			res[i] = super.population.getNbPersonnes(i);
		}
		return res;
	}
	/**
	 * 
	 * @return Le nom de chaque catégorie de population
	 */
	@Override
	public String[] getPopsName() {
		String[] res = new String[super.population.getPop().size()];
		int i=0;
		for(CategoriePopulationSEIR t: CategoriePopulationSEIR.values()) {
			res[i] = t.name();
			i++;
		}
		return res;
	}
	/**
	 * 
	 * @return Une nouvelle instance du modèle
	 */
	@Override
	public Modele reinit() {
		return new ModeleSEIR();
	}
}
