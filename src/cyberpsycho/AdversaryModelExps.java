package cyberpsycho;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.sun.tools.javac.code.Attribute.Array;

//import cs.Interval.contraction.SecurityGameContraction;
//import cs.Interval.contraction.TargetNode;
import cyberpsycho.Data.Headers_minimum;
/*import games.EmpiricalMatrixGame;
import games.MatrixGame;
import games.MixedStrategy;
import games.OutcomeDistribution;
import games.OutcomeIterator;*/
//import groupingtargets.ClusterTargets;
import kmeans.KmeanClustering;
import kmeans.Weka;
/*import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;*/
/*import solvers.QRESolver;
import solvers.SolverUtils;
import subnet.MonteCarloParallel;*/


public class AdversaryModelExps {

	public static Random rand = new Random(5);
	public static int defbr = 0;


	public static void doDummyTesting()
	{
		// read the data we got from MTurk
		ArrayList<ArrayList<String>> data =  Data.readData();


		int gameinstance0 = 4;
		int gameinstance1 = 1;

		// keep the users who played all 6 games
		// that means they have 
		ArrayList<String> users_refined = refineUser(data, -1, 1);
		ArrayList<ArrayList<String>>  data_refined = refineData(data,1,users_refined, gameinstance0, gameinstance1);
		System.out.println("Total number of users "+ users_refined.size());


		// list the game play of the users

		HashMap<String, int[][]> att_game_play = buildGamePlay(users_refined, data_refined, 1);
		HashMap<String, int[][]> def_game_play = buildGamePlay(users_refined, data_refined, 0);
		HashMap<String, int[][]> reward = buildGameRewards(users_refined, data_refined);

		printGamePlay(att_game_play);



		AdversaryModel.computeLambda(users_refined, att_game_play, def_game_play,reward, data_refined, 4);





	}

	private static void printGamePlay(HashMap<String, int[][]> game_play) {


		System.out.println("Game play : ");
		for(String user: game_play.keySet())
		{
			System.out.println("user " + user);
			int[][] play = game_play.get(user);

			for(int ins =0; ins<6; ins++)
			{
				for(int r=0; r<5; r++)
				{
					System.out.print(play[ins][r] + " ");
				}
				System.out.println();
			}
		}

	}

	private static void printOneStageGamePlay(HashMap<String, Integer> game_play) 
	{


		System.out.println("Game play : ");
		for(String user: game_play.keySet())
		{
			System.out.println("user " + user);
			int play = game_play.get(user);


			System.out.print(play + " ");

		}

	}

