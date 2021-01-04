package Population;

public class Personne {
	int posX=0;
	int posY=0;
	
	public void setPosX(int x) {
		posX=x;
	}
	public void setPosY(int y) {
		posY=y;
	}
	public void setPos(int x, int y) {
		setPosX(x);
		setPosY(y);
	}
	public int getPosX() {
		return posX;
	}
	public int getPosY() {
		return posY;
	}
}
