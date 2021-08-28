package com.shitajimado.academicwritingrecommender.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class StatisticalMethods {

	int[] markers; //количество маркеров

	public StatisticalMethods() {
		this.markers = null;
	}

	public StatisticalMethods(int[] mark) {
		this.markers = mark;
	}

	//среднее выборочное
	double getMean() {
		double sum = (double) Arrays.stream(markers).sum();
		double N = (double) markers.length;
		double result = sum/N;
		return this.round(result);
	}

	//мода
	double getMode() {
		ArrayList<Double> mode = new ArrayList<Double>();
		int[] unique_array = Arrays.stream(markers).distinct().toArray();

		int[] count = new int[unique_array.length];

		for (int i=0; i<unique_array.length;i++){
			for (int m : markers) {
				if (unique_array[i]==m)
					count[i]++; //подсчет количества повторений каждого значения
			}
		}

		int max_repeat = Arrays.stream(count).max().getAsInt();

		for(int i=0; i<count.length; i++){
			if(count[i]==max_repeat) //определяем количество мод
				mode.add(Double.valueOf(unique_array[i]));
		}

		Double[] mod = mode.toArray(new Double[mode.size()]);
		double[] modes = Stream.of(mod).mapToDouble(Double::doubleValue).toArray();

		double median = getMedian();

		//выбор моды, которая больше всего приближена к медиане
		if(modes.length>1){
			double closest = Double.MAX_VALUE;
			for(double m: modes){
				if(Math.abs(m-median) < Math.abs(closest-median))
					closest=m;
			}
			return closest;
		}
		
		return modes[0];   
   }


   //медиана
   double getMedian()
   {
	   Arrays.sort(markers);
	   if (markers.length%2==0){
		   return (Double.valueOf(markers[markers.length/2-1])+Double.valueOf(markers[markers.length/2]))/2;
	   }
	   else{
		   if(markers.length==1)
		   		return markers[0];
		   return (Double.valueOf(markers[(markers.length-1)/2]));
	   }
   }

   //дисперсия
   double getVariance(){
	   double mean = getMean();
	   double numerator = 0;
	for(double m :markers)
		numerator += Math.pow(m-mean, 2);
	return this.round(numerator/(markers.length));
   }

   //среднеквадратичное отклонение
   double getStandardDeviation() {
	return this.round(Math.sqrt(getVariance()));
}

//коэффициент вариации
double getCoefficientOfVariation() {
	if(getMean()>0)
		return this.round(getStandardDeviation()/getMean()*100);
	return 0.7777;
}

//центральный момент
double getCenralMoment(int pow) {
	double mean = getMean();
	double numerator = 0;
	for(double m: markers)
		numerator+= Math.pow(m-mean, pow);
	return numerator/markers.length;
}

//коэффициен асиметрии
double getSkewness() {
	double central_moment_3 = getCenralMoment(3);
	double standard_deviation_3 = Math.pow(getStandardDeviation(),3);
	if(standard_deviation_3!=0)
		return this.round(central_moment_3/standard_deviation_3);
	return 0.7777;
}

//эксцесс
double getKurtosis() {
	double central_moment_4 = getCenralMoment(4);
	double standard_deviation_4 = Math.pow(getVariance(),2);
	if(standard_deviation_4!=0)
		return this.round(central_moment_4/standard_deviation_4-3);
	return 0.7777;
}

//минимальное значение
double getMin() {
	Arrays.sort(markers);
	return markers[0];
}

//максимальное значение
double getMax() {
	Arrays.sort(markers);
	return markers[markers.length-1];
}

//квартиль уровня p
double getQuartile(double p) {
	Arrays.sort(markers);
	if(markers.length*p == Math.ceil(markers.length*p))
		if((int)(markers.length*p)>0)
			return markers[(int)(markers.length*p)-1];
		else
			return  markers[(int)(markers.length*p)];
	else
		return markers[(int)(markers.length*p)];
}

//интерквартильный размах
double getInterQuartile() {
	return this.round(this.getQuartile(0.75) - this.getQuartile(0.25));
}

//округление значения статистик до сотых
double round(double stats) {
	return new BigDecimal(stats).setScale(2, RoundingMode.HALF_UP).doubleValue();
 }

public static Statistics Calculation(Map<String, double[]> input_data){
	List<StatisticsNode> nodes = new ArrayList<>();

    for(java.util.Map.Entry<String, double[]> mark : input_data.entrySet()) {
		int[] marker = new int[mark.getValue().length];
		int i = 0;
		for(double d : mark.getValue()){
			marker[i] = (int)d;
			i=i+1;
		}

		int[] frequency = marker.clone();

		if(marker.length>1)
        {
			StatisticalMethods calc = new StatisticalMethods(marker); //вычисление статистик по каждому маркеру
			double [] stats = new double[]{calc.getMean(), calc.getMode(), calc.getMedian(), calc.getVariance(),
				calc.getStandardDeviation(), calc.getCoefficientOfVariation(), calc.getSkewness(), calc.getKurtosis(),
				calc.getMin(), calc.getQuartile(0.25), calc.getMedian(), calc.getQuartile(0.75), calc.getMax(),
				calc.getInterQuartile()};

				nodes.add(new StatisticsNode(mark.getKey(), stats, frequency));
		}
    }
    return new Statistics(nodes);  
}

}
