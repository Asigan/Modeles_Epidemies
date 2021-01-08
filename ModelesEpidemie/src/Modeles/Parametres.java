package Modeles;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class Parametres {
	
	static public enum typesVariables{
		Integer,
		Coeff,
		Pourcentage,
		Boolean,
	}
	
	private HashMap<String, Double> dict;
	private List<String> keys; //garde l'ordre des variables
	private HashMap<String, typesVariables> dictType; //permet de gérer l'affichage dans le fichier en fonction du type. Il existe probablement une méthode plus propre pour cela...
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
		dictType = new HashMap<String, typesVariables>();
		keys = new ArrayList<String>(); 
		// valeurs censées être des entiers
		put("S0", 500.0, typesVariables.Integer);
		put("E0", 0.0, typesVariables.Integer);
		put("I0", 1.0, typesVariables.Integer);
		put("R0", 0.0, typesVariables.Integer);
		put("Taille du monde", 50.0, typesVariables.Integer);
		//coefficients
		put("Beta", 0.01, typesVariables.Coeff);
		put("Gamma", 0.16, typesVariables.Coeff);
		put("Alpha", 0.5, typesVariables.Coeff);
		put("Proportion de Naissances", 0.05, typesVariables.Coeff);
		put("Proportion de morts naturelles", 0.025, typesVariables.Coeff);
		// Options:
		put("Spatialisation", 0.0, typesVariables.Boolean);
		put("Dynamiques de population", 0.0, typesVariables.Boolean);
		// politiques publiques
		put("Confinement", 0.0, typesVariables.Pourcentage);
		put("Seuil confinement", 100.0, typesVariables.Pourcentage);
		put("Port du masque", 0.0, typesVariables.Pourcentage);
		put("Seuil port du masque", 100.0, typesVariables.Pourcentage);
		put("Quarantaine", 0.0, typesVariables.Boolean);
		put("Vaccination", 0.0, typesVariables.Pourcentage);
	}
	public void put(String key, double value, typesVariables type) {
		dict.put(key, value);
		keys.add(key);
		dictType.put(key, type);
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
	public double getParam(String key){
		return dict.get(key).doubleValue();
	}
	protected class GestionFichier {

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
		void extraireParam(String cheminFichier) throws ParamException{
			BufferedReader fichierSource=null;
			try {
				fichierSource = new BufferedReader(new FileReader(cheminFichier));
				String line="";
				for(String key: keys) {
					while(!line.startsWith(key)) {
						line = fichierSource.readLine();
						if(line==null) {
							throw new ParamException("Le fichier de paramètres a été corrompu: la variable "+key+" n'a pas pu être trouvée");
						}
					}
					String[] l = line.split(":"); 
					if(l.length<2) {
						throw new ParamException("La ligne commençant par "+key+" ne contient pas de valeur");
					}
					l[1]=l[1].trim();
					switch(dictType.get(key)) {
					case Integer:
						try {
							dict.replace(key, (double)Integer.parseInt(l[1]));
						}
						catch(NumberFormatException nfe){
							throw new ParamException("La ligne commençant par "+key+" devrait contenir une valeur entière", nfe);
						}
						break;
					case Coeff:
						Double valeur = null;
						try {
							valeur = Double.parseDouble(l[1]);
						}
						catch(NumberFormatException nfe){
							throw new ParamException("La ligne commençant par "+key+" devrait contenir une valeur décimale", nfe);
						}
						
						if(valeur!=null) {
							if(valeur>=0.0 && valeur<=1.0) {
								dict.replace(key, valeur);
							}
							else {
								throw new ParamException("La ligne commençant par "+key+" devrait contenir une valeur décimale comprise entre 0 et 1");
							}
						}

						break;
					case Pourcentage:
						Double valeurPourcentage = null;
						try {
							valeurPourcentage = Double.parseDouble(l[1]);
						}
						catch(NumberFormatException nfe){
							throw new ParamException("La ligne commençant par "+key+" devrait contenir une valeur décimale", nfe);
						}
						
						if(valeurPourcentage!=null) {
							if(valeurPourcentage>=0.0 && valeurPourcentage<=100.0) {
								dict.replace(key, valeurPourcentage);
							}
							else {
								throw new ParamException("La ligne commençant par "+key+" devrait contenir une valeur décimale comprise entre 0 et 100");
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
							throw new ParamException("La ligne commençant par "+key+" devrait contenir soit Y soit N (attention: sensible à la casse)");
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
