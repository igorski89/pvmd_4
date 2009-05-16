import java.util.Hashtable;


/**
 * реалізує метод відбору інформативних ознак шляхом 
 * апроксимації матриці відстаней
 * @author igorevsukov
 *
 */
public class ChoosingInformationalAttributeMethod {
	/**
	 * конструктор класу
	 * @param sample вибірка, для якої буде проводитись метод
	 */
	public ChoosingInformationalAttributeMethod(MDSample sample){
		this.sample = sample.clone();
		D = new double[this.sample.size()][this.sample.size()];
		calcD();
		Dj = new Hashtable<Integer, double[][]>(this.sample.getDimension());
		calcDj();
//		sortedAttributes = new Hashtable<Integer, Integer>(this.sample.getDimension());
		sortedAttributes = new int[this.sample.getDimension()];
		sortAttributes();
	}
	
	/**
	 * вибірка, для якої проводиться метод
	 */
	private MDSample sample;
	/**
	 * @return вибірка, для якої проводиться метод
	 */
	public MDSample getSample(){ return sample; }
	
	/**
	 *  матриця відстаней між об'єктами
	 */
	private double[][] D;
	/**
	 * @return матриця відстаней між об'єктами
	 */
	public double[][] getD(){ return D; }
	/**
	 * вираховує значення матриці відстаней
	 */
	private void calcD(){
		final int n = sample.size();
		for(int i = 0; i < n; i++) 
			for(int l = 0; l < n; l++) 
				D[i][l] = distanceEuclid(sample.get(i), sample.get(l));
	}
	
	/**
	 * @param obj1 перший об'єкт
	 * @param obj2 другий об'єкт
	 * @return Евклідова відстань між об'єктами
	 */
	private double distanceEuclid(MDObject obj1, MDObject obj2){
		double d = 0.0;
		final int len = obj1.getParams().length;
		for(int i = 0; i < len; i++)
			d += Math.pow(obj1.getParams()[i]-obj2.getParams()[i], 2);
		
		d = Math.sqrt(d);
		return d;
	}

	/**
	 * матриці відстаней між об'єктами за кожним признаком
	 */
	private Hashtable<Integer, double[][]> Dj;
	/**
	 * @return таблиця матрицей відстаней за признаками
	 */
	public Hashtable<Integer, double[][]> getDj() { return Dj; }
	/**
	 * вираховує значення матриць відстаней за кожним признаком
	 */
	private void calcDj(){
		final int p = sample.getDimension(); // розмірність елементів
		final int n = sample.size(); //кількість елементів у вибірці
		for(int j = 0; j < p; j++){ //вираховуємо кожну матрицю Dj
			double[][] d = new double[n][n];
			for(int i = 0; i < n; i++)
				for(int l = 0; l < n; l++)
					d[i][l] = Math.abs(sample.get(i).getParams()[j]-sample.get(l).getParams()[j]);
				
			Dj.put(j, d);
		}
	}
	
	/**
	 * масив номерів параметрів відсортованих за інформативністю
	 */
	private int[] sortedAttributes;
	/**
	 * @return масив номерів параметрів відсортованих за інформативністю
	 */
	public int[] getSortedAttributes() { return sortedAttributes; }
	/**
	 * сортує параметри за їх інформативністю
	 */
	private void sortAttributes(){
		final int p = sample.getDimension();
		final int n = sample.size();
		//считаем начальные расстояния
		double [] delta = new double[p];
		for(int j = 0; j < p; j++){
			double tmp = 0;
			for(int i=0; i<n; i++)
				for(int l=0; l<n; l++)
					tmp += Math.abs(D[i][l]-Dj.get(j)[i][l]);
			delta[j] = tmp;
			sortedAttributes[j] = j;
		}
		//сортируем
		for(int i = p-1; i>=0; i--)
			for(int j = 0; j<i; j++)
				if (delta[j] > delta[j+1]){
					double tmp = delta[j];
					delta[j] = delta[j+1];
					delta[j+1] = tmp;
					int tmpi = sortedAttributes[j];
					sortedAttributes[j] = sortedAttributes[j+1];
					sortedAttributes[j+1] = tmpi;
				}		
	}
	
}
