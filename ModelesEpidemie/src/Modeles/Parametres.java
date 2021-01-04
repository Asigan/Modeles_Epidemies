package Modeles;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
public class Parametres {
	
	static protected enum typesVariables{
		Integer,
		Coeff,
		Pourcentage,
		Boolean,
	}
	
	protected HashMap<String, Double> dict;
	protected GestionFichier gfile;
	
	private static Parametres instance = null;
	static Parametres getInstance() {
		if(instance==null) {
			instance = new Parametres();
		}
		return instance;
	}
	
	private Parametres() {
		gfile = new GestionFichier();
		dict = new HashMap<String, Double>();
		// valeurs censées être des entiers
		dict.put("S0", 500.0);
		dict.put("I0", 1.0);
		dict.put("R0", 0.0);
		dict.put("Taille du monde", 50.0);
		// Coefficients
		dict.put("Beta", 0.01);
		dict.put("Gamma", 0.16);
		dict.put("Alpha", 0.5);
		dict.put("Proportion de Naissances", 0.05);
		dict.put("Proportion de morts naturelles", 0.025);
		// Options:
		dict.put("Spatialisation", 0.0);
		dict.put("Dynamiques de population", 0.0);
		// politiques publiques
		dict.put("Confinement", 0.0);
		dict.put("Seuil confinement", 100.0);
		dict.put("Port du masque", 0.0);
		dict.put("Seuil port du masque", 100.0);
		dict.put("Quarantaine", 0.0);
		dict.put("Vaccination", 0.0);
		
		
		
	}
	public void regenererFichier(String cheminFichier) throws IOException{
		gfile.genererFichier(cheminFichier, true);	
		}
	public void genererFichierSiNonExistant(String cheminFichier)throws IOException{
		gfile.genererFichier(cheminFichier, false);
	}
	
	public void setParametresConsole() {
		
	}
	protected class GestionFichier {
		protected final String[] keys = new String[] {"S0","I0","R0","Taille du monde","Beta","Gamma","Alpha","Proportion de Naissances",
				"Proportion de morts naturelles","Spatialisation", "Dynamiques de population", "Confinement","Seuil confinement",
				"Port du masque","Seuil port du masque","Quarantaine","Vaccination"};  //sert seulement à garder l'ordre des variables dans le fichier(et que ça paraisse organisé)
		protected HashMap<String, typesVariables> dictType; //permet de gérer l'affichage dans le fichier. Il existe probablement une méthode plus propre pour cela...
		GestionFichier(){
			// type de chaque variables (l'idée est de garder un seul dictionnaire tout en différentiant 
			// l'affichage et le traitement lors de l'utilisation du fichier par l'utilisateur)
			dictType = new HashMap<String, typesVariables>();
			dictType.put("S0", typesVariables.Integer);
			dictType.put("I0", typesVariables.Integer);
			dictType.put("R0", typesVariables.Integer);
			dictType.put("Taille du monde", typesVariables.Integer);
			//coeffs
			dictType.put("Beta", typesVariables.Coeff);
			dictType.put("Gamma", typesVariables.Coeff);
			dictType.put("Alpha", typesVariables.Coeff);
			dictType.put("Proportion de Naissances", typesVariables.Coeff);
			dictType.put("Proportion de morts naturelles", typesVariables.Coeff);
			//options de simulation
			dictType.put("Spatialisation",typesVariables.Boolean);
			dictType.put("Dynamiques de population",typesVariables.Boolean);
			//politiques publiques
			dictType.put("Confinement", typesVariables.Pourcentage);
			dictType.put("Seuil confinement", typesVariables.Pourcentage);
			dictType.put("Port du masque", typesVariables.Pourcentage);
			dictType.put("Seuil port du masque", typesVariables.Pourcentage);
			dictType.put("Quarantaine", typesVariables.Boolean);
			dictType.put("Vaccination",typesVariables.Boolean);
		}
		private String genererContenuFichier() {
			StringBuilder res= new StringBuilder();
			res.append("Paramètres:\n\nParamètres initiaux\n");
			for(String key: keys) {
				res.append(key);
				switch(dictType.get(key)) {
				case Integer:
					res.append(": ");
					res.append(dict.get(key).intValue());
					res.append("\n");
					if(key.equals("Taille du monde")) {
						res.append("\nCoefficients:\n");
					}
					break;
				case Coeff:
					res.append(": ");
					res.append(dict.get(key).doubleValue());
					res.append("\n");
					if(key.equals("Proportion de morts naturelles")) {
						res.append("\nOptions de simulation:\n");
					}
					break;
				case Boolean:
					res.append("(Y/N): ");
					res.append(dict.get(key).intValue()==0?"N":"Y");
					res.append("\n");
					if(key.equals("Dynamiques de population")) {
						res.append("\nPolitiques publiques:\n");
					}
					break;
				case Pourcentage:
					res.append("(%): ");
					res.append(dict.get(key).doubleValue());
					res.append("\n");
					break;
				}
			}
			return res.toString();
		}
		void genererFichier(String cheminFichier, boolean ecraserSiFichierExistant) throws IOException{
			String contenu = genererContenuFichier();
			File fichier = new File(cheminFichier);
			if(!fichier.exists() || ecraserSiFichierExistant) {
				FileWriter f = new FileWriter(cheminFichier);
				BufferedWriter bw = new BufferedWriter(f);
				bw.write(contenu);
				bw.close();
			}
		}
	}
}