	private static HashMap<String, int[][]> buildGamePlay(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined, int i) {


		HashMap<String, int[][]> gameplay = new HashMap<String, int[][]>();

		// for every user

		System.out.println("Building game play");
		for(String user_id: users_refined)
		{
			System.out.println("\nuser "+ user_id);
			//int gameinstance = 1;
			//int round = 0;
			int[][] tmpgameplay = new int[6][5];
			for(ArrayList<String> example: data_refined)
			{
				// get user id
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				// if example is for user_id
				if(user_id.equals(tmpuser))
				{

					if(user_id.equals("\"$2y$10$1eppnuF14Ls9jlRDjIPIOuX4Dg3v.KS6CP.6nl0NZCZuKAnS7Kosm\""))
					{
						//System.out.print("h");
						int g=1;
					}

					int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));


					int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
					String attackeraction = example.get(Headers_minimum.attacker_action.getValue());

					if(i==0)
					{
						attackeraction = example.get(Headers_minimum.defender_action.getValue());
					}

					System.out.print(attackeraction+" ");

					if(round==4)
					{
						System.out.println();
					}

					if(attackeraction.equals(" ") || attackeraction.equals(""))
					{
						attackeraction = "5";
					}

					int action = Integer.parseInt(attackeraction);
					tmpgameplay[gameinstance-1][round-1] = (action);

				}

			}
			gameplay.put(user_id, tmpgameplay);

		}
		return gameplay;
	}


	private static HashMap<String, Integer> buildOneStageGamePlay(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined, int i) {


		HashMap<String, Integer> gameplay = new HashMap<String, Integer>();

		// for every user

		//System.out.println("Building game play");
		for(String user_id: users_refined)
		{
			//System.out.println("\nuser "+ user_id);
			//int gameinstance = 1;
			//int round = 0;
			int tmpgameplay = -1;
			for(ArrayList<String> example: data_refined)
			{
				// get user id
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				// if example is for user_id
				if(user_id.equals(tmpuser))
				{

					/*if(user_id.equals("\"$2y$10$1eppnuF14Ls9jlRDjIPIOuX4Dg3v.KS6CP.6nl0NZCZuKAnS7Kosm\""))
					{
						//System.out.print("h");
						int g=1;
					}*/




					int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));

					if(round==1)
					{
						//int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));

						String attackeraction = example.get(Headers_minimum.attacker_action.getValue());

						if(i==0)
						{
							attackeraction = example.get(Headers_minimum.defender_action.getValue());
						}

						//System.out.print(attackeraction+" ");



						if(attackeraction.equals(" ") || attackeraction.equals(""))
						{
							attackeraction = "5";
						}

						int action = Integer.parseInt(attackeraction);
						tmpgameplay = (action);
					}



				}

			}
			gameplay.put(user_id, tmpgameplay);

		}
		return gameplay;
	}


	private static HashMap<String, int[][]> buildGameRewards(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined) {


		HashMap<String, int[][]> reward = new HashMap<String, int[][]>();

		// for every user

		System.out.println("Building game play");
		for(String user_id: users_refined)
		{
			System.out.println("\nuser "+ user_id);
			//int gameinstance = 1;
			//int round = 0;
			int[][] tmpgameplay = new int[6][5];
			for(ArrayList<String> example: data_refined)
			{
				// get user id
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				// if example is for user_id
				if(user_id.equals(tmpuser))
				{

					if(user_id.equals("\"$2y$10$Zss70qaplxmdn5QIxlkM4Oh/4GtT2f0BmWg9ISHZ1OxRvikqrMCOC\""))
					{
						//System.out.print("h");
					}

					int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));


					int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
					String attackeraction = example.get(Headers_minimum.attacker_points.getValue());
					System.out.print(attackeraction+" ");

					if(round==4)
					{
						System.out.println();
					}

					if(attackeraction.equals(" ") || attackeraction.equals(""))
					{
						attackeraction = "5";
					}

					int action = Integer.parseInt(attackeraction);
					tmpgameplay[gameinstance-1][round-1] = (action);

				}

			}
			reward.put(user_id, tmpgameplay);

		}
		return reward;
	}



	private static ArrayList<ArrayList<String>> refineData(ArrayList<ArrayList<String>> data, int game_type, ArrayList<String> users_refined, int gameinstance0, int gameinstance1) {


		ArrayList<ArrayList<String>> examples = new ArrayList<ArrayList<String>>();


		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
			int gametype = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;

			/*boolean isuser = examples.contains(tmpuser);
			if(users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type)
			{

			}*/


			String def_order = example.get(Headers_minimum.pick_def_order.getValue());
			int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
			//int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
			/*String action = example.get(Headers_minimum.attacker_action.getValue());
			int attackaction = 0;
			if(!action.equals(" "))
			{
				attackaction = Integer.parseInt(action);
			}*/

			/**
			 * we can use data from different game instances gameinstance== 4,5,6 or 1,2,3
			 */
			if(def_order.equals("0") && (gameinstance==gameinstance0) && users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type) // asc, take 4th game instance to 6th
			{

				examples.add(example);
			}
			else if(def_order.equals("1") && (gameinstance==gameinstance1) && users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type) // desc, take 1st game instance to 3rd
			{
				examples.add(example);
			}




		}


		return examples;
	}


	private static ArrayList<ArrayList<String>> refineDataAdaptive(ArrayList<ArrayList<String>> data, int game_type,
			ArrayList<String> users_refined, int gameinstance0, int deforder) {


		ArrayList<ArrayList<String>> examples = new ArrayList<ArrayList<String>>();


		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
			int gametype = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;

			/*boolean isuser = examples.contains(tmpuser);
			if(users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type)
			{

			}*/


			int def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue()));
			int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
			//int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
			/*String action = example.get(Headers_minimum.attacker_action.getValue());
			int attackaction = 0;
			if(!action.equals(" "))
			{
				attackaction = Integer.parseInt(action);
			}*/

			/**
			 * we can use data from different game instances gameinstance== 4,5,6 or 1,2,3
			 */
			if(def_order == deforder && (gameinstance==gameinstance0) && users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type) // asc, take 4th game instance to 6th
			{

				examples.add(example);
			}




		}


		return examples;
	}
	
	
	private static ArrayList<ArrayList<String>> refineDataAdaptiveRangeWOOrder(ArrayList<ArrayList<String>> data, int game_type,
			ArrayList<String> users_refined, String alg) {

		
		System.out.println("************Entering  refineDataAdaptiveRangeWOOrder");

		ArrayList<ArrayList<String>> examples = new ArrayList<ArrayList<String>>();

		
		

		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
			int gametype = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;
			int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
			
			int def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue()));

			/*boolean isuser = examples.contains(tmpuser);
			if(users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type)
			{

			}*/

			if(tmpuser.equals("\"$2y$10$/kXfM1T.mNbwDiQxSjzFdOILFqbymZ41aFbKzrC6dn00dnLWZ8Asa\""))
			{
				System.out.println("user: "+tmpuser+" | attackaction null");
			}
			

			//int def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue()));
			//int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
			int round =  Integer.parseInt(example.get(Headers_minimum.round.getValue()));
			
			
			
			String action = example.get(Headers_minimum.attacker_action.getValue());
			String defaction = example.get(Headers_minimum.defender_action.getValue());
			int attackaction = 5;
			int defenderaction = 0;
			
			
			//System.out.println("attackaction "+ action);
			
			
			if(!action.equals(" ") && !action.equals(""))
			{
				attackaction = Integer.parseInt(action);
			}
			else
			{
				System.out.println("user: "+tmpuser+" | attackaction null");
				System.out.println("gameinstance "+ gameinstance);
				System.out.println("gameplayed "+ gameplayed);
				System.out.println("round  "+ round);
				
			}
			
			if(!defaction.equals(" ") && !defaction.equals(""))
			{
				defenderaction = Integer.parseInt(defaction);
			}
			else
			{
				System.out.println("user: "+tmpuser+" | def action null");
				System.out.println("gameinstance "+ gameinstance);
				System.out.println("gameplayed "+ gameplayed);
				System.out.println("round  "+ round);
			}
			
			
			
			
			
			
			
			int fgi = -1;
			int lgi = -1;
			
			if(alg.equals("r") && def_order==0)
			{
				fgi= 1;
				lgi=3;
			}
			else if(alg.equals("r") && def_order==1)
			{
				fgi= 4;
				lgi=6;
			}
			else if(alg.equals("s") && def_order==0)
			{
				fgi= 4;
				lgi=6;
			}
			else if(alg.equals("s") && def_order==1)
			{
				fgi= 1;
				lgi=3;
			}
			
			

			/**
			 * we can use data from different game instances gameinstance== 4,5,6 or 1,2,3
			 */
			if(users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type && (gameinstance >= fgi && gameinstance <= lgi)) // asc, take 4th game instance to 6th
			{

				examples.add(example);
			}




		}


		
		System.out.println("************Exiting  refineDataAdaptiveRangeWOOrder");
		
		return examples;
	}
	
	
	private static ArrayList<ArrayList<String>> refineDataAdaptiveRange(ArrayList<ArrayList<String>> data, int game_type,
			ArrayList<String> users_refined, int fgi,  int lgi, int deforder) {


		ArrayList<ArrayList<String>> examples = new ArrayList<ArrayList<String>>();


		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
			int gametype = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;

			/*boolean isuser = examples.contains(tmpuser);
			if(users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type)
			{

			}*/


			int def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue()));
			int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
			//int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
			/*String action = example.get(Headers_minimum.attacker_action.getValue());
			int attackaction = 0;
			if(!action.equals(" "))
			{
				attackaction = Integer.parseInt(action);
			}*/

			/**
			 * we can use data from different game instances gameinstance== 4,5,6 or 1,2,3
			 */
			if(def_order == deforder && (gameinstance>=fgi && gameinstance<=lgi) && users_refined.contains(tmpuser) && gameplayed == 6 && gametype == game_type) // asc, take 4th game instance to 6th
			{

				examples.add(example);
			}




		}


		return examples;
	}
	
	

	private static ArrayList<String> refineUser(ArrayList<ArrayList<String>> data, int def_order, int gametype) {


		ArrayList<String> users = new ArrayList<String>();


		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());

			//if(!tmpuser.equals("\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"") && !tmpuser.equals("\"$2y$10$MVaZ8l6MqBDdc9ODF844puqPXFePcZ3ECDt4mx37nrvb9nHrBThDm\""))
			{


				int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
				int tmp_def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue())) ;
				int game_type = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;
				if(!users.contains(tmpuser) && gameplayed == 6 && game_type==gametype)
				{
					if(def_order>=0 && def_order == tmp_def_order)
					{
						users.add(tmpuser);
						System.out.println("Adding user "+ tmpuser + " , played "+ gameplayed + " games, deforder "+ tmp_def_order);
					}
					else
					{
						users.add(tmpuser);
						System.out.println("Adding user "+ tmpuser + " , played "+ gameplayed + " games, deforder "+ tmp_def_order);
					}
				}
			}

			/*if(users.size()==10)
				break;*/

		}


		return users;
	}


	private static ArrayList<String> refineUserAdaptive(ArrayList<ArrayList<String>> data, int def_order, int gametype) {


		ArrayList<String> users = new ArrayList<String>();


		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());

			//if(!tmpuser.equals("\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"") && !tmpuser.equals("\"$2y$10$MVaZ8l6MqBDdc9ODF844puqPXFePcZ3ECDt4mx37nrvb9nHrBThDm\""))
			{


				int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
				int tmp_def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue())) ;
				int game_type = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;
				if(!users.contains(tmpuser) && gameplayed == 6 && game_type==gametype)
				{
					if(def_order==tmp_def_order)
					{
						users.add(tmpuser);
						//System.out.println("Adding user "+ tmpuser + " , played "+ gameplayed + " games, deforder "+ tmp_def_order);
					}
					/*else
				{
					users.add(tmpuser);
					System.out.println("Adding user "+ tmpuser + " , played "+ gameplayed + " games, deforder "+ tmp_def_order);
				}*/
				}
			}

			/*if(users.size()==10)
				break;*/

		}


		return users;
	}
	
	
	private static ArrayList<String> refineUserAdaptiveCombined(ArrayList<ArrayList<String>> data, int def_order, int gametype) {


		ArrayList<String> users = new ArrayList<String>();


		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());

			//if(!tmpuser.equals("\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"") && !tmpuser.equals("\"$2y$10$MVaZ8l6MqBDdc9ODF844puqPXFePcZ3ECDt4mx37nrvb9nHrBThDm\""))
			{


				int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
				int tmp_def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue())) ;
				int game_type = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;
				if(!users.contains(tmpuser) && gameplayed == 6 && game_type==gametype)
				{
					if(def_order==tmp_def_order)
					{
						users.add(tmpuser);
						//System.out.println("Adding user "+ tmpuser + " , played "+ gameplayed + " games, deforder "+ tmp_def_order);
					}
					/*else
				{
					users.add(tmpuser);
					System.out.println("Adding user "+ tmpuser + " , played "+ gameplayed + " games, deforder "+ tmp_def_order);
				}*/
				}
			}

			/*if(users.size()==10)
				break;*/

		}


		return users;
	}
	
	
	
	private static ArrayList<String> refineUserAdaptiveWOOrder(ArrayList<ArrayList<String>> data, int gametype) {


		ArrayList<String> users = new ArrayList<String>();


		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());

			//if(!tmpuser.equals("\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"") && !tmpuser.equals("\"$2y$10$MVaZ8l6MqBDdc9ODF844puqPXFePcZ3ECDt4mx37nrvb9nHrBThDm\""))
			{


				int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
				int tmp_def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue())) ;
				int game_type = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;
				if(!users.contains(tmpuser) && gameplayed == 6 && game_type==gametype)
				{
					//if(def_order==tmp_def_order)
					
						users.add(tmpuser);
						//System.out.println("Adding user "+ tmpuser + " , played "+ gameplayed + " games, deforder "+ tmp_def_order);
					
					/*else
				{
					users.add(tmpuser);
					System.out.println("Adding user "+ tmpuser + " , played "+ gameplayed + " games, deforder "+ tmp_def_order);
				}*/
				}
			}

			/*if(users.size()==10)
				break;*/

		}


		return users;
	}


	/**
	 * use a 5x5 game
	 * 
	 * compute mixed strategy for defender
	 * 
	 * use defender's stratgey. generate attacker strategy given defenders strategy using AQRE
	 * @throws Exception 
	 */
	/*public static void doDummyTest2() throws Exception {


		int nrow = 1;

		int ncol = 5;

		int dmax = 20;



		int ITER = 1;

		int als[] = {2}; //DO + weka + CON target per cluster
		//radius


		int ranges[][] = {{0,2},{3,8},{9, 10}};
		int[] percforranges = {80, 10, 10};

		int ranges[][] = {{0,7},{6,8},{8, 10}};
		int[] percforranges = {90, 0, 10};



		int blockdim = 2; // block = blockdim x blockdim

		// nrow has to be divisible by block

		int nTargets = nrow*ncol;

		int ncat = 3;
		int[] targetsincat = getTargetsInCats(nTargets, percforranges);
		double[][] density=SecurityGameContraction.generateRandomDensityV2(ncat, ITER, ranges, nTargets, targetsincat);


		HashMap<Integer, ArrayList<TargetNode>> alltargets = new HashMap<Integer, ArrayList<TargetNode>>();
		HashMap<Integer, HashMap<Integer, TargetNode>> alltargetmaps = new HashMap<Integer, HashMap<Integer, TargetNode>>();
		//HashMap<Integer, ArrayList<Integer>[]> allclus = new HashMap<Integer, ArrayList<Integer>[]>();
		HashMap<Integer, ArrayList<Integer>[]> allclus = new HashMap<Integer, ArrayList<Integer>[]>();
		//double[][] density=SecurityGameContraction.generateRandomDensity( perc, ITER, lstart, lend,  hstart, hend, nTargets, false);

		//double[][] density = new double[ITER][nTargets];



		for(int iter = 0; iter<ITER; iter++)
		{
			ArrayList<TargetNode> targets = new ArrayList<TargetNode>();  //createGraph();
			HashMap<Integer, TargetNode> targetmaps = new HashMap<Integer, TargetNode>();
			ClusterTargets.buildcsvGraphExp(nrow,ncol,density,targets, iter );
			//SecurityGameContraction.assignRandomDensityZeroSum(density, gamedata, targets, iter);
			//SecurityGameContraction.buildGraph(nrow, ncol, gamedata, targets);
			//SecurityGameContraction.assignRandomDensityZeroSum(density, gamedata, targets, iter);
			alltargets.put(iter, targets);
			for(TargetNode t : targets)
			{
				targetmaps.put(t.getTargetid(), t);

			}
			alltargetmaps.put(iter, targetmaps);

			ClusterTargets.buildFile(nrow,ncol,density,targets, iter );

			int g=0;



		}


	}
*/
	private static int[] getTargetsInCats(int nTargets, int[] percforcats) {


		int x[] = new int[percforcats.length];


		int sum = 0;

		for(int i=0; i<percforcats.length; i++)
		{
			x[i] = (int)Math.floor(nTargets*(percforcats[i]/100.00));
			sum += x[i];
		}




		if(sum<nTargets)
		{

			int max = Integer.MIN_VALUE;
			int index= 0;
			for(int i=0; i<percforcats.length; i++)
			{
				if(max>percforcats[i])
				{
					max = percforcats[i];
					index = i;
				}
			}

			x[index] += (nTargets-sum);

		}



		return x;
	}


	public static int randInt(int min, int max) {

		// Usually this should be a field rather than a method variable so
		// that it is not re-seeded every call.


		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	/*public static void getLambdaOneStageGame() throws MatlabConnectionException, MatlabInvocationException
	{
		int[] actions = {3,3};
		MatrixGame gm = new MatrixGame(2,actions);
		OutcomeIterator itr = gm.iterator();
		while(itr.hasNext())
		{
			int[] outcome = itr.next();
			double payoff = randInt(5, 10);
			gm.setPayoff(outcome, 0, payoff);
			System.out.println("outcome :"+ outcome[0] + ", "+outcome[1]+ " : 0 "+payoff);
			payoff = randInt(5, 10);
			gm.setPayoff(outcome, 1, payoff);
			System.out.println("outcome :"+ outcome[0] + ", "+outcome[1]+ " : 1 "+payoff);

		}

		MixedStrategy[] gamestrategy = new MixedStrategy[2];

		QRESolver qre = new QRESolver(100);
		EmpiricalMatrixGame emg = new EmpiricalMatrixGame(gm);
		qre.setDecisionMode(QRESolver.DecisionMode.RAW);
		for(int i=0; i< gm.getNumPlayers(); i++ )
		{
			gamestrategy[i] = qre.solveGame(emg, i);
		}

		System.out.println("s");

		//gamestrategy = RegretLearner.solveGame(gm);



		// parse data

		double[] lambdas = {0.08,.08, .1, .5, 1, 2};
		for(double lambda: lambdas)
		{

			ArrayList<ArrayList<String>> data = Data.readData(lambda);

			HashMap<Integer, Integer> ni = computeNi(data);

			HashMap<Integer, Double> ui = computeUi(data);

			int n=300;

			double A=0;
			for(int action=1; action <=3; action++)
			{
				A += (ui.get(action)*ni.get(action));
			}

			double B = A/n;

			double[] coeffs = new double[3];

			for(int i=0; i<3; i++)
			{
				coeffs[i] = B-ui.get(i+1);
			}



			//int x=0;

			double estimatedlambda = estimateLambda(ni, n, ui);

			System.out.println("Estimated lambda "+ estimatedlambda);


		}

	}*/

	/*private static double estimateLambda(HashMap<Integer, Integer> ni, int n, HashMap<Integer, Double> ui) throws MatlabConnectionException, MatlabInvocationException
	{

		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();

		//Set a variable, add to it, retrieve it, and print the result

		proxy.eval("syms lambda");

		proxy.setVariable("n",300);
		// set ni variables
		String n_i [] = new String[ni.size()];
		for(int i=1; i<=3; i++)
		{
			n_i[i-1] = "n"+i;
			proxy.setVariable(n_i[i-1], ni.get(i));

		}

		// set ui variables
		String u_i [] = new String[ui.size()];
		for(int i=1; i<=3; i++)
		{
			u_i[i-1] = "u"+i;
			proxy.setVariable(u_i[i-1], ui.get(i));

		}


		//Eq1 = 0 == (exp(lambda*U1) + exp(lambda*U2) + exp(lambda*U3)) * ((N1*U1)+(N2*U2)+(N3*U3)) - N*(exp(lambda*U1)*(U1) + exp(lambda*U2)*(U2) + exp(lambda*U2)*(U2));
		// build the equation string
		//Eq1 = 0 ==(exp( lambda *u1)+exp( lambda *u2)+exp( lambda *u3))*((n1*u1)+(n2*u2)+(n3*u3))-n*(exp( lambda *u1)*u1+exp( lambda *u2)*u2+exp( lambda *u3)*u3)


		String eqn = buildEqnString(ui, ni, u_i, n_i);

		System.out.println("\n"+eqn);

		proxy.eval(eqn);
		proxy.eval("symlambda = solve(Eq1, lambda)");

		proxy.eval("dlambda = double(symlambda)");

		double result = ((double[]) proxy.getVariable("dlambda"))[0];
		System.out.println("\n dlambda: " + result);

		//Disconnect the proxy from MATLAB
		proxy.disconnect();


		return 0.0;

	}*/

/*
	private static double estimateFlipItLambda(HashMap<Integer, Integer> ni, int n, HashMap<Integer, Double> ui) throws MatlabConnectionException, MatlabInvocationException
	{

		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();

		//Set a variable, add to it, retrieve it, and print the result

		proxy.eval("syms lambda");

		proxy.setVariable("n",n);
		// set ni variables
		String n_i [] = new String[ni.size()];
		for(int i=0; i<ni.size(); i++)
		{
			n_i[i] = "n"+i;
			proxy.setVariable(n_i[i], ni.get(i));
			System.out.println(n_i[i]+"="+ni.get(i) );

		}

		// set ui variables
		String u_i [] = new String[ui.size()];
		for(int i=0; i<ui.size(); i++)
		{
			u_i[i] = "u"+i;
			proxy.setVariable(u_i[i], ui.get(i));
			System.out.println(u_i[i]+"="+ui.get(i));

		}


		//Eq1 = 0 == (exp(lambda*U1) + exp(lambda*U2) + exp(lambda*U3)) * ((N1*U1)+(N2*U2)+(N3*U3)) - N*(exp(lambda*U1)*(U1) + exp(lambda*U2)*(U2) + exp(lambda*U2)*(U2));
		// build the equation string
		//Eq1 = 0 ==(exp( lambda *u1)+exp( lambda *u2)+exp( lambda *u3))*((n1*u1)+(n2*u2)+(n3*u3))-n*(exp( lambda *u1)*u1+exp( lambda *u2)*u2+exp( lambda *u3)*u3)


		String eqn = buildFlipItEqnString(ui, ni, u_i, n_i);

		System.out.println("\n"+eqn);

		proxy.eval(eqn);
		proxy.eval("symlambda = solve(Eq1, lambda)");

		proxy.eval("dlambda = double(symlambda)");

		double result = ((double[]) proxy.getVariable("dlambda"))[0];
		System.out.println("\nlambda: " + result);

		//Disconnect the proxy from MATLAB
		proxy.disconnect();


		return result;

	}**/

/*
	private static double estimateFlipItLambdaV2(HashMap<Integer, Integer> ni, int n, HashMap<Integer, Double> ui, MatlabProxy proxy) throws MatlabConnectionException, MatlabInvocationException
	{



		//Set a variable, add to it, retrieve it, and print the result

		proxy.eval("syms lambda");

		proxy.setVariable("n",n);
		// set ni variables
		String n_i [] = new String[ni.size()];
		for(int i=0; i<ni.size(); i++)
		{
			n_i[i] = "n"+i;
			proxy.setVariable(n_i[i], ni.get(i));

		}

		// set ui variables
		String u_i [] = new String[ui.size()];
		for(int i=0; i<ui.size(); i++)
		{
			u_i[i] = "u"+i;
			proxy.setVariable(u_i[i], ui.get(i));

		}


		//Eq1 = 0 == (exp(lambda*U1) + exp(lambda*U2) + exp(lambda*U3)) * ((N1*U1)+(N2*U2)+(N3*U3)) - N*(exp(lambda*U1)*(U1) + exp(lambda*U2)*(U2) + exp(lambda*U2)*(U2));
		// build the equation string
		//Eq1 = 0 ==(exp( lambda *u1)+exp( lambda *u2)+exp( lambda *u3))*((n1*u1)+(n2*u2)+(n3*u3))-n*(exp( lambda *u1)*u1+exp( lambda *u2)*u2+exp( lambda *u3)*u3)


		String eqn = buildFlipItEqnString(ui, ni, u_i, n_i);

		System.out.println("\n"+eqn);

		proxy.eval(eqn);
		proxy.eval("symlambda = solve(Eq1, lambda)");

		proxy.eval("dlambda = double(symlambda)");

		double result = ((double[]) proxy.getVariable("dlambda"))[0];
		System.out.println("\nlambda: " + result);




		return result;

	}*/

	private static String buildEqnString(HashMap<Integer,Double> ui, HashMap<Integer,Integer> ni, String[] u_i, String[] n_i) {

		String eqn = "Eq1 = 0 ==";

		// build first loop
		String e1 = "";
		for(int i=1; i<=ui.size(); i++)
		{
			e1 = e1 + "exp( lambda *"+u_i[i-1]+ ")";

			if(i != ui.size())
			{
				e1 = e1 + "+";
			}
		}

		e1 = "(" + e1 + ")";



		String e2 = "";
		for(int i=1; i<=ui.size(); i++)
		{
			e2 = e2 + "("+n_i[i-1]+"*"+ u_i[i-1]+")";

			if(i != ui.size())
			{
				e2 = e2 + "+";
			}
		}

		e2 = "(" + e2 + ")";


		eqn = eqn + e1 + "*" + e2;


		String e3 = "";
		for(int i=1; i<=ui.size(); i++)
		{
			e3 = e3 + "exp( lambda *"+u_i[i-1]+ ")*"+u_i[i-1];

			if(i != ui.size())
			{
				e3 = e3 + "+";
			}
		}

		e3 = "(" + e3 + ")";

		eqn = eqn + "-" + "n" + "*" + e3;

		return eqn;
	}


	private static String buildFlipItEqnString(HashMap<Integer,Double> ui, HashMap<Integer,Integer> ni, String[] u_i, String[] n_i) {

		String eqn = "Eq1 = 0 ==";

		// build first loop
		String e1 = "";
		for(int i=0; i<ui.size(); i++)
		{
			e1 = e1 + "exp( lambda *"+u_i[i]+ ")";

			if(i != ui.size()-1)
			{
				e1 = e1 + "+";
			}
		}

		e1 = "(" + e1 + ")";



		String e2 = "";
		for(int i=0; i<ui.size(); i++)
		{
			e2 = e2 + "("+n_i[i]+"*"+ u_i[i]+")";

			if(i != ui.size()-1)
			{
				e2 = e2 + "+";
			}
		}

		e2 = "(" + e2 + ")";


		eqn = eqn + e1 + "*" + e2;


		String e3 = "";
		for(int i=0; i<ui.size(); i++)
		{
			e3 = e3 + "exp( lambda *"+u_i[i]+ ")*"+u_i[i];

			if(i != ui.size()-1)
			{
				e3 = e3 + "+";
			}
		}

		e3 = "(" + e3 + ")";

		eqn = eqn + "-" + "n" + "*" + e3;

		return eqn;
	}


	private static HashMap<Integer, Integer> computeNi(ArrayList<ArrayList<String>> data) {


		HashMap<Integer, Integer> ni = new HashMap<Integer, Integer>();

		int[] count = new int[3];



		for(ArrayList<String> ex: data)
		{
			String action = ex.get(1);
			int a = Integer.parseInt(action);

			count[a-1]++;
		}
		for(int i=0; i<3; i++)
		{
			ni.put(i+1, count[i]);
		}

		return ni;

	}


	private static HashMap<Integer, Double> computeUi(ArrayList<ArrayList<String>> data) {


		HashMap<Integer, Double> ui = new HashMap<Integer, Double>();

		int[] count = {0,0,0};
		Double[] uis = {0.0,0.0,0.0};



		for(ArrayList<String> ex: data)
		{
			String action = ex.get(1);
			int a = Integer.parseInt(action);

			String us = ex.get(2);
			double u = Double.parseDouble(us);

			uis[a-1] += u;
			count[a-1]++;
		}




		for(int i=0; i<3; i++)
		{
			uis[i] /= count[i];

			ui.put(i+1, uis[i]);
		}

		return ui;

	}



	/*public static void generateOneStageGameData() {

		int[] actions = {3,3};
		MatrixGame gm = new MatrixGame(2,actions);
		OutcomeIterator itr = gm.iterator();
		while(itr.hasNext())
		{
			int[] outcome = itr.next();
			double payoff = randInt(5, 10);
			gm.setPayoff(outcome, 0, payoff);
			System.out.println("outcome :"+ outcome[0] + ", "+outcome[1]+ " : 0 "+payoff);
			payoff = randInt(5, 10);
			gm.setPayoff(outcome, 1, payoff);
			System.out.println("outcome :"+ outcome[0] + ", "+outcome[1]+ " : 1 "+payoff);

		}

		MixedStrategy[] gamestrategy = new MixedStrategy[2];

		QRESolver qre = new QRESolver(100);
		EmpiricalMatrixGame emg = new EmpiricalMatrixGame(gm);
		qre.setDecisionMode(QRESolver.DecisionMode.RAW);
		for(int i=0; i< gm.getNumPlayers(); i++ )
		{
			gamestrategy[i] = qre.solveGame(emg, i);
		}

		System.out.println("s");

		//gamestrategy = RegretLearner.solveGame(gm);

		System.out.println("h");


		int ITER = 300;


		double[] lambdas = {0.02,.08, .1, .5, 1, 2};
		for(double lambda: lambdas)
		{

			try 
			{

				// create a file using different lambda

				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("result/lambda"+lambda+".csv"),true));
				//PrintWriter pw = new PrintWriter(new FileOutputStream(new File("/Users/fake/Documents/workspace/IntervalSGAbstraction/"+"result.csv"),true));
				for(int i=0; i<ITER; i++)
				{

					// use defender strategy to play defender action
					int defaction = getDefenderMove(gm, gamestrategy[0]);
					//System.out.println("lambda "+lambda+", iter "+ i + ", defaction "+ defaction);
					// use defender strategy to find attacker action using AQRE for different lambda
					double attprobs[] = getAttackerProbs(gm,gamestrategy, lambda);
					int attaction = getAttackerMove(attprobs);
					if(attaction==-1)
					{
						int f=1;
					}
					int[] outcome = {defaction, attaction};
					double attpayoff = gm.getPayoff(outcome, 1);
					double defpayoff = gm.getPayoff(outcome, 0);
					for(int a=0; a<gamestrategy[1].getNumActions(); a++)
					{
						gamestrategy[1].setProb(a+1, attprobs[a]);
					}

					double[] exppayoffs = getExp(gamestrategy, gm);


					double exp = getExp(gamestrategy, gm, 1, attaction);


					System.out.println("lambda "+lambda+", iter "+ i + ", attaction "+ attaction + ", attpayoff "+ exp + ", defpayoff "+ defpayoff+", defexp "+ exppayoffs[0] + ", attexp "+ exppayoffs[1]);
					pw.append(i +","+attaction+ ","+exp +"\n");
				}
				pw.close();

			}
			catch(Exception e)
			{

			}


			//lambda += 1;
		}




	}
*/
	private static int getAttackerMove(double[] probs) {
		int action = -1;
		Random random = new Random();

		//double[] probs = mixedStrategy.getProbs();
		double probsum = 0.0;
		double r = random.nextDouble();
		for(int j=0; j<(probs.length); j++)
		{
			probsum += probs[j];
			if(r<probsum)
			{
				action = j;
				break;
			}
		}




		return action+1;
	}

	/*private static double[] getAttackerProbs(MatrixGame gm, MixedStrategy mixedStrategy[], double lambda) {

		//int attaction = -1;

		// find probability of making every move

		double probs[] = new double[gm.getNumActions(1)];

		for(int action=0; action<probs.length; action++)
		{
			probs[action] = getProbAQRE(action, gm, mixedStrategy, lambda);
		}

		return probs;




	}*/

	/*private static double getProbAQRE(int action, MatrixGame gm, MixedStrategy mixedStrategy[], double lambda) {


		double prob = 0.0;

		double[] logit = new double[gm.getNumActions(1)];





		double logitsum  = 0;

		for(int a=0; a<logit.length; a++)
		{
			double ux = getExp(mixedStrategy, gm, 1, a+1);

			//System.out.println("action "+ (a+1) + ", expected payoff "+ ux);

			logit[a] = Math.exp(ux*lambda);

			//System.out.println("action "+ (a+1) + ", logit  "+ logit[a]);
			logitsum += logit[a];

		}

		prob = logit[action]/logitsum;



		return prob;
	}*/

	/*private static double getExp(MixedStrategy[] mixedStrategy, MatrixGame gm, int player, int action) {


		List<MixedStrategy> strategylist = new ArrayList<MixedStrategy>();

		for(int i=0; i<mixedStrategy[1].getNumActions(); i++)
		{
			if((i+1) == action)
			{
				mixedStrategy[1].setProb(action, 1);
			}
			else
			{
				mixedStrategy[1].setProb(i+1, 0.0);
			}
		}


		for(int i=0; i<mixedStrategy.length; i++)
		{
			strategylist.add(mixedStrategy[i]);
		}
		OutcomeDistribution origdistribution = new OutcomeDistribution(strategylist);
		double[]  originalpayoff = SolverUtils.computeOutcomePayoffs(gm, origdistribution);

		return originalpayoff[player];

	}


	private static double[] getExp(MixedStrategy[] mixedStrategy, MatrixGame gm) {


		List<MixedStrategy> strategylist = new ArrayList<MixedStrategy>();



		for(int i=0; i<mixedStrategy.length; i++)
		{
			strategylist.add(mixedStrategy[i]);
		}
		OutcomeDistribution origdistribution = new OutcomeDistribution(strategylist);
		double[]  originalpayoff = SolverUtils.computeOutcomePayoffs(gm, origdistribution);

		return originalpayoff;

	}*/

	/*private static int getDefenderMove(MatrixGame gm, MixedStrategy mixedStrategy) 
	{


		int action = 0;
		Random random = new Random();

		double[] probs = mixedStrategy.getProbs();
		double probsum = 0.0;
		double r = random.nextDouble();
		for(int j=0; j<(probs.length-1); j++)
		{
			probsum += probs[j+1];
			if(r<probsum)
			{
				action = j+1;
				break;
			}
		}

		return action;

	}*/

	/*public static void getLambdaOneStageFlipIt() throws MatlabConnectionException, MatlabInvocationException 
	{



		// read the data we got from MTurk
		ArrayList<ArrayList<String>> data =  Data.readData();



		// keep the users who played all 6 games
		// that means they have 
		ArrayList<String> users_refined = refineUser(data, -1, 1);
		System.out.println("NUmber of users found "+ users_refined.size());


		*//**
		 * 1. high score
		 * 2. Low score
		 * 
		 * 3. Rank depending on personality
		 * 3a. remove those for whom corresponding type score is not the maximum
		 * 3b. Keep High variation
		 * 3c. Remove high variation
		 * 4. cluster depending on personality score
		 * 5. cluster depending on frequency
		 * 6. cluster depending on play in each round
		 *//*

		int personality = 	-1;
		int user_refine_type = 1;
		int gameinstance0 = 4;
		int gameinstance1 = 1;



		ArrayList<String> users_refined_type = refineUsers(users_refined, data, user_refine_type, personality);
		//ArrayList<String> users_lowscore = refineUsers(users_refined, data, 0, personality);


		ArrayList<ArrayList<String>>  data_refined = refineData(data,1,users_refined_type, gameinstance0, gameinstance1);
		System.out.println("Total number of high score users "+ users_refined_type.size());


		// list the game play of the users

		//users_refined = users_refined_type;

		// keep 50

		int remove = 0;

		int keep = users_refined.size() - remove;

		// keep 0 to 50

		int keepstart = 121;
		int keepend = 154;

		users_refined.clear();

		double sumscore = 0;

		double sum_mscore =0;
		double sum_nscore = 0;
		double sum_pscore = 0;


		for(int i=keepstart; i<keepend; i++)
		{
			users_refined.add(users_refined_type.get(i));

			String tmpusr = users_refined_type.get(i);

			sumscore += getUserScore(tmpusr, data_refined);

			sum_mscore += getPersonalityScore(tmpusr, data_refined, 0);
			sum_nscore += getPersonalityScore(tmpusr, data_refined, 1);
			sum_pscore += getPersonalityScore(tmpusr, data_refined, 2);


			System.out.println("kept user "+ tmpusr);
		}

		sumscore /= users_refined.size();
		sum_mscore /= users_refined.size();
		sum_nscore /= users_refined.size();
		sum_pscore /= users_refined.size();



		HashMap<String, Integer> att_game_play = buildOneStageGamePlay(users_refined, data_refined, 1);
		//HashMap<String, int[][]> def_game_play = buildGamePlay(users_refined, data_refined, 0);
		//HashMap<String, int[][]> reward = buildGameRewards(users_refined, data_refined);

		printOneStageGamePlay(att_game_play);

		HashMap<String, HashMap<String, Double>> strategy = Data.readStrategy("g5d5_FI.txt");
		String key = "EMPTY EMPTY"; 
		HashMap<String, Double> probs = strategy.get(key);

		//AdversaryModel.computeLambda(users_refined, att_game_play, def_game_play,reward, data_refined, 4);

		HashMap<Integer, Double > ui = attExpPayoffs(att_game_play, probs);


		//ArrayList<ArrayList<String>> data = Data.readData(lambda);

		HashMap<Integer, Integer> ni = computeFLipItNi(att_game_play);

		//HashMap<Integer, Double> ui = computeUi(data);

		int n=users_refined.size();

		double A=0;
		for(int action=0; action <6; action++)
		{
			double u = ui.get(action);
			double n_i = ni.get(action);

			A += (u*n_i);
		}

		double B = A/n;

		double[] coeffs = new double[6];

		for(int i=0; i<6; i++)
		{
			coeffs[i] = B-ui.get(i);
		}



		//int x=0;

		double estimatedlambda = estimateFlipItLambda(ni, n, ui);

		System.out.println("Estimated lambda "+ estimatedlambda + ", avg score "+ sumscore);


		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("iter-lambda.csv"),true));
			// gamenumber, subgame, psne, meb,qre
			pw.append(keepstart+"-"+keepend+","+sumscore +","+estimatedlambda+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}




	}
*/



	/**
	 * 
	 * @param users_refined
	 * @param data
	 * @param user_refine_type
	 * @param personality 
	 * @return
	 */
	private static ArrayList<String> refineUsers(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data,
			int user_refine_type, int personality) {



		ArrayList<String> sorted_users = new ArrayList<String>();


		if(user_refine_type==0)// high score
		{
			ArrayList<int[]> new_users = new ArrayList<int[]>();
			int userindex = 0;
			for(String tmpusr: users_refined)
			{

				int score = getUserScore(tmpusr,data);

				System.out.println("User "+ tmpusr + ", score " + score);

				int[] ex = {userindex, score};

				new_users.add(ex);
				userindex++;
			}

			// sort the users
			System.out.println("Sorting the users");

			int[][] srted_users = sortUsersDesc(new_users);



			for(int i=0; i<srted_users.length; i++)
			{
				sorted_users.add(users_refined.get(srted_users[i][0]));
				System.out.println("User "+ users_refined.get(srted_users[i][0]) + ", score "+ srted_users[i][1]);
			}
		}
		else if(user_refine_type==1) // low score
		{
			ArrayList<int[]> new_users = new ArrayList<int[]>();
			int userindex = 0;
			for(String tmpusr: users_refined)
			{

				int score = getUserScore(tmpusr,data);

				System.out.println("User "+ tmpusr + ", score " + score);

				int[] ex = {userindex, score};

				new_users.add(ex);
				userindex++;
			}

			// sort the users
			System.out.println("Sorting the users");

			int[][] srted_users = sortUsersAsc(new_users);



			for(int i=0; i<srted_users.length; i++)
			{
				sorted_users.add(users_refined.get(srted_users[i][0]));
				System.out.println("User "+ users_refined.get(srted_users[i][0]) + ", score "+ srted_users[i][1]);
			}
		}
		else if(personality >= 0) // mach 
		{
			ArrayList<double[]> new_users = new ArrayList<double[]>();
			int userindex = 0;
			for(String tmpusr: users_refined)
			{

				double mscore = getPersonalityScore(tmpusr,data, 0);
				double nscore = getPersonalityScore(tmpusr,data, 1);
				double pscore = getPersonalityScore(tmpusr,data, 2);

				System.out.println("user "+ tmpusr + " m: "+ mscore + ", n: "+ nscore + ", p: "+ pscore);


				double maxp = (( (mscore>=nscore)?mscore:nscore)>=pscore)?(((mscore>=nscore)?mscore:nscore)):pscore;

				if(maxp==mscore && personality==0)
				{
					System.out.println("User "+ tmpusr + ", max mscore " + mscore);

					double[] ex = {userindex, mscore};

					new_users.add(ex);
					userindex++;
				}
				else if(maxp==nscore && personality==1)
				{
					System.out.println("User "+ tmpusr + ", max nscore " + nscore);

					double[] ex = {userindex, nscore};

					new_users.add(ex);
					userindex++;
				}
				else if(maxp==pscore && personality==2)
				{
					System.out.println("User "+ tmpusr + ", max pscore " + pscore);

					double[] ex = {userindex, pscore};

					new_users.add(ex);
					userindex++;
				}


			}

			// sort the users
			System.out.println("Sorting the users");

			double[][] srted_users = sortUsersAscD(new_users);



			for(int i=0; i<srted_users.length; i++)
			{
				sorted_users.add(users_refined.get((int)srted_users[i][0]));
				System.out.println("User "+ users_refined.get((int)srted_users[i][0]) + ", score "+ srted_users[i][1]);
			}
		}


		return sorted_users;
	}



/**
 * tested...ok
 * @param tmpusr
 * @param data
 * @param personality
 * @return
 */
	private static double getPersonalityScore(String tmpusr, ArrayList<ArrayList<String>> data, int personality) {

		int start =-1;
		int end = -1;

		if(personality==0)
		{
			start = AdversaryModel.M_START_INDEX;
			end = AdversaryModel.M_END_INDEX;
		}
		else if(personality==1)
		{
			start = AdversaryModel.N_START_INDEX;
			end = AdversaryModel.N_END_INDEX;
		}
		else if(personality == 2)
		{
			start = AdversaryModel.P_START_INDEX;
			end = AdversaryModel.P_END_INDEX;
		}


		for(ArrayList<String> example: data)
		{
			if(example.get(Headers_minimum.user_id.getValue()).equals(tmpusr))
			{
				double sum = 0;
				int count = 0;
				for(int i=start; i<=end; i++)
				{
					String s = example.get(i);
					if(!s.equals(" "))
					{


						int choice = Integer.parseInt(s);
						int score = AdversaryModel.scoremap.get(choice);
						if(((i-10)==11) || ((i-10)==15) || ((i-10)==17) || ((i-10)==20) || ((i-10)==25))
						{
							score = choice;
						}
						count++;
						sum += score;
					}
				}
				sum = sum/count;
				return sum;

			}

		}

		return -1;
	}

	public static int[][] sortUsersDesc(ArrayList<int[]> users) 
	{

		int[][] srted = new int[users.size()][2];
		for(int i=0; i<srted.length; i++)
		{
			srted[i][0] = users.get(i)[0];
			srted[i][1] = users.get(i)[1];
		}
		int[] swap = {0,0};

		for (int k = 0; k < srted.length; k++) 
		{
			for (int d = 1; d < srted.length-k; d++) 
			{
				if (srted[d-1][1] < srted[d][1])    // ascending order
				{
					swap = srted[d];
					srted[d]  = srted[d-1];
					srted[d-1] = swap;
				}
			}
		}
		return srted;
	}
	
	

	public static double[][] sortDTUsersDescD(double[][] srted) 
	{

		//int[][] srted = new int[users.size()][2];
		
		double[] swap = {0,0};

		for (int k = 0; k < srted.length; k++) 
		{
			for (int d = 1; d < srted.length-k; d++) 
			{
				if (srted[d-1][1] < srted[d][1])    
				{
					swap = srted[d];
					srted[d]  = srted[d-1];
					srted[d-1] = swap;
				}
			}
		}
		return srted;
	}
	

	public static double[][] sortUsersDescD(ArrayList<double[]> users) 
	{

		double[][] srted = new double[users.size()][2];
		for(int i=0; i<srted.length; i++)
		{
			srted[i][0] = users.get(i)[0];
			srted[i][1] = users.get(i)[1];
		}
		double[] swap = {0,0};

		for (int k = 0; k < srted.length; k++) 
		{
			for (int d = 1; d < srted.length-k; d++) 
			{
				if (srted[d-1][1] < srted[d][1])    // ascending order
				{
					swap = srted[d];
					srted[d]  = srted[d-1];
					srted[d-1] = swap;
				}
			}
		}
		return srted;
	}

	public static int[][] sortUsersAsc(ArrayList<int[]> users) 
	{

		int[][] srted = new int[users.size()][2];
		for(int i=0; i<srted.length; i++)
		{
			srted[i][0] = users.get(i)[0];
			srted[i][1] = users.get(i)[1];
		}
		int[] swap = {0,0};

		for (int k = 0; k < srted.length; k++) 
		{
			for (int d = 1; d < srted.length-k; d++) 
			{
				if (srted[d-1][1] > srted[d][1])    // ascending order
				{
					swap = srted[d];
					srted[d]  = srted[d-1];
					srted[d-1] = swap;
				}
			}
		}
		return srted;
	}

	public static double[][] sortUsersAscD(ArrayList<double[]> users) 
	{

		double[][] srted = new double[users.size()][2];
		for(int i=0; i<srted.length; i++)
		{
			srted[i][0] = users.get(i)[0];
			srted[i][1] = users.get(i)[1];
		}
		double[] swap = {0,0};

		for (int k = 0; k < srted.length; k++) 
		{
			for (int d = 1; d < srted.length-k; d++) 
			{
				if (srted[d-1][1] > srted[d][1])    // ascending order
				{
					swap = srted[d];
					srted[d]  = srted[d-1];
					srted[d-1] = swap;
				}
			}
		}
		return srted;
	}




	private static int getUserScore(String tmpusr, ArrayList<ArrayList<String>> data) {

		/*for(ArrayList<String> example: data)
		{
			if(example.get(Headers_minimum.user_id.getValue()).equals(tmpusr))
				return Integer.parseInt(example.get(Headers_minimum.total_points.getValue()));
		}*/



		int sum = 0;

		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			// if example is for user_id
			if(tmpusr.equals(tmpuser))
			{
				//int gameins = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
				int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
				String def_order = example.get(Headers_minimum.pick_def_order.getValue());
				int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));


				if(def_order.equals("0") && (gameinstance==6) && round==5) // asc, take 4th game instance to 6th
				{

					sum += Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue()));

				}
				else if(def_order.equals("1") && (gameinstance==3) && round==5) // desc, take 1st game instance to 3rd
				{
					sum += Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue()));
				}



			}

		}
		return sum;
	}


	private static int getAllUserScore(String tmpusr, ArrayList<ArrayList<String>> data, int gameinstance0, int gameinstance1) {

		/*for(ArrayList<String> example: data)
		{
			if(example.get(Headers_minimum.user_id.getValue()).equals(tmpusr))
				return Integer.parseInt(example.get(Headers_minimum.total_points.getValue()));
		}*/



		int sum = 0;

		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			// if example is for user_id
			if(tmpusr.equals(tmpuser))
			{
				//int gameins = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
				int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
				String def_order = example.get(Headers_minimum.pick_def_order.getValue());
				int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));


				if(def_order.equals("0") && (gameinstance==gameinstance0) && round==5) // asc, take 4th game instance to 6th
				{

					sum += Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue()));

				}
				else if(def_order.equals("1") && (gameinstance==gameinstance1) && round==5) // desc, take 1st game instance to 3rd
				{
					sum += Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue()));
				}



			}

		}
		return sum;
	}


	private static int getAllUserScoreAdaptive(String tmpusr, ArrayList<ArrayList<String>> data, int gameinstance0, int def_order2) {

		/*for(ArrayList<String> example: data)
		{
			if(example.get(Headers_minimum.user_id.getValue()).equals(tmpusr))
				return Integer.parseInt(example.get(Headers_minimum.total_points.getValue()));
		}*/



		int sum = 0;

		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			// if example is for user_id
			if(tmpusr.equals(tmpuser))
			{
				//int gameins = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
				int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
				int def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue()));
				int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));


				if(def_order == def_order2 && (gameinstance==gameinstance0) && round==5) // asc, take 4th game instance to 6th
				{

					sum += Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue()));

				}
				/*else if(def_order.equals("1") && (gameinstance==gameinstance1) && round==5) // desc, take 1st game instance to 3rd
					{
						sum += Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue()));
					}
				 */


			}

		}
		return sum;
	}
	
	
	private static int getAllUserScoreAdaptiveRange(String tmpusr, ArrayList<ArrayList<String>> data, int gameinstance0, int gameinstance1, int def_order2) {

		/*for(ArrayList<String> example: data)
		{
			if(example.get(Headers_minimum.user_id.getValue()).equals(tmpusr))
				return Integer.parseInt(example.get(Headers_minimum.total_points.getValue()));
		}*/



		int sum = 0;

		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			// if example is for user_id
			if(tmpusr.equals(tmpuser))
			{
				//int gameins = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
				int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
				int def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue()));
				int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));


				if(def_order == def_order2 && (gameinstance>=gameinstance0 && gameinstance<=gameinstance1) && round==5) // asc, take 4th game instance to 6th
				{

					sum += Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue()));

				}
				/*else if(def_order.equals("1") && (gameinstance==gameinstance1) && round==5) // desc, take 1st game instance to 3rd
					{
						sum += Integer.parseInt(example.get(Headers_minimum.attacker_points.getValue()));
					}
				 */


			}

		}
		return sum;
	}

	private static HashMap<Integer, Integer> computeFLipItNi(HashMap<String,Integer> att_game_play) {


		HashMap<Integer, Integer> ni = new HashMap<Integer, Integer>();

		int[] count = new int[6];



		for(String ex: att_game_play.keySet())
		{
			int a = att_game_play.get(ex);
			//int a = Integer.parseInt(action);

			count[a]++;
		}
		for(int i=0; i<6; i++)
		{
			ni.put(i, count[i]);
		}

		return ni;



	}

	private static HashMap<Integer, Double> attExpPayoffs(HashMap<String, Integer> att_game_play,
			HashMap<String, Double> probs) {


		HashMap<Integer, Double> ui = new HashMap<Integer, Double>();

		int[] count = {0,0,0,0,0,0};
		Double[] uis = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

		int[][] target = new int[6][4];


		/*target[0][0] = 0;
		target[0][1] = -2;
		target[0][2] = 2;
		target[0][3] = 0;

		target[1][0] = 0;
		target[1][1] = -8;
		target[1][2] = 8;
		target[1][3] = 0;

		target[2][0] = 0;
		target[2][1] = -2;
		target[2][2] = 2;
		target[2][3] = 0;

		target[3][0] = 0;
		target[3][1] = -4;
		target[3][2] = -4;
		target[3][3] = 0;

		target[4][0] = 0;
		target[4][1] = -5;
		target[4][2] = 5;
		target[4][3] = 0;


		target[5][0] = 0;
		target[5][1] = 0;
		target[5][2] = 0;
		target[5][3] = 0;


		 */


		target[0][0] = 0;
		target[0][1] = -10;
		target[0][2] = 2;
		target[0][3] = 0;

		target[1][0] = 0;
		target[1][1] = -10;
		target[1][2] = 8;
		target[1][3] = 0;

		target[2][0] = 0;
		target[2][1] = -2;
		target[2][2] = 2;
		target[2][3] = 0;

		target[3][0] = 0;
		target[3][1] = -4;
		target[3][2] = 0;
		target[3][3] = 0;

		target[4][0] = 0;
		target[4][1] = -10;
		target[4][2] = 5;
		target[4][3] = 0;


		target[5][0] = 0;
		target[5][1] = 0;
		target[5][2] = 0;
		target[5][3] = 0;









		for(String ex: att_game_play.keySet())
		{
			int a = att_game_play.get(ex);



			String sa = a+"";
			double cov = 0;
			if(probs.containsKey(sa))
			{
				cov = probs.get(sa);
			}

			double u = cov*target[a][3] + (1-cov)*target[a][2];


			uis[a] += u;
			count[a]++;
		}




		for(int i=0; i<6; i++)
		{
			if(count[i] != 0)
			{
				uis[i] /= count[i];
			}

			ui.put(i, uis[i]);
		}

		return ui;


	}

	/*public static void computeLambdaExps() throws Exception {

		// create the data
		*//**
		 * 1. load user data
		 * 2. prepare data for clustering 2d data (normalize the data)
		 * 3. cluster
		 * 4. For each cluster compute lambda
		 * 
		 *//*
		int k= 3; // how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 4;
		int gameinstance1 = 1;

		ArrayList<ArrayList<String>> data =  Data.readData();

		ArrayList<String> users_refined = refineUser(data, -1, 1);

		ArrayList<ArrayList<String>>  data_refined = refineData(data,1, users_refined, gameinstance0, gameinstance1);

		//double[][] examples = prepareExamplesDTScorePoints(data_refined, users_refined);
		double[][] examples = prepareExamplesNodeCostPoint(data_refined, users_refined, gameinstance0, gameinstance1);
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);


		double[][] dummydata = new double[6][4];

		dummydata[0][0] = 1.0;
		dummydata[0][1] = 2.0;
		dummydata[0][2] = 3.0;
		dummydata[0][3] = 5.0;

		dummydata[1][0] = 1.0;
		dummydata[1][1] = 2.0;
		dummydata[1][2] = 3.0;
		dummydata[1][3] = 5.0;



		dummydata[2][0] = 7.0;
		dummydata[2][1] = 8.0;
		dummydata[2][2] = 9.0;
		dummydata[2][3] = 10.0;

		dummydata[3][0] = 7.0;
		dummydata[3][1] = 8.0;
		dummydata[3][2] = 9.0;
		dummydata[3][3] = 10.0;


		dummydata[4][0] = 1.0;
		dummydata[4][1] = 3.0;
		dummydata[4][2] = 2.0;
		dummydata[4][3] = 1.0;

		dummydata[5][0] = 1.0;
		dummydata[5][1] = 3.0;
		dummydata[5][2] = 2.0;
		dummydata[5][3] = 1.0;


		//List<Integer>[] clusters = Weka.clusterUsers(k,normalizedexamples);



		List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);


		*//**
		 * next use weka to cluster
		 *//*

		//printClusters(clusters);

		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();
		 

		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

			pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}

		for(int cluster=0; cluster<k; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);

			int attackcount[] = getAttackFrequency(users_groups, data_refined, numberofnodes, gameinstance0, gameinstance1);


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}


			HashMap<String, Integer> att_game_play = buildOneStageGamePlay(users_groups, data_refined, 1);
			//HashMap<String, int[][]> def_game_play = buildGamePlay(users_refined, data_refined, 0);
			//HashMap<String, int[][]> reward = buildGameRewards(users_refined, data_refined);

			//printOneStageGamePlay(att_game_play);

			HashMap<String, HashMap<String, Double>> strategy = Data.readStrategy("g5d5_FI.txt");
			String key = "EMPTY EMPTY"; 
			HashMap<String, Double> probs = strategy.get(key);

			//AdversaryModel.computeLambda(users_refined, att_game_play, def_game_play,reward, data_refined, 4);

			HashMap<Integer, Double > ui = attExpPayoffs(att_game_play, probs);


			//ArrayList<ArrayList<String>> data = Data.readData(lambda);

			HashMap<Integer, Integer> ni = computeFLipItNi(att_game_play);

			//HashMap<Integer, Double> ui = computeUi(data);

			int n=users_groups.size();

			double A=0;
			for(int action=0; action <6; action++)
			{
				double u = ui.get(action);
				double n_i = ni.get(action);

				A += (u*n_i);
			}

			double B = A/n;

			double[] coeffs = new double[6];

			for(int i=0; i<6; i++)
			{
				coeffs[i] = B-ui.get(i);
			}



			//int x=0;

			double estimatedlambda = estimateFlipItLambda(ni, n, ui);

			//Disconnect the proxy from MATLAB
			//proxy.disconnect();



			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);

				sumscore += getUserScore(tmpusr, data_refined);

				sum_mscore += getPersonalityScore(tmpusr, data_refined, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore /= users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambda);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}

		}

		// for each of the user groups compute lambda



	}
*/
	private static double[][] prepareFrquencey(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int numberofnodes) {

		double [][] examples = new double[users_refined.size()][7]; // frequency , total points

		int exampleindex = 0;
		for(String user_id: users_refined)
		{

			int count[] = new int[numberofnodes];

			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					String def_order = tmpexample.get(Headers_minimum.pick_def_order.getValue());
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					//int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}
					if(def_order.equals("0") && (gameinstance>=4)) // asc, take 4th game instance to 6th
					{
						count[attackaction]++;

					}
					else if(def_order.equals("1") && (gameinstance<=3)) // desc, take 1st game instance to 3rd
					{
						count[attackaction]++;
					}

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop

			int findex =0;
			for(int c: count)
			{
				examples[exampleindex][findex++] = c;
			}
			examples[exampleindex][findex] = getUserScore(user_id, data_refined);;
			exampleindex++;
		}
		return examples;
	}
	
	
	
	private static double[][] prepareFrquenceyOneGame(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int numberofnodes, int gameinstance, int def_order) {

		double [][] examples = new double[users_refined.size()][7]; // frequency , total points

		int exampleindex = 0;
		for(String user_id: users_refined)
		{

			int count[] = new int[numberofnodes];

			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					int deforder = Integer.parseInt(tmpexample.get(Headers_minimum.pick_def_order.getValue()));
					int tmp_gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					//int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}
					if(def_order == deforder && (gameinstance==tmp_gameinstance)) // asc, take 4th game instance to 6th
					{
						count[attackaction]++;

					}
					/*else if(def_order.equals("1") && (gameinstance==3)) // desc, take 1st game instance to 3rd
					{
						count[attackaction]++;
					}*/

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop

			int findex =0;
			for(int c: count)
			{
				examples[exampleindex][findex++] = c;
			}
			examples[exampleindex][findex] = getUserScore(user_id, data_refined);;
			exampleindex++;
		}
		return examples;
	}

	private static int[] getAttackFrequency(ArrayList<String> users_groups, ArrayList<ArrayList<String>> data_refined, int numberofnodes, int gameinstance0, int gameinstance1) {

		int count[] = new int[numberofnodes];

		for(String user_id: users_groups)
		{
			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					String def_order = tmpexample.get(Headers_minimum.pick_def_order.getValue());
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}
					if(def_order.equals("0") && (gameinstance==gameinstance0) /*&& round==1*/) // asc, take 4th game instance to 6th
					{
						System.out.println(attackaction);
						count[attackaction]++;

					}
					else if(def_order.equals("1") && (gameinstance==gameinstance1) /*&& round==1*/) // desc, take 1st game instance to 3rd
					{
						count[attackaction]++;
					}

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop
		}



		return count;

	}
	
	
	
	private static int[] getAttackFrequencyAdaptive(ArrayList<String> users_groups, ArrayList<ArrayList<String>> data_refined, 
			int numberofnodes, int gameinstance0, int deforder) {

		int count[] = new int[numberofnodes];

		for(String user_id: users_groups)
		{
			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					int def_order = Integer.parseInt(tmpexample.get(Headers_minimum.pick_def_order.getValue()));
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}
					if((def_order == deforder) && (gameinstance==gameinstance0) /*&& round==1*/) // asc, take 4th game instance to 6th
					{
						//System.out.println(attackaction);
						count[attackaction]++;

					}
					

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop
		}



		return count;

	}
	
	
	
	private static int[] getAttackFrequencyAdaptiveRange(ArrayList<String> users_groups, ArrayList<ArrayList<String>> data_refined, 
			int numberofnodes, int gameinstance0, int gameinstance1, int deforder) {

		int count[] = new int[numberofnodes];

		for(String user_id: users_groups)
		{
			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					int def_order = Integer.parseInt(tmpexample.get(Headers_minimum.pick_def_order.getValue()));
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}
					if((def_order == deforder) && (gameinstance>=gameinstance0 && gameinstance<=gameinstance1) /*&& round==1*/) // asc, take 4th game instance to 6th
					{
						//System.out.println(attackaction);
						count[attackaction]++;

					}
					

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop
		}



		return count;

	}
	
	
	
	private static int[] getAttackFrequencyAdaptiveWOOrder(ArrayList<String> users_groups, ArrayList<ArrayList<String>> data_refined, 
			int numberofnodes, String alg) {

		int count[] = new int[numberofnodes];

		for(String user_id: users_groups)
		{
			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					int def_order = Integer.parseInt(tmpexample.get(Headers_minimum.pick_def_order.getValue()));
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}
					
					int fgi = -1;
					int lgi = -1;
					
					if(alg.equals("r") && def_order==0)
					{
						fgi= 1;
						lgi=3;
					}
					else if(alg.equals("r") && def_order==1)
					{
						fgi= 4;
						lgi=6;
					}
					else if(alg.equals("s") && def_order==0)
					{
						fgi= 4;
						lgi=6;
					}
					else if(alg.equals("s") && def_order==1)
					{
						fgi= 1;
						lgi=3;
					}
					
					
					if((gameinstance>=fgi && gameinstance<=lgi) /*&& round==1*/) // asc, take 4th game instance to 6th
					{
						//System.out.println(attackaction);
						count[attackaction]++;

					}
					

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop
		}



		return count;

	}
	
	
	
	

	private static double[][] prepareExamplesNodeCostPoint(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int gameinstance0, int gameinstance1) {



		int[][] target = new int[6][2];


		target[0][0] = 10;
		target[0][1] = 8;


		target[1][0] = 10;
		target[1][1] = 2;

		target[2][0] = 4;
		target[2][1] = 2;


		target[3][0] = 4;
		target[3][1] = 8;

		target[4][0] = 10;
		target[4][1] = 5;



		target[5][0] = 0;
		target[5][1] = 0;




		//3 features per round, 1 games, 5 round per game , 45 features
		double examples[][] = new double[users_refined.size()][15]; // nodevalue,cost,pointforaction,


		int exampleindex = 0;
		for(String user_id: users_refined)
		{
			// get all the actions in every round and poins

			System.out.println("User : "+ user_id);
			int featureindex = 0;

			//examples[exampleindex][0] = exampleindex;

			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					String def_order = tmpexample.get(Headers_minimum.pick_def_order.getValue());
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}


					int nodevalue = target[attackaction][0];
					int nodecost = target[attackaction][1];
					int attackerpoints = Integer.parseInt(tmpexample.get(Headers_minimum.attacker_points.getValue()));





					if(def_order.equals("0") && (gameinstance==gameinstance0) /*&& round==1*/) // asc, take 4th game instance to 6th
					{
						System.out.println("instance "+gameinstance +", round "+ round);
						//int featuregameinstance = gameinstance-4;
						//featureindex = round + (featuregameinstance*5);
						System.out.println("findex "+ featureindex + ", v: "+nodevalue);
						examples[exampleindex][featureindex++] = nodevalue;
						System.out.println("findex "+ featureindex + ", c: "+nodecost);
						examples[exampleindex][featureindex++] = nodecost; 
						System.out.println("findex "+ featureindex + ", p: "+attackerpoints);
						examples[exampleindex][featureindex++] = attackerpoints; 




					}
					else if(def_order.equals("1") && (gameinstance==gameinstance1)/* && round==1*/) // desc, take 1st game instance to 3rd
					{
						System.out.println("instance "+gameinstance +", round "+ round);
						//int featuregameinstance = gameinstance-1;
						//int featureindex = round + (featuregameinstance*5);
						System.out.println("findex "+ featureindex + ", v: "+nodevalue);
						examples[exampleindex][featureindex++] = nodevalue;
						System.out.println("findex "+ featureindex + ", c: "+nodecost);
						examples[exampleindex][featureindex++] = nodecost; 
						System.out.println("findex "+ featureindex + ", p: "+attackerpoints);
						examples[exampleindex][featureindex++] = attackerpoints; 
					}

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop


			exampleindex++;
		}

		return examples;

	}
	
	
	private static double[][] prepareExamplesNodeCostPointAdaptive(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int gameinstance0, int deforder) {



		int[][] target = new int[6][2];


		target[0][0] = 10;
		target[0][1] = 8;


		target[1][0] = 10;
		target[1][1] = 2;

		target[2][0] = 4;
		target[2][1] = 2;


		target[3][0] = 4;
		target[3][1] = 8;

		target[4][0] = 10;
		target[4][1] = 5;



		target[5][0] = 0;
		target[5][1] = 0;




		//3 features per round, 1 games, 5 round per game , 45 features
		double examples[][] = new double[users_refined.size()][15]; // nodevalue,cost,pointforaction,


		int exampleindex = 0;
		for(String user_id: users_refined)
		{
			// get all the actions in every round and poins

			System.out.println("User : "+ user_id);
			int featureindex = 0;

			//examples[exampleindex][0] = exampleindex;

			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					int def_order = Integer.parseInt(tmpexample.get(Headers_minimum.pick_def_order.getValue()));
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}


					int nodevalue = target[attackaction][0];
					int nodecost = target[attackaction][1];
					int attackerpoints = Integer.parseInt(tmpexample.get(Headers_minimum.attacker_points.getValue()));





					if(def_order == deforder && (gameinstance==gameinstance0) /*&& round==1*/) // asc, take 4th game instance to 6th
					{
						System.out.println("instance "+gameinstance +", round "+ round);
						//int featuregameinstance = gameinstance-4;
						//featureindex = round + (featuregameinstance*5);
						System.out.println("findex "+ featureindex + ", v: "+nodevalue);
						examples[exampleindex][featureindex++] = nodevalue;
						System.out.println("findex "+ featureindex + ", c: "+nodecost);
						examples[exampleindex][featureindex++] = nodecost; 
						System.out.println("findex "+ featureindex + ", p: "+attackerpoints);
						examples[exampleindex][featureindex++] = attackerpoints; 




					}
					

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop


			exampleindex++;
		}

		return examples;

	}
	
	
	private static double[][] prepareExamplesNodeCostPointAdaptiveProgressive(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int gameinstance0, int deforder) {



		int[][] target = new int[6][2];


		target[0][0] = 10;
		target[0][1] = 8;


		target[1][0] = 10;
		target[1][1] = 2;

		target[2][0] = 4;
		target[2][1] = 2;


		target[3][0] = 4;
		target[3][1] = 8;

		target[4][0] = 10;
		target[4][1] = 5;



		target[5][0] = 0;
		target[5][1] = 0;



		

		//3 features per round, 1 games, 5 round per game , 45 features
		double examples[][] = new double[users_refined.size()][15*1]; // nodevalue,cost,pointforaction,


		int exampleindex = 0;
		for(String user_id: users_refined)
		{
			// get all the actions in every round and poins

			//System.out.println("User : "+ user_id);
			int featureindex = 0;

			//examples[exampleindex][0] = exampleindex;

			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					int def_order = Integer.parseInt(tmpexample.get(Headers_minimum.pick_def_order.getValue()));
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}


					int nodevalue = target[attackaction][0];
					int nodecost = target[attackaction][1];
					int attackerpoints = Integer.parseInt(tmpexample.get(Headers_minimum.attacker_points.getValue()));





					if(def_order == deforder && (gameinstance<=gameinstance0) /*&& round==1*/) // asc, take 4th game instance to 6th
					{
						//System.out.println("instance "+gameinstance +", round "+ round);
						//int featuregameinstance = gameinstance-4;
						//featureindex = round + (featuregameinstance*5);
						//System.out.println("findex "+ featureindex + ", v: "+nodevalue);
						examples[exampleindex][featureindex++] = nodevalue;
						//System.out.println("findex "+ featureindex + ", c: "+nodecost);
						examples[exampleindex][featureindex++] = nodecost; 
						//System.out.println("findex "+ featureindex + ", p: "+attackerpoints);
						examples[exampleindex][featureindex++] = attackerpoints; 




					}
					

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop


			exampleindex++;
		}

		return examples;

	}
	
	
	
	private static double[][] prepareExamplesNodeCostPointRange(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int gameinstance0, int gameinstance1, int deforder, int ngames) {



		int[][] target = new int[6][2];


		target[0][0] = 10;
		target[0][1] = 8;


		target[1][0] = 10;
		target[1][1] = 2;

		target[2][0] = 4;
		target[2][1] = 2;


		target[3][0] = 4;
		target[3][1] = 8;

		target[4][0] = 10;
		target[4][1] = 5;



		target[5][0] = 0;
		target[5][1] = 0;



		

		//3 features per round, 1 games, 5 round per game , 45 features
		double examples[][] = new double[users_refined.size()][15*ngames]; // nodevalue,cost,pointforaction,


		int exampleindex = 0;
		for(String user_id: users_refined)
		{
			// get all the actions in every round and poins

			//System.out.println("User : "+ user_id);
			int featureindex = 0;

			//examples[exampleindex][0] = exampleindex;

			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					int def_order = Integer.parseInt(tmpexample.get(Headers_minimum.pick_def_order.getValue()));
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 0;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}


					int nodevalue = target[attackaction][0];
					int nodecost = target[attackaction][1];
					int attackerpoints = Integer.parseInt(tmpexample.get(Headers_minimum.attacker_points.getValue()));





					if(def_order == deforder && (gameinstance>=gameinstance0 && gameinstance<=gameinstance1) /*&& round==1*/) // asc, take 4th game instance to 6th
					{
						//System.out.println("instance "+gameinstance +", round "+ round);
						//int featuregameinstance = gameinstance-4;
						//featureindex = round + (featuregameinstance*5);
						//System.out.println("findex "+ featureindex + ", v: "+nodevalue);
						examples[exampleindex][featureindex++] = nodevalue;
						//System.out.println("findex "+ featureindex + ", c: "+nodecost);
						examples[exampleindex][featureindex++] = nodecost; 
						//System.out.println("findex "+ featureindex + ", p: "+attackerpoints);
						examples[exampleindex][featureindex++] = attackerpoints; 




					}
					

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop


			exampleindex++;
		}

		return examples;

	}
	
	
	private static double[][] prepareExamplesNodeCostPointWOOrder(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int ngames, String alg) {



		int[][] target = new int[6][2];


		target[0][0] = 10;
		target[0][1] = 8;


		target[1][0] = 10;
		target[1][1] = 2;

		target[2][0] = 4;
		target[2][1] = 2;


		target[3][0] = 4;
		target[3][1] = 8;

		target[4][0] = 10;
		target[4][1] = 5;



		target[5][0] = 0;
		target[5][1] = 0;



		

		//3 features per round, 1 games, 5 round per game , 45 features
		double examples[][] = new double[users_refined.size()][15*ngames]; // nodevalue,cost,pointforaction,


		int exampleindex = 0;
		for(String user_id: users_refined)
		{
			// get all the actions in every round and poins

			//System.out.println("User : "+ user_id);
			int featureindex = 0;

			//examples[exampleindex][0] = exampleindex;

			for(ArrayList<String> tmpexample: data_refined)
			{
				String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
				if(tmpuserid.equals(user_id))
				{
					// print attacker action, get node value
					// get node cost
					// get point in this round

					int def_order = Integer.parseInt(tmpexample.get(Headers_minimum.pick_def_order.getValue()));
					int gameinstance = Integer.parseInt(tmpexample.get(Headers_minimum.game_instance.getValue()));
					int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
					String action = tmpexample.get(Headers_minimum.attacker_action.getValue());
					int attackaction = 5;
					if(!action.equals(" "))
					{
						attackaction = Integer.parseInt(action);
					}


					int nodevalue = target[attackaction][0];
					int nodecost = target[attackaction][1];
					int attackerpoints = Integer.parseInt(tmpexample.get(Headers_minimum.attacker_points.getValue()));


					int fgi = -1;
					int lgi = -1;
					
					if(alg.equals("r") && def_order==0)
					{
						fgi= 1;
						lgi=3;
					}
					else if(alg.equals("r") && def_order==1)
					{
						fgi= 4;
						lgi=6;
					}
					else if(alg.equals("s") && def_order==0)
					{
						fgi= 4;
						lgi=6;
					}
					else if(alg.equals("s") && def_order==1)
					{
						fgi= 1;
						lgi=3;
					}


					if((gameinstance>=fgi && gameinstance<=lgi) /*&& round==1*/) // asc, take 4th game instance to 6th
					{
						//System.out.println("instance "+gameinstance +", round "+ round);
						//int featuregameinstance = gameinstance-4;
						//featureindex = round + (featuregameinstance*5);
						//System.out.println("findex "+ featureindex + ", v: "+nodevalue);
						examples[exampleindex][featureindex++] = nodevalue;
						//System.out.println("findex "+ featureindex + ", c: "+nodecost);
						examples[exampleindex][featureindex++] = nodecost; 
						//System.out.println("findex "+ featureindex + ", p: "+attackerpoints);
						examples[exampleindex][featureindex++] = attackerpoints; 




					}
					

					//System.out.println("Game type "+ tmpexample.get(Headers_minimum.game_type.getValue()));

				} // end if
			} // end for loop


			exampleindex++;
		}

		return examples;

	}
	
	

	private static ArrayList<String> getUserGroup(List<Integer> list, ArrayList<String> users_refined) {


		ArrayList<String> users = new ArrayList<String>();

		for(double index: list)
		{
			//System.out.println("adding usser index "+ index);
			users.add(users_refined.get((int)index));
		}

		return users;


	}
	
	
	private static int getIndivUserGroup(List<Integer>[] clusters, ArrayList<String> users_refined, String user_id) 
	{

		
		
		int index = users_refined.indexOf(user_id);
		
		for(int i=0; i<clusters.length; i++)
		{
			if(clusters[i].contains(index))
			{
				return i;
			}
			
		}
		
		

		return -1;


	}

	private static void printClusters(List<Double>[] clusters) {


		System.out.println();
		for(int i=0; i<clusters.length; i++)
		{
			System.out.print("cluster "+i + ": ");
			for(Double x: clusters[i])
			{
				System.out.print(x.intValue()+", ");
			}
			System.out.println();
		}




	}

	private static void printClustersInt(List<Integer>[] clusters) {


		System.out.println();
		for(int i=0; i<clusters.length; i++)
		{
			System.out.print("cluster "+i + ": ");
			for(Integer x: clusters[i])
			{
				System.out.print(x.intValue()+", ");
			}
			System.out.println();
		}




	}

	private static double[][] normalizeData(double[][] examples) {


		double[][] normalizedExamples = new double[examples.length][examples[0].length];

		for(int feature=0; feature<examples[0].length; feature++)
		{
			double dataLowHigh[] = getDataLow(examples, feature);
			double normalizedLowHigh[] = {0.0, 10};
			//System.out.println("Feature  "+ feature + ", low "+ dataLowHigh[0] + ", high "+ dataLowHigh[1]);

			for(int row=0; row<examples.length; row++)
			{
				double normx = normalize(examples[row][feature], dataLowHigh[0], dataLowHigh[1], normalizedLowHigh[0], normalizedLowHigh[1]);
				normalizedExamples[row][feature] = normx;
				//System.out.println("Feature  "+ feature + ", low "+ dataLowHigh[0] + ", high "+ dataLowHigh[1] + ", value "+examples[row][feature]+ ", normval "+ normx);
			}

		}

		return normalizedExamples;

	}

	private static double[] getDataLow(double[][] examples, int feature) {

		double[] lowhigh = {Double.MAX_VALUE, Double.MIN_VALUE};

		for(int row=0; row<examples.length; row++)
		{
			if(examples[row][feature]<lowhigh[0])
			{
				lowhigh[0] = examples[row][feature];
			}
			else if(examples[row][feature]>lowhigh[1])
			{
				lowhigh[1] = examples[row][feature];
			}
		}
		return lowhigh;
	}

	/**
	 * 
	 * @param x
	 * @param dataLow
	 * @param dataHigh
	 * @param normalizedLow
	 * @param normalizedHigh
	 * @return
	 */
	public static double normalize(double x, double dataLow, double dataHigh, double normalizedLow, double normalizedHigh ) 
	{
		return ((x - dataLow) 
				/ (dataHigh - dataLow))
				* (normalizedHigh - normalizedLow) + normalizedLow;
	}




	private static void printData(ArrayList<String> users_refined, double[][] examples) {

		for(int i=0; i<examples.length; i++)
		{
			//System.out.print("id_"+ i + "="+i+"" );
			for(int j=0; j<examples[i].length; j++)
			{
				System.out.print(" f_"+j+"= "+ examples[i][j]);
			}
			System.out.println();
		}

	}

	/**
	 * prepare data for clustering using DT score and points
	 * @param data_refined
	 * @param users_refined
	 * @param gameinstance1 
	 * @param gameinstance0 
	 * @return
	 */
	private static double[][] prepareExamplesDTScorePoints(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int gameinstance0, int gameinstance1) {

		double[][] examples = new double[users_refined.size()][4];

		/**
		 * for each user compute DT scores 
		 */

		int userindex = 0;
		for(String usr_id: users_refined)
		{
			double mscore = getPersonalityScore(usr_id, data_refined, 0);
			double nscore = getPersonalityScore(usr_id, data_refined, 1);
			double pscore = getPersonalityScore(usr_id, data_refined, 2);
			double totalpoints = getAllUserScore(usr_id, data_refined, gameinstance0, gameinstance1);

			examples[userindex][0] = mscore;
			examples[userindex][1] = nscore;
			examples[userindex][2] = pscore;
			examples[userindex][3] = totalpoints;
			userindex++;
		}
		return examples;
	}
	
	
	/**
	 * prepare data for clustering using DT score and points
	 * @param data_refined
	 * @param users_refined
	 * @param gameinstance1 
	 * @param gameinstance0 
	 * @return
	 */
	private static double[][] prepareExamplesDTScorePointsOneGame(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int gameinstance0, int deforder) {

		double[][] examples = new double[users_refined.size()][4];

		/**
		 * for each user compute DT scores 
		 */

		int userindex = 0;
		for(String usr_id: users_refined)
		{
			double mscore = getPersonalityScore(usr_id, data_refined, 0);
			double nscore = getPersonalityScore(usr_id, data_refined, 1);
			double pscore = getPersonalityScore(usr_id, data_refined, 2);
			double totalpoints = getAllUserScoreAdaptive(usr_id, data_refined, gameinstance0, deforder);

			examples[userindex][0] = mscore;
			examples[userindex][1] = nscore;
			examples[userindex][2] = pscore;
			examples[userindex][3] = totalpoints;
			userindex++;
		}
		return examples;
	}
	
	
	private static double[][] prepareExamplesDTScorePointsRange(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int gameinstance0, int gameinstance1,int deforder, int ngames) {

		double[][] examples = new double[users_refined.size()][4];

		/**
		 * for each user compute DT scores 
		 */

		int userindex = 0;
		for(String usr_id: users_refined)
		{
			double mscore = getPersonalityScore(usr_id, data_refined, 0);
			double nscore = getPersonalityScore(usr_id, data_refined, 1);
			double pscore = getPersonalityScore(usr_id, data_refined, 2);
			double totalpoints = getAllUserScoreAdaptiveRange(usr_id, data_refined, gameinstance0, gameinstance1, deforder);

			examples[userindex][0] = mscore;
			examples[userindex][1] = nscore;
			examples[userindex][2] = pscore;
			examples[userindex][3] = totalpoints;
			userindex++;
		}
		return examples;
	}
	
	
	private static double[][] prepareExamplesDTScorePointsWOOrder(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int ngames, String alg) {

		double[][] examples = new double[users_refined.size()][4];

		/**
		 * for each user compute DT scores 
		 */

		int userindex = 0;
		for(String usr_id: users_refined)
		{
			double mscore = getPersonalityScore(usr_id, data_refined, 0);
			double nscore = getPersonalityScore(usr_id, data_refined, 1);
			double pscore = getPersonalityScore(usr_id, data_refined, 2);
			
			int def_order = getDefOrder(usr_id, data_refined);
			
			
			int fgi = -1;
			int lgi = -1;
			
			
			
			if(alg.equals("r") && def_order==0)
			{
				fgi= 1;
				lgi=3;
			}
			else if(alg.equals("r") && def_order==1)
			{
				fgi= 4;
				lgi=6;
			}
			else if(alg.equals("s") && def_order==0)
			{
				fgi= 4;
				lgi=6;
			}
			else if(alg.equals("s") && def_order==1)
			{
				fgi= 1;
				lgi=3;
			}
			
			double totalpoints = getAllUserScoreAdaptiveRange(usr_id, data_refined, fgi, lgi, def_order);

			examples[userindex][0] = mscore;
			examples[userindex][1] = nscore;
			examples[userindex][2] = pscore;
			examples[userindex][3] = totalpoints;
			userindex++;
		}
		return examples;
	}
	
	
	
	private static int getDefOrder(String usr_id, ArrayList<ArrayList<String>> data_refined) {
		
		
		for(ArrayList<String> tmpexample: data_refined)
		{
			String tmpuserid = tmpexample.get(Headers_minimum.user_id.getValue());
			if(tmpuserid.equals(usr_id))
			{
				// print attacker action, get node value
				// get node cost
				// get point in this round

				int def_order = Integer.parseInt(tmpexample.get(Headers_minimum.pick_def_order.getValue()));
				return def_order;
			}
		}
		
		
		return -1;
	}

	/**
	 * get user points
	 * @param data_refined
	 * @param users_refined
	 * @param gameinstance1 
	 * @param gameinstance0 
	 * @return
	 */
	private static double getUserPointsOneGame(ArrayList<ArrayList<String>> data_refined,
			ArrayList<String> users_refined, int gameinstance0, int deforder) {

		
		
		double sum = 0;

		/**
		 * for each user compute DT scores 
		 */

		int userindex = 0;
		for(String usr_id: users_refined)
		{
			
			double totalpoints = getAllUserScoreAdaptive(usr_id, data_refined, gameinstance0, deforder);
			sum += totalpoints;

			
		}
		return sum/users_refined.size();
	}



	/**
	 * 1. groups users
	 * 2. estimate lambda based on different groups
	 * @throws Exception 
	 * 
	 */
	public static void computeLambdaQR() throws Exception {


		// how many clusters you want
		int numberofnodes = 6;
		int gameinstance0 = 4;
		int gameinstance1 = 1;




		int DEPTH_LIMIT = 2; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .05;
		double maxlambda = .18;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);

		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender
		ArrayList<String> users_refined = refineUser(data, -1, 1);

		ArrayList<ArrayList<String>>  data_refined = refineData(data,1, users_refined, gameinstance0, gameinstance1);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);

		HashMap<String, String> alluser_seq = new HashMap<String, String>();
		HashMap<String, Integer> alluser_reward = new HashMap<String, Integer>();
		int[][] allusergameplay = createGamePlay(ngames, users_refined, data_refined, roundlimit,alluser_seq);

		double allattpoints = EquationGenerator.computeAttackerReward(noderewards, alluser_seq, alluser_reward);


		ArrayList<String> inconsistentuser = new ArrayList<String>();



		System.out.println("users size "+ users_refined.size());

		for(String usr: alluser_seq.keySet())
		{

			int tmpscore= getAllUserScore(usr, data_refined, gameinstance0, gameinstance1);
			int score = alluser_reward.get(usr);

			if(tmpscore != score)
			{
				inconsistentuser.add(usr);
			}
		}

		for(String usr: inconsistentuser)
		{
			if(users_refined.contains(usr))
			{
				users_refined.remove(usr);
			}
		}

		System.out.println("after removing inconsistent users size "+ users_refined.size());

		data_refined = refineData(data,1, users_refined, gameinstance0, gameinstance1);







		//double[][] examples = prepareExamplesDTScorePoints(data_refined, users_refined, gameinstance0, gameinstance1);
		double[][] examples = prepareExamplesNodeCostPoint(data_refined, users_refined, gameinstance0, gameinstance1);
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);

		int k= 2;

		//List<Integer>[] clusters = Weka.clusterUsers(k, normalizedexamples);



		List<Integer>[] clusters = Weka.clusterUsers(normalizedexamples);



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);




		/**
		 * next use weka to cluster
		 */

		//printClusters(clusters);

		//Create a proxy, which we will use to control MATLAB
		/*MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();
		 */

		/*try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

			//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}

		 */





		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequency(users_groups, data_refined, numberofnodes, gameinstance0, gameinstance1);






			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");


			//HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();

			//double tmplambda = 0.6;


			double estimatedlambdanaive = estimateLambdaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			System.out.println("Estmiated lambda "+ estimatedlambdanaive);




			/*DNode root1 = EquationGenerator.buildGameTreeRecur(DEPTH_LIMIT, naction, defstrategy, attstrategy, tmplambda);

			computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);*/





			/*DNode root = EquationGenerator.buildGameTree(DEPTH_LIMIT, naction);



			HashMap<String, ArrayList<DNode>> I = EquationGenerator.prepareInformationSets(root, DEPTH_LIMIT, naction);
			EquationGenerator.printInfoSet(I);
			HashMap<String, InfoSet> isets = EquationGenerator.prepareInfoSet(I);
			 *//**
			 * compute information sets according to depth
			 *//*
			HashMap<Integer, ArrayList<String>> depthinfoset = depthInfoSet(DEPTH_LIMIT, isets,1); // for player 1: attacker, player 0 is defender
			EquationGenerator.printISets(isets);


			EquationGenerator.updateTreeWithDefStartegy(isets, root, strategy, naction);
			  */


			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);







			// use attackstrategy to compute lambda




			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				int tmpscore= getAllUserScore(tmpusr, data_refined, gameinstance0, gameinstance1); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}


				sumscore += tmpscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(cluster+","+users_groups.size()+","+ estimatedlambdanaive+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		}

		// for each of the user groups compute lambda


	}


	/**
	 * whether users aDpt their srategy when they faced intelligent defender
	 * tracking user trend which gro
	 * @param k 
	 * @param depthlimit 
	 * @param featureset 
	 * @param game_type 
	 * @param def_order2 
	 * @throws Exception
	 */
	public static void computeLambdaForAdaptivenessQR(int k, int def_order, int depthlimit, int featureset, int game_type) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);


		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		//int def_order = 0; //asc 1,2,3 random def.... 4,5,6 stratgic def
		//int game_type = 1;


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);


		// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);
		ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		ArrayList<ArrayList<String>>  data_refined_sixth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance5, def_order);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsers(users_refined, data_refined_first_game, ngames, gameinstance0, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);

	//	int k = 0;
		
		
		/**
		 * find frequency for the first three game play
		 */
		
		if(def_order==0)
		{

			ArrayList<ArrayList<String>> newdata = new ArrayList<ArrayList<String>>();
			//addData(data_refined_first_game, newdata);
			
			//newdata = data_refined_first_game;

			findFrequencyForGroup(users_refined, data_refined_first_game, ngames, gameinstance0, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);

			
			/**
			 * append previous data to the cur data
			 */
			
			
			
			
			//addData(data_refined_second_game, newdata);
			//newdata = data_refined_second_game;
			
			findFrequencyForGroup(users_refined, data_refined_second_game, ngames, gameinstance1, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);

			/**
			 * append previous data to the cur data
			 */
			
			//addData(data_refined_third_game, newdata);

			
			
			findFrequencyForGroup(users_refined, data_refined_third_game, ngames, gameinstance2, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);


			
			
			
			
			
			/**
			 * append previous data to the cur data
			 */
			
			//newdata = new ArrayList<ArrayList<String>>();
			
			//addData( data_refined_fourth_game, newdata);

			findLambdaForGroup(users_refined, data_refined_fourth_game, ngames, gameinstance3, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);

			/**
			 * append previous data to the cur data
			 */
			
			//addData( data_refined_fifth_game, newdata);
			
			findLambdaForGroup(users_refined, data_refined_fifth_game, ngames, gameinstance4, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);

			/**
			 * append previous data to the cur data
			 */
			//addData(data_refined_sixth_game, newdata);
			
			findLambdaForGroup(users_refined, data_refined_sixth_game, ngames, gameinstance5, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);

		}
		else if(def_order==1)
		{
			ArrayList<ArrayList<String>> newdata = new ArrayList<ArrayList<String>>();
			//addData(data_refined_first_game, newdata);

			findLambdaForGroup(users_refined, data_refined_first_game, ngames, gameinstance0, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);

			
			/**
			 * append previous data to the cur data
			 */
			
			
			
			
			//addData(data_refined_second_game, newdata);
			
			findLambdaForGroup(users_refined, data_refined_second_game, ngames, gameinstance1, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);

			/**
			 * append previous data to the cur data
			 */
			
			//addData(data_refined_third_game, newdata);

			findLambdaForGroup(users_refined, data_refined_third_game, ngames, gameinstance2, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);


			
			
			
			
			
			/**
			 * append previous data to the cur data
			 */
			
			//newdata = new ArrayList<ArrayList<String>>();
			
			//addData( data_refined_fourth_game, newdata);

			findFrequencyForGroup(users_refined, data_refined_fourth_game, ngames, gameinstance3, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);

			/**
			 * append previous data to the cur data
			 */
			
			//addData( data_refined_fifth_game, newdata);
			
			findFrequencyForGroup(users_refined, data_refined_fifth_game, ngames, gameinstance4, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);

			/**
			 * append previous data to the cur data
			 */
			//addData(data_refined_sixth_game, newdata);
			
			findFrequencyForGroup(users_refined, data_refined_sixth_game, ngames, gameinstance5, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata);
		}

		// for each of the user groups compute lambda


	}
	
	
	public static void computeLambdaForAdaptivenessCombinedQR(int k, int depthlimit, int featureset, int game_type, String algorithm) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);


		// how many clusters you want
		int numberofnodes = 6;


		//int def_order = 0; //asc 1,2,3 random def.... 4,5,6 stratgic def
		//int game_type = 1;


		int ngames = -1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		


	
		
		int fgi = -1;
		int lgi = -1;
		int def_order=0;
		int exampleinstances = 15;
		
		if(algorithm.equals("r1") )
		{
			def_order=0;
			fgi= 1;
			lgi= 3;
			ngames=3;

		}
		else if(algorithm.equals("r2"))
		{
			def_order=1;
			fgi= 4;
			lgi=6;
			ngames=3;
		}
		else if(algorithm.equals("s1"))
		{
			def_order=1;
			fgi= 1;
			lgi= 3;
			ngames=3;

		}
		else if(algorithm.equals("s2"))
		{
			def_order=0;
			fgi= 4;
			lgi=6;
			ngames=3;
		}
		
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);
		
		
		
		
		ArrayList<ArrayList<String>>  data_refined = refineDataAdaptiveRange(data,game_type, users_refined, fgi, lgi, def_order);





		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsersRange(users_refined, data_refined, ngames, fgi, lgi, data, noderewards, game_type, roundlimit, def_order, exampleinstances);



		findLambdaForGroupRange(users_refined, data_refined, ngames, fgi, lgi,data,
				noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, algorithm);


	}
	
	
	
	public static void computeLambdaForAdaptivenessWODefOrderQR(int k, int depthlimit, int featureset, int game_type, String algorithm, boolean generic) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);


		// how many clusters you want
		int numberofnodes = 6;

		

		int ngames = -1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		


		
		
		int fgi = -1;
		int lgi = -1;
		//int def_order=0;
		int exampleinstances = 15;
		
		if(algorithm.equals("r") )
		{
			//def_order=0;
			fgi= 1;
			lgi= 3;
			ngames=3;

		}
		else if(algorithm.equals("s"))
		{
			//def_order=1;
			fgi= 4;
			lgi=6;
			ngames=3;
		}
		
		
		ArrayList<String> users_refined = refineUserAdaptiveWOOrder(data, game_type);
		
		
		/*// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);
		ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		
		*/
		
		
		ArrayList<ArrayList<String>>  data_refined = refineDataAdaptiveRangeWOOrder(data,game_type, users_refined, algorithm);





		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);

		
		//removeIncosistentUsers(users_refined, data_refined, exampleinstances, algorithm);
		

		sanitizeUsersRangeWOOrder(users_refined, data_refined, ngames, data, noderewards, game_type, roundlimit, exampleinstances, algorithm);
		
		
		/*sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);*/

	//	int k = 0;
		
		
		/**
		 * find frequency for the first three game play
		 */
		
		
			//addData(data_refined_second_game, newdata);
		
		
		
		findLambdaForGroupRangeWOOrder(users_refined, data_refined, ngames,data,
				noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, featureset, algorithm, generic);

	
			
		/*computeDefBR(users_refined, data_refined, ngames,data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, featureset, algorithm, genericgroup);
*/
			
		// for each of the user groups compute lambda


	}
	
	
	
	public static void computeOmegaWODefOrderSUQR(int k, int depthlimit, int featureset, int game_type, String algorithm, boolean genericgroup, 
			double minw1, double maxw1, double minw2, double maxw2, double minw3, double maxw3, double minw4, double maxw4, double w1step, 
			double w2step, double w3step, double w4step, boolean parallel) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		
		
		double lambda = 1;//generateLambdaArray(minlambda, maxlambda, step);


		// how many clusters you want
		int numberofnodes = 6;

		

		int ngames = -1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		


		
		
		int fgi = -1;
		int lgi = -1;
		//int def_order=0;
		int exampleinstances = 15;
		
		if(algorithm.equals("r") )
		{
			//def_order=0;
			fgi= 1;
			lgi= 3;
			ngames=3;

		}
		else if(algorithm.equals("s"))
		{
			//def_order=1;
			fgi= 4;
			lgi=6;
			ngames=3;
		}
		
		
		ArrayList<String> users_refined = refineUserAdaptiveWOOrder(data, game_type);
		
		
		
		
		
		ArrayList<ArrayList<String>>  data_refined = refineDataAdaptiveRangeWOOrder(data,game_type, users_refined, algorithm);





		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);

		
		//removeIncosistentUsers(users_refined, data_refined, exampleinstances, algorithm);
		

		sanitizeUsersRangeWOOrder(users_refined, data_refined, ngames, data, noderewards, game_type, roundlimit, exampleinstances, algorithm);
		
		
		
		
		
		findOmega(users_refined, data_refined, ngames,data,
				noderewards, game_type, roundlimit, numberofnodes, lambda, naction, DEPTH_LIMIT, k, featureset, algorithm, 
				minw1, maxw1, minw2, maxw2, minw3, maxw3, minw4, maxw4, w1step, w2step, w3step, w4step, genericgroup, parallel);

		
		
		


	}
	
	
	
	
	
	public static void computeOmegaForAdaptivenessSUQR(int k, int def_order, int depthlimit, int featureset, int game_type) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double lambda = 1;//generateLambdaArray(minlambda, maxlambda, step);


		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		//int def_order = 0; //asc 1,2,3 random def.... 4,5,6 stratgic def
		//int game_type = 1;


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);


		// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);
		ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		ArrayList<ArrayList<String>>  data_refined_sixth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance5, def_order);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsers(users_refined, data_refined_first_game, ngames, gameinstance0, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);

	//	int k = 0;
		
		
		/**
		 * find frequency for the first three game play
		 */
		
		if(def_order==0)
		{

			//ArrayList<ArrayList<String>> newdata = new ArrayList<ArrayList<String>>();
			//addData(data_refined_first_game, newdata);

			findFrequencyForGroup(users_refined, data_refined_first_game, ngames, gameinstance0, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_first_game);

			
			/**
			 * append previous data to the cur data
			 */
			
			
			
			
			//addData(data_refined_second_game, newdata);
			
			findFrequencyForGroup(users_refined, data_refined_second_game, ngames, gameinstance1, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_second_game);

			/**
			 * append previous data to the cur data
			 */
			
			//addData(data_refined_third_game, newdata);

			findFrequencyForGroup(users_refined, data_refined_third_game, ngames, gameinstance2, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_third_game);


			
			
			
			
			
			/**
			 * append previous data to the cur data
			 */
			
			//newdata = new ArrayList<ArrayList<String>>();
			
			//addData( data_refined_fourth_game, newdata);

			findOmegaForGroup(users_refined, data_refined_fourth_game, ngames, gameinstance3, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_fourth_game);

			/**
			 * append previous data to the cur data
			 */
			
			//addData( data_refined_fifth_game, newdata);
			
			findOmegaForGroup(users_refined, data_refined_fifth_game, ngames, gameinstance4, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_fifth_game);

			/**
			 * append previous data to the cur data
			 */
			//addData(data_refined_sixth_game, newdata);
			
			findOmegaForGroup(users_refined, data_refined_sixth_game, ngames, gameinstance5, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_sixth_game);

		}
		else if(def_order==1)
		{
			//ArrayList<ArrayList<String>> newdata = new ArrayList<ArrayList<String>>();
			//addData(data_refined_first_game, newdata);

			findOmegaForGroup(users_refined, data_refined_first_game, ngames, gameinstance0, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_first_game);

			
			/**
			 * append previous data to the cur data
			 */
			
			
			
			
			//addData(data_refined_second_game, newdata);
			
			findOmegaForGroup(users_refined, data_refined_second_game, ngames, gameinstance1, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_second_game);

			/**
			 * append previous data to the cur data
			 */
			
			//addData(data_refined_third_game, newdata);

			findOmegaForGroup(users_refined, data_refined_third_game, ngames, gameinstance2, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_third_game);


			
			
			
			
			
			/**
			 * append previous data to the cur data
			 */
			
			//newdata = new ArrayList<ArrayList<String>>();
			
			//addData( data_refined_fourth_game, newdata);

			findFrequencyForGroup(users_refined, data_refined_fourth_game, ngames, gameinstance3, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_fourth_game);

			/**
			 * append previous data to the cur data
			 */
			
			//addData( data_refined_fifth_game, newdata);
			
			findFrequencyForGroup(users_refined, data_refined_fifth_game, ngames, gameinstance4, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_fifth_game);

			/**
			 * append previous data to the cur data
			 */
			//addData(data_refined_sixth_game, newdata);
			
			findFrequencyForGroup(users_refined, data_refined_sixth_game, ngames, gameinstance5, data,
					noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_sixth_game);
		}

		// for each of the user groups compute lambda


	}
	
	
	
	public static void computeOmegaSUQRBatchJobTrending(int k, int def_order, int depthlimit, int featureset, int game_type, int gameinstance,
			double minw1, double maxw1, double minw2, double maxw2,
			double minw3, double maxw3, double minw4, double maxw4, double minw5, double maxw5, 
			double w1step, double w2step, double w3step, double w4step, double w5step) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .1;
		double lambda = 1;//generateLambdaArray(minlambda, maxlambda, step);


		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		//int def_order = 0; //asc 1,2,3 random def.... 4,5,6 stratgic def
		//int game_type = 1;


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);


		// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);
		ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		ArrayList<ArrayList<String>>  data_refined_sixth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance5, def_order);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsers(users_refined, data_refined_first_game, ngames, gameinstance0, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);

	//	int k = 0;
		
		
		/**
		 * find frequency for the first three game play
		 */
		
		if(def_order==0)
		{

			//ArrayList<ArrayList<String>> newdata = new ArrayList<ArrayList<String>>();
			//addData(data_refined_first_game, newdata);
			
			
			if(gameinstance==1)
			{

				findFrequencyForGroup(users_refined, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_first_game);
			}
			else if(gameinstance==2)
			{
				//addData(data_refined_second_game, newdata);

				findFrequencyForGroup(users_refined, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_second_game);
			}
			else if(gameinstance==3)
			{
				//addData(data_refined_third_game, newdata);

				findFrequencyForGroup(users_refined, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_third_game);
			}
			else if(gameinstance==4)
			{
				findOmegaForGroupBatchJob(users_refined, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_fourth_game,
						minw1, maxw1, minw2, maxw2, minw3, maxw3, minw4, maxw4, minw5, maxw5, w1step, w2step, w3step, w4step, w5step);
			}
			else if(gameinstance==5)
			{
				findOmegaForGroupBatchJob(users_refined, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_fifth_game,
						minw1, maxw1, minw2, maxw2, minw3, maxw3, minw4, maxw4, minw5, maxw5, w1step, w2step, w3step, w4step, w5step);

			}
			else if(gameinstance==6)
			{
				findOmegaForGroupBatchJob(users_refined, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_sixth_game,
						minw1, maxw1, minw2, maxw2, minw3, maxw3, minw4, maxw4, minw5, maxw5, w1step, w2step, w3step, w4step, w5step);
			}

			

		}
		else if(def_order==1)
		{
			
			if(gameinstance==1)
			{

				findOmegaForGroupBatchJob(users_refined, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_first_game,
						minw1, maxw1, minw2, maxw2, minw3, maxw3, minw4, maxw4, minw5, maxw5, w1step, w2step, w3step, w4step, w5step);
			}
			else if(gameinstance==2)
			{
				//addData(data_refined_second_game, newdata);

				findOmegaForGroupBatchJob(users_refined, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_second_game,
						minw1, maxw1, minw2, maxw2, minw3, maxw3, minw4, maxw4, minw5, maxw5, w1step, w2step, w3step, w4step, w5step);
			}
			else if(gameinstance==3)
			{
				//addData(data_refined_third_game, newdata);

				findOmegaForGroupBatchJob(users_refined, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_third_game,
						minw1, maxw1, minw2, maxw2, minw3, maxw3, minw4, maxw4, minw5, maxw5, w1step, w2step, w3step, w4step, w5step);
			}
			else if(gameinstance==4)
			{
				findFrequencyForGroup(users_refined, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_fourth_game);
			}
			else if(gameinstance==5)
			{
				findFrequencyForGroup(users_refined, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_fifth_game);

			}
			else if(gameinstance==6)
			{
				findFrequencyForGroup(users_refined, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_sixth_game);
			}
			
			//ArrayList<ArrayList<String>> newdata = new ArrayList<ArrayList<String>>();
			//addData(data_refined_first_game, newdata);

			/*findOmegaForGroup(users_refined, data_refined_first_game, ngames, gameinstance0, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, data_refined_first_game);*/

			
			
		}

		// for each of the user groups compute lambda


	}
	
	
	
	
	public static void userPointsTrending(int k, int def_order, int depthlimit, int featureset, int level, int game_type) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);


		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		//int def_order = 0; //asc 1,2,3 random def.... 4,5,6 stratgic def
		//int game_type = 1;


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);


		// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);
		ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		ArrayList<ArrayList<String>>  data_refined_sixth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance5, def_order);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsers(users_refined, data_refined_first_game, ngames, gameinstance0, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);

	//	int k = 0;
		
		
		/**
		 * find frequency for the first three game play
		 */
		
		if(def_order==0)
		{

			ArrayList<ArrayList<String>> newdata = new ArrayList<ArrayList<String>>();
			addData(data_refined_first_game, newdata);

			trendUserPointsFreq(users_refined, data_refined_first_game, ngames, gameinstance0, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);

			
			/**
			 * append previous data to the cur data
			 */
			
			
			
			
			addData(data_refined_second_game, newdata);
			
			trendUserPointsFreq(users_refined, data_refined_second_game, ngames, gameinstance1, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);

			/**
			 * append previous data to the cur data
			 */
			
			addData(data_refined_third_game, newdata);

			trendUserPointsFreq(users_refined, data_refined_third_game, ngames, gameinstance2, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);


			
			
			
			
			
			/**
			 * append previous data to the cur data
			 */
			
			newdata = new ArrayList<ArrayList<String>>();
			
			addData( data_refined_fourth_game, newdata);

			trendUserPointsLambda(users_refined, data_refined_fourth_game, ngames, gameinstance3, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);

			/**
			 * append previous data to the cur data
			 */
			
			addData( data_refined_fifth_game, newdata);
			
			trendUserPointsLambda(users_refined, data_refined_fifth_game, ngames, gameinstance4, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);

			/**
			 * append previous data to the cur data
			 */
			addData(data_refined_sixth_game, newdata);
			
			trendUserPointsLambda(users_refined, data_refined_sixth_game, ngames, gameinstance5, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);

		}
		else if(def_order==1)
		{
			ArrayList<ArrayList<String>> newdata = new ArrayList<ArrayList<String>>();
			addData(data_refined_first_game, newdata);

			trendUserPointsLambda(users_refined, data_refined_first_game, ngames, gameinstance0, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);

			
			/**
			 * append previous data to the cur data
			 */
			
			
			
			
			addData(data_refined_second_game, newdata);
			
			trendUserPointsLambda(users_refined, data_refined_second_game, ngames, gameinstance1, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);

			/**
			 * append previous data to the cur data
			 */
			
			addData(data_refined_third_game, newdata);

			trendUserPointsLambda(users_refined, data_refined_third_game, ngames, gameinstance2, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);


			
			
			
			
			
			/**
			 * append previous data to the cur data
			 */
			
			newdata = new ArrayList<ArrayList<String>>();
			
			addData( data_refined_fourth_game, newdata);

			trendUserPointsFreq(users_refined, data_refined_fourth_game, ngames, gameinstance3, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);

			/**
			 * append previous data to the cur data
			 */
			
			addData( data_refined_fifth_game, newdata);
			
			trendUserPointsFreq(users_refined, data_refined_fifth_game, ngames, gameinstance4, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);

			/**
			 * append previous data to the cur data
			 */
			addData(data_refined_sixth_game, newdata);
			
			trendUserPointsFreq(users_refined, data_refined_sixth_game, ngames, gameinstance5, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, k, def_order, featureset, newdata, level);
		}

		// for each of the user groups compute lambda


	}
	
	
	
	private static ArrayList<ArrayList<String>> addData(ArrayList<ArrayList<String>> src1,
			ArrayList<ArrayList<String>> dest) {
		
		
		
		//ArrayList<ArrayList<String>> newdata = new ArrayList<ArrayList<String>>();
		
		for(ArrayList<String> exmple: src1)
		{
			dest.add(exmple);
		}
		
		
		
		
		return dest;
		
		
	}

	/**
	 * finds  clusters based on the first game's data then tracks how those groups behaves in the next games
	 * @param k
	 * @param featureset 
	 * @param gameinsforcluster 
	 * @throws Exception
	 */
	public static void trackUsersPerformanceQR(int k, int def_order, int depthlimit, int featureset, int game_type, int gameinsforcluster) throws Exception {




		/*File file = new File("lambda.csv"); //filepath is being passes through //ioc         //and filename through a method 

		
		if (file.exists()) 
		{
			file.delete(); //you might want to check if delete was successfull
		}*/


		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);


		//int def_order = 0; // 0 means asc 1,2,3 random def.... 4,5,6 stratgic def
		//int game_type = 1;
		
		
		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);

		
		// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);
		ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		ArrayList<ArrayList<String>>  data_refined_sixth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance5, def_order);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsers(users_refined, data_refined_first_game, ngames, gameinstance0, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);

	//	int k = 0;
		
		
		/**
		 * now cluster users based on personality score
		 */
		
		
		double[][] examples = null;
		ArrayList<ArrayList<String>>  data_forclustering  = null;
		
		if(gameinsforcluster==1)
		{
			data_forclustering = data_refined_first_game;
		}
		else if(gameinsforcluster==2)
		{
			data_forclustering = data_refined_second_game;
		}
		else if(gameinsforcluster==3)
		{
			data_forclustering = data_refined_third_game;
		}
		else if(gameinsforcluster==4)
		{
			data_forclustering = data_refined_fourth_game;
		}
		else if(gameinsforcluster==5)
		{
			data_forclustering = data_refined_fifth_game;
		}
		else if(gameinsforcluster==6)
		{
			data_forclustering = data_refined_sixth_game;
		}
		
		
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointAdaptive(data_forclustering, users_refined, gameinsforcluster, def_order);
		}
		else
		{
			examples = prepareExamplesDTScorePointsOneGame(data_forclustering, users_refined, gameinsforcluster, def_order);
		}
		
		
		
		
		/*//double[][] examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance0, def_order);
		
		double[][] examples = prepareExamplesDTScorePointsOneGame(data_refined_first_game, users_refined, gameinstance0, def_order);
		
		//double [][] examples = prepareFrquenceyOneGame(data_refined_first_game, users_refined, numberofnodes, gameinstance0, def_order);
*/
		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);

		//int k= 2;

		List<Integer>[] clusters = null;
		
		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}
		
		
		for(int cluster = 0; cluster<clusters.length; cluster++)
		{

			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);
			//ArrayList<String> users_groups = users_refined;//getUserGroup(clusters[cluster], users_refined);


			/**
			 * find frequency for the first three game play
			 */
			
			
			if(def_order==0)
			{
				
				//first 3 games against random def

				findFrequencyForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);


				findFrequencyForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);


				
				
				//next 3 games against intelligent def

				double la = findLambdaForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				
				/*findOmegaForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				
				
				

				la = findLambdaForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				
				/*findOmegaForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				
				

				la = findLambdaForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				/*findOmegaForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				
				
				
			}
			else if(def_order == 1)
			{
				double la = findLambdaForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				
				/*findOmegaForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				

				la = findLambdaForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				/*findOmegaForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				


				la = findLambdaForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				/*findOmegaForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/


				
				
				
				
				

				findFrequencyForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
			}
			
			//break;
			

		}

		// for each of the user groups compute lambda


	}
	
	
	public static void trackDarkTriadPerformanceQR(int k, int def_order, int depthlimit, int personality, int game_type) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);


		//int def_order = 0; // 0 means asc 1,2,3 random def.... 4,5,6 stratgic def
		//int game_type = 1;
		
		
		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);

		
		// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);
		ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		ArrayList<ArrayList<String>>  data_refined_sixth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance5, def_order);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsers(users_refined, data_refined_first_game, ngames, gameinstance0, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);
		
		System.out.println("Number of refined users "+ users_refined.size());
		
		if(personality<=2 && personality>=0)
		{

			/**
			 * now refine users more based on personality score
			 */

			HashMap<String, Double> dtpoints = new HashMap<String, Double>();

			double [][] dtscores = computeDTScores(users_refined, personality, data_refined_first_game);

			// sort the users

			sortDTUsersDescD(dtscores);

			System.out.println("scores after sorting personality  "+ personality);

			printUsers(users_refined, dtscores);

			// compute mean

			double meanscore = computeMedian(dtscores);// //computeMeanScore(dtscores);

			System.out.println("mean score for peronality "+ personality + ", score "+ meanscore);



			// remove users with score less than mean

			ArrayList<String> sorted_users = removeUsersLessThanMean(dtscores, users_refined, meanscore, dtpoints);


			users_refined = sorted_users;


			System.out.println("number of users above mean dt score "+ users_refined.size());

		}
		else if(personality==3)
		{
			/**
			 * now refine users more based on personality score
			 */

			HashMap<String, Double> dtpoints = new HashMap<String, Double>();

			double [][] dtscores = computeAllDTScores(users_refined, personality, data_refined_first_game);

			// sort the users

			//sortDTUsersDescD(dtscores);

			//System.out.println("scores after sorting personality  "+ personality);

			//printUsers(users_refined, dtscores);

			// compute mean

			double[] meanscore = computeAllMeanScore(dtscores);

			System.out.println("mean score for peronality "+ personality + ", mscore "+ meanscore[0]+ ", nscore "+ meanscore[1]+ ", pscore "+ meanscore[2]);



			// remove users with score less than mean

			ArrayList<String> sorted_users = removeUsersNotAroundMean(dtscores, users_refined, meanscore, dtpoints);


			users_refined = sorted_users;


			System.out.println("number of users around mean dt score "+ users_refined.size());
		}


	//	int k = 0;
		
		
		/**
		 * now cluster users based on personality score
		 */
		
		
		/*double[][] examples = null;
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance0, def_order);
		}
		else
		{
			examples = prepareExamplesDTScorePointsOneGame(data_refined_first_game, users_refined, gameinstance0, def_order);
		}
		
		
		
		
		//double[][] examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance0, def_order);
		
		double[][] examples = prepareExamplesDTScorePointsOneGame(data_refined_first_game, users_refined, gameinstance0, def_order);
		
		//double [][] examples = prepareFrquenceyOneGame(data_refined_first_game, users_refined, numberofnodes, gameinstance0, def_order);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);

		//int k= 2;

		List<Integer>[] clusters = null;
		
		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}*/
		
		
		//for(int cluster = 0; cluster<clusters.length; cluster++)
	//	{

			int cluster = 0;
			ArrayList<String> users_groups = users_refined;//getUserGroup(clusters[cluster], users_refined);
			//ArrayList<String> users_groups = users_refined;//getUserGroup(clusters[cluster], users_refined);


			/**
			 * find frequency for the first three game play
			 */
			
			
			if(def_order==0)
			{
				
				//first 3 games against random def

				findFrequencyForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);


				findFrequencyForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);


				
				
				//next 3 games against intelligent def

				double la = findLambdaForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				
				/*findOmegaForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				
				
				

				la = findLambdaForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				
				/*findOmegaForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				
				

				la = findLambdaForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				/*findOmegaForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				
				
				
			}
			else if(def_order == 1)
			{
				double la = findLambdaForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				
				/*findOmegaForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				

				la = findLambdaForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				/*findOmegaForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				


				la = findLambdaForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				/*findOmegaForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/


				
				
				
				
				

				findFrequencyForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
			}
			
			//break;
			

	//	}

		// for each of the user groups compute lambda


	}
	
	
	
	public static void trackDTStdDevQR(int k, int def_order, int depthlimit, int game_type, double stddev) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);
		//double stddev = 1;


		//int def_order = 0; // 0 means asc 1,2,3 random def.... 4,5,6 stratgic def
		//int game_type = 1;
		
		
		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);

		
		// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);
		ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		ArrayList<ArrayList<String>>  data_refined_sixth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance5, def_order);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsers(users_refined, data_refined_first_game, ngames, gameinstance0, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);
		
		System.out.println("Number of refined users "+ users_refined.size());
		
		
		
			/**
			 * now refine users more based on personality score
			 */

			HashMap<String, double[]> dtpoints = new HashMap<String, double[]>();

			double [][] dtscores = computeAllDTScores(users_refined, data_refined_first_game, dtpoints);

			// sort the users

			//sortDTUsersDescD(dtscores);

			//System.out.println("scores after sorting personality  "+ personality);

			//printUsers(users_refined, dtscores);

			// compute mean

			double[] meanscore = computeAllMeanScore(dtscores);

			System.out.println("mean score for peronality mscore "+ meanscore[0]+ ", nscore "+ meanscore[1]+ ", pscore "+ meanscore[2]);


			List<String>[] clusters = new ArrayList[4];
			HashMap<String, Integer> user_cluster = new HashMap<String, Integer>();
			
			for(int i=0; i<4; i++)
			{
				clusters[i] = new ArrayList<String>();
			}
			
			
			
			
			determineUserclusters(clusters, user_cluster, meanscore, dtpoints, stddev);
			
			for(int i=0; i<4; i++)
			{
				System.out.println("cluster "+ i + ", #user "+ clusters[i].size());
			}
			

			//ArrayList<String> sorted_users = removeUsersNotAroundMean(dtscores, users_refined, meanscore, dtpoints);


			//users_refined = sorted_users;


			//System.out.println("number of users around mean dt score "+ users_refined.size());
		


	
		
		for(int cluster = 0; cluster<clusters.length; cluster++)
		{

			//int cluster = 0;
			ArrayList<String> users_groups = (ArrayList<String>)clusters[cluster];
			
			if(def_order==0)
			{
				
				//first 3 games against random def

				findFrequencyForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);


				findFrequencyForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);


				
				
				//next 3 games against intelligent def

				double la = findLambdaForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				
				/*findOmegaForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				
				
				

				la = findLambdaForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				
				/*findOmegaForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				
				

				la = findLambdaForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				/*findOmegaForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				
				
				
			}
			else if(def_order == 1)
			{
				double la = findLambdaForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				
				/*findOmegaForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				

				la = findLambdaForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				/*findOmegaForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/
				
				
				


				la = findLambdaForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
				/*findOmegaForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);*/


				
				
				
				
				

				findFrequencyForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
			}
			
			//break;
			

		}

		// for each of the user groups compute lambda


	}
	
	
	
	public static void trackDTQRCombined(int k, int depthlimit, int game_type, double stddev, String algorithm) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);
		//double stddev = 1;


		//int def_order = 0; // 0 means asc 1,2,3 random def.... 4,5,6 stratgic def
		//int game_type = 1;
		
		
		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();


		int fgi = -1;
		int lgi = -1;
		int def_order=0;
		int exampleinstances = 15;
		
		if(algorithm.equals("r1") )
		{
			def_order=0;
			fgi= 1;
			lgi= 3;
			ngames=3;

		}
		else if(algorithm.equals("r2"))
		{
			def_order=1;
			fgi= 4;
			lgi=6;
			ngames=3;
		}
		else if(algorithm.equals("s1"))
		{
			def_order=1;
			fgi= 1;
			lgi= 3;
			ngames=3;

		}
		else if(algorithm.equals("s2"))
		{
			def_order=0;
			fgi= 4;
			lgi=6;
			ngames=3;
		}
		
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);
		
		
		
		
		ArrayList<ArrayList<String>>  data_refined = refineDataAdaptiveRange(data,game_type, users_refined, fgi, lgi, def_order);





		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsersRange(users_refined, data_refined, ngames, fgi, lgi, data, noderewards, game_type, roundlimit, def_order, exampleinstances);
		
		System.out.println("Number of refined users "+ users_refined.size());
		
		
		
			/**
			 * now refine users more based on personality score
			 */

			HashMap<String, double[]> dtpoints = new HashMap<String, double[]>();

			double [][] dtscores = computeAllDTScores(users_refined, data_refined, dtpoints);

			// sort the users

			//sortDTUsersDescD(dtscores);

			//System.out.println("scores after sorting personality  "+ personality);

			//printUsers(users_refined, dtscores);

			// compute mean

			double[] meanscore = computeAllMeanScore(dtscores);

			System.out.println("mean score for peronality mscore "+ meanscore[0]+ ", nscore "+ meanscore[1]+ ", pscore "+ meanscore[2]);


			List<String>[] clusters = new ArrayList[3];
			HashMap<String, Integer> user_cluster = new HashMap<String, Integer>();
			
			for(int i=0; i<3; i++)
			{
				clusters[i] = new ArrayList<String>();
			}
			
			
			
			
			determineUserclustersDT(clusters, user_cluster, meanscore, dtpoints, stddev);
			
			for(int i=0; i<3; i++)
			{
				System.out.println("cluster "+ i + ", #user "+ clusters[i].size());
			}
			

			//ArrayList<String> sorted_users = removeUsersNotAroundMean(dtscores, users_refined, meanscore, dtpoints);


			//users_refined = sorted_users;


			//System.out.println("number of users around mean dt score "+ users_refined.size());
		


	
		
		for(int cluster = 0; cluster<clusters.length; cluster++)
		{

			//int cluster = 0;
			ArrayList<String> users_groups = (ArrayList<String>)clusters[cluster];
			
			double la = findLambdaForOneGroupDT(users_groups, data_refined, ngames, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order, fgi, lgi, algorithm, cluster);
			
			//break;
			

		}

		// for each of the user groups compute lambda


	}
	
	
	public static void trackDTQRWOOrder(int k, int depthlimit, int game_type, double stddev, String algorithm, boolean genericgroup) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);
		//double stddev = 1;


		//int def_order = 0; // 0 means asc 1,2,3 random def.... 4,5,6 stratgic def
		//int game_type = 1;
		
		
		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();


		
		
		int fgi = -1;
		int lgi = -1;
		//int def_order=0;
		int exampleinstances = 15;
		
		if(algorithm.equals("r") )
		{
			//def_order=0;
			fgi= 1;
			lgi= 3;
			ngames=3;

		}
		else if(algorithm.equals("s"))
		{
			//def_order=1;
			fgi= 4;
			lgi=6;
			ngames=3;
		}
		
		
		ArrayList<String> users_refined = refineUserAdaptiveWOOrder(data, game_type);
		
		
		
		
		ArrayList<ArrayList<String>>  data_refined = refineDataAdaptiveRangeWOOrder(data,game_type, users_refined, algorithm);





		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);

		
		//removeIncosistentUsers(users_refined, data_refined, exampleinstances, algorithm);
		

		sanitizeUsersRangeWOOrder(users_refined, data_refined, ngames, data, noderewards, game_type, roundlimit, exampleinstances, algorithm);
		
		System.out.println("Number of refined users "+ users_refined.size());
		
		
		
			/**
			 * now refine users more based on personality score
			 */

			HashMap<String, double[]> dtpoints = new HashMap<String, double[]>();

			double [][] dtscores = computeAllDTScores(users_refined, data_refined, dtpoints);

			// sort the users

			//sortDTUsersDescD(dtscores);

			//System.out.println("scores after sorting personality  "+ personality);

			//printUsers(users_refined, dtscores);

			// compute mean

			double[] meanscore = computeAllMeanScore(dtscores);

			System.out.println("mean score for peronality mscore "+ meanscore[0]+ ", nscore "+ meanscore[1]+ ", pscore "+ meanscore[2]);


			List<String>[] clusters = new ArrayList[3];
			HashMap<String, Integer> user_cluster = new HashMap<String, Integer>();
			
			for(int i=0; i<3; i++)
			{
				clusters[i] = new ArrayList<String>();
			}
			
			
			
			
			determineUserclustersDT(clusters, user_cluster, meanscore, dtpoints, stddev);
			
			for(int i=0; i<3; i++)
			{
				System.out.println("cluster "+ i + ", #user "+ clusters[i].size());
			}
			

			//ArrayList<String> sorted_users = removeUsersNotAroundMean(dtscores, users_refined, meanscore, dtpoints);


			//users_refined = sorted_users;


			//System.out.println("number of users around mean dt score "+ users_refined.size());
		


	
		
		for(int cluster = 0; cluster<clusters.length; cluster++)
		{

			//int cluster = 0;
			ArrayList<String> users_groups = (ArrayList<String>)clusters[cluster];
			
			
			if(genericgroup)
			{
				users_groups = users_refined;
			}
			
			
			double la = findLambdaForOneGroupDTWOOrder(users_groups, data_refined, ngames, data,
					noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, algorithm, cluster);
			
			//break;
			if(genericgroup)
			{
				break;
			}
			

		}

		// for each of the user groups compute lambda


	}
	
	
	
	
	
	
	
	
	private static void determineUserclusters(List<String>[] clusters, HashMap<String, Integer> user_cluster,
			double[] meanscore, HashMap<String, double[]> dtpoints, double stddev) {
		// TODO Auto-generated method stub
		
		for(String user: dtpoints.keySet())
		{
			System.out.println("user "+ user);
			double scores[] = dtpoints.get(user);
			int clustindex = getUserCluster(meanscore, scores, stddev);
			System.out.println("cluster "+ clustindex);
			clusters[clustindex].add(user);
			user_cluster.put(user, clustindex);
		}
		
		
	}
	
	
	private static void determineUserclustersDT(List<String>[] clusters, HashMap<String, Integer> user_cluster,
			double[] meanscore, HashMap<String, double[]> dtpoints, double stddev) {
		// TODO Auto-generated method stub
		
		for(String user: dtpoints.keySet())
		{
			System.out.println("user "+ user);
			double scores[] = dtpoints.get(user);
			int clustindex = getUserClusterDT(scores);
			System.out.println("cluster "+ clustindex);
			clusters[clustindex].add(user);
			user_cluster.put(user, clustindex);
		}
		
		
	}
	

	private static int getUserCluster(double[] meanscore, double[] scores, double stddev) {
		
		
		int[] isabove = new int[3];
		int count = 0;
		int index = -1;
		
		System.out.println("meanscore "+ meanscore[0] + ", "+ meanscore[1]+", "+meanscore[2]);
		System.out.println("score "+ scores[0] + ", "+ scores[1]+", "+scores[2]);
		
		for(int i=0; i<scores.length; i++)
		{
			double diff = (scores[i]- meanscore[i]);
			if(diff>=stddev)
			{
				isabove[i] = 1;
				count++;
				index = i;
			}
		}
		
		if(count==1)
		{
			System.out.println("count "+ count);
			return index;
		}
		else if(count>1)
		{
			// choose maximum score and its personlaity
			System.out.println("count "+ count);
			double maxscore = Double.NEGATIVE_INFINITY;
			int maxindex = -1;
			
			for(int i=0; i< isabove.length; i++)
			{
				if(isabove[i]==1 && (maxscore<scores[i]))
				{
					maxscore = scores[i];
					maxindex = i;
				}
			}
			System.out.println("max index "+ maxindex);
			
			return maxindex;
			
			
		}
		else
		{
			//System.out.println("default cluster");
			return 3;
		}
		
		
	}
	
	
