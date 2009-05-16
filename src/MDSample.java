
import java.util.ArrayList;

/**
 * клас, що описує вибірку
 * @author igorevsukov
 */
public class MDSample implements Cloneable {
	/**
	 * контейнер для елементів вибірки
	 */
    private ArrayList<MDObject> data;
    private ArrayList<MDObject> backup;

    /**
     * розмірність елементів у вибірці
     */
    private int dimension;
    /**
     * @return кількість параметрів об'єкту вибірки
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * стрворює нову вибірку з елементами заданої розмірності
     * @param d розмірність елементів вибірки
     */
    public MDSample(int d) {
        dimension = d;
        data = new ArrayList<MDObject>();
        backup = new ArrayList<MDObject>();
        xmin = new double[d];
        xmax = new double[d];
        mean = new double[d];
        disp = new double[d];
        covariationMatrix = new double[dimension][dimension];
        corelationMatrix = new double[dimension][dimension];
    }

    /**
     * повертає елемент вибірки
     * @param i порядковий номер необхідного елементу
     * @return
     */
    public MDObject get(int i) {
        return data.get(i);
    }

    /**
     * розмір вибірки
     * @return кількість елементів у вибірці
     */
    public int size() {
        return data.size();
    }

    /**
     * додає об'єкт до вибірки
     * @param newObj новий об'єкт
     */
    public void add(MDObject newObj) throws Exception {
        if (newObj.getParams().length != dimension)
            throw new Exception("MDobject dimension is different to this MDSample");
        
        data.add(newObj);
    }

    /**
     * видаляє об'єкт з вибірки
     * @param i індекс об'єкту
     */
    public void remove(int i) {
        data.remove(i);
    }

    /**
     * видаляє об'єкт з вибірки
     * @param obj об'єкт, що видаляється
     */
    public void remove(MDObject obj) {
        data.remove(obj);
    }

    /**
     * видаляє всі об'єкти з вибірки
     */
    public void removeAll() {
        data.clear();
    }

	/**
	 * створює бекап з поточних даних вибірки
	 */
	public void backup() {
	    backup.clear();
	    for (MDObject obj:data) backup.add(obj.clone());
	}
    
    /**
     * відновлює дані з бекапу
     */
    public void restore() {
        data.clear();
        for(MDObject obj:backup) data.add(obj.clone());
    }
    
    /**
     * мінімальне значення по параметрам
     */
    private double [] xmin;
    public double[] getXmin() { return xmin; }
    /**
     * максимальне значення за параметрами
     */
    private double [] xmax;
    public double[] getXmax() { return xmax; }
    /**
     * середнє значення за параметрами
     */
    private double [] mean;
    public double[] getMean() { return mean; }
    /**
     * дисперсія за параметрами
     */
    private double [] disp;
    public double[] getDisp() { return disp; }
    
	/**
	 * кореляційна матриця
	 */
	private double[][] covariationMatrix;
	public double[][] getCovariationMatrix(){ return covariationMatrix; }
	
	/**
	 * вираховує коваріаційну матрицю
	 */
	public void calcCovariationMatrix(){
		final int data_size = data.size();
		for(int i=0; i<dimension; i++)
			for(int j=0; j<dimension; j++)
				for(int h=0; h<data_size; h++)
					covariationMatrix[i][j] += (data.get(h).getParam(i)-mean[i])*(data.get(h).getParam(j)-mean[j]);
		
		for(int i=0; i<dimension; i++)
			for(int j=0; j<dimension; j++)
				covariationMatrix[i][j] /= data_size;
		
		for(int i=0; i<dimension; i++)
			covariationMatrix[i][i] = 1;
	}
	
	private double[][] corelationMatrix;
	public double[][] getCorelationMatrix() { return corelationMatrix; }
	public void calcCorelationMatrix() {
		final int data_size = data.size();
		for(int i=0; i<dimension; i++)
			for(int j=0; j<dimension; j++)
				for(int h=0; h<data_size; h++)
					corelationMatrix[i][j] += (data.get(h).getParam(i)-mean[i])*(data.get(h).getParam(j)-mean[j])/(disp[i]*disp[j]);
		
		for(int i=0; i<dimension; i++)
			for(int j=0; j<dimension; j++)
				corelationMatrix[i][j] /= data_size;
		
		for(int i=0; i<dimension; i++)
			corelationMatrix[i][i] = 1;
	}
    
    /**
     * вираховує характеристики вибірки: середнє, дисперсію та ін.
     */
    public void calculateParams() {
        //присваиваем начальные значения
        for (int j=0; j<dimension; j++) {
            double tmp = data.get(0).getParams()[j];
            mean[j] = tmp;
            xmin[j] = tmp;
            xmax[j] = tmp;
        }

        //вычисляем реальные значения
        final int data_size = data.size();
        for(int i=1; i<data_size; i++) {
            for (int j=0; j<dimension; j++) {
                double tmp = data.get(i).getParams()[j];
                mean[j] += tmp;
                if (tmp < xmin[j])
                    xmin[j] = tmp;
                else if (tmp > xmax[j])
                    xmax[j] = tmp;
            }
        }

        for (int j=0; j<dimension; j++) {
            mean[j] /= data_size;
        }

        //если размер выборки == 1, то дисперсия нах не нужна
        if (data.size() > 1) {
            for (int i=0; i<data_size; i++)
                for (int j=0; j<dimension; j++)
                    disp[j] += Math.pow(data.get(i).getParams()[j]-mean[j], 2);

            for (int j=0; j<dimension; j++)
                disp[j] = Math.sqrt(disp[j]/(data_size-1.0));
        }
    	
//    	final int data_size = data.size();
//    	for(int j=0; j<dimension; j++)
//    		for(int i=0; i<data_size; i++)
//    			mean[j] += data.get(i).getParam(j);
//    	for(int j=0; j<dimension; j++)
//			mean[j] /= data_size;
//    	
//    	if (data_size > 1){
//        	for(int j=0; j<dimension; j++)
//        		for(int i=0; i<data_size; i++)
//        			disp[j] += Math.pow(data.get(i).getParam(j)-mean[j], 2);
//        	for(int j=0; j<dimension; j++)
//        		disp[j] = Math.sqrt(disp[j]/(data_size-1));
//    	}
    }
    
    /**
     * центрує дані вибірки 
     */
    public void center(){
    	calculateParams();
    	final int data_size = data.size();
    	final int p = getDimension();
    	for(int i = 0; i < data_size; i++){
    		for(int j = 0; j < p; j++)
    			data.get(i).getParams()[j] -= mean[j];
    	}
    	calculateParams();
    	calcCovariationMatrix();
    	calcCorelationMatrix();
    }
    
    /**
     * нормалізує дані вибірки
     */
    public void normalize(){
    	calculateParams();
    	final int data_size = data.size();
    	for(int i=0; i < data_size; i++)
    		for(int j=0; j < dimension; j++)
//    			data.get(i).getParams()[j] = (data.get(i).getParam(j)-mean[j])/disp[j];
    			data.get(i).setParam(j, (data.get(i).getParam(j)-mean[j])/disp[j]);
    	
    	calculateParams();
    	calcCovariationMatrix();
    	calcCorelationMatrix();
    }
    
    @Override
    public MDSample clone(){
    	MDSample clone = new MDSample(dimension);
    	final int data_size = data.size();
    	for(int i=0; i<data_size; i++){
			try {
				clone.add(data.get(i).clone());
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return clone;
    }
}
