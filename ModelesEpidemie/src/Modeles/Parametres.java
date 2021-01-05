package Modeles;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
public class Parametres {
	
	static public enum typesVariables{
		Integer,
		Coeff,
		Pourcentage,
		Boolean,
	}
	
	protected HashMap<String, Double> dict;
	protected GestionFichier gfile;
	
	private static Parametres instance = null;
	public static Parametres getInstance() {
		if(instance==null) {
			instance = new Parametres();
		}
		return instance;
	}
	
	private Parametres() {
		gfile = new GestionFichier();
		dict = new HashMap<String, Double>();
		// valeurs cens�es �tre des entiers
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
	
	public void setParametresAvecFichier(String cheminFichier) throws ParamException{
		gfile.extraireParam(cheminFichier);
	}
	protected class GestionFichier {
		protected final String[] keys = new String[] {"S0","I0","R0","Taille du monde","Beta","Gamma","Alpha","Proportion de Naissances",
				"Proportion de morts naturelles","Spatialisation", "Dynamiques de population", "Confinement","Seuil confinement",
				"Port du masque","Seuil port du masque","Quarantaine","Vaccination"};  //sert seulement � garder l'ordre des variables dans le fichier(et que �a paraisse organis�)
		protected HashMap<String, typesVariables> dictType; //permet de g�rer l'affichage dans le fichier. Il existe probablement une m�thode plus propre pour cela...
		GestionFichier(){
			// type de chaque variables (l'id�e est de garder un seul dictionnaire tout en diff�rentiant 
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
			res.append("Param�tres:\n\nParam�tres initiaux\n");
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
		void extraireParam(String cheminFichier) throws ParamException{
			BufferedReader fichierSource=null;
			try {
				fichierSource = new BufferedReader(new FileReader(cheminFichier));
				String line="";
				for(String key: keys) {
					while(!line.startsWith(key)) {
						line = fichierSource.readLine();
						if(line==null) {
							throw new ParamException("Le fichier de param�tres a �t� corrompu: la variable "+key+" n'a pas pu �tre trouv�e");
						}
					}
					String[] l = line.split(":"); 
					if(l.length<2) {
						throw new ParamException("La ligne commen�ant par "+key+" ne contient pas de valeur");
					}
					l[1]=l[1].trim();
					switch(dictType.get(key)) {
					case Integer:
						try {
							dict.replace(key, (double)Integer.parseInt(l[1]));
						}
						catch(NumberFormatException nfe){
							throw new ParamException("La ligne commen�ant par "+key+" devrait contenir une valeur enti�re", nfe);
						}
						break;
					case Coeff:
						Double valeur = null;
						try {
							valeur = Double.parseDouble(l[1]);
						}
						catch(NumberFormatException nfe){
							throw new ParamException("La ligne commen�ant par "+key+" devrait contenir une valeur d�cimale", nfe);
						}
						
						if(valeur!=null) {
							if(valeur>=0.0 && valeur<=1.0) {
								dict.replace(key, valeur);
							}
							else {
								throw new ParamException("La ligne commen�ant par "+key+" devrait contenir une valeur d�cimale comprise entre 0 et 1");
							}
						}

						break;
					case Pourcentage:
						Double valeurPourcentage = null;
						try {
							valeurPourcentage = Double.parseDouble(l[1]);
						}
						catch(NumberFormatException nfe){
							throw new ParamException("La ligne commen�ant par "+key+" devrait contenir une valeur d�cimale", nfe);
						}
						
						if(valeurPourcentage!=null) {
							if(valeurPourcentage>=0.0 && valeurPourcentage<=100.0) {
								dict.replace(key, valeurPourcentage);
							}
							else {
								throw new ParamException("La ligne commen�ant par "+key+" devrait contenir une valeur d�cimale comprise entre 0 et 100");
							}
						}
						break;
					case Boolean:
						if(l[1].equals("Y")) {
							dict.replace(key, 1.0);
						}
						else if(l[1].equals("N")) {
							dict.replace(key, 0.0);
						}
						else {
							throw new ParamException("La ligne commen�ant par "+key+" devrait contenir soit Y soit N (attention: sensible � la casse)");
						}
						break;
					}
				}
			}
			catch(IOException e) {
				throw new ParamException(e.getMessage(), e);
			}
			finally {

				if(fichierSource!=null)
					try {
						fichierSource.close();
					} catch (IOException e) {}

			}
			
		}
	}
}
