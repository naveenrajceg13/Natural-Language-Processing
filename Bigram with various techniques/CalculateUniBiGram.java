import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CalculateUniBiGram {

	Map<String,Double> unigram;
	Map<String,Map<String,Double>> bigram;
	
	public <T extends Comparable<? super T>> CalculateUniBiGram calculateUniBigram(File f) throws FileNotFoundException
	{
		Scanner s=new Scanner(f);
		Map<String,Double> map=new HashMap<>();
		Map<String,Map<String,Double>> biMap=new HashMap<>();
		String previous=null;
		while(s.hasNext())
		{
			
			String key=s.next();
			Double value=map.get(key);
			if(map.get(key) != null)
			   {
				  map.put(key,map.get(key)+1);
			   }
		    else
			   {
				map.put(key, (double)1);
			   }
			if(previous==null)
			{
				Map<String,Double> mapTemp=new HashMap<>();
				mapTemp.put(key, (double)0);
				biMap.put(key, mapTemp);
			}
			else
			{
				if(biMap.get(previous)!=null)
				{
					Map<String,Double> mapTemp=biMap.get(previous);
					if(mapTemp.get(key)!=null)
					{
						double value1=(double) mapTemp.get(key);
						mapTemp.put(key,value1+1);
						biMap.put(previous, mapTemp);
					}
					else
					{
						mapTemp.put(key,(double)1);
						biMap.put(previous, mapTemp);
					}
				}
				else
				{
					Map<String,Double> mapTemp=new HashMap<>();
					mapTemp.put(key,(double)1);
					biMap.put(previous, mapTemp);
				}
			}
			previous=key;
		}
		
		this.unigram=map;
		this.bigram=biMap;
		return this;
	}
}