private static int getUserClusterDT(double[] scores) {
		
		
		double maxscore = Double.NEGATIVE_INFINITY;
		int maxindex = -1;
		
		for(int i=0; i<scores.length; i++)
		{
			if(maxscore<scores[i])
			{
				maxscore = scores[i];
				maxindex = i;
			}
		}
		
		return maxindex;
		
	}

	private static ArrayList<String> removeUsersLessThanMean(double[][] dtscores, ArrayList<String> users_refined,
			double meanscore, HashMap<String,Double> dtpoints) {
		
		
		ArrayList<String> newusers = new ArrayList<String>();
		
		for(double[] s: dtscores)
		{
			if(s[1]>=meanscore)
			{
				newusers.add(users_refined.get((int)s[0]));
				dtpoints.put(users_refined.get((int)s[0]), s[1]);
			}
		}
		
		return newusers;
	}
	
	
	private static ArrayList<String> removeUsersNotAroundMean(double[][] dtscores, ArrayList<String> users_refined,
			double[] meanscore, HashMap<String,Double> dtpoints) {
		
		
		ArrayList<String> newusers = new ArrayList<String>();
		
		for(double[] s: dtscores)
		{
			
			double[] diff = {0,0,0};
			boolean f = true;
			
			for(int i=0; i<3; i++)
			{
				diff[i] = Math.abs(s[i+1] - meanscore[i]);
				if(diff[i]>.6)
				{
					f = false;
					break;
				}
			}
			
			
			if(f)
			{
				newusers.add(users_refined.get((int)s[0]));
				dtpoints.put(users_refined.get((int)s[0]), s[1]);
			}
		}
		
		return newusers;
	}

	private static double computeMeanScore(double[][] dtscores) {
		
		
		double sum = 0;
		
		for(double s[]: dtscores)
		{
			sum += s[1];
		}
		
		sum /= dtscores.length;
		
		return sum;
	}
	
	
