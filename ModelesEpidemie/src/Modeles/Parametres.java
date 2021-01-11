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
	private HashMap<String, typesVariables> dictType; //permet de g�rer l'affichage dans le fichier en fonction du type. Il existe probablement une m�thode plus propre pour cela...
	protected GestionFichier gfile;
	
	private static Parametres instance = null;
	/**
	 * 
	 * @return l'instance de Param�tres
	 */
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
		// valeurs cens�es �tre des entiers
		put("S0", 500.0, typesVariables.Integer);
		put("E0", 0.0, typesVariables.Integer);
		put("I0", 1.0, typesVariables.Integer);
		put("R0", 0.0, typesVariables.Integer);
		put("Taille du monde", 10.0, typesVariables.Integer);
		//coefficients
		put("Beta", 0.01, typesVariables.Coeff);
		put("Gamma", 0.16, typesVariables.Coeff);
		put("Alpha", 0.1, typesVariables.Coeff);
		put("Proportion de Naissances", 0.0005, typesVariables.Coeff);
		put("Proportion de morts naturelles", 0.0004, typesVariables.Coeff);
		// Options:
		put("Spatialisation", 0.0, typesVariables.Boolean);
		put("Dynamiques de population", 0.0, typesVariables.Boolean);
		// politiques publiques
		put("Confinement", 0.0, typesVariables.Pourcentage);
		put("Seuil confinement", 100.0, typesVariables.Pourcentage);
		put("Port du masque", 0.0, typesVariables.Pourcentage);
		put("Quarantaine", 0.0, typesVariables.Boolean);
		put("Vaccination", 0.0, typesVariables.Pourcentage);
	}
	/**
	 * Ajoute un nouveau param�tre
	 * @param key Nom du param�tre
	 * @param value Valeur du param�tre
	 * @param type La fa�on dont doit �tre repr�sent�e le param�tre pour l'utilisateur (Integer, Coeff, Pourcentage ou Boolean)
	 */
	public void put(String key, double value, typesVariables type) {
		dict.put(key, value);
		keys.add(key);
		dictType.put(key, type);
	}
	/**
	 * Cr�� le fichier, et l'�crase si n�cessaire
	 * @param cheminFichier 
	 * @throws IOException
	 */
	public void regenererFichier(String cheminFichier) throws IOException{
		gfile.genererFichier(cheminFichier, true);	
		}
	/**
	 * Cr�� le fichier s'il n'existe pas
	 * @param cheminFichier
	 * @throws IOException
	 */
	public void genererFichierSiNonExistant(String cheminFichier)throws IOException{
		gfile.genererFichier(cheminFichier, false);
	}
	/**
	 * Extrait les param�tres du fichier
	 * @param cheminFichier
	 * @throws ParamException
	 */
	public void setParametresAvecFichier(String cheminFichier) throws ParamException{
		gfile.extraireParams(cheminFichier);
	}
	/**
	 * Permet d'obtenir la valeur d'un param�tre
	 * @param key Nom du param�tre
	 * @return
	 */
	public double getParam(String key){
		return dict.get(key).doubleValue();
	}
	/**
	 * Renvoie le contenu � afficher � la console
	 * @return 
	 */
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("Param�tres:\n");
		for(int i=1; i<=keys.size(); i++) {
			res.append(i);
			res.append(" - ");
			res.append(gfile.genererLigne(keys.get(i-1)));
		}
		return res.toString();
	}
	/**
	 * Change la valeur d'un param�tre gr�ce � l'index
	 * @param numero Index du param�tre
	 * @param valueAsString Nouvelle valeur, telle qu'elle serait entr�e par l'utilisateur
	 * @throws ParamException
	 */
	public void setParam(int numero, String valueAsString) throws ParamException{
		setParam(keys.get(numero), valueAsString);
	}
	/**
	 * Change la valeur du param�tre gr�ce � son nom
	 * @param key Nom du param�tre
	 * @param newValueAsString Nouvelle valeur, telle qu'elle serait entr�e par l'utilisateur
	 * @throws ParamException
	 */
	public void setParam(String key, String newValueAsString) throws ParamException{
		newValueAsString = newValueAsString.trim();
		switch(dictType.get(key)) {
		case Integer:
			try {
				dict.replace(key, (double)Integer.parseInt(newValueAsString));
			}
			catch(NumberFormatException nfe){
				throw new ParamException("La ligne commen�ant par "+key+" devrait contenir une valeur enti�re", nfe);
			}
			break;
		case Coeff:
			Double valeur = null;
			try {
				valeur = Double.parseDouble(newValueAsString);
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
				valeurPourcentage = Double.parseDouble(newValueAsString);
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
			if(newValueAsString.equals("Y")) {
				dict.replace(key, 1.0);
			}
			else if(newValueAsString.equals("N")) {
				dict.replace(key, 0.0);
			}
			else {
				throw new ParamException("La ligne commen�ant par "+key+" devrait contenir soit Y soit N (attention: sensible � la casse)");
			}
			break;
		}
	}
	/**
	 * 
	 * @return Le nombre total de param�tres
	 */
	public int getNumberOfParameters() {
		return keys.size();
	}
	/**
	 * 
	 * @param index
	 * @return Le nom du param�tre � l'index indiqu�
	 */
	public String getKeyAtIndex(int index) {
		return keys.get(index);
	}
	
	
	protected class GestionFichier {

		private String genererContenuFichier() {
			StringBuilder res= new StringBuilder();
			res.append("Param�tres:\n\nParam�tres initiaux\n");
			for(String key: keys) {
				res.append(genererLigne(key));
			}
			return res.toString();
		}
		String genererLigne(String key) {
			StringBuilder res= new StringBuilder();
			res.append(key);
			switch(dictType.get(key)) {
			case Integer:
				res.append(": ");
				res.append(dict.get(key).intValue());
				res.append("\n");
				break;
			case Coeff:
				res.append(": ");
				res.append(dict.get(key).doubleValue());
				res.append("\n");
				break;
			case Boolean:
				res.append("(Y/N): ");
				res.append(dict.get(key).intValue()==0?"N":"Y");
				res.append("\n");
				break;
			case Pourcentage:
				res.append("(%): ");
				res.append(dict.get(key).doubleValue());
				res.append("\n");
				break;
			}
			return res.toString();
		}
		/**
		 * G�n�re le fichier de param�tres
		 * @param cheminFichier 
		 * @param ecraserSiFichierExistant Si le fichier existe d�j�, alors �crase le fichier si vrai, ne fait rien sinon
		 * @throws IOException
		 */
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
		/**
		 * Parcourt le fichier pour en extraire les param�tres 
		 * @param cheminFichier
		 * @throws ParamException
		 */
		void extraireParams(String cheminFichier) throws ParamException{
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
					setParam(key, l[1]);
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
