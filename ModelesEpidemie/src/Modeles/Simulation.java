package Modeles;

import java.io.IOException;

public class Simulation {
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
		Modele test = new ModeleSIR();
		int nbJours = 400;
		for(int i=0; i<=nbJours; i++) {
			test.unJour();
			int[] datas = test.getPopsNumbers();
			String[] names = test.getPopsName();
			System.out.println("Jour "+i+" - ");
			for(int j=0; j<datas.length; j++) {
				System.out.print(names[j]+" - "+ datas[j]+" - ");
			}
			System.out.print("\n");
		}
	}
}
