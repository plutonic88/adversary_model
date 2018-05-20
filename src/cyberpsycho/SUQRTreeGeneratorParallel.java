package cyberpsycho;




import java.util.HashMap;
import java.util.Random;

public class SUQRTreeGeneratorParallel implements Runnable{



	Random rand = new Random();


	public Thread t;
	public String threadName;
	public int depth_LIMIT;
	public int treenodecount;
	int naction = 6;
	
	public int suqrmodel;
	
	public double[][] omega;
	
	public double minllh;
	public double[] optimumomega = new double[4];
	
	public HashMap<String, HashMap<String, Double>> defstrategy = new HashMap<String, HashMap<String, Double>>();
	public HashMap<String, int[]> attackfrequency = new HashMap<String, int[]>();
	
	double lambda;
	
	public double llval;
	







	public SUQRTreeGeneratorParallel() {
		super();
	}

	public SUQRTreeGeneratorParallel(String threadName, int depth_lIMIT, int suqrmodel, double [][] omega, HashMap<String, 
			HashMap<String, Double>> defstrategy, HashMap<String, int[]> attackfrequency, double lambda) {
		super();
		//this.t = t;
		this.threadName = threadName;
		depth_LIMIT = depth_lIMIT;
		synchronized(this){
			treenodecount=0;
		}
		this.suqrmodel = suqrmodel;
		
		//synchronized(this){

			this.omega = new double [omega.length][omega[0].length];

			this.minllh = Double.POSITIVE_INFINITY;

			for(int i=0; i<omega.length; i++)
			{
				for(int j=0; j<omega[i].length; j++)
				{
					this.omega[i][j] = omega[i][j];
					//System.out.println("thrd "+ this.threadName + ", w1 "+ this.omega[i][j] + ", w2 "+ w[1] + ", w3 "+ w[2] + ", w4 "+ w[3] );
				}
			}
			
			for(double w[] : this.omega)
			{
				System.out.println("Thread "+this.threadName+",  w1 : "+w[0]+", w2 : "+ w[1]+ " w3 : "+w[2] + ", w4 : "+ w[3]);
			}
			
			
		//}
		
		this.defstrategy = defstrategy;
		this.attackfrequency = attackfrequency;
		this.lambda = lambda;
		
		
		
	}