private static double computeMedianScore(double[][] dtscores) {
		
		
		//double sum = 0;
		
		if(dtscores.length%2==0)
		{
			return (dtscores[dtscores.length/2][1] + dtscores[dtscores.length/2 + 1][1])/2;
		}
		else
		{
			return dtscores[dtscores.length/2][1];
		}
		
		//return sum;
	}
	
	
private static double[] computeAllMeanScore(double[][] dtscores) {
		
		
		double[] sum = new double[3];
		
		for(double s[]: dtscores)
		{
			sum[0] += s[1];
			sum[1] += s[2];
			sum[2] += s[3];
		}
		
		sum[0] /= dtscores.length;
		sum[1] /= dtscores.length;
		sum[2] /= dtscores.length;
		
		return sum;
	}

	private static void printUsers(ArrayList<String> users_refined, double[][] stscores) {
		
		
		for(double[] u: stscores)
		{
			System.out.println("user "+ users_refined.get((int)u[0]) + ", score "+ u[1] + ", index "+ u[0]);
		}
		
		
	}

	private static double[][] computeDTScores(ArrayList<String> users_refined, int personality, ArrayList<ArrayList<String>> data_refined_first_game) {
		
		
		double scores[][] = new double[users_refined.size()][2];
		
		int index = 0;
		
		for(String user: users_refined)
		{
			double score = getPersonalityScore(user, data_refined_first_game, personality);
			scores[index][0] = index;
			scores[index][1] = score;
			System.out.println("User "+ user + " , score "+ score + ", index "+ index + ", personality "+ personality);
			index++;
		}
		
		return scores;
	}
	

	
	
	
private static double[][] computeAllDTScores(ArrayList<String> users_refined, int personality, ArrayList<ArrayList<String>> data_refined_first_game) {
		
		
		double scores[][] = new double[users_refined.size()][4];
		
		int index = 0;
		
		for(String user: users_refined)
		{
			double mscore = getPersonalityScore(user, data_refined_first_game, 0);
			double nscore = getPersonalityScore(user, data_refined_first_game, 1);
			double pscore = getPersonalityScore(user, data_refined_first_game, 2);
			
			scores[index][0] = index;
			scores[index][1] = mscore;
			scores[index][2] = nscore;
			scores[index][3] = pscore;
			System.out.println("User "+ user + " , mscore "+ mscore+ " , nscore "+ nscore+ " , pscore "+ pscore + ", index "+ index + ", personality "+ personality);
			index++;
		}
		
		return scores;
	}



