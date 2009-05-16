
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Клас, що описує простий обєкт
 * @author I-Evsukov
 */
public class MDObject implements Cloneable {
	
    /**
     * параметри обєкту(x1,x2,..,xp)
     */
    private double params[];
    public double[] getParams() {
        return params;
    }
    /**
     * @param index індекс параметру
     * @return параметр об'єкту
     */
    public double getParam(int index){
    	return params[index];
    }
    public void setParams(double[] newParams) {
//    	for(int i = 0; i < params.length; i++)
//    		params[i] = newParams[i];
    	params = newParams;
    }
    /**
     * встановлює значення параметру за його індексом
     * @param i індекс параметра
     * @param newParam нове значення параметру
     */
    public void setParam(int i, double newParam){
    	if (i >= 0 && i < params.length)
    		params[i] = newParam;
    }

    /**
     * створює новий обєкт з масиву параметрів та номеру классу
     * @param objParams масив параметрів
     */
    public MDObject(double[] objParams){
//        params = new double[objParams.length];
//        for (int i = 0; i < objParams.length; i++) {
//            params[i] = objParams[i];
//        }
    	params = objParams;
    }

    /**
     * створює новий обєкт зчитуючи параметри та номер классу з строки(для
     * завантаження з файлу)
     * @param s строка, в якій записані параметри
     */
    public MDObject(String s) throws Exception{
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(s.trim());
        String[] str = matcher.replaceAll(" ").split(" ");
        params = new double[str.length];
        for(int i = 0; i< str.length; i++)
        	params[i] = Double.parseDouble(str[i]);
        
    }

    /**
     * повертає параметри та номер классу об'єкту у вигляді строки
     * @return (x1,x2,...,xp)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i]);
            sb.append(",");
        }
        sb.delete(sb.length()-2, sb.length()-1);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public MDObject clone() {
        double[] cloneParams = new double[params.length];
        for (int i=0; i<params.length; i++) 
            cloneParams[i] = params[i];
        
    	return new MDObject(cloneParams);
    }
}