	@Override
	public void run() 
	{

		for(double[] w: this.omega)
		{

			//double omega[] = w;
			this.llval = 0.0;
			HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
			try {
				DNode root1 = buildGameTreeRecurSUQR(this.depth_LIMIT, naction, defstrategy, attstrategy, lambda, attackfrequency, w);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			double llh = -this.llval;//computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);

			//double llh = -likeHoodValue(isets, attackfrequency, naction, defstrategy, root, dEPTH_LIMIT, depthinfoset, lambda[i]);

			
			//System.out.println("Thread "+this.threadName+", llh "+llh +", min w1 : "+w[0]+", w2 : "+ w[1]+ " w3 : "+w[2] + ", w4 : "+ w[3]);
			
			if(llh<this.minllh)
			{
				this.minllh = llh;
				this.optimumomega = w;

				System.out.println("Thread "+this.threadName+", minllh "+minllh +", min w1 : "+w[0]+", w2 : "+ w[1]+ " w3 : "+w[2] + ", w4 : "+ w[3]);
			}

		}


	}

	public void start () 
	{
		System.out.println("Starting " +  threadName );
		if (t == null) 
		{
			t = new Thread (this, threadName);
			t.start ();
		}
	}

	public  DNode buildGameTreeRecurSUQR(int DEPTH_LIMIT, int naction, HashMap<String,HashMap<String,Double>> defstrategy,
			HashMap<String, double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency, double[] omega) throws Exception 
	{

		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		DNode root = createGameTreeRecurSUQR(DEPTH_LIMIT, naction, noderewards, defstrategy, attstrategy, lambda, attackfrequency, omega);
		//System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		//System.out.println();
		//printTree(root, naction);


		return root;
	}
	
	
	private  DNode createGameTreeRecurSUQR(int DEPTH_LIMIT, int naction, HashMap<Integer,
			Integer[]> noderewards, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String, double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency, double[] omega) throws Exception 
	{

		DNode root = new DNode(0, 0, 0);
		
		synchronized(this){
		
		treenodecount = 0;
		treenodecount++;
		}
		
		genTreeRecurSUQR(0, naction, DEPTH_LIMIT, root, noderewards, "", defstrategy, attstrategy, lambda, attackfrequency, omega);
		return root;

	}
	
	
	private double[][] genTreeRecurSUQR(int depth, int naction, int DEPTH_LIMIT, DNode node, 
			HashMap<Integer,Integer[]> noderewards, String seq, HashMap<String,HashMap<String,Double>> defstrategy, 
			HashMap<String,double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency, double[] omega) throws Exception 
	{

		if(depth==DEPTH_LIMIT)
		{
			//System.out.println("leaf Node " + node.nodeid + ", seq "+ seq);
			int defreward = 99999;//computeDefenderReward(node, noderewards);

			int[] utility = computeAttackerUtilities(seq, noderewards);

			int reward = 20+utility[0];
			int cost = utility[1];
			//System.out.println();

			node.attacker_reward = reward;
			node.attacker_penalty = cost;

			node.defender_reward = defreward;
			node.leaf = true;

			double[][] rd = new double[2][naction];
			rd[0][node.prevaction] = node.attacker_reward;
			rd[1][node.prevaction] = node.attacker_penalty;
			//System.out.println("Leafndoe, returning attacker rewards ");
			/*for(int i=0; i<2; i++)
			{
				for(int j=0; j<naction; j++)
				{
					System.out.print(rd[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println("\n");*/
			return rd;
		}



		if(node.player==1) // attacker
		{
			//System.out.println("player 1 node, returning all reward from the childs(defnodes) ");
			double[][] rwrds = new double[2][naction];
			//double[] costs = new double[naction];
			for(int action = 0; action<naction; action++)
			{
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				synchronized(this){
					treenodecount++;
				}
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[][] tmprwrd = genTreeRecurSUQR(depth+1, naction, DEPTH_LIMIT, child, noderewards, 
						tmpseq, defstrategy, attstrategy, lambda, attackfrequency, omega);
				rwrds[0][action] = tmprwrd[0][action];
				rwrds[1][action] = tmprwrd[1][action];

			}
			/*for(int i=0; i<2; i++)
			{
				for(int j=0; j<naction; j++)
				{
					System.out.print(rwrds[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println("\n");*/
			return rwrds;
		}
		else if(node.player==0) // defender
		{

			//System.out.println("player 0 node, received rewards from attacker nodes ");

			HashMap<Integer, double[]> rewrdsmap = new HashMap<Integer, double[]>();
			HashMap<Integer, double[]> penaltysmap = new HashMap<Integer, double[]>();
			for(int action = 0; action<naction; action++)
			{
				//System.out.println("def action "+ action);
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				synchronized(this){
					treenodecount++;
				}
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[][] tmprwrd = genTreeRecurSUQR(depth+1, naction, DEPTH_LIMIT, 
						child, noderewards, tmpseq, defstrategy, attstrategy, lambda, attackfrequency, omega);
				/*for(int i=0; i<naction; i++)
				{
					System.out.println("attaction "+i+" : "+tmprwrd[i]);
				}
				System.out.println("\n");*/
				rewrdsmap.put(action, tmprwrd[0]); // these are the rewards from attacker
				penaltysmap.put(action, tmprwrd[1]);
			}
			/**
			 *  1. compute attacker Q-BR
			 *  2. Need sequence
			 *  3. defender strategy
			 */




			String key = getDefAttckrSeq(seq);



			/**
			 * attacksuccess: #times attacked and successfully captured it
			 * 
			 * value range: 0-1
			 * 
			 * compute attack success rate (0-1) then multiply with 10
			 * 
			 */


			//TODO
			/**
			 * attackfailure: #times attacked and failed
			 * 
			 * value range: 0-1
			 * 
			 * compute attack failre rate (0-1) then multiply with boost
			 * 
			 */

			/**
			 * % ofimmediate  points received from a node
			 * value range : 0-1
			 * boost 
			 */

			int boost = 1;
			double[] suqrpref = new double[naction];
			if(this.suqrmodel==0)
			{
				//int sboost = 5;
				suqrpref = computeAttackSuccess(seq, naction, noderewards, boost); 
			}
			else if(this.suqrmodel==1)
			{
				//int fboost = 5;
				suqrpref = computeAttackFailure(seq, naction, noderewards, boost); 
			}
			else if(this.suqrmodel==2)
			{
				//int ipboost = 5;
				suqrpref = computeImmediatePointPercentage(seq, naction, noderewards, boost); 
			}
			else if(this.suqrmodel==3)
			{
				//int tpboost = 5;
				suqrpref = computeTotalPointPercentage(seq, naction, noderewards, boost); 
			}
			else if(this.suqrmodel==4)
			{
				//int ppboost = 5;
				suqrpref = computeTotalPenaltyPercentage(seq, naction, noderewards, boost); 
			}








			double[] defstrat = buildDefStrat(key, defstrategy, naction);
			/*System.out.println("defender strategy : ");
			for(int i=0; i<naction; i++)
			{
				System.out.println("defaction "+i+" : "+defstrat[i]);
			}*/
			/**
			 * now compute Q-BR
			 * 
			 * 1. for every action of attacker
			 * 		for every action of defender
			 * 			compute exp
			 */
			
			double[] recentattstrat = null;
			synchronized(this){
			
				recentattstrat = computeAttackerSUQBR(key, defstrat, naction, lambda, rewrdsmap, penaltysmap, omega, suqrpref);

			}
			attstrategy.put(key, recentattstrat);


			/**
			 * compute the loglikelihoodvalue for attacker strategy 
			 * 1. first get the sequence key 
			 */

			double llvalsum = computeLLV(attackfrequency, recentattstrat, key, naction);




			this.llval += llvalsum;
			//System.out.println("llval : "+ (-SUQRTreeGeneratorParallel.llval));

			/**
			 * now compute create an empty array and return the expected payoff of attacker for the prev action
			 */

			double[][] attrerdprevation = computeReturnRewardSUQR(defstrat, naction, rewrdsmap, recentattstrat, node, penaltysmap);





			/*System.out.println("Non Leafndoe, returning attacker reward for node******************** ");
			for(int i=0; i<2; i++)
			{
				for(int j=0; j<naction; j++)
				{
					System.out.print(attrerdprevation[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println("\n");*/

			return attrerdprevation;




			// return expected payoff of attacker if attacker plays the action that leads to this node

		}



		return null;

	}
	
	
	public static HashMap<Integer, Integer[]> createNodeRewards(int naction) {


		HashMap<Integer, Integer[]> values = new HashMap<Integer, Integer[]>();

		Integer [] v = {10, 8};
		values.put(0, v);

		Integer[] v1 = {10, 2};
		values.put(1, v1);

		Integer [] v2 = {4, 2};
		values.put(2, v2);

		Integer[] v3 = {4, 8};
		values.put(3, v3);


		Integer [] v4 = {10, 5};
		values.put(4, v4);

		Integer[] v5 = {0, 0};
		values.put(5, v5);





		return values;
	}
	
	
	private double[][] computeReturnRewardSUQR(double[] defstrat, int naction, HashMap<Integer, double[]> rewrdsmap, double[] recentattstrat, DNode node, HashMap<Integer,double[]> penaltysmap) {


		double[][] attrerdprevation = new double[2][naction];

		double expreward = 0.0;
		double exppenalty = 0.0;

		for(int defaction = 0; defaction<naction; defaction++)
		{
			// use rewards map to get the payoffs of attacker
			double[] rwrd = rewrdsmap.get(defaction);
			double[] pnlty = penaltysmap.get(defaction);

			double defprob = defstrat[defaction];


			double attexpreward = 0;
			double attexppenalty = 0;

			for(int attaction=0; attaction<naction; attaction++)
			{
				double tmp = rwrd[attaction]*recentattstrat[attaction];
				double tmppenlty = pnlty[attaction]*recentattstrat[attaction];

				attexpreward += tmp;
				attexppenalty+= tmppenlty;
			}
			expreward += (defprob*attexpreward);
			exppenalty +=(defprob*attexppenalty);

		}

		//System.out.println("att action  "+node.prevaction+" : "+exppayoff);

		if(node.prevaction >=0 )
		{
			attrerdprevation[0][node.prevaction] = expreward;
			attrerdprevation[1][node.prevaction] = exppenalty;
		}
		else
		{
			attrerdprevation[0][0] = expreward;
			attrerdprevation[1][0] = exppenalty;
		}


		return attrerdprevation;

	}
	
	
	private double computeLLV(HashMap<String, int[]> attackfrequency, double[] recentattstrat, String key, int naction) {

		double llvalsum = 0;


		//System.out.println("seq : "+ key + "\n");
		if(attackfrequency.containsKey(key))
		{
			int[] freq = attackfrequency.get(key);
			//double[] attstrtgy = attackstrategy.get(seq);
			for(int a=0; a<naction; a++)
			{
				if(freq[a]>0 && recentattstrat[a]>0)
				{
					double tmpllval = freq[a]* Math.log(recentattstrat[a]);
					//System.out.println("llval : "+ tmpllval);
					llvalsum += tmpllval;
				}

			}
			//System.out.println("llvalsum : "+ llvalsum);
		}
		else
		{
			/*System.out.println("DOes not have the sequence");
			//throw new Exception("DOes not have the sequence");
			int[] freq = attackfrequency.get(key);
			double[] attstrtgy = {0, 0, 0, 0, 0, 1};


			double tmpllval = freq[0]* Math.log(attstrtgy[0]);
			//System.out.println("llval : "+ tmpllval);
			llvalsum += tmpllval;
			 */


			//System.out.println("llvalsum : "+ llvalsum);
		}

		return llvalsum;
	}
	
	
	private double[] computeAttackerSUQBR(String key, double[] defstrat, int naction, double lambda, HashMap<Integer, 
			double[]> rewrdsmap, HashMap<Integer,double[]> penaltysmap, double[] omega, double[] attacksuccess) throws Exception {


		HashMap<Integer, Double> attsu = new HashMap<Integer, Double>();


		double exponnentsum = 0.0;

		for(int attaction = 0; attaction<naction; attaction++)
		{
			double sum = 0.0;
			for(int defaction = 0; defaction<naction; defaction++)
			{
				double atttmprwrd[] = rewrdsmap.get(defaction); // get the rewards of attacker for dfenders  action
				double atttmppnlty[] = penaltysmap.get(defaction);
				// now get the reward for attacker;s action
				double attrwd = atttmprwrd[attaction];
				double attpnlty = atttmppnlty[attaction];



				/**
				 * TODO
				 * use a linear combination of features
				 */

				double linearcombutiltiy = omega[0]*defstrat[defaction] + omega[1]*attrwd + omega[2]*attpnlty + omega[3]*attacksuccess[attaction]; 
				sum += linearcombutiltiy;

				//sum += (attrwd* defstrat[defaction]);

			}
			attsu.put(attaction, sum);
			exponnentsum += Math.exp(lambda*sum);
		}

		double [] recentattstrat = new double[naction];


		double sm = 0.0;
		
		//System.out.println(" exponnentsum "+ exponnentsum);

		//System.out.println("atatcker strategy: ");
		for(int attaction = 0; attaction<naction; attaction++)
		{
			double prob = Math.exp(lambda*attsu.get(attaction))/exponnentsum; 
			recentattstrat[attaction] = prob;
			sm += prob;
			
			//System.out.println("attsu.get(attaction) "+ attsu.get(attaction));

			//System.out.println("attaction "+attaction+" : "+recentattstrat[attaction] + ", lambda "+ lambda);

		}
		
		if(sm==0 || sm<(1-0.001))
		{
			System.out.println("problem in attaacker strategy, sum(prob) != 1 sm = "+sm);
			System.out.println("playing default strategy pass for attacker");
			for(int attaction = 0; attaction<naction; attaction++)
			{
				double prob = Math.exp(lambda*attsu.get(attaction))/exponnentsum; 
				recentattstrat[attaction] = prob;
				System.out.println("attsu.get(attaction) "+ attsu.get(attaction));
				System.out.println("attaction "+attaction+" : "+recentattstrat[attaction] + ", lambda "+ lambda);
				System.out.println("w0 "+omega[0] +", w1 "+ omega[1]+" , w2 " + omega[2]+" w3  " + omega[3] );

			}
			
			recentattstrat = new double[naction];
			recentattstrat[5] = 1;
		}

		if(sm<(1-0.001))
		{

			System.out.println("problem in attaacker strategy, sum(prob) != 1 sm = "+sm);
			System.out.println(" exponnentsum "+ exponnentsum);
			for(int attaction = 0; attaction<naction; attaction++)
			{
				double prob = Math.exp(lambda*attsu.get(attaction))/exponnentsum; 
				recentattstrat[attaction] = prob;
				System.out.println("attsu.get(attaction) "+ attsu.get(attaction));
				System.out.println("attaction "+attaction+" : "+recentattstrat[attaction] + ", lambda "+ lambda);
				System.out.println("w0 "+omega[0] +", w1 "+ omega[1]+" , w2 " + omega[2]+" w3  " + omega[3] );

			}
			
			//throw new Exception("problem with prob sum");
		}

		return recentattstrat;

	}
	
	
	private double[] buildDefStrat(String key, HashMap<String, HashMap<String, Double>> defstrategy, int naction) {


		double defstrat[] = new double[naction]; 


		defstrat[0] = 1.0;




		if(defstrategy.containsKey(key))
		{

			HashMap<String,Double> defstrt = defstrategy.get(key);
			for(int i=0; i<naction; i++)
			{
				if(defstrt.containsKey(i+""))
				{
					defstrat[i] = defstrt.get(i+"");
				}
				else
				{
					defstrat[i] = 0.0;
				}
			}
		}
		else
		{
			//System.out.println("No strategy exist, using default");
		}


		return defstrat;
	}
	
	
	private static double[] computeTotalPenaltyPercentage(String seq, int naction, HashMap<Integer, Integer[]> noderewards, int boost) {


		double perc[] = new double[naction];

		double[] attacksrewards= new double[naction];
		double[] attackpenalties = new double[naction];




		//seq = "0,1,1,0,5,3,1,3,5,0";

		int[] controllers = new int[noderewards.size()];

		//int attpoints = 0;

		int attrwrd = 0;
		int sumimmediaterwrd = 0;
		int attckpenlty = 0;

		int defpoints = 0;

		String[] splittedseq = seq.split(",");

		/*//System.out.print("");

	for(int i= 0; i<seq.size(); i++)
	{
		System.out.print(seq.get(i) + ", ");
	}
		 */
		//System.out.println();
		for(int i= 0; i<(splittedseq.length/2); i++)
		{

			int defaction = Integer.parseInt(splittedseq[2*i]);
			int attaction = Integer.parseInt(splittedseq[2*i+1]);
			//attacksrewards[attaction]++; // increase counter when attacker attacked it


			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action

			//attpoints -= attcost;
			attckpenlty += (-attcost);
			attackpenalties[attaction] += (-attcost);


			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;

				attrwrd += attreward;
				sumimmediaterwrd += attreward;

				controllers[attaction] = 1;
				controllers[defaction] = 0;

				attacksrewards[attaction] += attreward;


			}
			else if((defaction == attaction) && (controllers[attaction]==1)) // when def==att
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;
				attrwrd += attreward;

				attacksrewards[attaction] += attreward;

				sumimmediaterwrd += attreward;




			}
			else
			{
				// failed attack for attackaction
				//failcount[attaction]++;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((j != attaction) && (controllers[j]==1))
				{
					int attreward = noderewards.get(j)[0];
					//attpoints += attreward;
					attrwrd += attreward;
					attacksrewards[j] += attreward;


				}
			}
		}
		//System.out.print( attpoints+", ");


