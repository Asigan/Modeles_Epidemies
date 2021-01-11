package Population;

public class Personne {
	private int posX=0;
	private int posY=0;
	private int categorie;
	/**
	 * 
	 * @param cat La cat�gorie � laquelle appartient la personne dans la Population
	 */
	public Personne(int cat) {
		categorie = cat;
	}
	/**
	 * Change la coordonn�e x de la personne
	 * @param x
	 */
	public void setPosX(int x) {
		posX=x;
	}
	/**
	 * Change la coordonn�e y de la personne
	 * @param y
	 */
	public void setPosY(int y) {
		posY=y;
	}
	/**
	 * Change les coordonn�es de la personne
	 * @param x
	 * @param y
	 */
	public void setPos(int x, int y) {
		setPosX(x);
		setPosY(y);
	}
	/**
	 * Change la cat�gorie de la personne
	 * @param num Num�ro de la nouvelle cat�gorie
	 */
	public void setCategorie(int num) {
		categorie = num;
	}
	/**
	 * 
	 * @return la coordonn�e x de la personne
	 */
	public int getPosX() {
		return posX;
	}
	/**
	 * 
	 * @return la coordonn�e y de la personne
	 */
	public int getPosY() {
		return posY;
	}
	/**
	 * 
	 * @return la cat�gorie actuelle de la personne
	 */
	public int getCategorie() {
		return categorie;
	}
	
}
