package Modeles;

import java.io.IOException;

public class main {
	static Parametres param;
	public static void main(String[] args) {
		param = Parametres.getInstance();
		String cheminFichier="param.txt";
		try {
			param.genererFichierSiNonExistant(cheminFichier);
			param.setParametresAvecFichier(cheminFichier);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParamException e) {
			e.printStackTrace();
		}
	}
}
