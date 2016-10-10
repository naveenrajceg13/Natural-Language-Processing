import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Naveenraj
 * 
 * Class computes bigram using no smoothing, Adverse Smoothing and Good tuning
 */
public class BiGram {
	/**
	 * @param args
	 * Driver function to call other function
	 */
	public static void main(String args[])
	{
		CalculateUniBiGram biGram=new CalculateUniBiGram();
		BiGram ngram=new BiGram();
		String filename=null;
		String inputString=null;
		if(args.length>=1)
		{
			inputString=args[0];                       // Input value of the text
			File f=new File(inputString);
			Scanner s=null;
			try {
				s = new Scanner(f);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			inputString="";
			while(s.hasNext())
			{
				inputString+=s.next();
				
				if(s.hasNext())
					inputString+=" ";
			}
		}
		else
		{
			inputString="The president has relinquished his control of the company's board.";
		}
		if(args.length>1)
		{
			filename=args[1];
		}
		else
		{
			filename="input.txt";                   // Input corpus 
		}
		
		try {
			biGram=biGram.calculateUniBigram(new File(filename));        //Calculate Bigram for words in corpus
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ArrayList<String> inputWords=ngram.calculateGivenWords(inputString);     //Get input words and put in a ArrayList
		Map<String,Map<String,Double>> bigramForinput=ngram.balanceOut(inputWords, biGram.bigram);  //balance the bigram in such a way that match not found are 0
		int N=ngram.calculateN(biGram.unigram);   //Total number of words in corpus 
		int V=biGram.unigram.size();  //Number of distinct words in corpus
		Map<String,Map<String,Double>> bigramWithOutSmoothing=ngram.withOutSmoothing(bigramForinput, biGram.unigram,inputWords);   //Bigram without Smoothing
		Map<String,Map<String,Double>> bigramForAddone=ngram.bigramTableforAddoneSmoothing(bigramForinput, N, V);
		Map<String,Map<String,Double>> bigramAddOneSmoothing=ngram.withaddOneSmoothing(bigramForAddone, biGram.unigram, inputWords, V); // Bigram with add one smoothing
		Map<String,Map<String,Double>> bigramForGoodTurning=ngram.goodTurningBigramInput(bigramForinput, biGram.unigram, inputWords); //Bigram good turning 
		Map<String,Map<String,Double>> goodTurning=ngram.goodTurning(bigramForGoodTurning, N);
		
		System.out.println("Bigram Count Table for No Smoothing");
		ngram.print(bigramForinput);   
		System.out.println("Bigram Count Table for Add One Smoothing");
		ngram.print(bigramForAddone);     
		System.out.println("Bigram Count Table for Good Turning");
		ngram.print(bigramForGoodTurning);              
		
		System.out.println("Bigram Probability Table for No Smoothing");
		ngram.print(bigramWithOutSmoothing);   
		System.out.println("Bigram Probability Table for Add One Smoothing");
		ngram.print(bigramAddOneSmoothing);     
		System.out.println("Bigram Probability Table for Good Turning");
		ngram.print(goodTurning);   
		
		System.out.print("Total Probability Using No Smoothing : ");
		System.out.print(ngram.calculateProbability(inputString, bigramWithOutSmoothing)+" \n");  
		System.out.print("Total Probability Using Add one Smoothing :");
		System.out.print(ngram.calculateProbability(inputString, bigramAddOneSmoothing)+" \n");
		System.out.print("Total Probability Using Good Turning :");
		System.out.print(ngram.calculateProbability(inputString, goodTurning)+" \n");
		
		
	}
	
	
	public <T extends Comparable<? super T>> Map<String,Map<String,Double>> goodTurning(Map<String,Map<String,Double>> bigram,int N)
	{
		Map<String,Map<String,Double>> goodTurning=new HashMap<>();
		
		for(String x: bigram.keySet())
		{
			Map<String,Double> map=bigram.get(x);
			Map<String,Double> newMap=new HashMap<>();
			
			for(String xx: map.keySet())
			{
				double value= map.get(xx);
				value=((value)/(double)(N));                     //  P*= C*/N;
				newMap.put(xx, value);
			}
			
			goodTurning.put(x, newMap);
			
		}
			
		return goodTurning;

	}
	
	
	public <T extends Comparable<? super T>> Map<String,Map<String,Double>> bigramTableforAddoneSmoothing(Map<String,Map<String,Double>> bigram,int N,int V)
	{
		Map<String,Map<String,Double>> addOneBigram=new HashMap<>();
	
		for(String x: bigram.keySet())
		{
			Map<String,Double> map=bigram.get(x);
			Map<String,Double> newMap=new HashMap<>();
			
			for(String xx: map.keySet())
			{
				double value= map.get(xx);
				value=((value+1)*((double)(N)/(double)(N+V)));                       // C* = (C+1) / (N/N+v)  
				newMap.put(xx, value);
			}
			
			addOneBigram.put(x, newMap);
			
		}
			
		return addOneBigram;

	}
	
	/**
	 * procedure to calculate total probability of word|word 
	 * @param inputText
	 * @param bigram
	 * @return
	 */
	public <T extends Comparable<? super T>> double calculateProbability(String inputText,Map<String,Map<String,Double>> bigram)
	{
		String s[]=inputText.split(" ");
		String prev=null;
		double count=1;
		for(String text: s)
		{
			if(prev!=null)
			{
				Map<String,Double> tempMap=bigram.get(prev);    
				double value=tempMap.get(text);
				count=count*value;                                 //get the probability of each(word|word)
				prev=text;                             
			}
			else
			{
				prev=text;
			}
			
		}
		return count;
	}
	
	/**
	 * Procedure to calculate total words in corpus
	 * @param unigram
	 * @return
	 */
	public <T extends Comparable<?super T>> int calculateN(Map<String,Double> unigram)
	{
		int value=0;
		for(Double x: unigram.values())
		{
			value+=x;                       //count each word count
		}
		return value;
	}
	
	
	/**
	 * Procedure to compute bigram probability without smoothing
	 * @param bigram
	 * @param unigram
	 * @param givenInput
	 * @return
	 */
	public <T extends Comparable<? super T>> Map<String,Map<String,Double>> withOutSmoothing(Map<String,Map<String,Double>> bigram,Map<String,Double> unigram,ArrayList<String> givenInput)
	{
		Map<String,Map<String,Double>> mapForInput=new HashMap<>();
		for(String x: givenInput)
		{
			double value;
			if(unigram.get(x)!=null)
			   value=unigram.get(x);                           //get C(word)
			else
				value=0;
			Map<String,Double> m=bigram.get(x);
			Map<String,Double> newMap=new HashMap<>();
			for(String y : givenInput)
			{
				    if(value!=0)
					newMap.put(y, (((m.get(y)))/value));       // c(word|word1) / count (word1)
				    else
				    newMap.put(y, (double)0);
			}
			mapForInput.put(x, newMap);
		}

		return mapForInput;
	}
	
	/**
	 * Procedure for add one smoothing 
	 * @param bigram
	 * @param unigram
	 * @param givenList
	 * @param V
	 * @return
	 */
	public <T extends Comparable<? super T>> Map<String,Map<String,Double>> withaddOneSmoothing(Map<String,Map<String,Double>> bigram,Map<String,Double> unigram,ArrayList<String> givenList,double V)
	{
		Map<String,Map<String,Double>> hm= new HashMap<>();
		
		for(String x: givenList)
		{
			double value;
			if(unigram.get(x)!=null)
			value=unigram.get(x);
			else
			value=0;
			Map<String,Double> m=bigram.get(x);
			Map<String,Double> map=new HashMap<>();
			for(String y : givenList)
			{
				if(value!=0)
				map.put(y, (m.get(y)+1)/((value)+V));              // (count(word|word1)+1 )/(count(word1)+number of distinct word in corpus) 
				else
				map.put(y, 1/V);                    
			}
			hm.put(x, map);
			
		}
		
		return hm;
	}
	
	public <T extends Comparable<? super T>> Map<String,Map<String,Double>> balanceOut(ArrayList<String> givenInput,Map<String,Map<String,Double>> bigram)
	{
		Map<String,Map<String,Double>> mapForInput=new HashMap<>();
		for(String x: givenInput)
		{
			Map<String,Double> m=bigram.get(x);   // take a The row
			Map<String,Double> newMap=new HashMap<>();
			for(String y: givenInput)
			{
				if(m!=null)                      // if THe row is not thee just put 0 for all
				{
				   if(m.get(y)!=null)           // Check with The president, The is , THe lal
				   {
					newMap.put(y,m.get(y));
				   }
				   else
				   {
					newMap.put(y,(double)0);    // if previously word not encountered put 0
				   }
				}
				else
				{
					newMap.put(y,(double)0);    // if THe row itself not there put 0
				}
			}
			
			mapForInput.put(x, newMap);
			
		}
		return mapForInput;	
	}
	
	/**
	 * Procedure to calculate goodTurning
	 * @param bigram
	 * @param unigram
	 * @param givenWords
	 * @param n
	 * @return
	 */
	public <T extends Comparable<? super T>> Map<String,Map<String,Double>> goodTurningBigramInput(Map<String,Map<String,Double>> bigram,Map<String,Double> unigram,ArrayList<String> givenWords)
	{
		
		Map<Double,Double> countMap=countGram(bigram);

		Map<Double,Double> resultMap=new HashMap<>();
		Map<String,Map<String,Double>> tempigram=new HashMap<>(bigram);
		for(Double c0: countMap.keySet())
		{
			double sum1=c0+1;
			double N0=countMap.get(c0);
			double N1;
			if(countMap.get(c0+1)!=null)
				N1=countMap.get(c0+1);                            // c* = ( c*+1 (n*+1/n*) )
			else
				N1=0;
			double cStar=sum1*(N1/N0);
			resultMap.put(c0,cStar);                           //  Replace all N with C*
		}
		
	   	return replaceProbability(tempigram, resultMap);
	}
	
	/**
	 * Procedure to replace all N* with P*
	 * @param bigram
	 * @param replaceMap
	 * @return
	 */
	public <T extends Comparable<? super T>> Map<String,Map<String,Double>> replaceProbability(Map<String,Map<String,Double>> bigram,Map<Double,Double> replaceMap)
	{
		Map<String,Map<String,Double>> resultMap=new HashMap<>();
		for(String s: bigram.keySet())
		{
			
			Map<String,Double> tempMap=bigram.get(s);
			Map<String,Double> newMap=new HashMap<>();
			for(String ss: tempMap.keySet())
			{
				newMap.put(ss, replaceMap.get(tempMap.get(ss)));                   // get N* and replace with P*
			}
			
			resultMap.put(s, newMap);
		}
		
		return resultMap;
	}
	
	/**
	 * Count of each value in bigram, how many times it present 
	 * @param bigram
	 * @return
	 */
	public <T extends Comparable<? super T>> Map<Double,Double> countGram(Map<String,Map<String,Double>> bigram)
	{
		Map<Double,Double> result=new HashMap<>();
		
		for(String s: bigram.keySet())
		{
			Map<String,Double> tempMap=bigram.get(s);
			for(String ss:bigram.keySet())
			{
				double value=tempMap.get(ss);
				if(result.get(value)!=null)
				{
					result.put(value, result.get(value)+1);               //count of each number in bigram table 
				}
				else
				{
					result.put(value, (double)1);
				}
			}
		}

		return result;
	}
	
	public <T extends Comparable<? super T>> void print(Map bigram)
	{
		System.out.println();
		int maxlenth=0;
		for(Object x: bigram.keySet())
		{
			Map<String,Double> m=(Map) bigram.get(x);
			for(Object y : m.keySet())
			{
				int length=y.toString().length();
				
				if(length>maxlenth)
				{
					maxlenth=length;
				}
			}
			break;
		}
		for(Object x: bigram.keySet())
		{
			System.out.print("\t     ");
			Map<String,Double> m=(Map) bigram.get(x);
			for(Object y : m.keySet())
			{
				int length=y.toString().length();
				
				if(length>maxlenth)
				{
					maxlenth=length;
				}
				String toPrint=y.toString();
				for(int i=y.toString().length();i<maxlenth;i++)
				{
					toPrint+=" ";
				}
				System.out.print(toPrint+"\t");
			}
			break;
		}
		System.out.println();
		for(Object x: bigram.keySet())
		{
			String toPrint=x.toString();
			for(int i=x.toString().length();i<maxlenth;i++)
			{
				toPrint+=" ";
			}
			System.out.print(toPrint+" ");
			Map<String,Double> m=(Map) bigram.get(x);
			for(Object y : m.keySet())
			{
				toPrint=m.get(y).toString();
				for(int i=m.get(y).toString().length();i<maxlenth;i++)
				{
					toPrint+=" ";
				}
				System.out.print(toPrint+"\t");
			}
			
			System.out.println();	
		}
		
	}
	
	public <T extends Comparable<? super T>> ArrayList<String> calculateGivenWords(String givenWords)
	{
		ArrayList<String> givenwords=new ArrayList<>();
		String[] s=givenWords.split(" ");
		for(String ss: s)
		{
			givenwords.add(ss);
		}
		return givenwords;
	}
	
}
