package Modeles;



import java.util.LinkedList;


import Population.Personne;
/**
 * Mod�le �pid�miologique SIR
 * @author titouan
 *
 */
public class ModeleSIR extends Modele{
	/**
	 * Cat�gories de population du mod�le �pid�miologique SIR
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
		 * @return Le num�ro correspondant � la cat�gorie de population
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
	 * M�caniques de transmissions d'une population � l'autre
	 * d�pendantes du mod�le SIR (Infections, r�cup�rations, vaccinations)
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
				
			// R�cup�ration des personnes infect�es
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
	 * Calcule le nombre de personnes infect�es en une journ�e selon le nombre de personnes 
	 * des cat�gories concern�es en contact
	 * @param nbSusceptibles Nombre de personnes susceptibles d'�tre contamin�es
	 * @param nbInfectes Nombre de personnes d�j� infect�es
	 * @return Nombre de personne infect�es dans la journ�e dans le cluster
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
	 * Calcule le nombre de personne ayant r�cup�r�s en une journ�e selon le nombre de personnes 
	 * des cat�gories concern�es en contact
	 * @param nbInfectes Le nombre de personnes actuellement infect�es dans le cluster
	 * @return Le nombre de personnes ayant r�cup�r� dans la journ�e parmi les personnes infect�es du cluster
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
	 * Calcule le nombre de personnes allant se faire vacciner dans la journ�e parmi la population
	 * @param nbSusceptibles Nombre de personnes susceptibles d'�tre infect�es dans le cluster
	 * @return Le nombre de personnes se faisant vacciner dans la journ�e parmi les personnes susceptibles du cluster
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
	 * Simulation d'un jour pour la population (� appeler pour faire une simulation)
	 */
	@Override
	public void unJour() {
		super.unJour();
		transmission();
	}
	/**
	 * 
	 * @return Le nombre de personne dans chaque cat�gorie de population
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
	 * @return Le nom de chaque cat�gorie de population
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
	 * @return Une nouvelle instance du mod�le
	 */
	@Override
	public Modele reinit() {
		return new ModeleSIR();
	}
}
