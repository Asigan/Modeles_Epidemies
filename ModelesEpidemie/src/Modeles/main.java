package Modeles;

import java.io.IOException;

public class main {
	static Parametres param;
	public static void main(String[] args) {
		param = Parametres.getInstance();
		try {
			param.genererFichierSiNonExistant("param.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
