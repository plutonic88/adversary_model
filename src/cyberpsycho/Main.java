package cyberpsycho;

public class Main {
	
	
	public static void main(String[] args) throws Exception 
	{
		//AdversaryModelExps.doDummyTesting();
		
		//AdversaryModelExps.doDummyTest2();
		
		AdversaryModel.scoremap.put(1, 5);
		AdversaryModel.scoremap.put(2, 4);
		AdversaryModel.scoremap.put(3, 3);
		AdversaryModel.scoremap.put(4, 2);
		AdversaryModel.scoremap.put(5, 1);
		
		
		//AdversaryModelExps.generateOneStageGameData();
		
		//AdversaryModelExps.getLambdaOneStageGame();
		
		//AdversaryModelExps.getLambdaOneStageFlipIt();
		
		//AdversaryModelExps.computeLambdaExps();
		
		
		//AdversaryModelExps.computeLambdaQR();
		//AdversaryModelExps.generateApprximateNEPlay();
		
		
		//AdversaryModelExps.errorInQRLambdaEst();
		//AdversaryModelExps.generatePTPlay();
		
		
		
		/**
		 * do testing
		 * 1. strategy computation
		 * 2. frequency multiplication
		 * 3. how do i propagate reward upwards? do i use the weighted functions?
		 * 4. add noise to the used parameters and copmute ptval 305
		 */
		//AdversaryModelExps.fitPT();
		
		
		
		/**
		 * Work in subjective QR
		 */
		
		AdversaryModelExps.computeOmegaSUQR();
		//AdversaryModelExps.generateApprximateNEPlay();
				
		
		
		
		
		
		
	}
	
	
	
	

}
