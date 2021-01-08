package Population;
public class mainTestTmp {

	public static void main(String[] args) {
		int[] qtes = new int[3];
		qtes[0]=523;
		qtes[1]=2;
		qtes[2]=0;
		Population pops = new Population(qtes,1);
		try {
			pops.nait(0);
			pops.meurt(1, 1);
			pops.changePop(0, 520);
			System.out.println(pops.getPop(0).size());
		}
		catch(CustomPopException e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

}
