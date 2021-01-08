package Population;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Population {
	private List<List<Personne>> population= new ArrayList<List<Personne>>();
	private Map map;
	public Population(int[] popQte, int tailleMonde) {
		for(int i=0; i<popQte.length;i++) {
			population.add(new ArrayList<Personne>());
			for(int j=0; j<popQte[i]; j++) {
				population.get(i).add(new Personne(i));
			}
		}
		map = new Map(this, tailleMonde);
	}
	public int getNbPersonnesTotal() {
		int nb=0;
		for(List<Personne> l: population) {
			nb+=l.size();
		}
		return nb;
	}
	public int getNbPersonnes(int i) {
		try {
			return getPop(i).size();
		}catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
	}
	public void changePop(int categorieSourceIndex, int personneIndex) throws CustomPopException {
		
		try {
			changePop(categorieSourceIndex, categorieSourceIndex+1, personneIndex);
		}
		
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
	}
	public void changePop(int categorieSourceIndex, int categorieCibleIndex, int personneIndex) throws CustomPopException {
		
		try {
			List<Personne> pop = this.getPop(categorieSourceIndex);
			Personne p = pop.get(personneIndex);
			this.getPop(categorieCibleIndex).add(p);
			p.setCategorie(categorieCibleIndex);
			pop.remove(personneIndex);
		}
		
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
		catch(Exception e) {
			throw new CustomPopException("Index "+ personneIndex +" doesn't match with a person in category "+ categorieSourceIndex, e);
		}
		
	}
	public void changePop(Personne personne, int categorieCibleIndex) {
		try {
			List<Personne> pop = this.getPop(personne.getCategorie());
			getPop(categorieCibleIndex).add(personne);
			personne.setCategorie(categorieCibleIndex);
			pop.remove(personne);
		}
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
		catch(Exception e) {
			throw new CustomPopException("Person couldn't be found in its category", e);
		}
	}
	public void changePop(Personne personne) {
		try {
			changePop(personne, personne.getCategorie());
		}
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
	}
	public void meurt(int categorieIndex, int personneIndex) throws CustomPopException{
		
		try {
			List<Personne> pop = this.getPop(categorieIndex);
			Personne p = pop.remove(personneIndex);
			map.remove(p);
		}
		
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
		catch(Exception e) {
			throw new CustomPopException("Index "+ personneIndex +" doesn't match with a person in category "+categorieIndex, e);
		}
		
	}
	
	public void nait(int numCat) throws CustomPopException{
		
		try {
			Personne p = new Personne(numCat);
			population.get(numCat).add(p);
			map.add(p, true);
		}
		
		catch(Exception err) {
			throw new CustomPopException("Index "+numCat+" is out of bounds", err);
		}
		
	}
	
	public List<List<Personne>> getPop(){
		
		return population;
		
	}
	
	public List<Personne> getPop(int numero) throws CustomPopException{
		
		try {
			return getPop().get(numero);
		}
		
		catch(Exception err) {
			throw new CustomPopException("Index out of range: category number "+numero+" doesn't exist", err);
		}
		
	}
	public boolean deplace(Personne p, int newPosX, int newPosY) {
		boolean res = map.remove(p);
		if(res) {
			p.setPos(newPosX, newPosY);
			try {
				map.add(p);
			}
			catch(IndexOutOfBoundsException e) {
				e.printStackTrace();
				System.out.println("L'index ("+newPosX+", "+newPosY+") ne semble par correspondre à une case de la carte");
			}
		}
		return res;
	}
	
	public LinkedList<Personne> getNextCaseOccupee() {
		LinkedList<Personne> res=null;
		do {
			res = map.getNextCase();
		}while(res!=null && res.isEmpty());
		return res;
	}

	
}
