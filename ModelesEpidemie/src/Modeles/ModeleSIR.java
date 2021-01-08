package Modeles;



import java.util.LinkedList;


import Population.Personne;

public class ModeleSIR extends Modele{
	
	private enum CategoriePopulationSIR{
		Susceptible(0),
		Infecte(1),
		Recupere(2);
		
		private int numero;
		private CategoriePopulationSIR(int numero) {
			this.numero = numero;
		}
		public int getNumero() {
			return this.numero;
		}
	};
	static Parametres param = Parametres.getInstance();
	public ModeleSIR() {
		super(new int[] {(int)param.getParam("S0"), (int)param.getParam("I0"), (int)param.getParam("R0")});
	}
	
	
	
	@Override
	protected void transmission() {
		
		LinkedList<Personne> caseij = super.population.getNextCaseOccupee();

		while(caseij!=null) {
			// on commence par recenser la population sur la case
			int cmptInfectes=0;
			int cmptSusceptibles=0;
			for(Personne p: caseij) {
				if(p.getCategorie()==CategoriePopulationSIR.Susceptible.getNumero()) {
					cmptSusceptibles++;
				}
				else if(p.getCategorie()==CategoriePopulationSIR.Infecte.getNumero()) {
					cmptInfectes++;
				}
			}	
			//infection des personnes susceptibles sur la case	
			int nbNouveauxInfectes = nbPersonnesAInfecter(cmptSusceptibles, cmptInfectes);
			transfertCategoriePersonnesSurCase(caseij, nbNouveauxInfectes,
					CategoriePopulationSIR.Susceptible.getNumero(), CategoriePopulationSIR.Infecte.getNumero());
				
			// Récupération des personnes infectées
			int nbNouveauxRecuperes = nbPersonnesQuiRecuperent(cmptInfectes);
			transfertCategoriePersonnesSurCase(caseij, nbNouveauxRecuperes,
					CategoriePopulationSIR.Infecte.getNumero(), CategoriePopulationSIR.Recupere.getNumero());
			
			// Vaccination des personnes susceptibles
			int nbVaccinesFinal = nbPersonnesVaccinees(cmptSusceptibles);
			transfertCategoriePersonnesSurCase(caseij, nbVaccinesFinal,
					CategoriePopulationSIR.Susceptible.getNumero(), CategoriePopulationSIR.Recupere.getNumero());
				
			caseij = super.population.getNextCaseOccupee();
		}
					}
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
	
	protected int nbPersonnesQuiRecuperent(int nbInfectes) {
		int nbNouveauxRecuperes =0;
		if(nbInfectes>0) {
			double gamma = param.getParam("Gamma");
			nbNouveauxRecuperes = getNbEntier(gamma*nbInfectes, nbInfectes);
		}
		return nbNouveauxRecuperes;
	}
	
	protected int nbPersonnesVaccinees(int nbSusceptibles) {
		int nbVaccines = 0;
		if(nbSusceptibles>0 && param.getParam("Vaccination")>0.0) {
			double tauxVaccination = param.getParam("Vaccination")/100;
			nbVaccines = getNbEntier(tauxVaccination*nbSusceptibles, nbSusceptibles);
		}
		return nbVaccines;
	}
	
	@Override
	public void unJour() {
		super.unJour();
		transmission();
	}
	@Override
	public int[] getPopsNumbers() {
		int[] res = new int[super.population.getPop().size()];
		for(int i=0; i<res.length; i++) {
			res[i] = super.population.getNbPersonnes(i);
		}
		return res;
	}
	
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

}
