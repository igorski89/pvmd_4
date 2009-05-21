
import java.util.ArrayList;

import Jama.*;

/**
 * реалізує метод головних компонент
 * @author igorevsukov
 */
public class MainComponentsMethod {
	/**
	 * вибірка, для якої проводиться метод
	 */
	private MDSample sample;
	public MDSample getSample(){
		return sample;
	}
	
	private ArrayList<double[]> A = new ArrayList<double[]>();
	public ArrayList<double[]> getA() { return A; }
	
	private ArrayList<double[]> Z = new ArrayList<double[]>();
	/**
	 * @return нова вибірка
	 */
	public ArrayList<double[]> getZ() { return Z; }
	
	private ArrayList<Double> eignValue = new ArrayList<Double>();
	/**
	 * @return ветор власних значень
	 */
	public ArrayList<Double> getEignValue() { return eignValue; }
	
	private ArrayList<Double> delta = new ArrayList<Double>();
	public ArrayList<Double> getDelta() { return delta; }
	
	private int compCount = 0;
	public int getCompCount() { return compCount; }
	
	/**
	 * конструктор класу
	 * @param sample вибірка, для якої буде проводитись метод
	 */
	public MainComponentsMethod(MDSample sample){
		this.sample = sample.clone();
		this.sample.normalize();
//		this.sample.center();
	}
	
	/**
	 * проводить мінімізацію МГК та повертає ті компоненти,
	 * що в суммі пояснюють не менше 75% загальної дисперсії
	 */
	public void minimize75(){
		minimize();
		
		compCount = 0;
		double s = 0;
		for(Double val:eignValue)
			s+=val.doubleValue();
		
		double disp = 0;
		while (disp < 0.75) {
			disp += eignValue.get(compCount)/s;
			compCount++;
		}
		
		calcDelta();
		calcNewVectors();
	}
	
	/**
	 * проводить мінімізацію МГК та повертає ті компоненти,
	 * власі числа котрих більше порог
	 * @param threshold
	 */
	public void minimizeByThreshold(double threshold){
		minimize();
		
		compCount = 0;
		while(compCount < eignValue.size() && eignValue.get(compCount) >= threshold)
			compCount++;
		
		calcDelta();
		calcNewVectors();
	}
	
	/**
	 * проводить мінімізацію МГК та повертає всі компоненти, як головні
	 */
	public void minimizeAll(){
		minimize();
		
		compCount = eignValue.size();

		calcDelta();
		calcNewVectors();
	}
	
	private void minimize(){
		double[][] covMatrix = sample.getCovariationMatrix();
//		EigenvalueDecomposition eign = new EigenvalueDecomposition(new Matrix(sample.getCovariationMatrix()));
		EigenvalueDecomposition eign = new EigenvalueDecomposition(new Matrix(covMatrix));
		
		ArrayList<Integer> ind = new ArrayList<Integer>(eign.getRealEigenvalues().length);
		for(int i=0; i < eign.getRealEigenvalues().length; i++)
			ind.add(new Integer(i));
		
		eignValue.clear();
		for(int i=0; i < eign.getRealEigenvalues().length; i++)
			eignValue.add(eign.getRealEigenvalues()[i]);
		
		for(int i=0; i < eignValue.size(); i++)
			for(int j=i; j < eignValue.size(); j++)
				if (eignValue.get(i) < eignValue.get(j)){
					double temp = eignValue.get(i);
					eignValue.set(i, eignValue.get(j).doubleValue());
					eignValue.set(j, temp);
					
					int tempi = ind.get(i);
					ind.set(i, ind.get(j).intValue());
					ind.set(j, tempi);
				}
		
		A.clear();
		Matrix eignMatrix = eign.getV();
		for(int i=0; i < eignValue.size(); i++) {
			double[] col = new double[eignMatrix.getRowDimension()];
			int colInd = ind.get(i);
			for(int rowInd=0; rowInd < col.length; rowInd++)
				col[rowInd] = eignMatrix.get(rowInd, colInd);
			A.add(col);
		}
		
		
		
	}
	
	public void calcNewVectors(){
		Z.clear();
		for(int i=0; i<sample.size(); i++){
//			double[] zTemp = new double[compCount];
			double[] zTemp = new double[sample.getDimension()];
//			for(int j=0; j<compCount; j++)
			for(int j=0; j<sample.getDimension(); j++)
				for(int h=0; h < sample.getDimension(); h++)
					zTemp[j] += A.get(j)[h]*sample.get(i).getParams()[h];
			
			Z.add(zTemp);
		}
	}
	
	public void calcDelta(){
		delta.clear();
		for(int i=0; i < sample.getDimension(); i++){
			double temp = 0;
			for(int j=0; j<compCount; j++)
//			for(int j=0; j<sample.getDimension(); j++)
				temp += A.get(j)[i]*A.get(j)[i];
			temp *= 100;
			delta.add(temp);
		}
	}
}
