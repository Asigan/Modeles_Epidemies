package Population;

import java.util.ArrayList;
import java.util.List;

public class Population {
	private List<List<Personne>> population= new ArrayList<List<Personne>>();
	
	public Population(int[] popQte) {
		
		for(int i=0; i<popQte.length;i++) {
			population.add(new ArrayList<Personne>());
			for(int j=0; j<popQte[i]; j++) {
				population.get(i).add(new Personne());
			}
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
			pop.remove(personneIndex);
		}
		
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
		catch(Exception e) {
			throw new CustomPopException("Index "+ personneIndex +" doesn't match with a person in category "+ categorieSourceIndex, e);
		}
		
	}
	public void meurt(int categorieIndex, int personneIndex) throws CustomPopException{
		
		try {
			List<Personne> pop = this.getPop(categorieIndex);
			pop.remove(personneIndex);
		}
		
		catch(CustomPopException e) {
			throw new CustomPopException(e.getMessage(), e);
		}
		
		catch(Exception e) {
			throw new CustomPopException("Index "+ personneIndex +" doesn't match with a person in category "+categorieIndex, e);
		}
		
	}
	
	public void nait() throws CustomPopException{
		
		try {
			population.get(0).add(new Personne());
		}
		
		catch(Exception err) {
			throw new CustomPopException("Can't create people if there is no category", err);
		}
		
	}
	
	public void nait(int nombre) throws CustomPopException{
		
		for(int i=0; i<nombre; i++) {
			nait();
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
	
}