		for(int a=0; a<naction; a++)
		{

			if(attacksrewards[a] != 0)
			{
				perc[a] = boost*(attackpenalties[a]/attckpenlty);
			}
			//System.out.println("a: "+ a + ", attackcuont "+ attackcount[a] + ", successcount "+ successcount[a] + ", success "+ success[a]);
		}

		return perc;
	}
	
	
	private double[] computeTotalPointPercentage(String seq, int naction, HashMap<Integer, Integer[]> noderewards, int boost) {


		double perc[] = new double[naction];

		double[] attacksrewards= new double[naction];
		double[] attackpenalties = new double[naction];




		//seq = "0,1,1,0,5,3,1,3,5,0";

		int[] controllers = new int[noderewards.size()];

		//int attpoints = 0;

		int attrwrd = 0;
		int sumimmediaterwrd = 0;
		int attckpenlty = 0;

		int defpoints = 0;

		String[] splittedseq = seq.split(",");

		/*//System.out.print("");

	for(int i= 0; i<seq.size(); i++)
	{
		System.out.print(seq.get(i) + ", ");
	}
		 */
		//System.out.println();
		for(int i= 0; i<(splittedseq.length/2); i++)
		{

			int defaction = Integer.parseInt(splittedseq[2*i]);
			int attaction = Integer.parseInt(splittedseq[2*i+1]);
			//attacksrewards[attaction]++; // increase counter when attacker attacked it


			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action

			//attpoints -= attcost;
			attckpenlty += (-attcost);
			attackpenalties[attaction] += (-attcost);


			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;

				attrwrd += attreward;
				sumimmediaterwrd += attreward;

				controllers[attaction] = 1;
				controllers[defaction] = 0;

				attacksrewards[attaction] += attreward;


			}
			else if((defaction == attaction) && (controllers[attaction]==1)) // when def==att
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;
				attrwrd += attreward;

				attacksrewards[attaction] += attreward;

				sumimmediaterwrd += attreward;




			}
			else
			{
				// failed attack for attackaction
				//failcount[attaction]++;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((j != attaction) && (controllers[j]==1))
				{
					int attreward = noderewards.get(j)[0];
					//attpoints += attreward;
					attrwrd += attreward;
					attacksrewards[j] += attreward;


				}
			}
		}
		//System.out.print( attpoints+", ");


		for(int a=0; a<naction; a++)
		{

			if(attacksrewards[a] != 0)
			{
				perc[a] = boost*(attacksrewards[a]/attrwrd);
			}
			//System.out.println("a: "+ a + ", attackcuont "+ attackcount[a] + ", successcount "+ successcount[a] + ", success "+ success[a]);
		}

		return perc;
	}
	
	
	
	private double[] computeImmediatePointPercentage(String seq, int naction, HashMap<Integer, Integer[]> noderewards, int boost) {


		double perc[] = new double[naction];

		double[] attacksrewards= new double[naction];
		double[] attackpenalties = new double[naction];




		//seq = "0,1,1,0,5,3,1,3,5,0";

		int[] controllers = new int[noderewards.size()];

		//int attpoints = 0;

		int attrwrd = 0;
		int sumimmediaterwrd = 0;
		int attckpenlty = 0;

		int defpoints = 0;

		String[] splittedseq = seq.split(",");

		/*//System.out.print("");

	for(int i= 0; i<seq.size(); i++)
	{
		System.out.print(seq.get(i) + ", ");
	}
		 */
		//System.out.println();
		for(int i= 0; i<(splittedseq.length/2); i++)
		{

			int defaction = Integer.parseInt(splittedseq[2*i]);
			int attaction = Integer.parseInt(splittedseq[2*i+1]);
			//attacksrewards[attaction]++; // increase counter when attacker attacked it


			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action

			//attpoints -= attcost;
			attckpenlty += (-attcost);
			attackpenalties[attaction] += (-attcost);


			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;

				attrwrd += attreward;
				sumimmediaterwrd += attreward;

				controllers[attaction] = 1;
				controllers[defaction] = 0;

				attacksrewards[attaction] += attreward;


			}
			else if((defaction == attaction) && (controllers[attaction]==1)) // when def==att
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;
				attrwrd += attreward;

				attacksrewards[attaction] += attreward;

				sumimmediaterwrd += attreward;




			}
			else
			{
				// failed attack for attackaction
				//failcount[attaction]++;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((j != attaction) && (controllers[j]==1))
				{
					int attreward = noderewards.get(j)[0];
					//attpoints += attreward;
					attrwrd += attreward;
					//attacksrewards[j] += attreward;


				}
			}
		}
		//System.out.print( attpoints+", ");


		for(int a=0; a<naction; a++)
		{

			if(attacksrewards[a] != 0)
			{
				perc[a] = boost*(attacksrewards[a]/sumimmediaterwrd);
			}
			//System.out.println("a: "+ a + ", attackcuont "+ attackcount[a] + ", successcount "+ successcount[a] + ", success "+ success[a]);
		}

		return perc;
	}
	
	
	private double[] computeAttackFailure(String seq, int naction, HashMap<Integer, Integer[]> noderewards, int boost) {


		double[] failure = new double[naction];

		double[] attackcount= new double[naction];
		double[] successcount = new double[naction];
		double[] failcount = new double[naction];



		//seq = "0,1,1,0,5,3,1,3,5,0";

		int[] controllers = new int[noderewards.size()];

		//int attpoints = 0;

		int attrwrd = 0;
		int attckpenlty = 0;

		int defpoints = 0;

		String[] splittedseq = seq.split(",");

		/*//System.out.print("");

		for(int i= 0; i<seq.size(); i++)
		{
			System.out.print(seq.get(i) + ", ");
		}
		 */
		//System.out.println();
		for(int i= 0; i<(splittedseq.length/2); i++)
		{

			int defaction = Integer.parseInt(splittedseq[2*i]);
			int attaction = Integer.parseInt(splittedseq[2*i+1]);
			attackcount[attaction]++; // increase counter when attacker attacked it


			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action

			//attpoints -= attcost;
			attckpenlty += (-attcost);


			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;

				attrwrd += attreward;

				controllers[attaction] = 1;
				controllers[defaction] = 0;

				successcount[attaction]++; // successful attack


			}
			else if((defaction == attaction) && (controllers[attaction]==1)) // when def==att
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;
				attrwrd += attreward;

				successcount[attaction]++;


			}
			else
			{
				// failed attack for attackaction
				failcount[attaction]++;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((j != attaction) && (controllers[j]==1))
				{
					int attreward = noderewards.get(j)[0];
					//attpoints += attreward;
					attrwrd += attreward;
				}
			}
		}
		//System.out.print( attpoints+", ");


		for(int a=0; a<naction; a++)
		{

			if(attackcount[a] != 0)
			{
				failure[a] = boost*(failcount[a]/attackcount[a]);
			}
			//System.out.println("a: "+ a + ", attackcuont "+ attackcount[a] + ", successcount "+ successcount[a] + ", success "+ success[a]);
		}

		return failure;
	}
	
	
	public  String getDefAttckrSeq(String seq) {


		if(seq.equals(""))
		{
			return "EMPTY EMPTY";
		}
		else
		{
			String[] seqarr = seq.split(",");
			String p0seq = "";
			String p1seq = "";

			for(int i=0; i<seqarr.length; i++)
			{
				if(i%2 == 0)
				{
					if(!p0seq.equals(""))
					{
						p0seq += "," + seqarr[i];
					}
					else
					{
						p0seq = seqarr[i];
					}
				}
				else
				{
					if(!p1seq.equals(""))
					{
						p1seq += "," + seqarr[i];
					}
					else
					{
						p1seq = seqarr[i];
					}
				}
			}

			String key = p0seq + " " + p1seq;
			return key;
		}

	}
	
	
	public double[] computeAttackSuccess(String seq, int naction, HashMap<Integer, Integer[]> noderewards, int boost) {


		double[] success = new double[naction];
		double[] attackcount= new double[naction];
		double[] successcount = new double[naction];
		double[] failcount = new double[naction];



		//seq = "0,1,1,0,5,3,1,3,5,0";

		int[] controllers = new int[noderewards.size()];

		//int attpoints = 0;

		int attrwrd = 0;
		int attckpenlty = 0;

		int defpoints = 0;

		String[] splittedseq = seq.split(",");

		/*//System.out.print("");

		for(int i= 0; i<seq.size(); i++)
		{
			System.out.print(seq.get(i) + ", ");
		}
		 */
		//System.out.println();
		for(int i= 0; i<(splittedseq.length/2); i++)
		{

			int defaction = Integer.parseInt(splittedseq[2*i]);
			int attaction = Integer.parseInt(splittedseq[2*i+1]);
			attackcount[attaction]++; // increase counter when attacker attacked it


			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action

			//attpoints -= attcost;
			attckpenlty += (-attcost);


			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;

				attrwrd += attreward;

				controllers[attaction] = 1;
				controllers[defaction] = 0;

				successcount[attaction]++; // successful attack


			}
			else if((defaction == attaction) && (controllers[attaction]==1)) // when def==att
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;
				attrwrd += attreward;

				successcount[attaction]++;


			}
			else
			{
				// failed attack for attackaction
				failcount[attaction]++;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((j != attaction) && (controllers[j]==1))
				{
					int attreward = noderewards.get(j)[0];
					//attpoints += attreward;
					attrwrd += attreward;
				}
			}
		}
		//System.out.print( attpoints+", ");


		for(int a=0; a<naction; a++)
		{

			if(attackcount[a] != 0)
			{
				success[a] = boost*(successcount[a]/attackcount[a]);
			}
			//System.out.println("a: "+ a + ", attackcuont "+ attackcount[a] + ", successcount "+ successcount[a] + ", success "+ success[a]);
		}

		return success;
	}
	
	public int[] computeAttackerUtilities(String seq, HashMap<Integer, Integer[]> noderewards) {


		int u[] = new int[2];

		//seq = "0,1,1,0,5,3,1,3,5,0";

		int[] controllers = new int[noderewards.size()];

		//int attpoints = 0;

		int attrwrd = 0;
		int attckpenlty = 0;

		int defpoints = 0;

		String[] splittedseq = seq.split(",");

		/*//System.out.print("");

		for(int i= 0; i<seq.size(); i++)
		{
			System.out.print(seq.get(i) + ", ");
		}
		 */
		//System.out.println();
		for(int i= 0; i<(splittedseq.length/2); i++)
		{

			int defaction = Integer.parseInt(splittedseq[2*i]);
			int attaction = Integer.parseInt(splittedseq[2*i+1]);


			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action

			//attpoints -= attcost;
			attckpenlty += (-attcost);


			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;

				attrwrd += attreward;

				controllers[attaction] = 1;
				controllers[defaction] = 0;
			}
			else if((defaction == attaction) && (controllers[attaction]==1)) // when def==att
			{
				int attreward = noderewards.get(attaction)[0];

				//attpoints += attreward;
				attrwrd += attreward;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((j != attaction) && (controllers[j]==1))
				{
					int attreward = noderewards.get(j)[0];
					//attpoints += attreward;
					attrwrd += attreward;
				}
			}
		}
		//System.out.print( attpoints+", ");

		u[0] = attrwrd;
		u[1] = attckpenlty;

		return u;
	}





}