private static double[][] computeAllDTScores(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, HashMap<String, double[]> dtpoints) {
	
	
	double scores[][] = new double[users_refined.size()][4];
	
	int index = 0;
	
	for(String user: users_refined)
	{
		double mscore = getPersonalityScore(user, data_refined_first_game, 0);
		double nscore = getPersonalityScore(user, data_refined_first_game, 1);
		double pscore = getPersonalityScore(user, data_refined_first_game, 2);
		
		scores[index][0] = index;
		scores[index][1] = mscore;
		scores[index][2] = nscore;
		scores[index][3] = pscore;
		
		double[] s = {mscore, nscore, pscore};
		
		dtpoints.put(user, s);
		
		//System.out.println("User "+ user + " , mscore "+ mscore+ " , nscore "+ nscore+ " , pscore "+ pscore + ", index "+ index + ", personality "+ personality);
		index++;
	}
	
	return scores;
}
	
	

	/**
	 * find if users switched groups : rational group irrational group
	 * 
	 * 
	 * 
	 * 
	 * @param k
	 * @param featureset 
	 * @throws Exception
	 */
	public static void trackIndivUsersSwitchingQR(int k, int def_order, int depthlimit, int featureset) throws Exception {


/**
 * idea
 * 1. in game 1 group users and find out in which cluster they are : rational or irrational
 * 2. In game 2 group users and find out in which group they are then find out whether they remained in their last group or 
 * changed to another group
 * 3. continue this process until game 6.
 */



		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .01;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);


		//int def_order = 0; // 0 means asc 1,2,3 random def.... 4,5,6 stratgic def
		int game_type = 1;
		
		
		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);

		
		// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);
		ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		ArrayList<ArrayList<String>>  data_refined_sixth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance5, def_order);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsers(users_refined, data_refined_first_game, ngames, gameinstance0, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);

	//	int k = 0;
		
		
		
		/**
		 * 0 switched to rational 
		 * 1 switched to irrational
		 * 2 stayed in their previous group rational
		 * 3 stayed in their previous group irrational
		 *  
		 *  
		 */
		
		/**
		 * now cluster users based on personality score
		 */
		
		ArrayList<ArrayList<String>> data_refined_game = new ArrayList<ArrayList<String>>();
		
		HashMap<Integer, HashMap<String, Integer>> usertracking = new HashMap<Integer, HashMap<String, Integer>>();
		
		for(int gi=0; gi<6; gi++)
		{

			

			if(gi==0)
			{
				data_refined_game = data_refined_first_game;
			}
			else if(gi==1)
			{
				data_refined_game = data_refined_second_game;
			}
			else if(gi==2)
			{
				data_refined_game = data_refined_third_game;
			}
			else if(gi==3)
			{
				data_refined_game = data_refined_fourth_game;
			}
			else if(gi==4)
			{
				data_refined_game = data_refined_fifth_game;
			}
			else if(gi==5)
			{
				data_refined_game = data_refined_sixth_game;
			}
			
			
			double[][] examples = null;
			
			if(featureset==0)
			{
				examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gi+1, def_order);
			}
			else
			{
				examples = prepareExamplesDTScorePointsOneGame(data_refined_first_game, users_refined, gi+1, def_order);
			}


			/*//double[][] examples = prepareExamplesNodeCostPointAdaptive(data_refined_game, users_refined, gi+1, def_order);

			double[][] examples = prepareExamplesDTScorePointsOneGame(data_refined_game, users_refined, gi+1, def_order);*/

			//double [][] examples = prepareFrquenceyOneGame(data_refined_first_game, users_refined, numberofnodes, gameinstance0, def_order);

			printData(users_refined,examples);

			// normalize the data

			double normalizedexamples[][] = normalizeData(examples);

			System.out.println("Normalized data: ");

			printData(users_refined, normalizedexamples);

			//int k= 2;

			List<Integer>[] clusters = null;

			if(k>1)
			{
				clusters = Weka.clusterUsers(k, normalizedexamples);
			}
			else
			{
				clusters = Weka.clusterUsers(normalizedexamples);
			}

			
			double grouplambda [] = new double[2];
			double grouppoints [] = new double[2];
			
			//HashMap<String ,Integer> usercluster = new HashMap<String ,Integer>();
			

			for(int cluster = 0; cluster<clusters.length; cluster++)
			{
				ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);
				if(def_order==0)
				{

					if(gi<6)
					{
						double tmpoints = getUserPointsOneGame(data_refined_game, users_groups, gi+1, def_order);
						grouppoints[cluster] = tmpoints;
					}
					else 
					{
						double la = findLambdaForOneGroup(users_groups, data_refined_game, ngames, gi+1, data,
								noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
						grouplambda[cluster] = la;
					}
					
				}
				else if(def_order == 1)
				{
					if(gi>=0)
					{
						double tmpoints = getUserPointsOneGame(data_refined_game, users_groups, gi+1, def_order);
						grouppoints[cluster] = tmpoints;
					}
					else 
					{
						double la = findLambdaForOneGroup(users_groups, data_refined_game, ngames, gi+1, data,
								noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
						grouplambda[cluster] = la;
					}

				}
			}
			
			/**
			 * now check if the user
			 */
			
			/**
			 * 0 switched to rational 
			 * 1 switched to irrational
			 * 2 stayed in their previous group rational
			 * 3 stayed in their previous group irrational
			 *  
			 *  
			 */
			
			/**
			 * user points
			 */
			
			if(gi<6)
			{

				for(int i=0; i<grouppoints.length; i++)
				{
					System.out.println("gi "+(gi+1)+" group "+ i + " points "+ grouppoints[i]);
				}
			}
			else
			{
				for(int i=0; i<grouppoints.length; i++)
				{
					System.out.println("gi "+(gi+1)+" group "+ i + " lambda "+ grouplambda[i]);
				}
			}
			
			
			HashMap<String, Integer> curusertracking = new HashMap<String, Integer>();
			
			if(def_order==0)
			{

				for(String user_id : users_refined)
				{
					
					
					if(gi==0)
					{
						// just enter in which group they are
						int clusindex = getIndivUserGroup(clusters, users_refined, user_id);
						// either 2 stayed in rational or 3 stayed in irrational
						boolean isrational = isRational(grouppoints, clusindex);
						System.out.println("points clus0 "+ grouppoints[0] + " points clus1 "+ grouppoints[1]);
						System.out.println("userid "+ user_id + ", clus "+ clusindex + ", rational "+ isrational);
						if(isrational)
						{
							curusertracking.put(user_id, AdversaryModel.STAYED_RATIONAL); // stayed rational
						}
						else
						{
							curusertracking.put(user_id, AdversaryModel.STAYED_IRRATIONAL);
						}
					}
					else
					{
						boolean isrational = false;
						int clusindex = getIndivUserGroup(clusters, users_refined, user_id);
						// either 2 stayed in rational or 3 stayed in irrational
						if(gi<6)
						{
							isrational = isRational(grouppoints, clusindex);
						}
						else
						{
							isrational = isRational(grouplambda, clusindex);
						}
						System.out.println("points clus0 "+ grouppoints[0] + " points clus1 "+ grouppoints[1]);
						System.out.println("userid "+ user_id + ", clus "+ clusindex + ", cur rational "+ isrational);
							
							// now decide what did he do in last round
							
							
							int lastrounddecision = getLastRoundDecision(user_id, usertracking.get(gi-1));
							
							
							
							boolean isrationallastround = false;
							
							if(lastrounddecision == AdversaryModel.STAYED_RATIONAL || lastrounddecision == AdversaryModel.SWITCHED_RATIONAL)
							{
								isrationallastround = true;
							}
							
							System.out.println("userid "+ user_id + ", clus "+ clusindex + ", last round rational "+ isrationallastround);
							
							if(isrationallastround && isrational)
							{
								// stayed rational
								curusertracking.put(user_id, AdversaryModel.STAYED_RATIONAL);
								System.out.println("userid "+ user_id + ", clus "+ clusindex + ", cur round decision stayed ratinal "+ AdversaryModel.STAYED_RATIONAL);
								
							}
							else if(!isrationallastround && isrational)
							{
								// swtiched to rational
								curusertracking.put(user_id, AdversaryModel.SWITCHED_RATIONAL);
								System.out.println("userid "+ user_id + ", clus "+ clusindex + ", cur round decision switched ratinal "+ AdversaryModel.SWITCHED_RATIONAL);
								
								
							}
							else if(!isrationallastround && !isrational)
							{
								// stayed rational
								curusertracking.put(user_id, AdversaryModel.STAYED_IRRATIONAL);
								System.out.println("userid "+ user_id + ", clus "+ clusindex + ", cur round decision stayed irratinal "+ AdversaryModel.STAYED_IRRATIONAL);
								
								
							}
							else if(isrationallastround && !isrational)
							{
								// swtiched to rational
								curusertracking.put(user_id, AdversaryModel.SWITCHED_IRRATIONAL);
								System.out.println("userid "+ user_id + ", clus "+ clusindex + ", cur round decision switched irratinal "+ AdversaryModel.SWITCHED_IRRATIONAL);
								
								
							}
							
						
					}
				}
			}
			else
			{
				// need to code when def order 1
			}
			
			
			usertracking.put(gi, curusertracking);
			
			
		}
		
		
		int totaluser = users_refined.size();
		
		
			
			
		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("indivusertrend.csv"),true));

			pw.append("gameinstance,stayed_rational,switch_rational,stayed_irrational,sw_irrational"+ "\n");


			for(int gi=0; gi<6; gi++)
			{
				double stayedrational = getStat(usertracking.get(gi), totaluser, AdversaryModel.STAYED_RATIONAL);
				double stayedirational = getStat(usertracking.get(gi), totaluser, AdversaryModel.STAYED_IRRATIONAL);
				double swrational = getStat(usertracking.get(gi), totaluser, AdversaryModel.SWITCHED_RATIONAL);
				double swirrational = getStat(usertracking.get(gi), totaluser, AdversaryModel.SWITCHED_IRRATIONAL);
				System.out.print("game "+ gi + " S_R "+ stayedrational + ", ");
				System.out.print(" S_IR "+ stayedirational + ", ");
				System.out.print(" SW_R "+ swrational + ", ");
				System.out.print(" SW_IR "+ swirrational);
				System.out.println();
				
				pw.append((gi+1)+","+stayedrational +","+ swrational + ","+ stayedirational +","+swirrational+"\n");
				

			}




			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}
			
			
			
		

		


	}
	
	
	private static double getStat(HashMap<String, Integer> hashMap, int totaluser, int stayedRational) {
		
		
		double count = 0;
		for(String user: hashMap.keySet())
		{
			if(hashMap.get(user).equals(stayedRational))
			{
				count++;
			}
		}
		double tmp = count/totaluser;
		return tmp*100.0;
	}

	private static int getLastRoundDecision(String user_id, HashMap<String, Integer> usertrack) {
		
		
		return usertrack.get(user_id);
	}

	private static boolean isRational(double[] grouppoints, int clusindex) {
		
		
		double val = grouppoints[clusindex];
		for(double v: grouppoints)
		{
			if((v != val) && (val<v))
				return false;
		}
		return true;
	}

	/**
	 * finds  clusters based on the first game's data then tracks how those groups behaves in the next games
	 * @param k
	 * @param depthlimit 
	 * @param def_order 
	 * @param featureset 
	 * @throws Exception
	 */
	public static void trackUsersPerformanceSUQR(int k, int def_order, int depthlimit, int featureset, int gameinsforcluster) throws Exception {






		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .01;
		double maxlambda = .5;
		double step = .05;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);


		// how many clusters you want
		int numberofnodes = 6;

		int gameinstance0 = 1; // the game they played first
		int gameinstance1 = 2;  // the game that they played next againt intelligent def
		int gameinstance2 = 3; // the game they played first
		int gameinstance3 = 4;  // the game that they played next againt intelligent def
		int gameinstance4 = 5; // the game they played first
		int gameinstance5 = 6;  // the game that they played next againt intelligent def

		//int def_order = 0; // 0 means asc 1,2,3 random def.... 4,5,6 stratgic def
		int game_type = 1;


		int ngames = 1;
		int roundlimit = 5;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender


		// get the users who played 1st game instance
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, game_type);

		
		// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);
		ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		ArrayList<ArrayList<String>>  data_refined_sixth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance5, def_order);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsers(users_refined, data_refined_first_game, ngames, gameinstance0, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);

	//	int k = 0;
		
		
		/**
		 * now cluster users based on personality score
		 */
		
		
		//double[][] examples = null;
		
		double[][] examples = null;
		ArrayList<ArrayList<String>>  data_forclustering  = null;
		
		if(gameinsforcluster==1)
		{
			data_forclustering = data_refined_first_game;
		}
		else if(gameinsforcluster==2)
		{
			data_forclustering = data_refined_second_game;
		}
		else if(gameinsforcluster==3)
		{
			data_forclustering = data_refined_third_game;
		}
		else if(gameinsforcluster==4)
		{
			data_forclustering = data_refined_fourth_game;
		}
		else if(gameinsforcluster==5)
		{
			data_forclustering = data_refined_fifth_game;
		}
		else if(gameinsforcluster==6)
		{
			data_forclustering = data_refined_sixth_game;
		}
		
		
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointAdaptive(data_forclustering, users_refined, gameinsforcluster, def_order);
		}
		else
		{
			examples = prepareExamplesDTScorePointsOneGame(data_forclustering, users_refined, gameinsforcluster, def_order);
		}
		
		
		
		
		/*double[][] examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance0, def_order);*/
		
		//double[][] examples = prepareExamplesDTScorePointsOneGame(data_refined_first_game, users_refined, gameinstance0, def_order);
		
		//double [][] examples = prepareFrquenceyOneGame(data_refined_first_game, users_refined, numberofnodes, gameinstance0, def_order);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);

		//int k= 2;

		List<Integer>[] clusters = null;
		
		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}
		
		
		for(int cluster = 0; cluster<clusters.length; cluster++)
		{

			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			/**
			 * find frequency for the first three game play
			 */
			
			double la =1;
			
			if(def_order==0)
			{
				
				//first 3 games against random def

				findFrequencyForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);


				findFrequencyForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);


				
				
				//next 3 games against intelligent def

				/*double la = findLambdaForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);*/
				
				findOmegaForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);
				
				
				
				
				

				/*la = findLambdaForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);*/
				
				findOmegaForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);
				
				
				
				

				/*la = findLambdaForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);*/
				findOmegaForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);
				
				
				
				
				
			}
			else if(def_order == 1)
			{
				/*double la = findLambdaForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);*/
				
				findOmegaForOneGroup(users_groups, data_refined_first_game, ngames, gameinstance0, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);
				
				
				

				/*findLambdaForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);*/
				findOmegaForOneGroup(users_groups, data_refined_second_game, ngames, gameinstance1, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);
				
				
				


				/*findLambdaForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);*/
				findOmegaForOneGroup(users_groups, data_refined_third_game, ngames, gameinstance2, data,
						noderewards, game_type, roundlimit, numberofnodes, step, naction, DEPTH_LIMIT, cluster, def_order, la);


				
				
				
				
				

				findFrequencyForOneGroup(users_groups, data_refined_fourth_game, ngames, gameinstance3, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_fifth_game, ngames, gameinstance4, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);

				findFrequencyForOneGroup(users_groups, data_refined_sixth_game, ngames, gameinstance5, data,
						noderewards, game_type, roundlimit, numberofnodes, lambda, step, naction, DEPTH_LIMIT, cluster, def_order);
			}

		}

		// for each of the user groups compute lambda


	}
	
	
	
	


	private static void findLambdaForGroup(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, int DEPTH_LIMIT, int k, int deforder, int featureset, ArrayList<ArrayList<String>> newdata) throws Exception {
		




		/**
		 * cluster based on the first play
		 * then measure their rationality ? frequency may be
		 * after that measure their ratinality in second game play
		 */
		
		double[][] examples = null;
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointAdaptiveProgressive(data_refined_first_game, users_refined, gameinstance, deforder);
			
			//examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		else
		{
			examples = prepareExamplesDTScorePointsOneGame(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		//printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		//System.out.println("Normalized data: ");

		//printData(users_refined, normalizedexamples);
		
		List<Integer>[] clusters = null;

		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);









		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance, deforder);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);
			
			//HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");



			double[] estimatedlambdanaive = estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			
			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(gameinstance+","+cluster+","+users_groups.size()+","+ estimatedlambdanaive[0]+","+ estimatedlambdanaive[1]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		}
		
	}
	
	
	private static void findLambdaForGroupRange(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance0, int gameinstance1, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, int DEPTH_LIMIT,
			int k, int deforder, int featureset, String algorithm) throws Exception {
		




		/**
		 * cluster based on the first play
		 * then measure their rationality ? frequency may be
		 * after that measure their ratinality in second game play
		 */
		
		double[][] examples = null;
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointRange(data_refined_first_game, users_refined, gameinstance0, gameinstance1, deforder, ngames);
			
			//examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		else
		{
			examples = prepareExamplesDTScorePointsRange(data_refined_first_game, users_refined, gameinstance0, gameinstance1, deforder, ngames);
		}
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		//printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		//System.out.println("Normalized data: ");

		//printData(users_refined, normalizedexamples);
		
		List<Integer>[] clusters = null;

		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);









		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay =  createGamePlayRange(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptiveRange(users_groups, data_refined_first_game, numberofnodes, gameinstance0, gameinstance1, deforder);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);
			
			
			//HashMap<String, int[]> defendfrequency = getAttackCountInDataDefender(gameplay, numberofnodes, 5);
			
			
			
			//HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);
			
			
			
			
			

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree


			HashMap<String, HashMap<String, Double>> defstrategy = null;


			if(algorithm.startsWith("s"))
			{
				defstrategy = Data.readStrategy("g5d5_FI.txt");
			}
			else if(algorithm.startsWith("r"))
			{
				HashMap<String, double[]> defendfrequency = getDefFreqInData(gameplay, numberofnodes, 5);
				defstrategy = convertFormatD(defendfrequency);
			}



			double[] estimatedlambdanaive = estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			
			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(algorithm+","+cluster+","+users_groups.size()+","+ estimatedlambdanaive[0]+","+ estimatedlambdanaive[1]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		}
		
	}
	
	
	private static void findLambdaForGroupRangeWOOrder(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, int DEPTH_LIMIT,
			int k, int featureset, String algorithm, boolean generic) throws Exception {
		




		/**
		 * cluster based on the first play
		 * then measure their rationality ? frequency may be
		 * after that measure their ratinality in second game play
		 */
		
		double[][] examples = null;
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointWOOrder(data_refined_first_game, users_refined, ngames, algorithm);
			
			//examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		else
		{
			examples = prepareExamplesDTScorePointsWOOrder(data_refined_first_game, users_refined, ngames, algorithm);
		}
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		//printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		//System.out.println("Normalized data: ");

		//printData(users_refined, normalizedexamples);
		
		List<Integer>[] clusters = null;

		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);









		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			if(generic) {
			
				users_groups = users_refined;
			}

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlayRange(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptiveWOOrder(users_groups, data_refined_first_game, numberofnodes, algorithm);


			int[][] roundfreq = new int[5][6];
			
			
			getPlayByRound(roundfreq, gameplay);
			
			
			printRoundFreq(roundfreq);



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			//HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);
			
			
			//HashMap<String, int[]> defendfrequency = getAttackCountInDataDefender(gameplay, numberofnodes, 5);
			
			
			
			HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);
			
			//printAttackerStrategy(attackfrequency, cluster);
			
			//checkConsistency(attackfrequency);
			
			
			

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree


			HashMap<String, HashMap<String, Double>> defstrategy = null;


			if(algorithm.startsWith("s"))
			{
				defstrategy = Data.readStrategy("g5d5_FI.txt");
			}
			else if(algorithm.startsWith("r"))
			{
				HashMap<String, double[]> defendfrequency = getDefFreqInData(gameplay, numberofnodes, 5);
				defstrategy = convertFormatD(defendfrequency);
			}


			/**
			 * use the int[] version. Divide the sum og llv by the total number of samples
			 */
			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			
			double[] estimatedlambdanaive = {0,0};//estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			
			
			
			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			double sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			//System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(algorithm+","+cluster+","+users_groups.size()+","+ estimatedlambdanaive[0]+","+ estimatedlambdanaive[1]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					double f = (c/sumattackcoutn)*100.0;
					pw.append(f+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}
			if(generic)
			{

				break;
			}

		}
		
	}
	
	
	private static void checkConsistency(HashMap<String, double[]> attackfrequency) {
		
		int count = 0;
		
		for(String seq: attackfrequency.keySet())
		{
			String [] s = seq.split(" ");
			String a = s[1];
			String d = s[0];
			boolean ok = true;
			if(a.length()==7)
			{
				double str[] = attackfrequency.get(seq);
				
				double su= 0;
				
				for(double x: str)
				{
					su += x;
				}
				if(su>0)
				{
					count ++;
				}
				
				
				//check if exists in strat
				
				String[] at = a.split(",");
				String[] df = d.split(",");
				//System.out.println("\nDef seq "+ d);
				//System.out.println("Att seq "+ a);
				for(int i=0; i<at.length; i++)
				{
					String subseq = "";
					String s1 = "";
					String s2 = "";
					for(int j=0; j<(i+1); j++)
					{
						s1 += df[i];
						s2 += at[i];
						if(j!=i)
						{
							s1+= ",";
							s2+= ",";
						}
					}
					subseq = s1 +" "+ s2;
					//System.out.println("Subseq "+ subseq);
					if(attackfrequency.containsKey(subseq))
					{
						//System.out.println("Subseq exists");
					}
					else
					{
						ok = false;
						break;
						//System.out.println("Subseq does not exist");
					}
				}
				if(!ok)
				{
					
					//System.out.println("\nNot ok\nDef seq "+ d);
					//System.out.println("Att seq "+ a);
				}
				
			}
		}
		System.out.println("count "+ count);
		
	}

	private static void printAttackerStrategy(HashMap<String, double[]> attackfrequency, int cluster) {
		
		try 
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("attackerstrategy"+cluster+".txt"),true));
			
			for(String key: attackfrequency.keySet())
			{
				//System.out.println(key);
				
				double[] s = attackfrequency.get(key);
				
				double sum = 0;
				
				
				for(double x: s)
				{
					sum+=x;
				}
				
				if(sum==0)
				{
					/*s[5] = 5;
					pw.append("Sequence: "+key+"\n");
					int node = 0;
					pw.append("Strategy:\n");
					for(double p: s)
					{
						pw.append(node+": "+p+"\n");
						node++;
					}*/
				}
				else if(sum>0)
				{
					pw.append("Sequence: "+key+"\n");
					int node = 0;
					pw.append("Strategy:\n");
					for(double p: s)
					{
						pw.append(node+": "+p+"\n");
						node++;
					}
				}
			}
			
			
			pw.close();
			
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void computeDefBR(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, int DEPTH_LIMIT,
			int k, int featureset, String algorithm, boolean genericgroup) throws Exception {
		




		/**
		 * cluster based on the first play
		 * then measure their rationality ? frequency may be
		 * after that measure their ratinality in second game play
		 */
		
		double[][] examples = null;
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointWOOrder(data_refined_first_game, users_refined, ngames, algorithm);
			
			//examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		else
		{
			examples = prepareExamplesDTScorePointsWOOrder(data_refined_first_game, users_refined, ngames, algorithm);
		}
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		//printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		//System.out.println("Normalized data: ");

		//printData(users_refined, normalizedexamples);
		
		List<Integer>[] clusters = null;

		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);









		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);

			
			if(genericgroup)
			{

				users_groups = users_refined;
			}

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlayRange(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptiveWOOrder(users_groups, data_refined_first_game, numberofnodes, algorithm);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);
			
			
			//HashMap<String, int[]> defendfrequency = getAttackCountInDataDefender(gameplay, numberofnodes, 5);
			
			
			
			//HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);
			
			
			

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree


			HashMap<String, HashMap<String, Double>> defstrategy = null;


			if(algorithm.startsWith("s"))
			{
				defstrategy = Data.readStrategy("g5d5_FI.txt");
			}
			else if(algorithm.startsWith("r"))
			{
				HashMap<String, double[]> defendfrequency = getDefFreqInData(gameplay, numberofnodes, 5);
				defstrategy = convertFormatD(defendfrequency);
			}


			/**
			 * use the int[] version. Divide the sum og llv by the total number of samples
			 */
			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			
			double defbestres = defBestResponseValue(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			
			
			
			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			//System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(algorithm+","+cluster+","+users_groups.size()+","+ defbestres+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}

			if(genericgroup)
			{
				break;
			}

		}
		
	}
	
	
	
	
	
	private static HashMap<String, HashMap<String, Double>> convertFormat(HashMap<String, int[]> defendfrequency) {
		
		
		HashMap<String, HashMap<String, Double>> strat = new HashMap<String, HashMap<String, Double>>();
		
		for(String key: defendfrequency.keySet())
		{
			int [] val = defendfrequency.get(key);
			
			int sum = 0;
			
			for(int c: val)
			{
				sum += c;
			}

			 HashMap<String, Double> str = new  HashMap<String, Double>();
			
			if(sum!=0)
			{

				for(int i=0; i<6; i++)
				{

					String k = i+"";
					
					double v = val[i]/sum;
					
					str.put(k, v);
					

				}
			}
			else
			{
				for(int i=0; i<6; i++)
				{

					String k = i+"";
					
					double v = 0;
					
					if(i==0)
						v=1;
					
					str.put(k, v);
					

				}
			}
			strat.put(key, str);
		}
		
		
		
		
		//for()
		
		
		return strat;
	}
	
	
