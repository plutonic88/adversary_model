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
		
		
		
		/****************************************EXPERIMENTS**********************/
		
		
		/**
		 * plan
		 * 
		 * Can we differentiate the users so that defender can respond better depending on the type of user he is facing?
		 * 
		 * How we prove that defender has benefits if he can determine user type?
		 * 
		 * How the defender will determine type? How does the defender respond to different type? how does the game model work?
		 * 
		 * 
		 * 
		 * 
		 * How can we prove that there are different user groups based on their rationality and behavior?
		 * 
		 * If there are different degree of rationality or preference, can we connect that with personality type?
		 * 
		 * 
		 * How can we connect personality type with attacker behavior?
		 * 
		 * 
		 * 
		 * 
		 * 
		 * Short goal: Determining if we can differentiate user groups based on their behavior. 
		 * Can we connect personality type with this?
		 * 
		 * Find out the rationality of the whole group in game 1,2,3,4,5,6
		 * 
		 * Then try to find user groups based on data or personality type and then  measure their 
		 * rationality(lambda) or preference(omega) using QR, SUQRE
		 * 
		 */
		
		
		int k = 2;//Integer.parseInt(args[0]);
		
		int def_order = 1;//Integer.parseInt(args[0]); // def order 0 strategic last. def order 1 strategic def first
		
		int personlaity = 0;//Integer.parseInt(args[1]); // 0 mac, 1 narc, 2 psyc, 3 medium
		
		int depthlimit = 10;//Integer.parseInt(args[1]);
		
		int pointslevel = 0;//Integer.parseInt(args[1]);
		
		int gametype = 1;//Integer.parseInt(args[1]); // 0 noinfo 1 fullinfo
		
		// 0 bhv 1 DT
		
		
		
		int featureset = 0;//Integer.parseInt(args[1]); 
		
		AdversaryModel.suqrw4 = Integer.parseInt(args[0]);
		
		
		///***************** SUQR********************///
		
		//TODO
		/**
		 * 1. compute SUQR for different personalities
		 * 2. compute SUQR for different point levels
		 * 3. compute SUQR for behavior
		 * 4. compute SUQR for all users
		 */
		
		
		
		int gameins0 = 1; // 1,2,3,4,5,6
		
		
		AdversaryModelExps.computeOmegaSUQR(k, depthlimit, def_order, gameins0, gametype);
		
		//trending, modify it for SUQR
		//AdversaryModelExps.computeLambdaForAdaptivenessQR(k, def_order, depthlimit, featureset); 
		//tracking
		//AdversaryModelExps.trackUsersPerformanceSUQR(k, def_order, depthlimit, featureset);
		
		
		////*********************//////
		
		
		//may be irrational players decrease in size
		// some players does not adapt
		//server 1 2
		//user trends
		//AdversaryModelExps.computeLambdaForAdaptivenessQR(k, def_order, depthlimit, featureset, gametype); 
		
		//check which data is used first to cluster before runnig the method
		//AdversaryModelExps.trackUsersPerformanceQR(k, def_order, depthlimit, featureset, gametype);
		
		// whether users switched or stayed
		//AdversaryModelExps.trackIndivUsersSwitchingQR(k, def_order, depthlimit, featureset);
		
		
		
		//TO DO score based analysis
		/**
		 * high mac, high psyc, high narc
		 * see if it's a better fit than fitting the whole group TODO
		 * 
		 * find users based on scores...then for every game find lambda or freq and points....and llval 
		 * 
		 * 
		 * Then do clustering analysis to see if there are different levels of pesonlaity scores...
		 */
		//AdversaryModelExps.trackDarkTriadPerformanceQR(k, def_order, depthlimit, featureset, personlaity, gametype);
		
		
		// do testing for partial info game
		
		//AdversaryModelExps.userPointsTrending(k, def_order, depthlimit, featureset, pointslevel, gametype); 
		
		
		
		
		
		
		//AdversaryModelExps.generateApprximateNEPlay();
		
		/**
		 * Also do def_order 1
		 * 
		 * and personality score clustering
		 */
		
		
		
		//TODO tracking users
		
		
		
		
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
		 * show agent model with estimated parameter VS actual data difference in a chart for attack node frequency
		 * 
		 * loglikelihood value should also show that
		 */
		
		
		
		/**
		 * Work in subjective QR
		 */
		
		
				
		
		
		
		
		
		
	}
	
	
	
	

}
