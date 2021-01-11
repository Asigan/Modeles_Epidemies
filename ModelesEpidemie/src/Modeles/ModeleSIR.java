package Modeles;



import java.util.LinkedList;


import Population.Personne;
/**
 * Modèle épidémiologique SIR
 * @author titouan
 *
 */
public class ModeleSIR extends Modele{
	/**
	 * Catégories de population du modèle épidémiologique SIR
	 * @author titouan
	 *
	 */
	protected enum CategoriePopulationSIR{
		Susceptible(0),
		Infectious(1),
		Recovered(2);
		
		private int numero;
		private CategoriePopulationSIR(int numero) {
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
	private static Parametres param = Parametres.getInstance();
	/**
	 * 
	 */
	public ModeleSIR() {
		super(new int[] {(int)param.getParam("S0"), (int)param.getParam("I0"), (int)param.getParam("R0")});
	}
	
	
	/**
	 * Mécaniques de transmissions d'une population à l'autre
	 * dépendantes du modèle SIR (Infections, récupérations, vaccinations)
	 */
	@Override
	protected void transmission() {
		
		LinkedList<Personne> caseij = super.population.getCaseOccupeeSuivante();

		while(caseij!=null) {
			// on commence par recenser la population sur la case
			int cmptInfectes=0;
			int cmptSusceptibles=0;
			for(Personne p: caseij) {
				if(p.getCategorie()==CategoriePopulationSIR.Susceptible.getNumero()) {
					cmptSusceptibles++;
				}
				else if(p.getCategorie()==CategoriePopulationSIR.Infectious.getNumero()) {
					cmptInfectes++;
				}
			}	
			//infection des personnes susceptibles sur la case	
			int nbNouveauxInfectes = nbPersonnesAInfecter(cmptSusceptibles, cmptInfectes);
			transfertCategoriePersonnesSurCase(caseij, nbNouveauxInfectes,
					CategoriePopulationSIR.Susceptible.getNumero(), CategoriePopulationSIR.Infectious.getNumero());
				
			// Récupération des personnes infectées
			int nbNouveauxRecuperes = nbPersonnesQuiRecuperent(cmptInfectes);
			transfertCategoriePersonnesSurCase(caseij, nbNouveauxRecuperes,
					CategoriePopulationSIR.Infectious.getNumero(), CategoriePopulationSIR.Recovered.getNumero());
			
			// Vaccination des personnes susceptibles
			int nbVaccinesFinal = nbPersonnesVaccinees(cmptSusceptibles);
			transfertCategoriePersonnesSurCase(caseij, nbVaccinesFinal,
					CategoriePopulationSIR.Susceptible.getNumero(), CategoriePopulationSIR.Recovered.getNumero());
				
			caseij = super.population.getCaseOccupeeSuivante();
		}
	}
	/**
	 * Calcule le nombre de personnes infectées en une journée selon le nombre de personnes 
	 * des catégories concernées en contact
	 * @param nbSusceptibles Nombre de personnes susceptibles d'être contaminées
	 * @param nbInfectes Nombre de personnes déjà infectées
	 * @return Nombre de personne infectées dans la journée dans le cluster
	 */
	protected int nbPersonnesAInfecter(int nbSusceptibles, int nbInfectes) {
		int nbNouveauxInfectes=0;
		if(nbInfectes>0) {
			if(nbSusceptibles>0 && (int)param.getParam("Quarantaine")==0) {
				double beta = param.getParam("Beta");
				double efficaciteMasque = 0.90;
				beta -= beta*efficaciteMasque*param.getParam("Port du masque")/100;
				nbNouveauxInfectes= getNbEntier(beta*nbInfectes*nbSusceptibles, nbSusceptibles);
			}
		}
		return nbNouveauxInfectes;
	}
	/**
	 * Calcule le nombre de personne ayant récupérés en une journée selon le nombre de personnes 
	 * des catégories concernées en contact
	 * @param nbInfectes Le nombre de personnes actuellement infectées dans le cluster
	 * @return Le nombre de personnes ayant récupéré dans la journée parmi les personnes infectées du cluster
	 */
	protected int nbPersonnesQuiRecuperent(int nbInfectes) {
		int nbNouveauxRecuperes =0;
		if(nbInfectes>0) {
			double gamma = param.getParam("Gamma");
			nbNouveauxRecuperes = getNbEntier(gamma*nbInfectes, nbInfectes);
		}
		return nbNouveauxRecuperes;
	}
	/**
	 * Calcule le nombre de personnes allant se faire vacciner dans la journée parmi la population
	 * @param nbSusceptibles Nombre de personnes susceptibles d'être infectées dans le cluster
	 * @return Le nombre de personnes se faisant vacciner dans la journée parmi les personnes susceptibles du cluster
	 */
	protected int nbPersonnesVaccinees(int nbSusceptibles) {
		int nbVaccines = 0;
		if(nbSusceptibles>0 && param.getParam("Vaccination")>0.0) {
			double tauxVaccination = param.getParam("Vaccination")/100;
			nbVaccines = getNbEntier(tauxVaccination*nbSusceptibles, nbSusceptibles);
		}
		return nbVaccines;
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
		for(CategoriePopulationSIR t: CategoriePopulationSIR.values()) {
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
		return new ModeleSIR();
	}
}
