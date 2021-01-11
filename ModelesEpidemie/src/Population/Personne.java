package Population;

public class Personne {
	private int posX=0;
	private int posY=0;
	private int categorie;
	/**
	 * 
	 * @param cat La catégorie à laquelle appartient la personne dans la Population
	 */
	public Personne(int cat) {
		categorie = cat;
	}
	/**
	 * Change la coordonnée x de la personne
	 * @param x
	 */
	public void setPosX(int x) {
		posX=x;
	}
	/**
	 * Change la coordonnée y de la personne
	 * @param y
	 */
	public void setPosY(int y) {
		posY=y;
	}
	/**
	 * Change les coordonnées de la personne
	 * @param x
	 * @param y
	 */
	public void setPos(int x, int y) {
		setPosX(x);
		setPosY(y);
	}
	/**
	 * Change la catégorie de la personne
	 * @param num Numéro de la nouvelle catégorie
	 */
	public void setCategorie(int num) {
		categorie = num;
	}
	/**
	 * 
	 * @return la coordonnée x de la personne
	 */
	public int getPosX() {
		return posX;
	}
	/**
	 * 
	 * @return la coordonnée y de la personne
	 */
	public int getPosY() {
		return posY;
	}
	/**
	 * 
	 * @return la catégorie actuelle de la personne
	 */
	public int getCategorie() {
		return categorie;
	}
	
}