private static HashMap<String, HashMap<String, Double>> convertFormatD(HashMap<String, double[]> defendfrequency) {
		
		
		HashMap<String, HashMap<String, Double>> strat = new HashMap<String, HashMap<String, Double>>();
		
		for(String key: defendfrequency.keySet())
		{
			double [] val = defendfrequency.get(key);
			
			

			 HashMap<String, Double> str = new  HashMap<String, Double>();
			
			
				for(int i=0; i<6; i++)
				{

					String k = i+"";
					
					double v = val[i];
					
					str.put(k, v);
					

				}
			
			strat.put(key, str);
		}
		
		
		
		
		//for()
		
		
		return strat;
	}


	private static void findOmegaForGroup(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double lambda, double step, int naction, int DEPTH_LIMIT, int k, int deforder, int featureset, ArrayList<ArrayList<String>> newdata) throws Exception {
		



		
		double minw1 = -10;
		double maxw1 = -8;
		double stepw1 = .5;


		double minw2 = -2;
		double maxw2 = 2;
		double stepw2 = .1;


		double minw3 = -2;
		double maxw3 = 2;
		double stepw3 = .1;


		double minw4 = -10;
		double maxw4 = 10;
		double stepw4 = 0.5;




		double[] w1 = generateAlphaArray(minw1, maxw1, stepw1);


		double[] w2 = generateBetaArray(minw2, maxw2, stepw2);


		double[] w3 = generateBetaArray(minw3, maxw3, stepw3);


		double[] w4 = generateGammaArray(minw4, maxw4, stepw4);


		/**
		 * cluster based on the first play
		 * then measure their rationality ? frequency may be
		 * after that measure their ratinality in second game play
		 */
		
		double[][] examples = null;
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointAdaptiveProgressive(newdata, users_refined, gameinstance, deforder);
			
			//examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		else
		{
			examples = prepareExamplesDTScorePointsOneGame(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);
		
		List<Integer>[] clusters = null;

		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);









		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance, deforder);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);
			
			//HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");



			//double[] estimatedlambdanaive = estimateLambdaNaiveBinarySDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			double[] estimatedw = estimateOmegaNaiveParallel(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, w1, w2, w3, w4);
			
			
			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			//System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("suqr.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(gameinstance+","+cluster+","+users_groups.size()+","+ estimatedw[4]+","+ estimatedw[0]+","+ estimatedw[1]+","+ estimatedw[2]+","+ estimatedw[3]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		}
		
	}
	
	
	
	private static void findOmegaForGroupBatchJob(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, 
			double lambda, double step, int naction, int DEPTH_LIMIT, int k, int deforder, int featureset, ArrayList<ArrayList<String>> newdata,
			double minw1, double maxw1, double minw2, double maxw2,
			double minw3, double maxw3, double minw4, double maxw4, double minw5, double maxw5, double w1step, double w2step, double w3step, double w4step, double w5step) throws Exception {
		



		//step = 0.1;

		double[] w1 = generateAlphaArray(minw1, maxw1, w1step);


		double[] w2 = generateBetaArray(minw2, maxw2, w2step);


		double[] w3 = generateBetaArray(minw3, maxw3, w3step);


		double[] w4 = generateGammaArray(minw4, maxw4, w4step);
		
		//double[] w5 = generateGammaArray(minw5, maxw5, w5step);


		/**
		 * cluster based on the first play
		 * then measure their rationality ? frequency may be
		 * after that measure their ratinality in second game play
		 */
		
		double[][] examples = null;
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointAdaptiveProgressive(newdata, users_refined, gameinstance, deforder);
			
			//examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		else
		{
			examples = prepareExamplesDTScorePointsOneGame(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		//printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		//System.out.println("Normalized data: ");

		//printData(users_refined, normalizedexamples);
		
		List<Integer>[] clusters = null;

		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples, minw1, minw2, minw3, minw4);
			//clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		//printClustersInt(clusters);








		System.out.println("Result");
		System.out.println("total users "+ users_refined.size());
		
		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);
			System.out.println("cluster "+ cluster + ", user number "+ users_groups.size());

			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance, deforder);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);
			
			//HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");



			//double[] estimatedlambdanaive = estimateLambdaNaiveBinarySDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			//double[] estimatedw = estimateOmegaNaiveParallel(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, w1, w2, w3, w4);
			double[] estimatedw = estimateOmegaParallelBatchJob(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, w1, w2, w3, w4/*, w5*/);
			
			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			//System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				//PrintWriter pw = new PrintWriter(new FileOutputStream(new File("suqr.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				//pw.append(gameinstance+","+cluster+","+users_groups.size()+","+ estimatedw[4]+","+ estimatedw[0]+","+ estimatedw[1]+","+ estimatedw[2]+","+ estimatedw[3]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				//System.out.print(gameinstance+","+cluster+","+users_groups.size()+","+ estimatedw[5]+","+ estimatedw[0]+","+ estimatedw[1]+","+ estimatedw[2]+","+ estimatedw[3]+","+ estimatedw[4]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");
				System.out.print(gameinstance+","+cluster+","+users_groups.size()+","+ estimatedw[4]+","+ estimatedw[0]+","+ estimatedw[1]+","+ estimatedw[2]+","+ estimatedw[3]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");
				
				int index=0;
				for(int c: attackcount)
				{
					//pw.append(c+"");
					System.out.print(c+"");
					if(index<(attackcount.length-1))
					{
						//pw.append(",");
						System.out.print(",");
					}

					index++;
				}
				//pw.append("\n");
				System.out.print("\n");

				//pw.close();
			}
			catch(Exception ex)
			{
				System.out.println("file... ");
			}


			//break;

		}
		
	}
	
	
	private static void findOmega(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double lambda, int naction, int DEPTH_LIMIT,
			int k, int featureset, String algorithm,
			double minw1, double maxw1, double minw2, double maxw2,
			double minw3, double maxw3, double minw4, double maxw4, double w1step, double w2step, double w3step, double w4step, boolean genericgroup, boolean parallel) throws Exception {
		



		//step = 0.1;

		double[] w1 = generateAlphaArray(minw1, maxw1, w1step);


		double[] w2 = generateBetaArray(minw2, maxw2, w2step);


		double[] w3 = generateBetaArray(minw3, maxw3, w3step);


		double[] w4 = generateGammaArray(minw4, maxw4, w4step);
		
		//double[] w5 = generateGammaArray(minw5, maxw5, w5step);
		
		

		/**
		 * cluster based on the first play
		 * then measure their rationality ? frequency may be
		 * after that measure their ratinality in second game play
		 */
		
		double[][] examples = null;
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointWOOrder(data_refined_first_game, users_refined, ngames, algorithm);
			
			//examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		else
		{
			examples = prepareExamplesDTScorePointsWOOrder(data_refined_first_game, users_refined, ngames, algorithm);
		}
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		//printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		//System.out.println("Normalized data: ");

		//printData(users_refined, normalizedexamples);
		
		List<Integer>[] clusters = null;

		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);





		System.out.println("Result");
		System.out.println("total users "+ users_refined.size());
		
		try {
		
		//PrintWriter pw = new PrintWriter(new FileOutputStream(new File("suqr.csv"),true));

		//pw.append("deftype,cluster,#users,llv,w1,w2,w3,w4,score,mscore,nscore,pscore"+ "\n");
		//pw.close();
		
		}
		catch(Exception ex)
		{
			
		}



		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);

			
			
			
			
			if(genericgroup)
			{
				users_groups = users_refined;
			}
			
			System.out.println("cluster "+ cluster + ", total users "+ users_groups.size());
			

			

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlayRange(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptiveWOOrder(users_groups, data_refined_first_game, numberofnodes, algorithm);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);
			
			
			//HashMap<String, int[]> defendfrequency = getAttackCountInDataDefender(gameplay, numberofnodes, 5);
			
			
			
			//HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);
			
			
			

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree


			HashMap<String, HashMap<String, Double>> defstrategy = null;


			if(algorithm.startsWith("s"))
			{
				defstrategy = Data.readStrategy("g5d5_FI.txt");
			}
			else if(algorithm.startsWith("r"))
			{
				HashMap<String, double[]> defendfrequency = getDefFreqInData(gameplay, numberofnodes, 5);
				defstrategy = convertFormatD(defendfrequency);
			}


			/**
			 * use the int[] version. Divide the sum og llv by the total number of samples
			 */
			
			
			//double[] estimatedw = estimateOmegaNaiveParallel(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, w1, w2, w3, w4);
			
			double[] estimatedw = null;
					
					
			if(parallel)
			{
				estimatedw = estimateOmegaParallelBatchJob(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, w1, w2, w3, w4);
			}
			else
			{
				estimatedw = estimateOmegaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, w1, w2, w3, w4, 1);
			}
			
					
			
		
			
			
			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			//System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				//PrintWriter pw = new PrintWriter(new FileOutputStream(new File("suqr.csv"),true));

				//pw.append("deftype,cluster,#users,llv,w1,w2,w3,w4,score,mscore,nscore,pscore"+ "\n");

				
				//pw.append(algorithm+","+cluster+","+users_groups.size()+","+ estimatedw[4]/users_groups.size()+","+ estimatedw[0]+","+ estimatedw[1]+","+ estimatedw[2]+","+ estimatedw[3]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");
				System.out.print(algorithm+","+cluster+","+users_groups.size()+","+ estimatedw[4]/users_groups.size()+","+ estimatedw[0]+","+ estimatedw[1]+","+ estimatedw[2]+","+ estimatedw[3]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");
				
				
				
				int index=0;
				for(int c: attackcount)
				{
					//pw.append(c+"");
					System.out.print(c+"");
					if(index<(attackcount.length-1))
					{
						//pw.append(",");
						System.out.print(",");
					}

					index++;
				}
				//pw.append("\n");
				System.out.print("\n");
				//pw.close();

			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			
			if(genericgroup)
			{
				break;
			}

		}
		


		
	}
	
	
	private static void trendUserPointsLambda(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, int DEPTH_LIMIT, 
			int k, int deforder, int featureset, ArrayList<ArrayList<String>> newdata, int level) throws Exception {
		




		






			int cluster =0;
		
			ArrayList<String> users_groups =  users_refined;
			
			
			double[][] userpoints = computeUserPoints(users_groups, gameinstance, data_refined_first_game, deforder);
			
			sortDTUsersDescD(userpoints);
			
			
			printUsers(users_groups, userpoints);
			
			//double avg = getAvgScore(userpoints);
			
			
			
			HashMap<String, Double> upoints = new HashMap<String, Double>();
			ArrayList<String> new_users_groups = updateUsers(userpoints, users_groups, level, upoints);
			
			users_groups = new_users_groups;
			
			
			printUserPoints(users_groups, upoints);


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance, deforder);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			//HashMap<String, int[]> attackfrequency = getAttackCountInData(gameplay, numberofnodes, 5);
			
			HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree

			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt"); // full info

			if(game_type==0)
			{
				defstrategy = Data.readStrategy("g5d5_AP.txt");
			}


			



			//double[] estimatedlambdanaive = estimateLambdaNaiveBinarySDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			
			double[] estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(gameinstance+","+cluster+","+users_groups.size()+","+ estimatedlambdanaive[0]+","+ estimatedlambdanaive[1]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		
	}
	
	
	private static void findLambdaForGroupProgressive(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, int DEPTH_LIMIT, int k, int deforder, int featureset) throws Exception {
		




		/**
		 * cluster based on the first play
		 * then measure their rationality ? frequency may be
		 * after that measure their ratinality in second game play
		 */
		
		double[][] examples = null;
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		else
		{
			examples = prepareExamplesDTScorePointsOneGame(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);
		
		List<Integer>[] clusters = null;

		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);









		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance, deforder);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");



			double estimatedlambdanaive[] = estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			
			//double estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			System.out.println("Estmiated lambda "+ estimatedlambdanaive);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(gameinstance+","+cluster+","+users_groups.size()+","+ estimatedlambdanaive+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		}
		
	}
	
	private static void findOmegaForOneGroup(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double step, int naction, int DEPTH_LIMIT, int group, int def_order, double lambda) throws Exception {
		

		
		
		
		double minw1 = -10;
		double maxw1 = 1;
		double stepw1 = .2;


		double minw2 = -1;
		double maxw2 = 1;
		double stepw2 = .05;


		double minw3 = -1;
		double maxw3 = 1;
		double stepw3 = .05;


		double minw4 = 0;
		double maxw4 = 0;
		double stepw4 = 1;




		double[] w1 = generateAlphaArray(minw1, maxw1, stepw1);


		double[] w2 = generateBetaArray(minw2, maxw2, stepw2);


		double[] w3 = generateBetaArray(minw3, maxw3, stepw3);


		double[] w4 = generateGammaArray(minw4, maxw4, stepw4);

		


			ArrayList<String> users_groups =  users_refined;


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance, def_order);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, roundlimit);

			// #10*3*5 attackfreq should be 150
			boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");



			//double estimatedlambdanaive = estimateLambdaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//double estimatedlambdanaive = estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);


			//double[] estimatedw = estimateOmegaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, w1, w2, w3, w4);
			
			double[] estimatedw = estimateOmegaNaiveParallel(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, w1, w2, w3, w4);
			
			//System.out.println("Estmiated lambda "+ estimatedlambdanaive);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			//System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			System.out.println("Cluster "+group+", user count "+users_groups.size()+", w:  "+ estimatedw[0]+","+ estimatedw[1]+","+ estimatedw[2]+","+estimatedw[3]);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("suqr.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(gameinstance+","+group+","+users_groups.size()+","+ estimatedw[0]+","+ estimatedw[1]+","+ estimatedw[2]+","+estimatedw[3]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		
		
	}
	
	
	private static double findLambdaForOneGroup(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, int DEPTH_LIMIT, int group, int def_order) throws Exception {
		



			ArrayList<String> users_groups =  users_refined;


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance, def_order);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			//HashMap<String, int[]> attackfrequency = getAttackCountInData(gameplay, numberofnodes, roundlimit);
			
			HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, roundlimit);

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");



			//double estimatedlambdanaive = estimateLambdaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//double estimatedlambdanaive[] = estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			double[] estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);


			
			System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			//System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				//File file = new File("lambda.csv"); //filepath is being passes through //ioc         //and filename through a method 

				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));
				/*if (file.exists()) 
				{
					file.delete(); //you might want to check if delete was successfull
				}*/
				
				

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(gameinstance+","+group+","+users_groups.size()+","+ estimatedlambdanaive[0]+","+ estimatedlambdanaive[1]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		return estimatedlambdanaive[1];
		
	}
	
	
	private static double findLambdaForOneGroupDT(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames,ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, 
			int DEPTH_LIMIT, int group, int deforder, int fgi, int lgi, String algorithm, int cluster) throws Exception {
		



			ArrayList<String> users_groups =  users_refined;


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlayRange(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptiveRange(users_groups, data_refined_first_game, numberofnodes, fgi, lgi, deforder);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);
			
			
			//HashMap<String, int[]> defendfrequency = getAttackCountInDataDefender(gameplay, numberofnodes, 5);
			
			
			
			//HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);
			
			
			
			
			

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree


			HashMap<String, HashMap<String, Double>> defstrategy = null;


			if(algorithm.startsWith("s"))
			{
				defstrategy = Data.readStrategy("g5d5_FI.txt");
			}
			else if(algorithm.startsWith("r"))
			{
				HashMap<String, double[]> defendfrequency = getDefFreqInData(gameplay, numberofnodes, 5);
				defstrategy = convertFormatD(defendfrequency);
			}



			double[] estimatedlambdanaive = estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			
			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			//System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(algorithm+","+cluster+","+users_groups.size()+","+ estimatedlambdanaive[0]+","+ estimatedlambdanaive[1]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;


		return estimatedlambdanaive[1];
		
	}
	
	

	private static double findLambdaForOneGroupDTWOOrder(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames,ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, 
			int DEPTH_LIMIT, int group, String algorithm, int cluster) throws Exception {
		



			ArrayList<String> users_groups =  users_refined;


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlayRange(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);
			
			
			//HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);
			
			


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptiveWOOrder(users_groups, data_refined_first_game, numberofnodes, algorithm);


			int[][] roundfreq = new int[5][6];
			
			
			getPlayByRound(roundfreq, gameplay);
			
			
			printRoundFreq(roundfreq);



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


		//	HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);
			
			
			//HashMap<String, int[]> defendfrequency = getAttackCountInDataDefender(gameplay, numberofnodes, 5);
			
			
			
			HashMap<String, double[]> attackfrequency = getAttackFreqInData(gameplay, numberofnodes, 5);
			
		//	printAttackerStrategy(attackfrequency, cluster);
			

			// #10*3*5 attackfreq should be 150
			/*boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}*/
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree


			HashMap<String, HashMap<String, Double>> defstrategy = null;


			if(algorithm.startsWith("s"))
			{
				defstrategy = Data.readStrategy("g5d5_FI.txt");
			}
			else if(algorithm.startsWith("r"))
			{
				HashMap<String, double[]> defendfrequency = getDefFreqInData(gameplay, numberofnodes, 5);
				defstrategy = convertFormatD(defendfrequency);
			}



			//double[] estimatedlambdanaive = estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);
			
			
			double[] estimatedlambdanaive = {0.0,0.0};//estimateLambdaNaiveBinaryDouble(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			//System.out.println("Estmiated lambda "+ estimatedlambdanaive[1]);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			double sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(algorithm+","+cluster+","+users_groups.size()+","+ estimatedlambdanaive[0]+","+ estimatedlambdanaive[1]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					double f = (c/sumattackcoutn)*100.0;
					pw.append(f+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}



			//break;


		return estimatedlambdanaive[1];
		
	}
	
	
	
	
	private static void printRoundFreq(int[][] roundfreq) {
	
		
		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("roundfrreq.csv"),true));

			//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

			
			
			
			pw.append("\n\n");
			
			
			for(int[] r: roundfreq)
			{
				double sm = 0;
				
				for(int i: r)
				{
					sm+=i;
				}
				
				
				for(int f: r)
				{
					double d = (f/sm)*100.0;;
					pw.append(d+",");
				}
				pw.append("\n");
			}
		
		pw.close();
	}
	catch(Exception ex)
	{
		System.out.println(" ");
	}
		
		
	}

	private static void getPlayByRound(int[][] roundfreq, int[][] gameplay) {
		
		
		int round = 1;

		for(int[] g: gameplay)
		{

			for(int r = 1; r<=5; r++)
			{
				int attr = 2*(r-1) + 1;
				int a = g[attr];
				roundfreq[r-1][a]++;

			}
		}

	}

	/**
	 * 
	 * @param users_refined
	 * @param data_refined_first_game
	 * @param ngames
	 * @param gameinstance
	 * @param data
	 * @param noderewards
	 * @param game_type
	 * @param roundlimit
	 * @param numberofnodes
	 * @param lambda
	 * @param step
	 * @param naction
	 * @param DEPTH_LIMIT
	 * @param k
	 * @param deforder
	 * @param featureset
	 * @param newdata only used for clustering
	 * @throws Exception
	 */
	private static void findFrequencyForGroup(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double step, int naction, 
			int DEPTH_LIMIT, int k, int deforder, int featureset, ArrayList<ArrayList<String>> newdata) throws Exception {
		




		/**
		 * cluster based on the first play
		 * then measure their rationality ? frequency may be
		 * after that measure their ratinality in second game play
		 */
		
		double[][] examples = null;
		
		// newdata is only used for clustering
		
		if(featureset==0)
		{
			examples = prepareExamplesNodeCostPointAdaptiveProgressive(data_refined_first_game, users_refined, gameinstance, deforder);
			//examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		else
		{
			examples = prepareExamplesDTScorePointsOneGame(data_refined_first_game, users_refined, gameinstance, deforder);
		}
		
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		//printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		//printData(users_refined, normalizedexamples);

		//int k= 2;

		List<Integer>[] clusters = null;
		
		if(k>1)
		{
			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}
		
		//List<Integer>[] clusters = Weka.clusterUsers(k, normalizedexamples);



		//List<Integer>[] clusters = Weka.clusterUsers(normalizedexamples);



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);









		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


		//	users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance, deforder);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");



			double estimatedlambdanaive = 0;//estimateLambdaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			System.out.println("Estmiated lambda "+ estimatedlambdanaive);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScore(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(gameinstance+","+cluster+","+users_groups.size()+","+0+","+ estimatedlambdanaive+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		}
		
	}
	
	
	
	private static void trendUserPointsFreq(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, 
			int DEPTH_LIMIT, int k, int deforder, int featureset, ArrayList<ArrayList<String>> newdata, int level) throws Exception {
		




		/**
		 * cluster based on the first play
		 * then measure their rationality ? frequency may be
		 * after that measure their ratinality in second game play
		 */
		
		



		int cluster=0;


		
		
		
		
		
			ArrayList<String> users_groups = users_refined;
			
			// compute user's score
			
			double[][] userpoints = computeUserPoints(users_groups, gameinstance, data_refined_first_game, deforder);
			
			sortDTUsersDescD(userpoints);
			
			
			printUsers(users_groups, userpoints);
			
			double avg = getAvgScore(userpoints);
			
			
			
			HashMap<String, Double> upoints = new HashMap<String, Double>();
			ArrayList<String> new_users_groups = updateUsers(userpoints, users_groups, level, upoints);
			
			users_groups = new_users_groups;
			
			
			printUserPoints(users_groups, upoints);
			
			
			


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance, deforder);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");



			double estimatedlambdanaive = 0;//estimateLambdaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			System.out.println("Estmiated lambda "+ estimatedlambdanaive);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				//int tmpscore= getAllUserScore(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(gameinstance+","+cluster+","+users_groups.size()+","+0+","+ estimatedlambdanaive+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		
		
	}
	
	
	
	
	private static void printUserPoints(ArrayList<String> users_groups, HashMap<String, Double> upoints) {
		
		
		for(String u: users_groups)
		{
			System.out.println("user "+ u + ", points "+ upoints.get(u));
		}
		
		
	}

	private static ArrayList<String> updateUsers(double[][] userpoints, ArrayList<String> users_groups, int level, HashMap<String,Double> upoints) {
		
		
		
		//int userperlevel = users_groups.size()/2;
		
		ArrayList<String> newusers = new ArrayList<String>();
		
		double median = computeMedian(userpoints);
		
		
		
		
		for(int i=0; i<userpoints.length; i++)
		{
			
			if(level==0 &&  userpoints[i][1]>=median)
			{
			
				int index = (int)userpoints[i][0];
				newusers.add(users_groups.get(index));
				upoints.put(users_groups.get(index), userpoints[i][1]);
			}
			if(level==1 &&  userpoints[i][1]<=median)
			{
			
				int index = (int)userpoints[i][0];
				newusers.add(users_groups.get(index));
				upoints.put(users_groups.get(index), userpoints[i][1]);
			}
			
		}
		
		
		
		return newusers;
	}

	private static double computeMedian(double[][] userpoints) {
		
		
		double median;
		if (userpoints.length % 2 == 0)
		    median = ((double)userpoints[userpoints.length/2][1] + (double)userpoints[userpoints.length/2 - 1][1])/2;
		else
		    median = (double) userpoints[userpoints.length/2][1];
		
		
		return median;
	}

	private static double getAvgScore(double[][] userpoints) {
		
		
		double sum =0;
		
		for(double u[]: userpoints)
		{
			sum += u[1];
		}
		
		return sum/=userpoints.length;
	}

	private static double[][] computeUserPoints(ArrayList<String> users_groups, int gameinstance,
			ArrayList<ArrayList<String>> data_refined_first_game, int deforder) {
		
		
		double[][] points = new double[users_groups.size()][2];
		int index = 0;
		
		for(String u: users_groups)
		{
			double point = getAllUserScoreAdaptive(u, data_refined_first_game, gameinstance, deforder);
			points[index][0] = index;
			points[index][1] = point;
			index++;
			
		}
		
		
		
		return points;
	}

	private static void findFrequencyForOneGroup(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined_first_game, 
			int ngames, int gameinstance, ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, 
			int game_type, int roundlimit, int numberofnodes, double[] lambda, double step, int naction, int DEPTH_LIMIT, int group, int def_order) throws Exception {
		



			ArrayList<String> users_groups =  users_refined;


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance, def_order);


			



			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");



			double estimatedlambdanaive = 0;//estimateLambdaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			System.out.println("Estmiated lambda "+ estimatedlambdanaive);




			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);


			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);



				


				//int tmpscore= getAllUserScore(tmpusr, data_refined_first_game, gameinstance0); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				/*if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}*/


				sumscore += computedscore;




				double m = getPersonalityScore(tmpusr, data_refined_first_game, 0);
				double n = getPersonalityScore(tmpusr, data_refined_first_game, 1);
				double ps  = getPersonalityScore(tmpusr, data_refined_first_game, 2);
				
				
				sum_mscore += m;
				sum_nscore += n;
				sum_pscore += ps;
				
				
				//System.out.println("user : "+ tmpusr + ", m="+ m + ", n="+n + ", p="+ps);
				


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			//System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				//filepath is being passes through //ioc         //and filename through a method 

				
				/*if (file.exists()) 
				{
					file.delete(); //you might want to check if delete was successfull
				}*/
				
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(gameinstance+","+group+","+users_groups.size()+","+0+","+ estimatedlambdanaive+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		
		
	}

	private static void sanitizeUsers(ArrayList<String> users_refined,
			ArrayList<ArrayList<String>> data_refined_first_game, int ngames, int gameinstance0,
			ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, int game_type, int roundlimit, int def_order) {


		HashMap<String, String> alluser_seq = new HashMap<String, String>();
		HashMap<String, Integer> alluser_reward = new HashMap<String, Integer>();
		int[][] allusergameplay = createGamePlay(ngames, users_refined, data_refined_first_game, roundlimit,alluser_seq);

		double allattpoints = EquationGenerator.computeAttackerReward(noderewards, alluser_seq, alluser_reward);


		ArrayList<String> inconsistentuser = new ArrayList<String>();



	//System.out.println("users size "+ users_refined.size());

		for(String usr: alluser_seq.keySet())
		{

			int tmpscore= getAllUserScoreAdaptive(usr, data_refined_first_game, gameinstance0, def_order);
			int score = alluser_reward.get(usr);

			if(tmpscore != score)
			{
				inconsistentuser.add(usr);
			}
		}

		for(String usr: inconsistentuser)
		{
			if(users_refined.contains(usr))
			{
				users_refined.remove(usr);
			}
		}

		//System.out.println("after removing inconsistent users size "+ users_refined.size());

		data_refined_first_game = refineDataAdaptive(data,game_type, users_refined, gameinstance0, def_order);



	}
	
	
	private static void sanitizeUsersRange(ArrayList<String> users_refined,
			ArrayList<ArrayList<String>> data_refined_first_game, int ngames, int gameinstance0, int gameinstance1,
			ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, int game_type, int roundlimit, int def_order, int exampleinstance) {



		ArrayList<String> inconsistentuser = new ArrayList<String>();

		for(String usr: users_refined)
		{


			int countinstances = countInstances(data, gameinstance0, gameinstance1, usr);


			if((countinstances != exampleinstance))
			{
				inconsistentuser.add(usr);
			}
		}

		for(String usr: inconsistentuser)
		{
			if(users_refined.contains(usr))
			{
				users_refined.remove(usr);
			}
		}





		//System.out.println("after removing inconsistent users size "+ users_refined.size());

		data_refined_first_game = refineDataAdaptiveRange(data,game_type, users_refined, gameinstance0, gameinstance1, def_order);



	}

	
	private static void sanitizeUsersRangeWOOrder(ArrayList<String> users_refined,
			ArrayList<ArrayList<String>> data_refined_first_game, int ngames,
			ArrayList<ArrayList<String>> data, HashMap<Integer,Integer[]> noderewards, int game_type, int roundlimit, int exampleinstance, String alg) {


		/*HashMap<String, String> alluser_seq = new HashMap<String, String>();
		HashMap<String, Integer> alluser_reward = new HashMap<String, Integer>();
		int[][] allusergameplay = createGamePlay(ngames, users_refined, data_refined_first_game, roundlimit,alluser_seq);
*/
		//double allattpoints = EquationGenerator.computeAttackerReward(noderewards, alluser_seq, alluser_reward);


		ArrayList<String> inconsistentuser = new ArrayList<String>();



	//System.out.println("users size "+ users_refined.size());

		for(String usr: users_refined)
		{
			
			
			if(usr.equals("\"$2y$10$/kXfM1T.mNbwDiQxSjzFdOILFqbymZ41aFbKzrC6dn00dnLWZ8Asa\""))
			{
				System.out.println("user: "+usr+" | attackaction null");
			}
			

			/*if(usr.equals("\"$2y$10$SIKHlQqLljPY7pjVJDQ86uKHuwdUnXlxpyXm6GrZFLvcvBIavXEhC\""))
			{
				int v=1;
			}*/
			//int tmpscore= getAllUserScoreAdaptiveRange(usr, data_refined_first_game, gameinstance0, gameinstance1, def_order);
			
			int countinstances = countInstancesWOOrder(data, usr, alg);
			
			//int score = alluser_reward.get(usr);

			if((countinstances != exampleinstance))
			{
				inconsistentuser.add(usr);
			}
		}

		for(String usr: inconsistentuser)
		{
			if(users_refined.contains(usr))
			{
				users_refined.remove(usr);
			}
		}
		
		
		
		

		//System.out.println("after removing inconsistent users size "+ users_refined.size());
		
		
		

		data_refined_first_game = refineDataAdaptiveRangeWOOrder(data_refined_first_game,game_type, users_refined, alg);



	}
	
	

	private static void removeIncosistentUsers(ArrayList<String> users_refined, ArrayList<ArrayList<String>> data, int exampleinstance, String alg) {
		
		
		ArrayList<String> inconsistentuser = new ArrayList<String>();
		
		for(String usr: users_refined)
		{

			/*if(usr.equals("\"$2y$10$SIKHlQqLljPY7pjVJDQ86uKHuwdUnXlxpyXm6GrZFLvcvBIavXEhC\""))
			{
				int v=1;
			}*/
			//int tmpscore= getAllUserScoreAdaptiveRange(usr, data_refined_first_game, gameinstance0, gameinstance1, def_order);
			
			int countinstances = countInstancesWOOrder(data, usr, alg);
			
			//int score = alluser_reward.get(usr);

			if((countinstances != exampleinstance))
			{
				inconsistentuser.add(usr);
			}
		}

		for(String usr: inconsistentuser)
		{
			if(users_refined.contains(usr))
			{
				users_refined.remove(usr);
			}
		}
		
		
	}

	private static int countInstances(ArrayList<ArrayList<String>> data, int gameinstance0, int gameinstance1,
			String usr) {
		
		int count = 0;
		
		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
			int gametype = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;

			if(tmpuser.equals(usr))
			{


				int def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue()));
				int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
				//int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
				/*String action = example.get(Headers_minimum.attacker_action.getValue());
			int attackaction = 0;
			if(!action.equals(" "))
			{
				attackaction = Integer.parseInt(action);
			}*/

				/**
				 * we can use data from different game instances gameinstance== 4,5,6 or 1,2,3
				 */
				if((gameinstance>=gameinstance0 && gameinstance<=gameinstance1) && gameplayed == 6) // asc, take 4th game instance to 6th
				{
					count++;

				}
			}




		}
		
		
		return count;
	}
	
	
	private static int countInstancesWOOrder(ArrayList<ArrayList<String>> data,
			String usr, String alg) {
		
		int count = 0;
		
		for(ArrayList<String> example: data)
		{
			// get user id
			String tmpuser = example.get(Headers_minimum.user_id.getValue());
			int gameplayed = Integer.parseInt(example.get(Headers_minimum.game_played.getValue())) ;
			int gametype = Integer.parseInt(example.get(Headers_minimum.game_type.getValue())) ;

			if(tmpuser.equals(usr))
			{


				int def_order = Integer.parseInt(example.get(Headers_minimum.pick_def_order.getValue()));
				int gameinstance = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
				//int round =  Integer.parseInt(tmpexample.get(Headers_minimum.round.getValue()));
				/*String action = example.get(Headers_minimum.attacker_action.getValue());
			int attackaction = 0;
			if(!action.equals(" "))
			{
				attackaction = Integer.parseInt(action);
			}*/
				
				int fgi = -1;
				int lgi = -1;
				
				if(alg.equals("r") && def_order==0)
				{
					fgi= 1;
					lgi=3;
				}
				else if(alg.equals("r") && def_order==1)
				{
					fgi= 4;
					lgi=6;
				}
				else if(alg.equals("s") && def_order==0)
				{
					fgi= 4;
					lgi=6;
				}
				else if(alg.equals("s") && def_order==1)
				{
					fgi= 1;
					lgi=3;
				}

				/**
				 * we can use data from different game instances gameinstance== 4,5,6 or 1,2,3
				 */
				if(gameplayed == 6 && (gameinstance >= fgi && gameinstance<= lgi)) // asc, take 4th game instance to 6th
				{
					count++;

				}
			}




		}
		
		
		return count;
	}

	/**
	 * 1. groups users
	 * 2. estimate lambda based on different groups
	 * @param def_order 
	 * @param depthlimit 
	 * @param gameins1 
	 * @param gameins0 
	 * @param k2 
	 * @throws Exception 
	 * 
	 */
	public static void computeOmegaSUQR(int k, int depthlimit, int def_order, int gameins0, int gametype) throws Exception {







		int DEPTH_LIMIT = depthlimit; // needs to be 10 for our experiment
		int naction = 6;
		/*double minlambda = .05;
		double maxlambda = .18;
		double step = .01;
		double lambda[] = {0.15, 0.08};//enerateLambdaArray(minlambda, maxlambda, step);
*/
		int ngames = 1;
		int roundlimit = 5;

		// how many clusters you want
		int numberofnodes = 6;
		int gameinstance0 = gameins0;
		//int gameinstance1 = 1;


		double minw1 = -10;
		double maxw1 = -9;
		double stepw1 = 1;


		double minw2 = -1;
		double maxw2 = -1;
		double stepw2 = 1;


		double minw3 = -1;
		double maxw3 = 1;
		double stepw3 = .5;


		double minw4 = -10;
		double maxw4 = 10;
		double stepw4 = 1;





		double[] w1 = generateAlphaArray(minw1, maxw1, stepw1);


		double[] w2 = generateBetaArray(minw2, maxw2, stepw2);


		double[] w3 = generateBetaArray(minw3, maxw3, stepw3);


		double[] w4 = generateGammaArray(minw4, maxw4, stepw4);






		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender
		
		ArrayList<String> users_refined = refineUserAdaptive(data, def_order, gametype);


		// now get their 1st play 
		ArrayList<ArrayList<String>>  data_refined_first_game = refineDataAdaptive(data,gametype, users_refined, gameinstance0, def_order);
		/*ArrayList<ArrayList<String>>  data_refined_second_game = refineDataAdaptive(data,game_type, users_refined, gameinstance1, def_order);
		ArrayList<ArrayList<String>>  data_refined_third_game = refineDataAdaptive(data,game_type, users_refined, gameinstance2, def_order);
		ArrayList<ArrayList<String>>  data_refined_fourth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance3, def_order);
		ArrayList<ArrayList<String>>  data_refined_fifth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance4, def_order);
		ArrayList<ArrayList<String>>  data_refined_sixth_game = refineDataAdaptive(data,game_type, users_refined, gameinstance5, def_order);
*/

		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);


		sanitizeUsers(users_refined, data_refined_first_game, ngames, gameinstance0, data, noderewards, gametype, roundlimit, def_order);
		/*sanitizeUsers(users_refined, data_refined_second_game, ngames, gameinstance1, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_third_game, ngames, gameinstance2, data, noderewards, game_type, roundlimit, def_order);
		
		sanitizeUsers(users_refined, data_refined_fourth_game, ngames, gameinstance3, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_fifth_game, ngames, gameinstance4, data, noderewards, game_type, roundlimit, def_order);
		sanitizeUsers(users_refined, data_refined_sixth_game, ngames, gameinstance5, data, noderewards, game_type, roundlimit, def_order);
*/




		//double[][] examples = prepareExamplesDTScorePointsOneGame(data_refined, users_refined, gameinstance0, def_order);
		double[][] examples = prepareExamplesNodeCostPointAdaptive(data_refined_first_game, users_refined, gameinstance0, def_order);
		//double [][] examples = prepareFrquenceyOneGame(data_refined, users_refined, numberofnodes, gameinstance0, def_order);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);
		
		List<Integer>[] clusters = null;

		if(k>1)
		{

			clusters = Weka.clusterUsers(k, normalizedexamples);
		}
		else
		{
			clusters = Weka.clusterUsers(normalizedexamples);
		}



		



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);




		/**
		 * next use weka to cluster
		 */

		//printClusters(clusters);

		//Create a proxy, which we will use to control MATLAB
		/*MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();
		 */

		/*try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

			//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}

		 */





		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined_first_game, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequencyAdaptive(users_groups, data_refined_first_game, numberofnodes, gameinstance0, def_order);






			//int[][] testgameplay = new int[10][10];


			/*for(int t=0; t<testgameplay.length; t++)
			{
				for(int u=0; u<testgameplay[t].length; u++)
				{
					testgameplay[t][u] = gameplay[t][u];
					System.out.print(testgameplay[t][u] + " ");
				}
				System.out.println();

			}
			 */


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, roundlimit);

			// #10*3*5 attackfreq should be 150
			boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");


			//HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();

			//double tmplambda = 0.6;


			/**
			 * also need success and failure rate of each action after each round
			 */


			double l = 1;

			/*if(users_groups.size()==76)
			{
				l= .15;
			}*/

			

			//double[] estimatedw = estimateOmegaNaive(l, attackfrequency, naction, defstrategy, DEPTH_LIMIT, w1, w2, w3, w4);
			
			double[] estimatedw = estimateOmegaNaiveParallel(l, attackfrequency, naction, defstrategy, DEPTH_LIMIT, w1, w2, w3, w4);
			

			//System.out.println("Estmiated w "+ estimatedw[0]+ ", "+estimatedw[1]+ ", "+ estimatedw[2] );




			/*DNode root1 = EquationGenerator.buildGameTreeRecur(DEPTH_LIMIT, naction, defstrategy, attstrategy, tmplambda);

			computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);*/





			/*DNode root = EquationGenerator.buildGameTree(DEPTH_LIMIT, naction);



			HashMap<String, ArrayList<DNode>> I = EquationGenerator.prepareInformationSets(root, DEPTH_LIMIT, naction);
			EquationGenerator.printInfoSet(I);
			HashMap<String, InfoSet> isets = EquationGenerator.prepareInfoSet(I);
			 *//**
			 * compute information sets according to depth
			 *//*
			HashMap<Integer, ArrayList<String>> depthinfoset = depthInfoSet(DEPTH_LIMIT, isets,1); // for player 1: attacker, player 0 is defender
			EquationGenerator.printISets(isets);


			EquationGenerator.updateTreeWithDefStartegy(isets, root, strategy, naction);
			  */


			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);







			// use attackstrategy to compute lambda




			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);






				int tmpscore= getAllUserScoreAdaptive(tmpusr, data_refined_first_game, gameinstance0, def_order); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}


				sumscore += tmpscore;




				sum_mscore += getPersonalityScore(tmpusr, data_refined_first_game, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined_first_game, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined_first_game, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore = sumscore/users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", w:  "+ estimatedw[0]+","+ estimatedw[1]+","+ estimatedw[2]+","+estimatedw[3]);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("suqr.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(cluster+","+users_groups.size()+","+estimatedw[4]+","+ estimatedw[0]+","+ estimatedw[1]+","+ estimatedw[2]+","+estimatedw[3]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		}

		// for each of the user groups compute lambda


	}



	/**
	 * 1. groups users
	 * 2. estimate lambda based on different groups
	 * @throws Exception 
	 * 
	 */
	public static void fitPT() throws Exception {



		int numberofnodes = 6;

		int gameinstance0 = 4;
		int gameinstance1 = 1;


		int DEPTH_LIMIT = 2; // needs to be 10 for our experiment
		int roundlimit = 5;
		int naction = 6;

		int ngames = 1;


		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender
		ArrayList<String> users_refined = refineUser(data, -1, 1);

		ArrayList<ArrayList<String>>  data_refined = refineData(data,1, users_refined, gameinstance0, gameinstance1);


		/**
		 * remove users whose points are not consistent
		 */

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);

		HashMap<String, String> alluser_seq = new HashMap<String, String>();
		HashMap<String, Integer> alluser_reward = new HashMap<String, Integer>();
		int[][] allusergameplay = createGamePlay(ngames, users_refined, data_refined, roundlimit,alluser_seq);

		double allattpoints = EquationGenerator.computeAttackerReward(noderewards, alluser_seq, alluser_reward);


		ArrayList<String> inconsistentuser = new ArrayList<String>();
		System.out.println("users size "+ users_refined.size());
		for(String usr: alluser_seq.keySet())
		{

			int tmpscore= getAllUserScore(usr, data_refined, gameinstance0, gameinstance1);
			int score = alluser_reward.get(usr);

			if(tmpscore != score)
			{
				inconsistentuser.add(usr);
			}
		}

		for(String usr: inconsistentuser)
		{
			if(users_refined.contains(usr))
			{
				users_refined.remove(usr);
			}
		}

		System.out.println("after removing inconsistent users size "+ users_refined.size());

		data_refined = refineData(data,1, users_refined, gameinstance0, gameinstance1);




		//double[][] examples = prepareExamplesDTScorePoints(data_refined, users_refined);
		double[][] examples = prepareExamplesNodeCostPoint(data_refined, users_refined, gameinstance0, gameinstance1);
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);

		int k= 3;

		//List<Integer>[] clusters = Weka.clusterUsers(k, normalizedexamples);

		List<Integer>[] clusters = Weka.clusterUsers(normalizedexamples);



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);



		double minalpha = .01;
		double maxalpha = 1;
		double stepalpha = .01;


		double minbeta = 0;
		double maxbeta = .99;
		double stepbeta = .01;


		double mintheta = 1;
		double maxtheta = 3;
		double steptheta = .1;


		double mingamma = .01;
		double maxgamma = .99;
		double stepgamma = .01;





		double[] alpha = generateAlphaArray(minalpha, maxalpha, stepalpha);


		double[] beta = generateBetaArray(minbeta, maxbeta, stepbeta);


		double[] theta = generateBetaArray(mintheta, maxtheta, steptheta);


		double[] gamma = generateGammaArray(mingamma, maxgamma, stepgamma);





		System.out.println("hello");


		/**
		 * next use weka to cluster
		 */

		//printClusters(clusters);

		//Create a proxy, which we will use to control MATLAB
		/*MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();
		 */






		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			//users_groups = users_refined;

			/**
			 * get attack count for different information set
			 */



			HashMap<String, String> user_seq = new HashMap<String, String>();

			int[][] gameplay = createGamePlay(ngames, users_groups, data_refined, roundlimit,user_seq);


			/*String u= "\"$2y$10$1.vgQUYwu1DmltOCcbkwt.fTPbViJwq/W4mURkZFKI.Z4zHvenYRq\"";

			String ss= user_seq.get(u);*/



			// = createGamePlay(ngames, users_groups, data_refined, roundlimit);



			HashMap<String, Integer> user_reward = new HashMap<String, Integer>();

			double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);




			int attackcount[] = getAttackFrequency(users_groups, data_refined, numberofnodes, gameinstance0, gameinstance1);






			//int[][] gameplay = createGamePlay(ngames, users_groups, data_refined, 5);
			//int attackcount[] = getAttackFrequency(users_groups, data_refined, numberofnodes, gameinstance0, gameinstance1);
			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(gameplay, numberofnodes, roundlimit);

			// #10*3*5 attackfreq should be 150
			boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				//throw new Exception("problem freq....");
			}
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");


			//HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();

			//double tmplambda = 0.6;


			double[] estimatedptparams = estimatePTParams(attackfrequency, naction, defstrategy, DEPTH_LIMIT, alpha, beta, theta, gamma);

			System.out.println("Estmiated PTparams  "+ estimatedptparams[0]+","+estimatedptparams[1]+","+estimatedptparams[2]+","+estimatedptparams[3]);




			/*DNode root1 = EquationGenerator.buildGameTreeRecur(DEPTH_LIMIT, naction, defstrategy, attstrategy, tmplambda);

			computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);*/





			/*DNode root = EquationGenerator.buildGameTree(DEPTH_LIMIT, naction);



			HashMap<String, ArrayList<DNode>> I = EquationGenerator.prepareInformationSets(root, DEPTH_LIMIT, naction);
			EquationGenerator.printInfoSet(I);
			HashMap<String, InfoSet> isets = EquationGenerator.prepareInfoSet(I);
			 *//**
			 * compute information sets according to depth
			 *//*
			HashMap<Integer, ArrayList<String>> depthinfoset = depthInfoSet(DEPTH_LIMIT, isets,1); // for player 1: attacker, player 0 is defender
			EquationGenerator.printISets(isets);


			EquationGenerator.updateTreeWithDefStartegy(isets, root, strategy, naction);
			  */


			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);







			// use attackstrategy to compute lambda




			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{

				String tmpusr = users_groups.get(i);


				int tmpscore= getAllUserScore(tmpusr, data_refined, gameinstance0, gameinstance1); // compute score from sequence

				int computedscore = user_reward.get(tmpusr);

				if(tmpscore != computedscore)
				{
					System.out.println(tmpusr + "   reward not matching");
					//throw new Exception("reward not matching");
				}


				sumscore += tmpscore;



				//sumscore += getUserScore(tmpusr, data_refined);

				sum_mscore += getPersonalityScore(tmpusr, data_refined, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore /= users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			//System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("pt.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				//pw.append(cluster+","+users_groups.size()+","+ estimatedlambdanaive+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");
				pw.append(cluster+","+users_groups.size() +","+ estimatedptparams[0]+","+estimatedptparams[1]+","+estimatedptparams[2]+","+estimatedptparams[3]+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");


				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			//break;

		}

		// for each of the user groups compute lambda


	}


	private static boolean verifyAttackFreq(HashMap<String, int[]> attackfrequency, int size) {



		int val = size*1*5;

		int sum = 0;

		for(int f[]: attackfrequency.values())
		{
			for(int i: f)
			{
				sum += i;
			}
		}

		if(val != sum)
			return false;

		return true;
	}

	private static void refineAttackFrequency(HashMap<String, int[]> attackfrequency) {

		ArrayList<String> toberemoved = new ArrayList<String>();

		for(String key: attackfrequency.keySet())
		{
			int[] freq = attackfrequency.get(key);

			boolean flag = false;
			for(int f: freq)
			{
				if(f>0)
				{
					flag = true;
					break;
				}
			}
			if(!flag)
			{
				toberemoved.add(key);
			}


		}


		for(String key: toberemoved)
		{
			if(attackfrequency.containsKey(key))
			{
				attackfrequency.remove(key);
			}
		}


	}

	private static double estimateLambdaNaive(double[] lambda, HashMap<String, InfoSet> isets,
			HashMap<String, int[]> attackfrequency, int naction, HashMap<String, HashMap<String, Double>> strategy,
			DNode root, int dEPTH_LIMIT, HashMap<Integer, ArrayList<String>> depthinfoset, double step) throws Exception {



		Double minllh = Double.MAX_VALUE;
		double minlambda = -1;

		for(int i=0; i<lambda.length; i++)
		{

			double llh = -likeHoodValue(isets, attackfrequency, naction, strategy, root, dEPTH_LIMIT, depthinfoset, lambda[i]);

			if(llh<minllh)
			{
				minllh = llh;
				minlambda = lambda[i];
			}
		}




		return minlambda;
	}

	
	
	private static double estimateLambdaBinarySearch(double[] lambda, HashMap<String, InfoSet> isets,
			HashMap<String, int[]> attackfrequency, int naction, HashMap<String, HashMap<String, Double>> strategy,
			DNode root, int dEPTH_LIMIT, HashMap<Integer, ArrayList<String>> depthinfoset, double step) throws Exception {



		double low = 0;
		double high = lambda.length-1;


		double lowllh = 0;//likeHoodValue(isets, attackfrequency, naction, strategy, root, dEPTH_LIMIT, depthinfoset, low);
		double highllh = 0;//likeHoodValue(isets, attackfrequency, naction, strategy, root, dEPTH_LIMIT, depthinfoset, high);
		double midllh = 0;//likeHoodValue(isets, attackfrequency, naction, strategy, root, dEPTH_LIMIT, depthinfoset, mid);




		while(low<=high)
		{
			double mid = (low+high)/2;

			System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);

			lowllh = likeHoodValue(isets, attackfrequency, naction, strategy, root, dEPTH_LIMIT, depthinfoset, low);
			highllh = likeHoodValue(isets, attackfrequency, naction, strategy, root, dEPTH_LIMIT, depthinfoset, high);
			midllh = likeHoodValue(isets, attackfrequency, naction, strategy, root, dEPTH_LIMIT, depthinfoset, mid);

			System.out.println(" lowllh "+ lowllh + ", highllh "+ highllh + ", midllh "+ midllh);


			if(midllh > lowllh && midllh > highllh) // triangle
			{
				low = low - step;
				high = high - step;

				System.out.println("midllh > lowllh && midllh > highllh.....true");
				System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);


			}
			else if(lowllh < midllh)
			{
				low = mid + step;
				System.out.println("lowllh < midllh.....true");
				System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);
			}
			else if(highllh < midllh)
			{
				high = mid - step;
				System.out.println("highllh < midllh.....true");
				System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);
			}


		}

		return midllh;
	}
	

	private static double estimateLambdaNaive(double[] lambda,
			HashMap<String, int[]> attackfrequency, int naction, HashMap<String, HashMap<String, Double>> defstrategy, int dEPTH_LIMIT, double step) throws Exception {



		Double minllh = Double.MAX_VALUE;
		double minlambda = -1;

		for(int i=0; i<lambda.length; i++)
		{

			EquationGenerator.llval = 0.0;
			HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
			DNode root1 = EquationGenerator.buildGameTreeRecur(dEPTH_LIMIT, naction, defstrategy, attstrategy, lambda[i], attackfrequency);



			double llh = -EquationGenerator.llval;//computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);

			//double llh = -likeHoodValue(isets, attackfrequency, naction, defstrategy, root, dEPTH_LIMIT, depthinfoset, lambda[i]);

			if(llh<minllh)
			{
				minllh = llh;
				minlambda = lambda[i];
				System.out.println("min lambda : "+minlambda+", minllh : "+ minllh);
			}
		}




		return minlambda;
	}
	
	
	private static double[] estimateLambdaNaiveBinaryS(double[] lambda,
			HashMap<String, int[]> attackfrequency, int naction, HashMap<String, HashMap<String, Double>> defstrategy, int dEPTH_LIMIT, double step) throws Exception {

/*

		Double minllh = Double.MAX_VALUE;
		double minlambda = -1;*/
		
		
		
		
		double low = 0;
		double high = lambda[lambda.length-1];
		double mid = (low+high)/2;


		double lowllh = 0;
		double highllh = 0;
		double midllh = 0;
		
		double[] llh = new double[3];
		double[] index = new double[3];
		


		boolean lowchanged = true;
		boolean highchanged = true;
		boolean midchanged = true;
		
		
		HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
		
		while(low<=high)
		{
			
			
			 mid = (low+high)/2;

			System.out.println(" low "+ low + ", mid "+ mid + ", high "+ high);

			if(lowchanged)
			{

				EquationGenerator.llval = 0.0;
				attstrategy = new HashMap<String, double[]>();
				EquationGenerator.buildGameTreeRecur(dEPTH_LIMIT, naction, defstrategy, attstrategy, low, attackfrequency);
				lowllh = EquationGenerator.llval;
			}


			if(midchanged)
			{

				EquationGenerator.llval = 0.0;
				attstrategy = new HashMap<String, double[]>();
				EquationGenerator.buildGameTreeRecur(dEPTH_LIMIT, naction, defstrategy, attstrategy, mid, attackfrequency);
				midllh = EquationGenerator.llval;
			}

			if(highchanged)
			{

				EquationGenerator.llval = 0.0;
				attstrategy = new HashMap<String, double[]>();
				EquationGenerator.buildGameTreeRecur(dEPTH_LIMIT, naction, defstrategy, attstrategy, high, attackfrequency);
				highllh = EquationGenerator.llval;
			}
			
			
			llh[0] = lowllh;
			llh[1] = midllh;
			llh[2] = highllh;
			
			index[0] = low;
			index[1] = mid;
			index[2] = high;
			
			
			System.out.println(" lowllh "+ lowllh + ", midllh "+ midllh + ", highllh "+ highllh);


			
			
			if(low==high)
			{
				double[] res = {llh[0], low};
				return res;
			}
			else if(midllh > lowllh && midllh > highllh) // triangle
			{
				low = low + step;
				
				high = high - step;
				
				lowchanged = true;
				highchanged = true;
				midchanged = false;

				System.out.println("midllh > lowllh && midllh > highllh.....true");
				//System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);


			}
			else if((lowllh > midllh) && (highllh < midllh))
			{
				high = mid - step;
				
				lowchanged = false;
				highchanged = true;
				midchanged = true;
				
				System.out.println("(lowllh > midllh) && (highllh < midllh).....true");
				//System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);
			}
			else if((highllh > midllh) && (midllh > lowllh))
			{
				low = mid + step;
				
				lowchanged = true;
				highchanged = false;
				midchanged = true;
				
				System.out.println("(highllh < midllh) && (midllh > lowllh)....true");
				//System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);
			}
			else if(midllh < lowllh && midllh < highllh)
			{
				if(lowllh>highllh)
				{
					high = mid - step;
					
					lowchanged = false;
					highchanged = true;
					midchanged = true;
					
					System.out.println("midllh < lowllh && midllh < highllh.....lowllh>highllh....true");
					//System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);
				}
				else if(lowllh<highllh)
				{
					low = mid + step;
					
					lowchanged = true;
					highchanged = false;
					midchanged = true;
					
					System.out.println("midllh < lowllh && midllh < highllh.....lowllh<highllh....true");
					//System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);
				}
			}


		}
		
		
		double max = Double.NEGATIVE_INFINITY;
		int maxindex = 0;
		
		for(int i=0; i<llh.length; i++)
		{
			if(max<llh[i])
			{
				max = llh[i];
				maxindex = i;
			}
		}

		
		
		
		
		
		
		
		

		/*for(int i=0; i<lambda.length; i++)
		{

			EquationGenerator.llval = 0.0;
			HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
			DNode root1 = EquationGenerator.buildGameTreeRecur(dEPTH_LIMIT, naction, defstrategy, attstrategy, lambda[i], attackfrequency);



			double llh = -EquationGenerator.llval;//computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);

			//double llh = -likeHoodValue(isets, attackfrequency, naction, defstrategy, root, dEPTH_LIMIT, depthinfoset, lambda[i]);

			if(llh<minllh)
			{
				minllh = llh;
				minlambda = lambda[i];
				System.out.println("min lambda : "+minlambda+", minllh : "+ minllh);
			}
		}

*/

		double [] res= {max, index[maxindex]};

		return res;
	}
	
	
	private static double defBestResponseValue(double[] lambda,
			HashMap<String, int[]> attackfrequency, int naction, HashMap<String, HashMap<String, Double>> defstrategy, int dEPTH_LIMIT, double step) throws Exception {


		double high = lambda[lambda.length-1];
		


		HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();

		AdversaryModelExps.defbr = 0;

		
		attstrategy = new HashMap<String, double[]>();
		EquationGenerator.buildGameTreeRecurDefBR(dEPTH_LIMIT, naction, defstrategy, attstrategy, high, attackfrequency);
		


		double  res = AdversaryModelExps.defbr;

		return res;
	}
	
	
	
	private static double[] estimateLambdaNaiveBinaryDouble(double[] lambda,
			HashMap<String, double[]> attackfrequency, int naction, HashMap<String, HashMap<String, Double>> defstrategy, int dEPTH_LIMIT, double step) throws Exception {

/*

		Double minllh = Double.MAX_VALUE;
		double minlambda = -1;*/
		
		
		
		
		double low = 0;
		double high = lambda[lambda.length-1];
		double mid = (low+high)/2;


		double lowllh = 0;
		double highllh = 0;
		double midllh = 0;
		
		double[] llh = new double[3];
		double[] index = new double[3];
		


		boolean lowchanged = true;
		boolean highchanged = true;
		boolean midchanged = true;
		
		
		HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
		
		while(low<=high)
		{
			
			
			 mid = (low+high)/2;

			System.out.println(" low "+ low + ", mid "+ mid + ", high "+ high);

			if(lowchanged)
			{

				EquationGenerator.llval = 0.0;
				attstrategy = new HashMap<String, double[]>();
				EquationGenerator.buildGameTreeRecurDouble(dEPTH_LIMIT, naction, defstrategy, attstrategy, low, attackfrequency);
				lowllh = EquationGenerator.llval;
			}


			if(midchanged)
			{

				EquationGenerator.llval = 0.0;
				attstrategy = new HashMap<String, double[]>();
				EquationGenerator.buildGameTreeRecurDouble(dEPTH_LIMIT, naction, defstrategy, attstrategy, mid, attackfrequency);
				midllh = EquationGenerator.llval;
			}

			if(highchanged)
			{

				EquationGenerator.llval = 0.0;
				attstrategy = new HashMap<String, double[]>();
				EquationGenerator.buildGameTreeRecurDouble(dEPTH_LIMIT, naction, defstrategy, attstrategy, high, attackfrequency);
				highllh = EquationGenerator.llval;
			}
			
			
			llh[0] = lowllh;
			llh[1] = midllh;
			llh[2] = highllh;
			
			index[0] = low;
			index[1] = mid;
			index[2] = high;
			
			
			System.out.println(" lowllh "+ lowllh + ", midllh "+ midllh + ", highllh "+ highllh);


			
			
			if(low==high)
			{
				double[] res = {llh[0], low};
				return res;
			}
			else if(midllh > lowllh && midllh > highllh) // triangle
			{
				low = low + step;
				
				high = high - step;
				
				lowchanged = true;
				highchanged = true;
				midchanged = false;

				System.out.println("midllh > lowllh && midllh > highllh.....true");
				//System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);


			}
			else if((lowllh > midllh) && (highllh < midllh))
			{
				high = mid - step;
				
				lowchanged = false;
				highchanged = true;
				midchanged = true;
				
				System.out.println("(lowllh > midllh) && (highllh < midllh).....true");
				//System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);
			}
			else if((highllh > midllh) && (midllh > lowllh))
			{
				low = mid + step;
				
				lowchanged = true;
				highchanged = false;
				midchanged = true;
				
				System.out.println("(highllh < midllh) && (midllh > lowllh)....true");
				//System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);
			}
			else if(midllh < lowllh && midllh < highllh)
			{
				if(lowllh>highllh)
				{
					high = mid - step;
					
					lowchanged = false;
					highchanged = true;
					midchanged = true;
					
					System.out.println("midllh < lowllh && midllh < highllh.....lowllh>highllh....true");
					//System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);
				}
				else if(lowllh<highllh)
				{
					low = mid + step;
					
					lowchanged = true;
					highchanged = false;
					midchanged = true;
					
					System.out.println("midllh < lowllh && midllh < highllh.....lowllh<highllh....true");
					//System.out.println(" low "+ low + ", high "+ high + ", mid "+ mid);
				}
			}


		}
		
		
		double max = Double.NEGATIVE_INFINITY;
		int maxindex = 0;
		
		for(int i=0; i<llh.length; i++)
		{
			if(max<llh[i])
			{
				max = llh[i];
				maxindex = i;
			}
		}

		
		
		
		
		
		
		
		

		/*for(int i=0; i<lambda.length; i++)
		{

			EquationGenerator.llval = 0.0;
			HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
			DNode root1 = EquationGenerator.buildGameTreeRecur(dEPTH_LIMIT, naction, defstrategy, attstrategy, lambda[i], attackfrequency);



			double llh = -EquationGenerator.llval;//computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);

			//double llh = -likeHoodValue(isets, attackfrequency, naction, defstrategy, root, dEPTH_LIMIT, depthinfoset, lambda[i]);

			if(llh<minllh)
			{
				minllh = llh;
				minlambda = lambda[i];
				System.out.println("min lambda : "+minlambda+", minllh : "+ minllh);
			}
		}

*/
		double[] res = {max, index[maxindex]};
		return res;

		//return index[maxindex];
	}



	private static double[] estimateOmegaNaive(double lambda,
			HashMap<String, int[]> attackfrequency, int naction, HashMap<String, HashMap<String, Double>> defstrategy,
			int dEPTH_LIMIT, double[] w1, double[] w2, double[] w3, double[] w4, int nusers) throws Exception {



		Double minllh = Double.MAX_VALUE;
		//double minlambda = -1;
		double mw1 = -9999;
		double mw2 = -9999;
		double mw3 = -9999;
		double mw4 = -9999;

		for(int i=0; i<w1.length; i++)
		{

			for(int j=0; j<w2.length; j++)
			{


				for(int k=0; k<w3.length; k++)
				{


					for(int l=0; l<w4.length; l++)
					{


						double omega[] = {w1[i], w2[j], w3[k], w4[l]};
						EquationGenerator.llval = 0.0;
						HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
						DNode root1 = EquationGenerator.buildGameTreeRecurSUQR(dEPTH_LIMIT, naction, defstrategy, attstrategy, lambda, attackfrequency, omega);



						double llh = -EquationGenerator.llval/nusers;//computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);

						//double llh = -likeHoodValue(isets, attackfrequency, naction, defstrategy, root, dEPTH_LIMIT, depthinfoset, lambda[i]);

						if(llh<minllh)
						{
							minllh = llh;

							mw1 = w1[i];
							mw2 = w2[j];
							mw3 = w3[k];
							mw4 = w4[l];


							//System.out.println("minllh "+minllh +", min w1 : "+mw1+", w2 : "+ mw2+ " w3 : "+mw3 + ", w4 : "+ mw4);
						}
					}

				}
			}
		}




		return new double[] {mw1, mw2, mw3, mw4, minllh};
	}
	
	
	
	
	private static double[] estimateOmegaNaiveParallel(double lambda,
			HashMap<String, int[]> attackfrequency, int naction, HashMap<String, HashMap<String, Double>> defstrategy,
			int dEPTH_LIMIT, double[] w1, double[] w2, double[] w3, double[] w4) throws Exception {



		//Double minllh = Double.MAX_VALUE;
		//double minlambda = -1;
		double mw1 = -9999;
		double mw2 = -9999;
		double mw3 = -9999;
		double mw4 = -9999;
		
		
		int totalcomb = w1.length*w2.length*w3.length*w4.length;
		
		
		//System.out.println("Total combination "+ totalcomb);
		
		int combcount = 0;
		
		int cores = Runtime.getRuntime().availableProcessors();
		
		SUQRTreeGeneratorParallel thrd1 [] = new SUQRTreeGeneratorParallel[cores];
		
		int peromega = totalcomb/cores;
		
		int mod = 0;
		
		HashMap<Integer, Integer> omegapercore = new HashMap<Integer, Integer>();
		for(int i=0; i<cores; i++)
		{
			omegapercore.put(i, peromega);
		}
		
		if((totalcomb % cores) != 0)
		{
			mod = (totalcomb % cores);
		}
		
		int counter = 0;
		while(mod>0)
		{
			omegapercore.put(counter, peromega+1);
			counter++;
			mod--;
			
		}

		//int i=0,j=0,k=0,l=0;
		double[][] ws = new double[omegapercore.get(0)][4];
		int core = 0;
		//for(int core=0; core<cores; core++)
		{
			combcount = 0;
			

			int m =0;
			
			
			
			for(double v1: w1)
			{
				

				for(double v2: w2)
				{
					

					for(double v3: w3)
					{
						

						for(double v4: w4)
						{
							
								ws[m][0] = v1;
								ws[m][1] = v2;
								ws[m][2] = v3;
								ws[m][3] = v4;
								m++;
								combcount++;
								
								if(combcount==omegapercore.get(core))
								{

									//System.out.println("thread started..."+ core);

									thrd1[core] = new SUQRTreeGeneratorParallel(core+"", dEPTH_LIMIT, AdversaryModel.suqrw4 , ws, defstrategy, attackfrequency, lambda);
									thrd1[core].start();
									core++;
									m=0;
									combcount = 0;
									
									if(omegapercore.containsKey(core))
									{
										ws = new double[omegapercore.get(core)][4];
									}
								}
								
							
							
						}
						
					}
					
				}
				
			}

			



		}
		
		
		for(core=0; core<cores; core++)
		{
			thrd1[core].t.join();
		}
		
		
		double minllh = Double.POSITIVE_INFINITY;
		int mincore = 0;
		
		
		for(core=0; core<cores; core++)
		{
			//double tmpllh = thrd1[core].minllh;
			
			System.out.println("thrd "+ core + ", llh "+ thrd1[core].minllh );
			double[] w = thrd1[core].optimumomega;
			System.out.println("thrd "+ core + ", w1 "+ w[0] + ", w2 "+ w[1] + ", w3 "+ w[2] + ", w4 "+ w[3] );
		}
		
		
		
		for(core=0; core<cores; core++)
		{
			double tmpllh = thrd1[core].minllh;
			
			if(minllh>tmpllh)
			{
				mincore = core;
				minllh = tmpllh;
				
			}
		}
		
		System.out.println("***************************minthrd "+ mincore + ", llh "+ thrd1[mincore].minllh );
		double[] w = thrd1[mincore].optimumomega;
		System.out.println("thrd "+ mincore + ", w1 "+ w[0] + ", w2 "+ w[1] + ", w3 "+ w[2] + ", w4 "+ w[3] );
		
		double res[] = new double[thrd1[mincore].optimumomega.length+1];
		
		int i=0;
		for(; i<thrd1[mincore].optimumomega.length; i++)
		{
			res[i] = thrd1[mincore].optimumomega[i];
		}
		res[i] = minllh;
		

		return res;
	}
	
	

	private static double[] estimateOmegaParallelBatchJob(double lambda,
			HashMap<String, int[]> attackfrequency, int naction, HashMap<String, HashMap<String, Double>> defstrategy,
			int dEPTH_LIMIT, double[] w1, double[] w2, double[] w3, double[] w4/*, double[] w5*/) throws Exception {



		//Double minllh = Double.MAX_VALUE;
		//double minlambda = -1;
		double mw1 = -9999;
		double mw2 = -9999;
		double mw3 = -9999;
		double mw4 = -9999;
		
		int numberofomega = 4;
		
		
		int totalcomb = w1.length*w2.length*w3.length*w4.length/**w5.length*/;
		
		
		//System.out.println("Total combination "+ totalcomb);
		
		int combcount = 0;
		
		int cores = Runtime.getRuntime().availableProcessors();
		
		SUQRTreeGeneratorParallel thrd1 [] = new SUQRTreeGeneratorParallel[cores];
		
		int peromega = totalcomb/cores;
		
		int mod = 0;
		
		HashMap<Integer, Integer> omegapercore = new HashMap<Integer, Integer>();
		for(int i=0; i<cores; i++)
		{
			omegapercore.put(i, peromega);
		}
		
		if((totalcomb % cores) != 0)
		{
			mod = (totalcomb % cores);
		}
		
		int counter = 0;
		while(mod>0)
		{
			omegapercore.put(counter, peromega+1);
			counter++;
			mod--;
			
		}

		//int i=0,j=0,k=0,l=0;
		double[][] ws = new double[omegapercore.get(0)][numberofomega];
		int core = 0;
		//for(int core=0; core<cores; core++)
		
			combcount = 0;
			

			int m =0;
			
			Date start = new Date();
			long l1 = start.getTime();

			
			for(double v1: w1)
			{
				

				for(double v2: w2)
				{
					

					for(double v3: w3)
					{
						

						for(double v4: w4)
						{
							//for(double v5: w5)
							{
								ws[m][0] = v1;
								ws[m][1] = v2;
								ws[m][2] = v3;
								ws[m][3] = v4;
								//ws[m][4] = v5;
								m++;
								combcount++;
								
								if(combcount==omegapercore.get(core))
								{

									//System.out.println("thread started..."+ core);

									thrd1[core] = new SUQRTreeGeneratorParallel(core+"", dEPTH_LIMIT, AdversaryModel.suqrw4 , ws, defstrategy, attackfrequency, lambda);
									thrd1[core].start();
									core++;
									m=0;
									combcount = 0;
									
									if(omegapercore.containsKey(core))
									{
										ws = new double[omegapercore.get(core)][numberofomega];
									}
									//break;
								}
							}
							
							//break;
						}
						//break;
						
					}
					//break;
					
				}
				//break;
				
			}

			



		
		
		
		for(core=0; core<cores; core++)
		{
			thrd1[core].t.join();
		}
		
		
		Date stop = new Date();
		long l2 = stop.getTime();
		long diff = l2 - l1;
		
		System.out.println("runtime "+diff);
		
		
		double minllh = Double.POSITIVE_INFINITY;
		int mincore = 0;
		
		
		for(core=0; core<cores; core++)
		{
			//double tmpllh = thrd1[core].minllh;
			
			//System.out.println("thrd "+ core + ", llh "+ thrd1[core].minllh );
			double[] w = thrd1[core].optimumomega;
			//System.out.println("thrd "+ core + ", w1 "+ w[0] + ", w2 "+ w[1] + ", w3 "+ w[2] + ", w4 "+ w[3] );
		}
		
		
		
		for(core=0; core<cores; core++)
		{
			double tmpllh = thrd1[core].minllh;
			
			if(minllh>tmpllh)
			{
				mincore = core;
				minllh = tmpllh;
				
			}
		}
		
		//System.out.println("***************************minthrd "+ mincore + ", llh "+ thrd1[mincore].minllh );
		double[] w = thrd1[mincore].optimumomega;
		//System.out.println("thrd "+ mincore + ", w1 "+ w[0] + ", w2 "+ w[1] + ", w3 "+ w[2] + ", w4 "+ w[3] );
		
		double res[] = new double[thrd1[mincore].optimumomega.length+1];
		
		int i=0;
		for(; i<thrd1[mincore].optimumomega.length; i++)
		{
			res[i] = thrd1[mincore].optimumomega[i];
		}
		res[i] = minllh;
		

		return res;
	}




	private static double[] estimatePTParams(
			HashMap<String, int[]> attackfrequency, int naction, HashMap<String, HashMap<String, Double>> defstrategy, int dEPTH_LIMIT, 
			double[] alpha, double[] beta, double[] theta, double[] gamma) throws Exception {

		double params [] = new double[4];

		Double maxptval = Double.NEGATIVE_INFINITY;
		double maxalpha = -1;
		double maxbeta = -1;
		double maxtheta = -1;
		double maxgamma = -1;

		for(int i=0; i<alpha.length; i++)
		{

			for(int j=0; j<beta.length; j++)
			{

				for(int k=0; k<theta.length; k++)
				{

					for(int l=0; l<gamma.length; l++)
					{
						EquationGenerator.ptval = 0.0;
						HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
						DNode root1 = EquationGenerator.buildGameTreeRecurPT(dEPTH_LIMIT, naction, defstrategy, attstrategy, attackfrequency, alpha[i], beta[j], theta[k], gamma[l]);

						//DNode root1 = EquationGenerator.buildGameTreeRecurPT(dEPTH_LIMIT, naction, defstrategy, attstrategy, attackfrequency, 0.88, 0.88, 2.25, 0.61);


						double ptval = EquationGenerator.ptval;//computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);


						System.out.println("ptval "+ ptval + ", maxptval "+ maxptval);

						//double llh = -likeHoodValue(isets, attackfrequency, naction, defstrategy, root, dEPTH_LIMIT, depthinfoset, lambda[i]);

						System.out.println("cur alpha : "+alpha[i]+", curbeta : "+ beta[j] + ", theta "+ theta[k] + ", gamma "+ gamma[l]);
						System.out.println("max alpha : "+maxalpha+", maxbeta : "+ maxbeta + ", maxtheta "+ maxtheta + ", maxgamma "+ maxgamma);

						if(ptval>maxptval)
						{
							maxptval = ptval;

							maxalpha = alpha[i];
							maxbeta = beta[j];
							maxtheta = theta[k];
							maxgamma = gamma[l];



						}
					}
				}
			}
		}

		params[0] = maxalpha;
		params[1] = maxbeta;
		params[2] = maxtheta;
		params[3] = maxgamma;

		return params;
	}


	

	private static double likeHoodValue(HashMap<String, InfoSet> isets, HashMap<String, int[]> attackfrequency,
			int naction, HashMap<String, HashMap<String, Double>> strategy, DNode root, int dEPTH_LIMIT,
			HashMap<Integer, ArrayList<String>> depthinfoset, double lambda) throws Exception {


		System.out.println("*****************lambda = "+ lambda+"*******************\n");

		EquationGenerator.computeAttackerBestResponse(isets, attackfrequency, naction, strategy, root, dEPTH_LIMIT, depthinfoset, lambda);
		/**
		 * print attacker strategy for different information sets and sequences
		 */
		HashMap<String, double[]> attackstrategy = EquationGenerator.prepareAttackerStrategy(depthinfoset, isets, naction);



		double loglikelihhoodvalue = computeLogLikeliHoodValue(attackfrequency, attackstrategy, naction);

		System.out.println("*****************lambda = "+ lambda+"   llval "+loglikelihhoodvalue+"\n");


		return loglikelihhoodvalue;
	}

	private static double[] generateLambdaArray(double minlambda, double maxlambda, double step) {



		int size = (int)Math.ceil((maxlambda-minlambda)/step);
		double arr[] = new double[size];
		arr[0] = minlambda;

		for(int i=1; i<size; i++)
		{
			arr[i] =  /*((arr[i-1])*100)/100*/ arr[i-1] +step;

			arr[i] = (arr[i]*100)/100;

		}





		return arr;
	}


	private static double[] generateAlphaArray(double minalpha, double maxalpha, double step) {



		int size = (int)Math.ceil((maxalpha-minalpha)/step);
		double arr[] = new double[size+1];
		arr[0] = minalpha;

		for(int i=1; i<=size; i++)
		{
			double tmp = arr[i-1] +step;
			if(tmp==0)
			{
				arr[i] =  0 +step;

				arr[i] = (arr[i]*100)/100;

				arr[i] = arr[i];
			}
			else
			{
				arr[i] =  /*((arr[i-1])*100)/100*/ arr[i-1] +step;

				arr[i] = (arr[i]*100)/100;

				arr[i] = arr[i];
			}

		}





		return arr;
	}


	private static double[] generateBetaArray(double min, double max, double step) {



		int size = (int)Math.ceil((max-min)/step);
		double arr[] = new double[size+1];
		arr[0] = min;

		for(int i=1; i<=size; i++)
		{
			arr[i] =  /*((arr[i-1])*100)/100*/ arr[i-1] +step;
			
			if(arr[i]==0)
			{
				arr[i] = step;
			}

			arr[i] = (arr[i]*100)/100;

		}





		return arr;
	}


	private static double[] generateGammaArray(double min, double max, double step) {



		int size = (int)Math.ceil((max-min)/step);
		double arr[] = new double[size+1];
		arr[0] = min;

		for(int i=1; i<=size; i++)
		{
			arr[i] =  /*((arr[i-1])*100)/100*/ arr[i-1] +step;
			
			if(arr[i]==0)
			{
				arr[i] = step;
			}

			arr[i] = (arr[i]*100)/100;

		}





		return arr;
	}

	private static double computeLogLikeliHoodValue(HashMap<String, int[]> attackfrequency,
			HashMap<String, double[]> attackstrategy, int naction) throws Exception {



		double llvalsum = 0.0;

		for(String seq: attackfrequency.keySet())
		{
			System.out.println("seq : "+ seq + "\n");
			if(attackstrategy.containsKey(seq))
			{
				int[] freq = attackfrequency.get(seq);
				double[] attstrtgy = attackstrategy.get(seq);
				for(int a=0; a<naction; a++)
				{
					if(freq[a]>0 && attstrtgy[a]>0)
					{
						double tmpllval = freq[a]* Math.log(attstrtgy[a]);
						System.out.println("llval : "+ tmpllval);
						llvalsum += tmpllval;
					}

				}
				System.out.println("llvalsum : "+ llvalsum);
			}
			else
			{
				System.out.println("DOes not have the sequence");
				//throw new Exception("DOes not have the sequence");
				int[] freq = attackfrequency.get(seq);
				double[] attstrtgy = {1, 0, 0, 0, 0, 0};


				double tmpllval = freq[0]* Math.log(attstrtgy[0]);
				System.out.println("llval : "+ tmpllval);
				llvalsum += tmpllval;



				System.out.println("llvalsum : "+ llvalsum);
			}
		}






		return llvalsum;
	}

	private static HashMap<Integer, ArrayList<String>> depthInfoSet(int dEPTH_LIMIT, HashMap<String, InfoSet> isets, int player) {


		HashMap<Integer, ArrayList<String>> depthinfset = new HashMap<Integer, ArrayList<String>>();


		for(int depth = 1; depth<=dEPTH_LIMIT; depth+=2)
		{

			ArrayList<String> ists = new ArrayList<String>();

			for(InfoSet iset: isets.values())
			{
				if(iset.depth==depth && iset.player==player)
				{
					ists.add(iset.id);
				}
			}

			depthinfset.put(depth, ists);

		}


		return depthinfset;
	}

	private static void printAttackFreq(HashMap<String, int[]> attackfrequency) {


		for(String key: attackfrequency.keySet())
		{
			System.out.print(key+ " : ");
			for(int a: attackfrequency.get(key))
			{
				System.out.print(a + ", ");
			}
			System.out.println();
		}



	}

	private static int[][] createGamePlay(int ngames, ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined, int roundlimit, HashMap<String, String> user_seq) {


		int[][] gameplay = new int[users_refined.size()*ngames][2*roundlimit]; 



		int usercount = 0;
		for(String user_id: users_refined)
		{

			String seq = "";
			
			int examplecoutn = 0;
			

			for(ArrayList<String> example: data_refined)
			{
				// get user id
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				// if example is for user_id

				if(usercount==38)
				{
					int v=1;
				}

				if(user_id.equals(tmpuser))
				{
					examplecoutn++;
					
					System.out.println(user_id + ", count  "+ examplecoutn);
					
					
					int gameins = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
					int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
					if(round <= roundlimit)
					{

						String attackeraction = example.get(Headers_minimum.attacker_action.getValue());
						String defenderaction = example.get(Headers_minimum.defender_action.getValue());

						if(attackeraction.equals(" ") || attackeraction.equals(""))
						{
							attackeraction = "5";
						}
						
						if(defenderaction.equals(" ") || defenderaction.equals(""))
						{
							defenderaction = "0";
						}
						
						int attackaction = Integer.parseInt(attackeraction);
						int defendaction = Integer.parseInt(defenderaction);


						if(round==1)
						{

							seq =  defenderaction + ","+ attackeraction;
						}
						else
						{
							seq +=  "," + defenderaction + ","+ attackeraction;
						}



						int defround = 2*(round-1)  ;
						int attackround = 2*(round-1)+1 ;


						gameplay[usercount][defround] = defendaction;
						gameplay[usercount][attackround] = attackaction; // in data round starts from 1. so use round-1

						if(round==roundlimit)
						{
							usercount++;
							//System.out.println("user count "+ usercount);
						}

					}

				}

			}
			user_seq.put(user_id, seq);


		}
		return gameplay;
	}
	
	
	private static int[][] createGamePlayRange(int ngames, ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined, int roundlimit, HashMap<String, String> user_seq) {


		int[][] gameplay = new int[users_refined.size()*ngames][2*roundlimit]; 



		int usercount = 0;
		for(String user_id: users_refined)
		{

			String seq = "";
			
			
			int excount = 0;
			for(ArrayList<String> example: data_refined)
			{
				
				// get user id
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				// if example is for user_id

				

				if(user_id.equals(tmpuser))
				{
					//excount++;
					//System.out.println("user "+ user_id + " exampl "+ excount);
					int gameins = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
					int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
					if(round <= roundlimit)
					{

						String attackeraction = example.get(Headers_minimum.attacker_action.getValue());
						String defenderaction = example.get(Headers_minimum.defender_action.getValue());

						if(attackeraction.equals(" ") || attackeraction.equals(""))
						{
							attackeraction = "5";
						}
						
						if(defenderaction.equals(" ") || defenderaction.equals(""))
						{
							defenderaction = "0";
						}
						
						int attackaction = Integer.parseInt(attackeraction);
						int defendaction = Integer.parseInt(defenderaction);


						if(round==1)
						{

							seq =  defenderaction + ","+ attackeraction;
						}
						else
						{
							seq +=  "," + defenderaction + ","+ attackeraction;
						}



						int defround = 2*(round-1)  ;
						int attackround = 2*(round-1)+1 ;


						gameplay[usercount][defround] = defendaction;
						gameplay[usercount][attackround] = attackaction; // in data round starts from 1. so use round-1

						if(round==roundlimit)
						{
							usercount++;
							//System.out.println("index count "+ usercount);
							if(usercount==116)
							{
								int v=1;
							}
						}

					}

				}

			}
			user_seq.put(user_id, seq);


		}
		return gameplay;
	}
	
	
	
	private static int[][] createGamePlayProgressive(int ngames, ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined, int roundlimit, HashMap<String, String> user_seq) {


		int[][] gameplay = new int[users_refined.size()*ngames][2*roundlimit*ngames]; 



		int usercount = 0;
		for(String user_id: users_refined)
		{

			String seq = "";

			for(ArrayList<String> example: data_refined)
			{
				// get user id
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				// if example is for user_id



				if(user_id.equals(tmpuser))
				{
					int gameins = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
					int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
					if(round <= roundlimit)
					{

						String attackeraction = example.get(Headers_minimum.attacker_action.getValue());
						String defenderaction = example.get(Headers_minimum.defender_action.getValue());

						if(attackeraction.equals(" ") || attackeraction.equals(""))
						{
							attackeraction = "5";
						}
						int attackaction = Integer.parseInt(attackeraction);
						int defendaction = Integer.parseInt(defenderaction);


						if(round==1)
						{

							seq =  defenderaction + ","+ attackeraction;
						}
						else
						{
							seq +=  "," + defenderaction + ","+ attackeraction;
						}



						int defround = (gameins-1)*10 + 2*(round-1)  ;
						int attackround = (gameins-1)*10 + 2*(round-1)+1 ;


						gameplay[usercount][defround] = defendaction;
						gameplay[usercount][attackround] = attackaction; // in data round starts from 1. so use round-1

						if(round==roundlimit)
						{
							usercount++;
						}

					}

				}

			}
			user_seq.put(user_id, seq);


		}
		return gameplay;
	}


	private static int[][] createGamePlay(int ngames, ArrayList<String> users_refined, ArrayList<ArrayList<String>> data_refined, int roundlimit) {


		int[][] gameplay = new int[users_refined.size()*ngames][2*roundlimit]; 



		int usercount = 0;
		for(String user_id: users_refined)
		{
			for(ArrayList<String> example: data_refined)
			{
				// get user id
				String tmpuser = example.get(Headers_minimum.user_id.getValue());
				// if example is for user_id
				if(user_id.equals(tmpuser))
				{
					int gameins = Integer.parseInt(example.get(Headers_minimum.game_instance.getValue()));
					int round = Integer.parseInt(example.get(Headers_minimum.round.getValue()));
					if(round <= roundlimit)
					{

						String attackeraction = example.get(Headers_minimum.attacker_action.getValue());
						String defenderaction = example.get(Headers_minimum.defender_action.getValue());

						if(attackeraction.equals(" ") || attackeraction.equals(""))
						{
							attackeraction = "5";
						}
						int attackaction = Integer.parseInt(attackeraction);
						int defendaction = Integer.parseInt(defenderaction);


						int defround = 2*(round-1)  ;
						int attackround = 2*(round-1)+1 ;


						gameplay[usercount][defround] = defendaction;
						gameplay[usercount][attackround] = attackaction; // in data round starts from 1. so use round-1

						if(round==roundlimit)
						{
							usercount++;
						}

					}

				}

			}


		}
		return gameplay;
	}

	public static HashMap<String, int[]> getAttackCountInDataAttacker(int[][] gameplay, int numberofnodes, int roundlimit)
	{

		HashMap<String, int[]> attackfrequency = new HashMap<String, int[]>();
		
		
		//printGamePl(gameplay);
		

		//for(int[][] gameplay: gamepl )
		{

			for(int r=0; r<roundlimit; r++)
			{
				/**
				 * just count number times attacker attacked different nodes in index=1
				 */

				int kk=1;

				if(r==0) // round 0
				{
					String key = "EMPTY EMPTY";
					int[] tmpfreq = new int[numberofnodes];
					for(int[] play: gameplay)
					{
						tmpfreq[play[1]]++; // index 1 is attacker's game play in round 1
					}
					attackfrequency.put(key, tmpfreq);
				}
				else
				{
					HashMap<String, int[]> tmp_attackfrequency = new HashMap<String, int[]>();
					for(int i=0; i<numberofnodes; i++)
					{
						for(int j=0; j<numberofnodes; j++)
						{
							// for every pre-existing key, add i and j 
							// create new key and count the frequency


							if(r==1) // for round 1
							{
								String key = i + " " + j;
								int[] tmpfreq = computeFreq(key, r, gameplay, numberofnodes);
								attackfrequency.put(key, tmpfreq);
							}
							else if(r>1)
							{




								for(String preexistingkey: attackfrequency.keySet())
								{


									if(!preexistingkey.equals("EMPTY EMPTY"))
									{
										String[] keys = preexistingkey.split(" ");

										String[] p0actions = keys[0].split(",");


										if(p0actions.length == (r-1)) // we want add actions only to the sequence for last round
										{

											String p0key = keys[0]+","+i;
											String p1key = keys[1]+","+j;
											String key = p0key + " " + p1key;

											/*if(key.equals("4,0 0,0"))
											{
												System.out.println("Hi");
											}
*/

											int[] tmpfreq = computeFreq(key, r, gameplay, numberofnodes);
											tmp_attackfrequency.put(key, tmpfreq);
										}
									}
								}

							}
						}
					}

					for(String key: tmp_attackfrequency.keySet())
					{
						attackfrequency.put(key, tmp_attackfrequency.get(key));
					}
				}
			}
		}
		return attackfrequency;
	}
	
	
	
	private static void printGamePl(int[][] gameplay) {
		
		
		
		for(int i=0; i<gameplay.length; i++)
		{
			for(int j=0; j<gameplay[i].length; j++)
			{
				System.out.print(gameplay[i][j]+", ");
			}
			System.out.println();
		}
		
	}

	private static HashMap<String, int[]> getAttackCountInDataDefender(int[][] gameplay, int numberofnodes, int roundlimit)
	{

		HashMap<String, int[]> attackfrequency = new HashMap<String, int[]>();

		//for(int[][] gameplay: gamepl )
		{

			for(int r=0; r<roundlimit; r++)
			{
				/**
				 * just count number times attacker attacked different nodes in index=1
				 */

				int kk=1;

				if(r==0) // round 0
				{
					String key = "EMPTY EMPTY";
					int[] tmpfreq = new int[numberofnodes];
					for(int[] play: gameplay)
					{
						tmpfreq[play[0]]++; // index 1 is attacker's game play in round 1
					}
					attackfrequency.put(key, tmpfreq);
				}
				else
				{
					HashMap<String, int[]> tmp_attackfrequency = new HashMap<String, int[]>();
					for(int i=0; i<numberofnodes; i++)
					{
						for(int j=0; j<numberofnodes; j++)
						{
							// for every pre-existing key, add i and j 
							// create new key and count the frequency


							if(r==1) // for round 1
							{
								String key = i + " " + j;
								int[] tmpfreq = computeFreqDef(key, r, gameplay, numberofnodes);
								attackfrequency.put(key, tmpfreq);
							}
							else if(r>1)
							{




								for(String preexistingkey: attackfrequency.keySet())
								{


									if(!preexistingkey.equals("EMPTY EMPTY"))
									{
										String[] keys = preexistingkey.split(" ");

										String[] p0actions = keys[0].split(",");


										if(p0actions.length == (r-1)) // we want add actions only to the sequence for last round
										{

											String p0key = keys[0]+","+i;
											String p1key = keys[1]+","+j;
											String key = p0key + " " + p1key;

											/*if(key.equals("4,0 0,0"))
											{
												System.out.println("Hi");
											}
*/

											int[] tmpfreq = computeFreqDef(key, r, gameplay, numberofnodes);
											tmp_attackfrequency.put(key, tmpfreq);
										}
									}
								}

							}
						}
					}

					for(String key: tmp_attackfrequency.keySet())
					{
						attackfrequency.put(key, tmp_attackfrequency.get(key));
					}
				}
			}
		}
		return attackfrequency;
	}
	
	
	private static HashMap<String, double[]> getAttackFreqInData(int[][] gameplay, int numberofnodes, int roundlimit)
	{

		HashMap<String, double[]> attackfrequency = new HashMap<String, double[]>();

		//for(int[][] gameplay: gamepl )
		{

			for(int r=0; r<roundlimit; r++)
			{
				/**
				 * just count number times attacker attacked different nodes in index=1
				 */

				int kk=1;

				if(r==0) // round 0
				{
					String key = "EMPTY EMPTY";
					double[] tmpfreq = new double[numberofnodes];
					
					int c=0;
					for(int[] play: gameplay)
					{
						tmpfreq[play[1]]++; // index 1 is attacker's game play in round 1
						c++;
					}
					
					
					for(int i=0; i<tmpfreq.length; i++)
					{
						tmpfreq[i] = tmpfreq[i]/c;
					}
					attackfrequency.put(key, tmpfreq);
				}
				else
				{
					HashMap<String, double[]> tmp_attackfrequency = new HashMap<String, double[]>();
					for(int i=0; i<numberofnodes; i++)
					{
						for(int j=0; j<numberofnodes; j++)
						{
							// for every pre-existing key, add i and j 
							// create new key and count the frequency


							if(r==1) // for round 1
							{
								String key = i + " " + j;
								double[] tmpfreq = computeFreqD(key, r, gameplay, numberofnodes);
								
								double sum = sum(tmpfreq);
								
								if(sum>0)
								{
									attackfrequency.put(key, tmpfreq);
								}
							}
							else if(r>1)
							{




								for(String preexistingkey: attackfrequency.keySet())
								{


									if(!preexistingkey.equals("EMPTY EMPTY"))
									{
										String[] keys = preexistingkey.split(" ");

										String[] p0actions = keys[0].split(",");


										if(p0actions.length == (r-1)) // we want add actions only to the sequence for last round
										{

											String p0key = keys[0]+","+i;
											String p1key = keys[1]+","+j;
											String key = p0key + " " + p1key;

											/*if(key.equals("4,0 0,0"))
											{
												System.out.println("Hi");
											}
*/

											double[] tmpfreq = computeFreqD(key, r, gameplay, numberofnodes);
											
											double sum = sum(tmpfreq);
											
											if(sum>0)
											{
												tmp_attackfrequency.put(key, tmpfreq);
											}
											
											//tmp_attackfrequency.put(key, tmpfreq);
										}
									}
								}

							}
						}
					}

					for(String key: tmp_attackfrequency.keySet())
					{
						attackfrequency.put(key, tmp_attackfrequency.get(key));
					}
				}
			}
		}
		return attackfrequency;
	}
	
	
	private static double sum(double[] tmpfreq) {
		// TODO Auto-generated method stub
		
		double sum = 0;
		
		
		for(double s: tmpfreq)
		{
			sum+=s;
		}
		
		
		return sum;
	}

	private static HashMap<String, double[]> getDefFreqInData(int[][] gameplay, int numberofnodes, int roundlimit)
	{

		HashMap<String, double[]> attackfrequency = new HashMap<String, double[]>();

		//for(int[][] gameplay: gamepl )
		{

			for(int r=0; r<roundlimit; r++)
			{
				/**
				 * just count number times attacker attacked different nodes in index=1
				 */

				int kk=1;

				if(r==0) // round 0
				{
					String key = "EMPTY EMPTY";
					double[] tmpfreq = new double[numberofnodes];
					for(int[] play: gameplay)
					{
						tmpfreq[play[0]]++; // index 1 is attacker's game play in round 1
					}
					attackfrequency.put(key, tmpfreq);
				}
				else
				{
					HashMap<String, double[]> tmp_attackfrequency = new HashMap<String, double[]>();
					for(int i=0; i<numberofnodes; i++)
					{
						for(int j=0; j<numberofnodes; j++)
						{
							// for every pre-existing key, add i and j 
							// create new key and count the frequency


							if(r==1) // for round 1
							{
								String key = i + " " + j;
								double[] tmpfreq = computeFreqDDef(key, r, gameplay, numberofnodes);
								attackfrequency.put(key, tmpfreq);
							}
							else if(r>1)
							{




								for(String preexistingkey: attackfrequency.keySet())
								{


									if(!preexistingkey.equals("EMPTY EMPTY"))
									{
										String[] keys = preexistingkey.split(" ");

										String[] p0actions = keys[0].split(",");


										if(p0actions.length == (r-1)) // we want add actions only to the sequence for last round
										{

											String p0key = keys[0]+","+i;
											String p1key = keys[1]+","+j;
											String key = p0key + " " + p1key;

											/*if(key.equals("4,0 0,0"))
											{
												System.out.println("Hi");
											}
*/

											double[] tmpfreq = computeFreqDDef(key, r, gameplay, numberofnodes);
											tmp_attackfrequency.put(key, tmpfreq);
										}
									}
								}

							}
						}
					}

					for(String key: tmp_attackfrequency.keySet())
					{
						attackfrequency.put(key, tmp_attackfrequency.get(key));
					}
				}
			}
		}
		return attackfrequency;
	}
	

	private static int[] computeFreq(String key, int r, int[][] gameplay, int nactions) {

		// split the string

		String[] keys = key.split(" ");


		int freq[] = new int[nactions];


		String[] defactions = keys[0].split(",");
		String[] attactions = keys[1].split(",");


		int totalplay = 0;

		for(int[] play: gameplay)
		{
			boolean flag = true;
			for(int cur_round=0; cur_round<r; cur_round++)
			{

				int defround = 2*cur_round;
				int attround = 2*(cur_round)+1;

				if( (Integer.parseInt(defactions[cur_round]) != play[defround]) || (Integer.parseInt(attactions[cur_round]) != play[attround]))
				{
					flag = false;
					break;
				}
			}
			if(flag)
			{
				totalplay++;
				int action = play[2*r+1]; // aciton in rth round
				freq[action]++; // increase the frequency
			}
		}
		
		
		return freq;
	}
	
	
	private static int[] computeFreqDef(String key, int r, int[][] gameplay, int nactions) {

		// split the string

		String[] keys = key.split(" ");


		int freq[] = new int[nactions];


		String[] defactions = keys[0].split(",");
		String[] attactions = keys[1].split(",");


		int totalplay = 0;

		for(int[] play: gameplay)
		{
			boolean flag = true;
			for(int cur_round=0; cur_round<r; cur_round++)
			{

				int defround = 2*cur_round;
				int attround = 2*(cur_round)+1;

				if( (Integer.parseInt(defactions[cur_round]) != play[defround]) || (Integer.parseInt(attactions[cur_round]) != play[attround]))
				{
					flag = false;
					break;
				}
			}
			if(flag)
			{
				totalplay++;
				int action = play[2*r]; // aciton in rth round
				freq[action]++; // increase the frequency
			}
		}
		
		
		return freq;
	}
	
	
	private static double[] computeFreqD(String key, int r, int[][] gameplay, int nactions) {

		// split the string

		String[] keys = key.split(" ");


		double freq[] = new double[nactions];


		String[] defactions = keys[0].split(",");
		String[] attactions = keys[1].split(",");


		double totalplay = 0.0;

		for(int[] play: gameplay)
		{
			boolean flag = true;
			for(int cur_round=0; cur_round<r; cur_round++)
			{

				int defround = 2*cur_round;
				int attround = 2*(cur_round)+1;

				if( (Integer.parseInt(defactions[cur_round]) != play[defround]) || (Integer.parseInt(attactions[cur_round]) != play[attround]))
				{
					flag = false;
					break;
				}
			}
			if(flag)
			{
				totalplay++;
				int action = play[2*r+1]; // aciton in rth round
				freq[action]++; // increase the frequency
			}
		}
		
		for(int i=0; i<freq.length; i++)
		{
			if(totalplay>0)
			{
				freq[i] = freq[i]/totalplay;
			}
		}



		//int p=1;

		return freq;
	}
	
	
	private static double[] computeFreqDDef(String key, int r, int[][] gameplay, int nactions) {

		// split the string

		String[] keys = key.split(" ");


		double freq[] = new double[nactions];


		String[] defactions = keys[0].split(",");
		String[] attactions = keys[1].split(",");


		int totalplay = 0;

		for(int[] play: gameplay)
		{
			boolean flag = true;
			for(int cur_round=0; cur_round<r; cur_round++)
			{

				int defround = 2*cur_round;
				int attround = 2*(cur_round)+1;

				if( (Integer.parseInt(defactions[cur_round]) != play[defround]) || (Integer.parseInt(attactions[cur_round]) != play[attround]))
				{
					flag = false;
					break;
				}
			}
			if(flag)
			{
				totalplay++;
				int action = play[2*r]; // aciton in rth round
				freq[action]++; // increase the frequency
			}
		}
		
		for(int i=0; i<freq.length; i++)
		{
			freq[i] = freq[i]/totalplay;
		}



		//int p=1;

		return freq;
	}

	public static void generateApprximateNEPlay() throws Exception {



		/**
		 * create e-NE attacker strategy against defender strategy
		 */


		HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI_v2.txt");
		//HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategyv1("g5d5_FI.txt");


		//HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();

		//double tmplambda = 0.6;

		double nelambda = .1;//.15;
		int DEPTH_LIMIT = 10; // needs to be 10 for our experiment
		int naction = 6;
		//double minlambda = .13;
		//double maxlambda = .2;
		//double step = .01;
		int nexamples = 100;
		//double[] lambda = generateLambdaArray(minlambda, maxlambda, step);
		int numberofnodes = 6;
		int roundlimit = 5;



		HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);
		EquationGenerator.buildGameTreeRecurNE(DEPTH_LIMIT, naction, defstrategy, attstrategy, nelambda, noderewards);








		/**
		 * play game and compute points
		 */




		int[][] negameplay = new int[nexamples][DEPTH_LIMIT];
		int[][] negameplaydef = new int[nexamples][DEPTH_LIMIT];

		double points = playGame(defstrategy, attstrategy, naction, roundlimit, negameplay, nexamples, noderewards, negameplaydef);


		//double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);



		System.out.println("lambda "+ nelambda + ", points "+ points);


		HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(negameplay, numberofnodes, 5);

		int attackcount[] = getAttackFrequency(negameplay, numberofnodes, roundlimit);
		int defcount[] = getDefFrequency(negameplay, numberofnodes, roundlimit);







		/**
		 * next estimate lambda for NE player
		 */


		double estimatedlambdanaive = nelambda;// estimateLambdaNaiveBinaryS(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);


		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda-sim.csv"),true));

			pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(4/1),nodeB(5/3),NodeC(10/9),nodeD(8/8),NodeE(15/20),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");

			pw.append("0,"+nexamples +","+ estimatedlambdanaive+","+points+","+0+","+0+","+0+",");

			int index=0;
			for(int c: attackcount)
			{
				pw.append(c+"");
				if(index<(attackcount.length-1))
				{
					pw.append(",");
				}

				index++;
			}
			pw.append("\n");

			//pw.close();
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}
		
		
		
		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("lambda-sim.csv"),true));

			//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");

			pw.append("1,"+nexamples +","+ estimatedlambdanaive+","+points+","+0+","+0+","+0+",");

			int index=0;
			for(int c: defcount)
			{
				pw.append(c+"");
				if(index<(defcount.length-1))
				{
					pw.append(",");
				}

				index++;
			}
			pw.append("\n");

			//pw.close();
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}



		/*


		 // how many clusters you want
		int numberofnodes = 6;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender
		ArrayList<String> users_refined = refineUser(data, -1, 1);

		ArrayList<ArrayList<String>>  data_refined = refineData(data,1, users_refined);

		//double[][] examples = prepareExamplesDTScorePoints(data_refined, users_refined);
		double[][] examples = prepareExamplesNodeCostPoint(data_refined, users_refined);
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);

		int k= 2;

		List<Integer>[] clusters = Weka.clusterUsers(k, normalizedexamples);

		//List<Integer>[] clusters = Weka.clusterUsers(normalizedexamples);



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);




		 *//**
		 * next use weka to cluster
		 *//*

		//printClusters(clusters);

		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();


		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

			pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}







		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			//users_groups = users_refined;

		  *//**
		  * get attack count for different information set
		  *//*



			int[][] gameplay = createGamePlay(3, users_groups, data_refined, 5);
			int attackcount[] = getAttackFrequency(users_groups, data_refined, numberofnodes);
			HashMap<String, int[]> attackfrequency = getAttackCountInData(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			//HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");


			//HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();

			//double tmplambda = 0.6;


			double estimatedlambdanaive = estimateLambdaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			System.out.println("Estmiated lambda "+ estimatedlambdanaive);




			DNode root1 = EquationGenerator.buildGameTreeRecur(DEPTH_LIMIT, naction, defstrategy, attstrategy, tmplambda);

			computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);





			DNode root = EquationGenerator.buildGameTree(DEPTH_LIMIT, naction);



			HashMap<String, ArrayList<DNode>> I = EquationGenerator.prepareInformationSets(root, DEPTH_LIMIT, naction);
			EquationGenerator.printInfoSet(I);
			HashMap<String, InfoSet> isets = EquationGenerator.prepareInfoSet(I);
		   *//**
		   * compute information sets according to depth
		   *//*
			HashMap<Integer, ArrayList<String>> depthinfoset = depthInfoSet(DEPTH_LIMIT, isets,1); // for player 1: attacker, player 0 is defender
			EquationGenerator.printISets(isets);


			EquationGenerator.updateTreeWithDefStartegy(isets, root, strategy, naction);



			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);







			// use attackstrategy to compute lambda




			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);

				sumscore += getUserScore(tmpusr, data_refined);

				sum_mscore += getPersonalityScore(tmpusr, data_refined, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore /= users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(cluster+","+users_groups.size()+","+ estimatedlambdanaive+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			break;

		}
		    */
	}



	public static void errorInQRLambdaEst() throws Exception {



		/**
		 * create e-NE attacker strategy against defender strategy
		 */


		HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");


		//HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();

		//double tmplambda = 0.6;

		double nelambda = .15;//.15;
		int DEPTH_LIMIT = 10; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = .13;
		double maxlambda = .2;
		double step = .01;
		int nexamples = 300;
		double[] lambda = generateLambdaArray(minlambda, maxlambda, step);
		int numberofnodes = 6;
		int roundlimit = 5;

		int ntrials = 10;

		double[] estlambda = new double[ntrials];






		HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);
		EquationGenerator.buildGameTreeRecurNE(DEPTH_LIMIT, naction, defstrategy, attstrategy, nelambda, noderewards);








		/**
		 * play game and compute points
		 */



		for(int t=0; t<ntrials; t++)
		{


			int[][] negameplay = new int[nexamples][DEPTH_LIMIT];
			int[][] negameplaydef = new int[nexamples][DEPTH_LIMIT];

			double points = playGame(defstrategy, attstrategy, naction, DEPTH_LIMIT/2, negameplay, nexamples, noderewards, negameplaydef);


			//double attpoints = EquationGenerator.computeAttackerReward(noderewards, user_seq, user_reward);



			System.out.println("lambda "+ nelambda + ", points "+ points);


			HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(negameplay, numberofnodes, 5);

			int attackcount[] = getAttackFrequency(negameplay, numberofnodes, roundlimit);







			/**
			 * next estimate lambda for NE player
			 */


			double estimatedlambdanaive = estimateLambdaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			estlambda[t] = estimatedlambdanaive;


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

				//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");

				pw.append("0,"+nexamples +","+ estimatedlambdanaive+","+points+","+0+","+0+","+0+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				//pw.close();
				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}

		}


		double e=0.0;

		for(int t=0; t<ntrials; t++)
		{
			double tmpe = ((nelambda-estlambda[t])/nelambda)*100.00;

			System.out.println("trials "+ t  + ", error "+ tmpe +"% ");

			e+=tmpe;
		}

		e /= ntrials;

		System.out.println("avg error "+ e);



		/*


		 // how many clusters you want
		int numberofnodes = 6;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender
		ArrayList<String> users_refined = refineUser(data, -1, 1);

		ArrayList<ArrayList<String>>  data_refined = refineData(data,1, users_refined);

		//double[][] examples = prepareExamplesDTScorePoints(data_refined, users_refined);
		double[][] examples = prepareExamplesNodeCostPoint(data_refined, users_refined);
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);

		int k= 2;

		List<Integer>[] clusters = Weka.clusterUsers(k, normalizedexamples);

		//List<Integer>[] clusters = Weka.clusterUsers(normalizedexamples);



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);




		 *//**
		 * next use weka to cluster
		 *//*

		//printClusters(clusters);

		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();


		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

			pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}







		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			//users_groups = users_refined;

		  *//**
		  * get attack count for different information set
		  *//*



			int[][] gameplay = createGamePlay(3, users_groups, data_refined, 5);
			int attackcount[] = getAttackFrequency(users_groups, data_refined, numberofnodes);
			HashMap<String, int[]> attackfrequency = getAttackCountInData(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			//HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");


			//HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();

			//double tmplambda = 0.6;


			double estimatedlambdanaive = estimateLambdaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			System.out.println("Estmiated lambda "+ estimatedlambdanaive);




			DNode root1 = EquationGenerator.buildGameTreeRecur(DEPTH_LIMIT, naction, defstrategy, attstrategy, tmplambda);

			computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);





			DNode root = EquationGenerator.buildGameTree(DEPTH_LIMIT, naction);



			HashMap<String, ArrayList<DNode>> I = EquationGenerator.prepareInformationSets(root, DEPTH_LIMIT, naction);
			EquationGenerator.printInfoSet(I);
			HashMap<String, InfoSet> isets = EquationGenerator.prepareInfoSet(I);
		   *//**
		   * compute information sets according to depth
		   *//*
			HashMap<Integer, ArrayList<String>> depthinfoset = depthInfoSet(DEPTH_LIMIT, isets,1); // for player 1: attacker, player 0 is defender
			EquationGenerator.printISets(isets);


			EquationGenerator.updateTreeWithDefStartegy(isets, root, strategy, naction);



			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);







			// use attackstrategy to compute lambda




			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);

				sumscore += getUserScore(tmpusr, data_refined);

				sum_mscore += getPersonalityScore(tmpusr, data_refined, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore /= users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(cluster+","+users_groups.size()+","+ estimatedlambdanaive+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			break;

		}
		    */
	}




	public static void generatePTPlay() throws Exception {



		/**
		 * create e-NE attacker strategy against defender strategy
		 */


		HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");


		//HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();

		//double tmplambda = 0.6;

		double ptalpha = 0.88;
		double ptbeta = 0.88;
		double pttheta = 2.25;
		double ptgamma = 0.61;



		/*double ptalpha = 1;
		double ptbeta = .6;
		double pttheta = 2.2;
		double ptgamma = 0.6;*/

		int DEPTH_LIMIT = 1; // needs to be 10 for our experiment
		int naction = 6;
		double minlambda = 0;
		double maxlambda = 5;
		double step = .5;
		int nexamples = 61;
		//double[] lambda = generateLambdaArray(minlambda, maxlambda, step);
		int numberofnodes = 6;
		int roundlimit = 5;




		double minalpha = .01;
		double maxalpha = 1;
		double stepalpha = .01;


		double minbeta = 0;
		double maxbeta = .99;
		double stepbeta = .01;


		double mintheta = 1;
		double maxtheta = 3;
		double steptheta = .1;


		double mingamma = .01;
		double maxgamma = .99;
		double stepgamma = .01;





		double[] alpha = generateAlphaArray(minalpha, maxalpha, stepalpha);


		double[] beta = generateBetaArray(minbeta, maxbeta, stepbeta);


		double[] theta = generateBetaArray(mintheta, maxtheta, steptheta);


		double[] gamma = generateGammaArray(mingamma, maxgamma, stepgamma);



		HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);
		EquationGenerator.buildGameTreeRecurPT(DEPTH_LIMIT, naction, defstrategy, attstrategy, ptalpha, ptbeta, pttheta, ptgamma);








		/**
		 * play game and compute points
		 */




		int[][] negameplay = new int[nexamples][DEPTH_LIMIT];
		int[][] negameplaydef = new int[nexamples][DEPTH_LIMIT];

		double points = playGame(defstrategy, attstrategy, naction, DEPTH_LIMIT/2, negameplay, nexamples, noderewards, negameplaydef);


		System.out.println("max alpha : "+ptalpha+", maxbeta : "+ ptbeta + ", maxtheta "+ pttheta + ", gamma "+ ptgamma);


		HashMap<String, int[]> attackfrequency = getAttackCountInDataAttacker(negameplay, numberofnodes, 5);

		int attackcount[] = getAttackFrequency(negameplay, numberofnodes, roundlimit);












		//double[] estimatedptparams = {ptalpha, ptbeta, pttheta, ptgamma};//estimatePTParams(attackfrequency, naction, defstrategy, DEPTH_LIMIT, alpha, beta, theta, gamma);
		double[] estimatedptparams = estimatePTParams(attackfrequency, naction, defstrategy, DEPTH_LIMIT, alpha, beta, theta, gamma);


		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("pt.csv"),true));

			//pw.append("cluster,#users,alpha,beta,theta,gamma,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");

			pw.append("0,"+nexamples +","+ estimatedptparams[0]+","+estimatedptparams[1]+","+estimatedptparams[2]+","+estimatedptparams[3]+","+points+","+0+","+0+","+0+",");

			//pw.append("0,"+nexamples +","+ ptalpha+","+ptbeta+","+pttheta+","+ptgamma+","+points+","+0+","+0+","+0+",");


			int index=0;
			for(int c: attackcount)
			{
				pw.append(c+"");
				if(index<(attackcount.length-1))
				{
					pw.append(",");
				}

				index++;
			}
			pw.append("\n");

			//pw.close();
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}



		/*


		 // how many clusters you want
		int numberofnodes = 6;

		ArrayList<ArrayList<String>> data =  Data.readData();

		// gametype 1 full info, 0 noinfo
		// deforder 0 asc: last 3 games max defender
		// defeorder 1 desc, 1st 3 games max defender
		ArrayList<String> users_refined = refineUser(data, -1, 1);

		ArrayList<ArrayList<String>>  data_refined = refineData(data,1, users_refined);

		//double[][] examples = prepareExamplesDTScorePoints(data_refined, users_refined);
		double[][] examples = prepareExamplesNodeCostPoint(data_refined, users_refined);
		//double [][] examples = prepareFrquencey(data_refined, users_refined, numberofnodes);

		printData(users_refined,examples);

		// normalize the data

		double normalizedexamples[][] = normalizeData(examples);

		System.out.println("Normalized data: ");

		printData(users_refined, normalizedexamples);

		int k= 2;

		List<Integer>[] clusters = Weka.clusterUsers(k, normalizedexamples);

		//List<Integer>[] clusters = Weka.clusterUsers(normalizedexamples);



		//List<Integer>[] clusters = KmeanClustering.clusterUsersV2(k, normalizedexamples);


		printClustersInt(clusters);




		 *//**
		 * next use weka to cluster
		 *//*

		//printClusters(clusters);

		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();


		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

			pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");
			pw.close();
		}
		catch(Exception ex)
		{
			System.out.println(" ");
		}







		for(int cluster=0; cluster<clusters.length; cluster++)
		{
			ArrayList<String> users_groups = getUserGroup(clusters[cluster], users_refined);


			//users_groups = users_refined;

		  *//**
		  * get attack count for different information set
		  *//*



			int[][] gameplay = createGamePlay(3, users_groups, data_refined, 5);
			int attackcount[] = getAttackFrequency(users_groups, data_refined, numberofnodes);
			HashMap<String, int[]> attackfrequency = getAttackCountInData(gameplay, numberofnodes, 5);

			// #10*3*5 attackfreq should be 150
			boolean isok = verifyAttackFreq(attackfrequency, users_groups.size());

			if(!isok)
			{
				throw new Exception("problem freq....");
			}
			// TODO remove sequence for which there is no action was played

			//refineAttackFrequency(attackfrequency);

			//printAttackFreq(attackfrequency);



			// now compute the best response in the tree





			//HashMap<String, HashMap<String, Double>> defstrategy = Data.readStrategy("g5d5_FI.txt");


			//HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();

			//double tmplambda = 0.6;


			double estimatedlambdanaive = estimateLambdaNaive(lambda, attackfrequency, naction, defstrategy, DEPTH_LIMIT, step);

			System.out.println("Estmiated lambda "+ estimatedlambdanaive);




			DNode root1 = EquationGenerator.buildGameTreeRecur(DEPTH_LIMIT, naction, defstrategy, attstrategy, tmplambda);

			computeLogLikeliHoodValue(attackfrequency, attstrategy, naction);





			DNode root = EquationGenerator.buildGameTree(DEPTH_LIMIT, naction);



			HashMap<String, ArrayList<DNode>> I = EquationGenerator.prepareInformationSets(root, DEPTH_LIMIT, naction);
			EquationGenerator.printInfoSet(I);
			HashMap<String, InfoSet> isets = EquationGenerator.prepareInfoSet(I);
		   *//**
		   * compute information sets according to depth
		   *//*
			HashMap<Integer, ArrayList<String>> depthinfoset = depthInfoSet(DEPTH_LIMIT, isets,1); // for player 1: attacker, player 0 is defender
			EquationGenerator.printISets(isets);


			EquationGenerator.updateTreeWithDefStartegy(isets, root, strategy, naction);



			//double estimatedlambda = estimateLambda(lambda, isets, attackfrequency, naction, strategy, root, DEPTH_LIMIT, depthinfoset, step);







			// use attackstrategy to compute lambda




			int p =1;


			int sumattackcoutn = 0;

			for(int c: attackcount)
			{
				sumattackcoutn += c;
			}




			double sumscore = 0;

			double sum_mscore =0;
			double sum_nscore = 0;
			double sum_pscore = 0;


			for(int i=0; i<users_groups.size(); i++)
			{


				String tmpusr = users_groups.get(i);

				sumscore += getUserScore(tmpusr, data_refined);

				sum_mscore += getPersonalityScore(tmpusr, data_refined, 0);
				sum_nscore += getPersonalityScore(tmpusr, data_refined, 1);
				sum_pscore += getPersonalityScore(tmpusr, data_refined, 2);


				//System.out.println("kept user "+ tmpusr);
			}

			sumscore /= users_groups.size();
			sum_mscore /= users_groups.size();
			sum_nscore /= users_groups.size();
			sum_pscore /= users_groups.size();



			System.out.println("Cluster "+cluster+", user count "+users_groups.size()+", lambda "+ estimatedlambdanaive);


			try
			{
				PrintWriter pw = new PrintWriter(new FileOutputStream(new File("cluster-lambda.csv"),true));

				//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore"+ "\n");

				pw.append(cluster+","+users_groups.size()+","+ estimatedlambdanaive+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+",");

				int index=0;
				for(int c: attackcount)
				{
					pw.append(c+"");
					if(index<(attackcount.length-1))
					{
						pw.append(",");
					}

					index++;
				}
				pw.append("\n");

				pw.close();
			}
			catch(Exception ex)
			{
				System.out.println(" ");
			}


			break;

		}
		    */
	}

	public static int[] getAttackFrequency(int[][] negameplay, int numberofnodes, int roundlimit) {


		int[] count = new int[numberofnodes];


		for(int i=0; i<negameplay.length; i++)
		{
			for(int j=0; j<roundlimit; j++)
			{
				count[negameplay[i][2*j+1]]++;
			}
		}


		return count;
	}
	
	
	public static int[] getDefFrequency(int[][] negameplay, int numberofnodes, int roundlimit) {


		int[] count = new int[numberofnodes];


		for(int i=0; i<negameplay.length; i++)
		{
			for(int j=0; j<roundlimit; j++)
			{
				count[negameplay[i][2*j]]++;
			}
		}


		return count;
	}

	public static double playGame(HashMap<String, HashMap<String, Double>> defstrategy,
			HashMap<String, double[]> attstrategy, int naction, int roundlimit, int[][] negameplay, int nexamples, HashMap<Integer,Integer[]> noderewards, int[][] negameplaydef) {

		double avgpoints = 0;



		for(int e=0; e<nexamples; e++)
		{

			String defseq = "";
			String attseq = "";

			String seq = "";

			for(int r=0; r<roundlimit; r++)
			{
				if(r==0)
				{
					String key = "EMPTY EMPTY";

					int defaction = makeDefMove(key, defstrategy, naction);
					int attaction = makeAttMove(key, attstrategy, naction);
					
					if(defaction >5 || attaction>5)
					{
						int g=1;
					}
					
					negameplay[e][2*r] = defaction;
					negameplay[e][2*r+1] = attaction;
					defseq = defaction+"";
					attseq = attaction+"";

					seq = defaction+","+attaction;

				}
				else if(r>0)
				{
					String key = defseq + " "+ attseq;

					int defaction = makeDefMove(key, defstrategy, naction);
					int attaction = makeAttMove(key, attstrategy, naction);
					negameplay[e][2*r] = defaction;
					negameplay[e][2*r+1] = attaction;
					defseq += ","+defaction;
					attseq += ","+attaction;
					seq += ","+ defaction+","+attaction;

				}
			}

			double attpoints = 20 + EquationGenerator.computeAttackerReward(seq, noderewards);
			System.out.println("example "+ e + ", points "+ attpoints);
			avgpoints += attpoints;


		}
		avgpoints /= (nexamples);




		return avgpoints;
	}

	private static int makeAttMove(String key, HashMap<String, double[]> attstrategy, int naction) {

		if(attstrategy.containsKey(key))
		{

			double[] strat = attstrategy.get(key);


			Random rand = new Random();

			double r = rand.nextDouble();
			double cumprob = 0;

			int a = 0;
			for(; a<naction; a++)
			{
				//String ac = a+"";

				double prob = strat[a];

				cumprob += prob;

				if (r < cumprob)
				{
					break;
				}


			}
			if(a>5)
			{
				int g=1;
			}
			return a;
		}
		else
		{
			return 5; // pass
		}
	}

	private static int makeDefMove(String key, HashMap<String, HashMap<String, Double>> defstrategy, int nactions) {



		if(defstrategy.containsKey(key))
		{

			HashMap<String, Double> strat = defstrategy.get(key);


			Random rand = new Random();

			double r = rand.nextDouble();
			double cumprob = 0;

			int a = 0;
			for(; a<nactions; a++)
			{
				String ac = a+"";

				double prob = 0;

				if(strat.containsKey(ac))
				{
					prob = strat.get(ac);
				}

				cumprob += prob;

				if (r < cumprob)
				{
					break;
				}


			}
			return a;
		}
		else
		{
			return 0; // first action
		}
	}

























}
