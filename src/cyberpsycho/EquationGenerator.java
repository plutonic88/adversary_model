package cyberpsycho;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import cyberpsycho.Data;

public class EquationGenerator {

	public static int treenodecount = 0;

	public static double llval = 0;
	public static double ptval = 0;



	public static void main(String[] args) throws Exception 
	{
		int DEPTH_LIMIT = 4;
		int naction = 6;

		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		DNode root = createGameTree(DEPTH_LIMIT, naction, noderewards);
		System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		System.out.println();
		printTree(root, naction);
		HashMap<String, ArrayList<DNode>> I = prepareInformationSets(root, DEPTH_LIMIT, naction);
		printInfoSet(I);

		HashMap<String, InfoSet> ISets = prepareInfoSet(I);

		printISets(ISets);

		//assignRewardToLeafNodes(ISets, root);

		//varify it
		//expUtility( 1, root, ISets, "d2_p0_1");


		// equation for an action in an information set
		//generateEqnInInformationSet(ISets, "d3_p1_3", 0 , root, "x(3)", "d3_p1_3_0");


		// need strategy for sequence
		HashMap<String, HashMap<String, Double>> strategy = Data.readStrategy("g5d5_FI.txt");
		String key = "EMPTY EMPTY"; 
		HashMap<String, Double> probs = strategy.get(key);


		printMatLabCodeQRETest(ISets, root, naction);



		printMatLabCodeQRE(ISets, root, naction);

		//printMatLabCodeMLE(ISets, root);


		//TODO write code for loglikelihood, so that we can plug anything
		/**
		 * 1. create sequence and corresponding frequency
		 * 2. Use searching method for finding lambda with max ll
		 * 3. Inside ll computation function use frequency and the solution from a method
		 */


	}

	private static void printMatLabCodeQRE(HashMap<String, InfoSet> iSets, DNode root, int naction) {


		String[] keyset = new String[iSets.size()];
		int indx=0;

		for(String is: iSets.keySet())
		{
			keyset[indx++] = is;
		}

		Arrays.sort(keyset);


		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println(qre_prob +" = "+ qre_var+";");
					doneprobs.add(qre_prob);
				}



			}
		}





		// print equations for every action in every information set
		// equation for an action in an information set
		//generateEqnInInformationSet(ISets, "d3_p1_3", 0 , root, I, "x(3)", "d3_p1_3_0");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			// find the player
			System.out.println("\n");

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_prob.keySet())
			{

				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);



				String[] x = qre_prob.split("_");
				int action = Integer.parseInt(qre_prob.split("_")[3]);


				if(!doneprobs.contains(qre_prob))
				{

					if(iset.player==0)
					{
						// for likelihood func
						//System.out.println("response_"+iset.qre_prob.get(nodeid) + " = " + iset.qre_prob.get(nodeid));

						//TODO
						// for equilibrium, need to adjust other values too
						String eqn = generateEqnInInformationSetQRE(iSets, iset.id, action , root, qre_var, qre_prob);
						System.out.println(/*"response_"+qre_prob + " = " +*/ eqn +";");
					}
					else if(iset.player==1)
					{
						String eqn = generateEqnInInformationSetQRE(iSets, iset.id, action , root, qre_var, qre_prob);
						System.out.println(/*"response_"+qre_prob + " = " +*/ eqn +";");

					}
					doneprobs.add(qre_prob);

				}

			}
		}

		/*

		System.out.println();
		String err ="";
		HashMap<String, String> seterror = new HashMap<String, String>();
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			err = "err_"+iset.id+" = ";
			int index = 0;
			ArrayList<String> done = new ArrayList<String>();
			for(String qre_var: iset.qre_var.values())
			{
				if(!done.contains(qre_var))
				{
					if(index==0)
					{
						err +=  qre_var;
					}
					else
					{
						err += " + " + qre_var;
					}
					done.add(qre_var);
				}
				index++;
			}
			System.out.println(err +" - 1 ;");
			seterror.put(iset.id, err +" - 1 ;");
		}
		System.out.println();




		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{
				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println("belief_error_"+qre_prob +" = "+ qre_prob +" - response_"+qre_prob +";");
					doneprobs.add(qre_prob);
				}
			}
		}

		 */

		System.out.println();

		System.out.print("belief_error_vector = [ ");


		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("err_"+iset.id + "; ");
		}



		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			// print all linear errors




			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);


				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("belief_error_"+qre_prob);



					System.out.print("; ");

					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.println("]; "+"\n");


		/*


		// create linear constraint using Aeq b


		int infcount = 0;

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			System.out.print("A"+infcount+" = [ ");

			int innercount = 0;

			for(String isetname1: keyset)
			{
				InfoSet iset1 = iSets.get(isetname1);


				if(iset.id.equals(iset1.id))
				{
					for(int i=0; i<naction; i++)
					{
						System.out.print("1 ");
						if(innercount<(iSets.size()*naction-1))
						{
							System.out.print(",");
						}
						innercount++;
					}
				}
				else
				{
					for(int i=0; i<naction; i++)
					{
						System.out.print("0 ");
						if(innercount<((iSets.size()*naction)-1))
						{
							System.out.print(",");
						}
						innercount++;
					}
				}




			}
			System.out.println(" ]; ");
			infcount++;

		}


		int count = 0;

		System.out.print("Aeq = [ ");
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("A"+count);
			count++;
			if(count<iSets.size())
			{
				System.out.print(";");
			}
		}
		System.out.println("]; "+"\n");



		count = 0;

		System.out.print("beq = [ ");
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("1");
			count++;
			if(count<iSets.size())
			{
				System.out.print("; ");
			}
		}
		System.out.println("]; ");






		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println(qre_prob +" = "+ qre_var+";");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.println("\n");

		System.out.print("lb = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0, ");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");


		System.out.print("ub = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("1, ");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");
		 */

		System.out.print("x0 = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0.166;");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");


		HashMap<String, String> container = new HashMap<String, String>();



		for(String isetname: keyset)
		{


			InfoSet iset = iSets.get(isetname);

			if(iset.player==1 && iset.depth>0)
			{

				/*
			ArrayList<String> doneprobs = new ArrayList<String>();
				 */
				// find the history of any node

				DNode node = iset.nodes.get(0);
				DNode child = null;
				for(DNode ch: node.child.values())
				{
					child= ch;
					break;
				}

				String histp1 = "";
				String histp0 = "";

				DNode tmp = node.parent;

				int pl = 1;

				while(tmp != null && tmp.nodeid != 0)
				{

					if(pl==0)
					{
						histp0 += (tmp.prevaction+1);
					}
					else
					{
						histp1 += (tmp.prevaction+1);
					}
					tmp = tmp.parent;
					pl = pl^1;
				}

				System.out.println(isetname + " hist : "+ histp0 + " "+ histp1 );


				String key = histp0 +"0"+histp1;
				String value = iset.qre_var.get(child.nodeid);
				container.put(key, value);

				int z=1;
			}




			/*for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0.5;");
					doneprobs.add(qre_prob);
				}



			}*/
		}

		String keyst = "{";
		String valuest = "{";



		String[] contkey = new String[container.keySet().size()];
		int ind = 0;

		for(String key: container.keySet())
		{
			contkey[ind++] = key;
		}

		Arrays.sort(contkey);


		int index = 0;



		for(String key: contkey)
		{
			String value = container.get(key);

			String tmpval = value.substring(2, value.length()-1);


			valuest += "\'"+tmpval +"\'";;
			keyst += "\'"+key +"\'";
			if(index<container.keySet().size()-1)
			{
				keyst += ", ";
				valuest += ", ";
			}

			index ++;
		}
		keyst += "}";
		valuest += "}";






		System.out.println("M = containers.Map(" + keyst +","+ valuest + ");" );










	}



	private static void printMatLabCodeQRETest(HashMap<String, InfoSet> iSets, DNode root, int naction) {


		String[] keyset = new String[iSets.size()];
		int indx=0;

		for(String is: iSets.keySet())
		{
			keyset[indx++] = is;
		}

		Arrays.sort(keyset);


		/*for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println(qre_prob +" = "+ qre_var+";");
					doneprobs.add(qre_prob);
				}



			}
		}*/



		// print equations for every action in every information set
		// equation for an action in an information set
		//generateEqnInInformationSet(ISets, "d3_p1_3", 0 , root, I, "x(3)", "d3_p1_3_0");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			// find the player
			System.out.println("\n");

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_prob.keySet())
			{

				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				double qre_var_val = iset.qre_var_val.get(nodeid);



				String[] x = qre_prob.split("_");
				int action = Integer.parseInt(qre_prob.split("_")[3]);


				if(!doneprobs.contains(qre_prob))
				{

					if(iset.player==0)
					{
						// for likelihood func
						//System.out.println("response_"+iset.qre_prob.get(nodeid) + " = " + iset.qre_prob.get(nodeid));

						//TODO
						// for equilibrium, need to adjust other values too
						double eqn = generateEqnInInformationSetQRE_V(iSets, iset.id, action , root, qre_var_val, qre_prob);
						System.out.println(iset.id+", action "+action+", "+ eqn +";");
						int c=1;

					}
					else if(iset.player==1)
					{
						double eqn = generateEqnInInformationSetQRE_V(iSets, iset.id, action , root, qre_var_val, qre_prob);
						System.out.println(/*"response_"+qre_prob + " = " +*/ eqn +";");

					}
					doneprobs.add(qre_prob);

				}

			}
		}



		/*System.out.println();
		String err ="";
		HashMap<String, String> seterror = new HashMap<String, String>();
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			err = "err_"+iset.id+" = ";
			int index = 0;
			ArrayList<String> done = new ArrayList<String>();
			for(String qre_var: iset.qre_var.values())
			{
				if(!done.contains(qre_var))
				{
					if(index==0)
					{
						err +=  qre_var;
					}
					else
					{
						err += " + " + qre_var;
					}
					done.add(qre_var);
				}
				index++;
			}
			System.out.println(err +" - 1 ;");
			seterror.put(iset.id, err +" - 1 ;");
		}
		System.out.println();
		 */



		/*for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{
				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println("belief_error_"+qre_prob +" = "+ qre_prob +" - response_"+qre_prob +";");
					doneprobs.add(qre_prob);
				}
			}
		}*/



		/*System.out.println();

		System.out.print("belief_error_vector = [ ");


		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("err_"+iset.id + "; ");
		}



		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			// print all linear errors




			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);


				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("belief_error_"+qre_prob);



					System.out.print("; ");

					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.println("]; "+"\n");
		 */




		// create linear constraint using Aeq b


		/*int infcount = 0;

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			System.out.print("A"+infcount+" = [ ");

			int innercount = 0;

			for(String isetname1: keyset)
			{
				InfoSet iset1 = iSets.get(isetname1);


				if(iset.id.equals(iset1.id))
				{
					for(int i=0; i<naction; i++)
					{
						System.out.print("1 ");
						if(innercount<(iSets.size()*naction-1))
						{
							System.out.print(",");
						}
						innercount++;
					}
				}
				else
				{
					for(int i=0; i<naction; i++)
					{
						System.out.print("0 ");
						if(innercount<((iSets.size()*naction)-1))
						{
							System.out.print(",");
						}
						innercount++;
					}
				}




			}
			System.out.println(" ]; ");
			infcount++;

		}
		 */

		/*int count = 0;

		System.out.print("Aeq = [ ");
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("A"+count);
			count++;
			if(count<iSets.size())
			{
				System.out.print(";");
			}
		}
		System.out.println("]; "+"\n");



		count = 0;

		System.out.print("beq = [ ");
		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			System.out.print("1");
			count++;
			if(count<iSets.size())
			{
				System.out.print("; ");
			}
		}
		System.out.println("]; ");

		 */




		/*for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println(qre_prob +" = "+ qre_var+";");
					doneprobs.add(qre_prob);
				}



			}
		}*/

		/*System.out.println("\n");

		System.out.print("lb = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0, ");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");


		System.out.print("ub = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("1, ");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");


		System.out.print("x0 = [ ");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0.5;");
					doneprobs.add(qre_prob);
				}



			}
		}

		System.out.print("];\n\n ");
		 */









	}

	public static HashMap<Integer, Integer[]> createNodeRewards(int naction) {


		HashMap<Integer, Integer[]> values = new HashMap<Integer, Integer[]>();

		Integer [] v = {4, 1};
		values.put(0, v);

		Integer[] v1 = {5, 3};
		values.put(1, v1);

		Integer [] v2 = {10, 9};
		values.put(2, v2);

		Integer[] v3 = {8,8};
		values.put(3, v3);


		Integer [] v4 = {15, 20};
		values.put(4, v4);

		Integer[] v5 = {0, 0};
		values.put(5, v5);
		
		
		/*Integer [] v = {4, 1};
		values.put(0, v);

		Integer[] v1 = {5, 3};
		values.put(1, v1);

		Integer [] v2 = {10, 9};
		values.put(2, v2);

		Integer[] v3 = {15, 25};
		values.put(3, v3);


		Integer [] v4 = {20, 45};
		values.put(4, v4);

		Integer[] v5 = {0, 0};
		values.put(5, v5);*/
		
		
		/*Integer [] v = {10, 8};
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
		values.put(5, v5);*/





		return values;
	}

	private static String generateEqnInInformationSet(HashMap<String, InfoSet> iSets, String infosetname, int action, DNode root, String variable, String prob) {


		// get the information set
		// get the variable for that action
		// get the probability for the action
		// u(action') - u(actoin)



		InfoSet infoset = iSets.get(infosetname);

		int naction = infoset.nodes.get(0).child.size();

		HashMap<Integer, String> exputilities = new HashMap<Integer, String>();

		//System.out.println("\nInfoset name "+ infosetname);
		for(int i =0; i<naction ; i++)
		{
			String expu_action = expUtility(i, root, iSets, infosetname);

			//System.out.println("Action "+ i + ", expected utility "+ expu_action);

			exputilities.put(i, expu_action);

		}


		String eqn = "";
		for(int i =0; i<naction ; i++)
		{



			String expterm = "";

			if(i!=action)
			{
				expterm = "exp(lambda*("+ "("+exputilities.get(i) +") - " +  "(" + exputilities.get(action) + ")" + "))"; 
			}
			else
			{
				expterm = "1"; 
			}

			if(i!=0)
			{
				eqn =  eqn +" + "+expterm;
			}
			else
			{
				eqn =  eqn + expterm;
			}
		}


		eqn = "response_"+prob +" = "+  variable +"*("+ eqn + ") - 1" ;

		//System.out.println("Eqn for variable "+ variable + ": "+ eqn);


		return eqn;











	}


	private static String generateEqnInInformationSetQRE(HashMap<String, InfoSet> iSets, String infosetname, int action, DNode root, String variable, String prob) {


		// get the information set
		// get the variable for that action
		// get the probability for the action
		// u(action') - u(actoin)



		InfoSet infoset = iSets.get(infosetname);

		int naction = infoset.nodes.get(0).child.size();

		HashMap<Integer, String> exputilities = new HashMap<Integer, String>();

		//System.out.println("\nInfoset name "+ infosetname);
		for(int i =0; i<naction ; i++)
		{
			String expu_action = expUtilityQRE(i, root, iSets, infosetname);

			//System.out.println("Action "+ i + ", expected utility "+ expu_action);

			exputilities.put(i, expu_action);

		}


		String eqn = "";
		for(int i =0; i<naction ; i++)
		{



			String expterm = "";

			if(i!=action)
			{
				expterm = "exp(lambda*("+ "("+exputilities.get(i) +") - " +  "(" + exputilities.get(action) + ")" + "))"; 
			}
			else
			{
				expterm = "1"; 
			}

			if(i!=0)
			{
				eqn =  eqn +" + "+expterm;
			}
			else
			{
				eqn =  eqn + expterm;
			}
		}


		eqn = "response_"+prob +" = "+  variable +"*("+ eqn + ") - 1" ;

		//System.out.println("Eqn for variable "+ variable + ": "+ eqn);


		return eqn;











	}



	private static double generateEqnInInformationSetQRE_V(HashMap<String, InfoSet> iSets, String infosetname, int action, DNode root, double variable, String prob) {


		// get the information set
		// get the variable for that action
		// get the probability for the action
		// u(action') - u(actoin)


		int lambda = 2;



		InfoSet infoset = iSets.get(infosetname);

		int naction = infoset.nodes.get(0).child.size();

		HashMap<Integer, Double> exputilities = new HashMap<Integer, Double>();

		//System.out.println("\nInfoset name "+ infosetname);
		for(int i =0; i<naction ; i++)
		{
			double expu_action = expUtilityQRE_V(i, root, iSets, infosetname);

			//System.out.println("Action "+ i + ", expected utility "+ expu_action);

			exputilities.put(i, expu_action);

		}


		double eqn = 0.0;


		for(int i =0; i<naction ; i++)
		{


			double expterm = 1;


			if(i!=action)
			{
				//expterm = "exp(lambda*("+ "("+exputilities.get(i) +") - " +  "(" + exputilities.get(action) + ")" + "))"; 

				expterm = Math.exp(lambda* (exputilities.get(i) - exputilities.get(action)));

			}
			else
			{
				expterm = 1; 
			}

			if(i!=0)
			{
				eqn =  eqn +expterm;
			}
			else
			{
				eqn =  eqn + expterm;
			}
		}


		eqn =  variable*eqn - 1 ;

		//System.out.println("Eqn for variable "+ variable + ": "+ eqn);


		return eqn;


	}




	private static void printMatLabCodeMLE(HashMap<String,InfoSet> iSets, DNode root) {


		// print initialization


		String[] keyset = new String[iSets.size()];
		int indx=0;

		for(String is: iSets.keySet())
		{
			keyset[indx++] = is;
		}

		Arrays.sort(keyset);


		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.println(qre_prob +" = "+ qre_var+";");
					doneprobs.add(qre_prob);
				}



			}
		}



		// print equations for every action in every information set
		// equation for an action in an information set
		//generateEqnInInformationSet(ISets, "d3_p1_3", 0 , root, I, "x(3)", "d3_p1_3_0");

		for(String isetname: keyset)
		{
			InfoSet iset = iSets.get(isetname);
			// find the player
			System.out.println("\n");

			ArrayList<String> doneprobs = new ArrayList<String>();
			for(Integer nodeid: iset.qre_prob.keySet())
			{

				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);



				String[] x = qre_prob.split("_");
				int action = Integer.parseInt(qre_prob.split("_")[3]);


				if(!doneprobs.contains(qre_prob))
				{

					if(iset.player==0)
					{
						// for likelihood func
						System.out.println("response_"+iset.qre_prob.get(nodeid) + " = " + iset.qre_prob.get(nodeid)+";");

						//TODO
						// for equilibrium, need to adjust other values too
						//String eqn = generateEqnInInformationSet(iSets, iset.id, action , root, qre_var, qre_prob);
						//System.out.println("response_"+qre_prob + " = " + eqn);
					}
					else if(iset.player==1)
					{
						String eqn = generateEqnInInformationSet(iSets, iset.id, action , root, qre_var, qre_prob);
						System.out.println(/*"response_"+qre_prob + " = " +*/ eqn +";");

					}
					doneprobs.add(qre_prob);

				}

			}




		}




	}

	public static void printISets(HashMap<String,InfoSet> iSets) 
	{

		for(InfoSet iset: iSets.values())
		{
			System.out.println("\nISet : "+ iset.id + ", player "+ iset.player + ", depth "+ iset.depth);
			System.out.print("nodes : ");
			for(DNode node: iset.nodes)
			{
				System.out.print(node.nodeid+" ");
			}
			System.out.println();
			for(Integer nodeid: iset.qre_prob.keySet())
			{
				System.out.println(nodeid + " prob:  "+ iset.qre_prob.get(nodeid));
			}

			for(Integer nodeid: iset.qre_var.keySet())
			{
				System.out.println(nodeid + " var :  "+ iset.qre_var.get(nodeid));
			}

		}

	}

	public static HashMap<String, InfoSet> prepareInfoSet(HashMap<String, ArrayList<DNode>> I) {


		HashMap<String, InfoSet> isets = new HashMap<String, InfoSet>();

		String[] keyset = new String[I.keySet().size()];


		int index = 0;
		for(String is: I.keySet())
		{
			keyset[index++] = is;
		}

		Arrays.sort(keyset);


		for(String is: keyset)
		{
			//System.out.println(is);
			InfoSet infset = makeObj(is, I.get(is));
			isets.put(is ,infset);
		}


		return isets;
	}

	private static InfoSet makeObj(String is, ArrayList<DNode> nodes) {

		int player = nodes.get(0).player;
		int depth = nodes.get(0).depth;

		InfoSet obj = new InfoSet(player, depth, is);


		for(DNode n: nodes)
		{
			n.infoset = is;
			obj.nodes.add(n);
		}



		if(player==0)
		{

			for(DNode node: nodes)
			{
				for(DNode child: node.child.values())
				{
					obj.qre_prob.put(child.nodeid, is+"_"+child.prevaction);
					obj.qre_var.put(child.nodeid, "x("+InfoSet.varcount+")");

					//obj.qre_prob_val.put(child.nodeid, 0.5);
					//obj.qre_var_val.put(child.nodeid, 0.5);



					InfoSet.varcount++;
				}
			}
		}
		else if(player==1)
		{

			for(int action=0; action<nodes.get(0).child.size(); action++)
			{
				for(DNode node: nodes)
				{
					for(DNode child: node.child.values())
					{
						if(child.prevaction == action)
						{
							obj.qre_prob.put(child.nodeid, is+"_"+child.prevaction);
							obj.qre_var.put(child.nodeid, "x("+InfoSet.varcount+")");

							//obj.qre_prob_val.put(child.nodeid, 0.5);
							//obj.qre_var_val.put(child.nodeid, 0.5);
						}

					}
				}
				InfoSet.varcount++;
			}
		}



		return obj;
	}

	private static String expUtility(int action, DNode root, HashMap<String,InfoSet> iSets, String infosetname)
	{
		String exputility = "";
		String probtoreach = "";
		String continuationvalue = "";
		int nodecount = 0;
		for(DNode node: iSets.get(infosetname).nodes)
		{
			// for node find the probability to reach the information set
			probtoreach = getProbToReachInfoNode(node, root, iSets, infosetname);

			ArrayList<Integer> seqofactions = getSequenceOfActions(node, root, iSets, infosetname);

			if(probtoreach.equals(""))
			{
				probtoreach = "1";
			}

			seqofactions.add(action);

			//System.out.println("\nNode "+ node.nodeid + ", Infoset "+ node.infoset + ", prob to reach : "+ probtoreach);


			// consider prob of playing action 1
			// play the continuation game
			DNode nextroot = node.child.get(action);
			continuationvalue = playContinuationGame(nextroot, "", iSets, seqofactions, "");
			nodecount++;

			/*System.out.println("\nInfoset "+ infosetname);
			System.out.println("In node "+ node.nodeid);
			System.out.println("Prob to reach infoset "+probtoreach);
			System.out.println("Continuation value "+continuationvalue);*/


			exputility += probtoreach +"*("+continuationvalue + ")";
			if(nodecount< iSets.get(infosetname).nodes.size())
			{
				exputility += "+";
			}
		}



		//System.out.println("\nEXP : "+exputility+"\n");

		return exputility;


	}


	private static String expUtilityQRE(int action, DNode root, HashMap<String,InfoSet> iSets, String infosetname)
	{
		String exputility = "";
		String probtoreach = "";
		String continuationvalue = "";
		int nodecount = 0;
		int player = iSets.get(infosetname).player;
		for(DNode node: iSets.get(infosetname).nodes)
		{
			// for node find the probability to reach the information set
			probtoreach = getProbToReachInfoNodeQRE(node, root, iSets, infosetname);

			ArrayList<Integer> seqofactions = getSequenceOfActionsQRE(node, root, iSets, infosetname);

			if(probtoreach.equals(""))
			{
				probtoreach = "1";
			}

			seqofactions.add(action);

			//System.out.println("\nNode "+ node.nodeid + ", Infoset "+ node.infoset + ", prob to reach : "+ probtoreach);


			// consider prob of playing action 1
			// play the continuation game
			DNode nextroot = node.child.get(action);
			continuationvalue = playContinuationGameQRE(nextroot, "", iSets, seqofactions, "", player);
			nodecount++;

			/*System.out.println("\nInfoset "+ infosetname);
			System.out.println("In node "+ node.nodeid);
			System.out.println("Prob to reach infoset "+probtoreach);
			System.out.println("Continuation value "+continuationvalue);*/


			exputility += probtoreach +"*("+continuationvalue + ")";
			if(nodecount< iSets.get(infosetname).nodes.size())
			{
				exputility += "+";
			}
		}



		//System.out.println("\nEXP : "+exputility+"\n");

		return exputility;


	}


	private static double expUtilityQRE_V(int action, DNode root, HashMap<String,InfoSet> iSets, String infosetname)
	{
		double exputility = 0.0;
		double probtoreach = 1;
		double continuationvalue = 1;
		int nodecount = 0;
		int player = iSets.get(infosetname).player;
		for(DNode node: iSets.get(infosetname).nodes)
		{
			// for node find the probability to reach the information set
			probtoreach = getProbToReachInfoNodeQRE_V(node, root, iSets, infosetname);

			ArrayList<Integer> seqofactions = getSequenceOfActionsQRE(node, root, iSets, infosetname);

			/*if(probtoreach.equals(""))
			{
				probtoreach = 1;
			}*/

			seqofactions.add(action);

			//System.out.println("\nNode "+ node.nodeid + ", Infoset "+ node.infoset + ", prob to reach : "+ probtoreach);


			// consider prob of playing action 1
			// play the continuation game
			DNode nextroot = node.child.get(action);
			continuationvalue = playContinuationGameQRE_V(nextroot, "", iSets, seqofactions, "", player);
			nodecount++;

			/*System.out.println("\nInfoset "+ infosetname);
			System.out.println("In node "+ node.nodeid);
			System.out.println("Prob to reach infoset "+probtoreach);
			System.out.println("Continuation value "+continuationvalue);*/


			exputility += probtoreach *(continuationvalue );
			if(nodecount< iSets.get(infosetname).nodes.size())
			{
				//exputility += "+";
			}
		}



		//System.out.println("\nEXP : "+exputility+"\n");

		return exputility;


	}

	private static ArrayList<Integer> getSequenceOfActions(DNode node, DNode root, HashMap<String, InfoSet> iSets,
			String infosetname) {


		DNode tempnode = node;

		String prb = "";

		ArrayList<Integer> seq = new ArrayList<Integer>();



		while(tempnode.parent != null)
		{

			String infsetname = tempnode.parent.infoset;
			InfoSet infset= iSets.get(infsetname);
			if(tempnode.parent.player==0)
			{
				prb = prb + tempnode.parent.infoset + "_"+ tempnode.prevaction;
			}
			else if(tempnode.parent.player==1)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);
			}
			if(tempnode.parent.nodeid!=0)
			{
				prb += "*";
			}

			seq.add(tempnode.prevaction);
			tempnode = tempnode.parent;

		}

		// reverse it. 


		ArrayList<Integer> revseq = new ArrayList<Integer>();

		for(int i =0; i<seq.size(); i++)
		{
			revseq.add(seq.get(seq.size()-i-1));
		}




		return revseq;
	}

	private static ArrayList<Integer> getSequenceOfActionsQRE(DNode node, DNode root, HashMap<String, InfoSet> iSets,
			String infosetname) {


		DNode tempnode = node;

		String prb = "";

		ArrayList<Integer> seq = new ArrayList<Integer>();



		while(tempnode.parent != null)
		{

			String infsetname = tempnode.parent.infoset;
			InfoSet infset= iSets.get(infsetname);
			if(tempnode.parent.player==0)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);
			}
			else if(tempnode.parent.player==1)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);
			}
			if(tempnode.parent.nodeid!=0)
			{
				prb += "*";
			}

			seq.add(tempnode.prevaction);
			tempnode = tempnode.parent;

		}

		// reverse it. 


		ArrayList<Integer> revseq = new ArrayList<Integer>();

		for(int i =0; i<seq.size(); i++)
		{
			revseq.add(seq.get(seq.size()-i-1));
		}




		return revseq;
	}


	private static String playContinuationGame(DNode nextroot, String conplay, HashMap<String,InfoSet> iSets, ArrayList<Integer> seqofactions, String godeep) {



		if(nextroot.leaf)
		{
			//compute reward for sequence
			return nextroot.attacker_reward+"";
		}

		// find the informations set

		String infsetname = nextroot.infoset;
		// find the variable associated with the node
		InfoSet iset = iSets.get(infsetname);

		String tmpplay = "";
		int childcount = 0;
		for(DNode child: nextroot.child.values())
		{

			String prob = "";
			if(nextroot.player==0)
			{
				prob = iset.qre_prob.get(child.nodeid); 
			}
			else
			{
				prob = iset.qre_var.get(child.nodeid); 
			}

			String val = playContinuationGame(child, conplay, iSets, seqofactions, godeep + " "+ child.prevaction);
			childcount++;

			tmpplay += prob +"*("+ val +")";

			if(childcount<nextroot.child.size())
			{
				tmpplay += "+";
			}
		}
		conplay = tmpplay;
		return conplay;
	}


	private static String playContinuationGameQRE(DNode nextroot, String conplay, HashMap<String,InfoSet> iSets, ArrayList<Integer> seqofactions, String godeep, int player) {



		if(nextroot.leaf)
		{
			//compute reward for sequence
			if(player==0)
			{
				return nextroot.defender_reward+"";
			}
			return nextroot.attacker_reward+"";


		}

		// find the informations set

		String infsetname = nextroot.infoset;
		// find the variable associated with the node
		InfoSet iset = iSets.get(infsetname);

		String tmpplay = "";
		int childcount = 0;
		for(DNode child: nextroot.child.values())
		{

			String prob = "";
			if(nextroot.player==0)
			{
				prob = iset.qre_var.get(child.nodeid); 
			}
			else
			{
				prob = iset.qre_var.get(child.nodeid); 
			}

			String val = playContinuationGameQRE(child, conplay, iSets, seqofactions, godeep + " "+ child.prevaction, player);
			childcount++;

			tmpplay += prob +"*("+ val +")";

			if(childcount<nextroot.child.size())
			{
				tmpplay += "+";
			}
		}
		conplay = tmpplay;
		return conplay;
	}



	private static double playContinuationGameQRE_V(DNode nextroot, String conplay, HashMap<String,InfoSet> iSets, ArrayList<Integer> seqofactions, String godeep, int player) {



		if(nextroot.leaf)
		{
			//compute reward for sequence
			if(player==0)
			{
				return nextroot.defender_reward;
			}
			return nextroot.attacker_reward;


		}

		// find the informations set

		String infsetname = nextroot.infoset;
		// find the variable associated with the node
		InfoSet iset = iSets.get(infsetname);

		double tmpplay = 0;;
		int childcount = 0;
		for(DNode child: nextroot.child.values())
		{

			double prob = 1;
			if(nextroot.player==0)
			{
				prob = iset.qre_var_val.get(child.nodeid); 
			}
			else
			{
				prob = iset.qre_var_val.get(child.nodeid); 
			}

			double val = playContinuationGameQRE_V(child, conplay, iSets, seqofactions, godeep + " "+ child.prevaction, player);
			childcount++;

			tmpplay += prob *( val );

			if(childcount<nextroot.child.size())
			{
				//tmpplay += "+";
			}
		}
		//conplay = tmpplay;
		return tmpplay;
	}

	private static InfoSet getInfoSet(String infsetname, ArrayList<InfoSet> iSets) {


		for(InfoSet is: iSets)
		{
			if(is.id.equals(infsetname))
			{
				return is;
			}
		}

		return null;
	}

	private static String getProbToReachInfoNode(DNode node, DNode root, HashMap<String,InfoSet> iSets, String infosetname) {


		DNode tempnode = node;

		String prb = "";




		while(tempnode.parent != null)
		{

			String infsetname = tempnode.parent.infoset;
			InfoSet infset= iSets.get(infsetname);
			if(tempnode.parent.player==0)
			{
				prb = prb + tempnode.parent.infoset + "_"+ tempnode.prevaction;
			}
			else if(tempnode.parent.player==1)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);
			}
			if(tempnode.parent.nodeid!=0)
			{
				prb += "*";
			}
			tempnode = tempnode.parent;
		}


		return prb;
	}


	private static String getProbToReachInfoNodeQRE(DNode node, DNode root, HashMap<String,InfoSet> iSets, String infosetname) {


		DNode tempnode = node;

		String prb = "";






		while(tempnode.parent != null)
		{

			String infsetname = tempnode.parent.infoset;
			InfoSet infset= iSets.get(infsetname);
			if(tempnode.parent.player==0)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);;
			}
			else if(tempnode.parent.player==1)
			{
				prb = prb + infset.qre_var.get(tempnode.nodeid);
			}
			if(tempnode.parent.nodeid!=0)
			{
				prb += "*";
			}
			tempnode = tempnode.parent;
		}


		return prb;
	}

	private static double getProbToReachInfoNodeQRE_V(DNode node, DNode root, HashMap<String,InfoSet> iSets, String infosetname) {


		DNode tempnode = node;

		double prb = 1;






		while(tempnode.parent != null)
		{

			String infsetname = tempnode.parent.infoset;
			InfoSet infset= iSets.get(infsetname);
			if(tempnode.parent.player==0)
			{
				prb = prb * infset.qre_var_val.get(tempnode.nodeid);;
			}
			else if(tempnode.parent.player==1)
			{
				prb = prb * infset.qre_var_val.get(tempnode.nodeid);
			}
			if(tempnode.parent.nodeid!=0)
			{
				//prb += "*";
			}
			tempnode = tempnode.parent;
		}


		return prb;
	}


	public static void printInfoSet(HashMap<String, ArrayList<DNode>> I) 
	{
		System.out.println();
		for(String infset: I.keySet())
		{
			System.out.print("Information set "+ infset +": ");
			for(DNode n: I.get(infset))
			{
				System.out.print(n.nodeid+" ");
			}
			System.out.println();

		}


	}

	public static HashMap<String, ArrayList<DNode>> prepareInformationSets(DNode root, int DEPTH_LIMIT, int naction) {


		HashMap<String, ArrayList<DNode>> I = new HashMap<String, ArrayList<DNode>>();

		int player = 1;

		System.out.println();
		for(int depth=0; depth<DEPTH_LIMIT; depth++)
		{
			player ^= 1;
			ArrayList<DNode> depthnodes = getNodes(depth, root);
			System.out.print("Depth "+ depth + " nodes ");
			for(DNode n: depthnodes)
			{
				System.out.print(n.nodeid +" ");
			}
			System.out.println();

			if(player==0)
			{
				int countI = 0;
				for(DNode n: depthnodes)
				{
					String key = "d"+depth+"_p"+player + "_" + countI;
					ArrayList<DNode> ns = new ArrayList<DNode>();
					n.infoset = key;
					ns.add(n);
					I.put(key, ns);
					countI++;
				}
			}
			else if(player==1)
			{
				// collect nodes who has same parent

				int prevdepth = depth-1;

				ArrayList<DNode> prevdepthnodes = getNodes(prevdepth, root);

				int countI =0;
				for(DNode parentnode: prevdepthnodes)
				{
					ArrayList<DNode> ns = new ArrayList<DNode>();
					String key = "d"+depth+"_p"+player + "_" + countI;
					for(DNode n: depthnodes)
					{
						if(n.parent.nodeid == parentnode.nodeid)
						{
							n.infoset = key;
							ns.add(n);
						}
					}

					I.put(key, ns);
					countI++;


				}


			}
		}

		return I;


	}

	private static ArrayList<DNode> getNodes(int depth, DNode root) {


		ArrayList<DNode> nodes = new ArrayList<DNode>();
		collectNodes(depth, 0, root, nodes);
		return nodes;
	}

	private static void collectNodes(int depth, int curdepth, DNode node, ArrayList<DNode> nodes) {


		if(depth==curdepth)
		{
			nodes.add(node);
			return;
		}

		for(int action = 0; action<node.child.size(); action++)
		{
			collectNodes(depth, curdepth+1, node.child.get(action), nodes);
		}

	}

	private static void printTree(DNode node, int naction) {


		if(node.child.get(0)==null)
			return;

		for(int action =0; action<naction; action++)
		{
			DNode c = node.child.get(action);
			System.out.println("Node id "+ c.nodeid + ", parent : "+
					node.nodeid + ", player "+ c.player +
					", leaf "+ c.leaf + ", prevaction "+ c.prevaction);
			printTree(node.child.get(action), naction);
		}

	}

	private static DNode createGameTree(int DEPTH_LIMIT, int naction, HashMap<Integer,Integer[]> noderewards) 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount = 0;
		treenodecount++;
		genTree(0, naction, DEPTH_LIMIT, root, noderewards);
		return root;

	}

	private static DNode createGameTreeRecur(int DEPTH_LIMIT, int naction, HashMap<Integer,
			Integer[]> noderewards, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String, double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency) 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount = 0;
		treenodecount++;
		genTreeRecur(0, naction, DEPTH_LIMIT, root, noderewards, "", defstrategy, attstrategy, lambda, attackfrequency);
		return root;

	}
	
	
	private static DNode createGameTreeRecurDefBR(int DEPTH_LIMIT, int naction, HashMap<Integer,
			Integer[]> noderewards, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String, double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency) 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount = 0;
		treenodecount++;
		genTreeRecurDefBR(0, naction, DEPTH_LIMIT, root, noderewards, "", defstrategy, attstrategy, lambda, attackfrequency);
		return root;

	}


	private static DNode createGameTreeRecurDouble(int DEPTH_LIMIT, int naction, HashMap<Integer,
			Integer[]> noderewards, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String, double[]> attstrategy, double lambda, HashMap<String,double[]> attackfrequency) 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount = 0;
		treenodecount++;
		genTreeRecurDouble(0, naction, DEPTH_LIMIT, root, noderewards, "", defstrategy, attstrategy, lambda, attackfrequency);
		return root;

	}


	private static DNode createGameTreeRecurSUQR(int DEPTH_LIMIT, int naction, HashMap<Integer,
			Integer[]> noderewards, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String, double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency, double[] omega) throws Exception 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount = 0;
		treenodecount++;
		genTreeRecurSUQR(0, naction, DEPTH_LIMIT, root, noderewards, "", defstrategy, attstrategy, lambda, attackfrequency, omega);
		return root;

	}


	private static DNode createGameTreeRecurPT(int DEPTH_LIMIT, int naction, HashMap<Integer,
			Integer[]> noderewards, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String, double[]> attstrategy, HashMap<String,int[]> attackfrequency, double alpha, double beta, double theta, double gamma) throws Exception 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount = 0;
		treenodecount++;
		genTreeRecurPT(0, naction, DEPTH_LIMIT, root, noderewards, "", defstrategy, attstrategy, attackfrequency, alpha, beta, theta, gamma);
		return root;

	}


	private static DNode createGameTreeRecurNE(int DEPTH_LIMIT, int naction, HashMap<Integer,
			Integer[]> noderewards, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String, double[]> attstrategy, double lambda) 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount = 0;
		treenodecount++;
		genTreeRecurNE(0, naction, DEPTH_LIMIT, root, noderewards, "", defstrategy, attstrategy, lambda);
		return root;

	}


	private static DNode createGameTreeRecurPT(int DEPTH_LIMIT, int naction, HashMap<Integer,
			Integer[]> noderewards, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String, double[]> attstrategy, double alpha, double beta, double theta, double gamma) throws Exception 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount = 0;
		treenodecount++;
		genTreeRecurPT(0, naction, DEPTH_LIMIT, root, noderewards, "", defstrategy, attstrategy, alpha, beta, theta, gamma);
		return root;

	}

	private static DNode createGameTreeBFS(int DEPTH_LIMIT, int naction, HashMap<Integer,Integer[]> noderewards) 
	{

		DNode root = new DNode(0, 0, 0);
		treenodecount = 0;
		treenodecount++;
		DNode rt = genTreeBFS(0, naction, DEPTH_LIMIT, root, noderewards);
		return rt;

	}

	private static DNode genTreeBFS(int depth, int naction, int DEPTH_LIMIT, DNode root, HashMap<Integer,Integer[]> noderewards) 
	{

		Queue<DNode> fringe = new LinkedList<DNode>();
		fringe.add(root);


		while(!fringe.isEmpty())
		{
			DNode node = fringe.poll();
			if(node.depth == DEPTH_LIMIT)
			{
				int defreward = computeDefenderReward(node, noderewards);
				int reward = computeAttackerReward(node, noderewards);
				//System.out.println();

				node.attacker_reward = reward;
				node.defender_reward = defreward;
				node.leaf = true;
				return root;
			}
			else
			{
				for(int action =0; action<naction; action++)
				{
					DNode child = new DNode(treenodecount, node.depth+1, node.player^1);
					treenodecount++;
					child.parent = node;
					child.prevaction = action;
					node.child.put(action, child);
					fringe.add(child);
					//genTree(depth+1, naction, DEPTH_LIMIT, child, noderewards);


				}
			}
		}

		return root;
	}


	private static void genTree(int depth, int naction, int DEPTH_LIMIT, DNode node, HashMap<Integer,Integer[]> noderewards) 
	{

		if(depth==DEPTH_LIMIT)
		{
			int defreward = computeDefenderReward(node, noderewards);
			int reward = computeAttackerReward(node, noderewards);
			//System.out.println();

			node.attacker_reward = reward;
			node.defender_reward = defreward;
			node.leaf = true;
			return;
		}

		for(int action =0; action<naction; action++)
		{
			DNode child = new DNode(treenodecount, depth+1, node.player^1);
			treenodecount++;
			child.parent = node;
			child.prevaction = action;
			node.child.put(action, child);
			genTree(depth+1, naction, DEPTH_LIMIT, child, noderewards);


		}

	}


	private static double[] genTreeRecurNE(int depth, int naction, int DEPTH_LIMIT, DNode node, 
			HashMap<Integer,Integer[]> noderewards, String seq, HashMap<String,HashMap<String,Double>> defstrategy,
			HashMap<String,double[]> attstrategy, double lambda) 
	{

		if(depth==DEPTH_LIMIT)
		{
			//System.out.println("leaf Node " + node.nodeid + ", seq "+ seq);
			int defreward = 99999;//computeDefenderReward(node, noderewards);
			int reward = 20+computeAttackerReward(seq, noderewards);
			//System.out.println();

			node.attacker_reward = reward;
			node.defender_reward = defreward;
			node.leaf = true;
			double[] rd = new double[naction];
			rd[node.prevaction] = node.attacker_reward;
			/*System.out.println("Leafndoe, returning attacker rewards ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(rd[i] + " ");
			}*/
			//System.out.println("\n");
			return rd;
		}



		if(node.player==1) // attacker
		{
			//System.out.println("player 1 node, returning all reward from the childs(defnodes) ");
			double[] rwrds = new double[naction];
			for(int action = 0; action<naction; action++)
			{
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecurNE(depth+1, naction, DEPTH_LIMIT, child, noderewards, 
						tmpseq, defstrategy, attstrategy, lambda);
				rwrds[action] = tmprwrd[action];

			}
			/*for(int i=0; i<naction; i++)
			{
				System.out.print(rwrds[i] + " ");
			}
			System.out.println("\n");*/
			return rwrds;
		}
		else if(node.player==0) // defender
		{

			//System.out.println("player 0 node, received rewards from attacker nodes ");

			HashMap<Integer, double[]> rewrdsmap = new HashMap<Integer, double[]>();
			for(int action = 0; action<naction; action++)
			{
				//System.out.println("def action "+ action);
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecurNE(depth+1, naction, DEPTH_LIMIT, 
						child, noderewards, tmpseq, defstrategy, attstrategy, lambda);
				/*for(int i=0; i<naction; i++)
				{
					System.out.println("attaction "+i+" : "+tmprwrd[i]);
				}
				System.out.println("\n");*/
				rewrdsmap.put(action, tmprwrd); // these are the rewards from attacker
			}
			/**
			 *  1. compute attacker Q-BR
			 *  2. Need sequence
			 *  3. defender strategy
			 */

			String key = getDefAttckrSeq(seq);

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



			double[] recentattstrat = computeAttackerQBR(key, defstrat, naction, lambda, rewrdsmap);

			attstrategy.put(key, recentattstrat);


			/**
			 * compute the loglikelihoodvalue for attacker strategy
			 * 1. first get the sequence key 
			 */

			//double llvalsum = computeLLV(attackfrequency, recentattstrat, key, naction);
			//EquationGenerator.llval += llvalsum;
			//System.out.println("llval : "+ (-EquationGenerator.llval));

			/**
			 * now compute create an empty array and return the expected payoff of attacker for the prev action
			 */

			double[] attrerdprevation = computeReturnReward(defstrat, naction, rewrdsmap, recentattstrat, node);





			/*System.out.println("Non Leafndoe, returning attacker reward for node******************** ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(attrerdprevation[i] + " ");
			}
			System.out.println("\n");*/

			return attrerdprevation;






			// return expected payoff of attacker if attacker plays the action that leads to this node

		}



		return null;

	}


	private static double[] genTreeRecur(int depth, int naction, int DEPTH_LIMIT, DNode node, 
			HashMap<Integer,Integer[]> noderewards, String seq, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String,double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency) 
	{

		if(depth==DEPTH_LIMIT)
		{
			//System.out.println("leaf Node " + node.nodeid + ", seq "+ seq);
			int defreward = 99999;//computeDefenderReward(node, noderewards);
			int reward = 20+computeAttackerReward(seq, noderewards);
			//System.out.println();

			node.attacker_reward = reward;
			node.defender_reward = defreward;
			node.leaf = true;
			double[] rd = new double[naction];
			rd[node.prevaction] = node.attacker_reward;
			/*System.out.println("Leafndoe, returning attacker rewards ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(rd[i] + " ");
			}*/
			//System.out.println("\n");
			return rd;
		}



		if(node.player==1) // attacker
		{
			//System.out.println("player 1 node, returning all reward from the childs(defnodes) ");
			double[] rwrds = new double[naction];
			for(int action = 0; action<naction; action++)
			{
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecur(depth+1, naction, DEPTH_LIMIT, child, noderewards, 
						tmpseq, defstrategy, attstrategy, lambda, attackfrequency);
				rwrds[action] = tmprwrd[action];

			}
			/*for(int i=0; i<naction; i++)
			{
				System.out.print(rwrds[i] + " ");
			}
			System.out.println("\n");*/
			return rwrds;
		}
		else if(node.player==0) // defender
		{

			//System.out.println("player 0 node, received rewards from attacker nodes ");

			HashMap<Integer, double[]> rewrdsmap = new HashMap<Integer, double[]>();
			for(int action = 0; action<naction; action++)
			{
				//System.out.println("def action "+ action);
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecur(depth+1, naction, DEPTH_LIMIT, 
						child, noderewards, tmpseq, defstrategy, attstrategy, lambda, attackfrequency);
				/*for(int i=0; i<naction; i++)
				{
					System.out.println("attaction "+i+" : "+tmprwrd[i]);
				}
				System.out.println("\n");*/
				rewrdsmap.put(action, tmprwrd); // these are the rewards from attacker
			}
			/**
			 *  1. compute attacker Q-BR
			 *  2. Need sequence
			 *  3. defender strategy
			 */

			String key = getDefAttckrSeq(seq);
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



			double[] recentattstrat = computeAttackerQBR(key, defstrat, naction, lambda, rewrdsmap);

			attstrategy.put(key, recentattstrat);


			/**
			 * compute the loglikelihoodvalue for attacker strategy
			 * 1. first get the sequence key 
			 */

			double llvalsum = computeLLV(attackfrequency, recentattstrat, key, naction);




			EquationGenerator.llval += llvalsum;
			//System.out.println("llval : "+ (-EquationGenerator.llval));

			/**
			 * now compute create an empty array and return the expected payoff of attacker for the prev action
			 */

			double[] attrerdprevation = computeReturnReward(defstrat, naction, rewrdsmap, recentattstrat, node);





			/*System.out.println("Non Leafndoe, returning attacker reward for node******************** ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(attrerdprevation[i] + " ");
			}
			System.out.println("\n");*/

			return attrerdprevation;




			// return expected payoff of attacker if attacker plays the action that leads to this node

		}



		return null;

	}
	
	
	
	
	private static double[] genTreeRecurDefBR(int depth, int naction, int DEPTH_LIMIT, DNode node, 
			HashMap<Integer,Integer[]> noderewards, String seq, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String,double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency) 
	{

		if(depth==DEPTH_LIMIT)
		{
			//System.out.println("leaf Node " + node.nodeid + ", seq "+ seq);
			int defreward = -999999;//computeDeffReward(seq, noderewards);
			int reward = 20+computeAttackerReward(seq, noderewards);
			//System.out.println();

			node.attacker_reward = reward;
			node.defender_reward = defreward;
			node.leaf = true;
			double[] rd = new double[naction];
			rd[node.prevaction] = node.attacker_reward;
			/*System.out.println("Leafndoe, returning attacker rewards ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(rd[i] + " ");
			}*/
			//System.out.println("\n");
			return rd;
		}



		if(node.player==1) // attacker
		{
			//System.out.println("player 1 node, returning all reward from the childs(defnodes) ");
			double[] rwrds = new double[naction];
			for(int action = 0; action<naction; action++)
			{
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecurDefBR(depth+1, naction, DEPTH_LIMIT, child, noderewards, 
						tmpseq, defstrategy, attstrategy, lambda, attackfrequency);
				rwrds[action] = tmprwrd[action];

			}
			/*for(int i=0; i<naction; i++)
			{
				System.out.print(rwrds[i] + " ");
			}
			System.out.println("\n");*/
			return rwrds;
		}
		else if(node.player==0) // defender
		{

			//System.out.println("player 0 node, received rewards from attacker nodes ");

			HashMap<Integer, double[]> rewrdsmap = new HashMap<Integer, double[]>();
			for(int action = 0; action<naction; action++)
			{
				//System.out.println("def action "+ action);
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecurDefBR(depth+1, naction, DEPTH_LIMIT, 
						child, noderewards, tmpseq, defstrategy, attstrategy, lambda, attackfrequency);
				/*for(int i=0; i<naction; i++)
				{
					System.out.println("attaction "+i+" : "+tmprwrd[i]);
				}
				System.out.println("\n");*/
				rewrdsmap.put(action, tmprwrd); // these are the rewards from attacker
			}
			/**
			 *  1. compute attacker Q-BR
			 *  2. Need sequence
			 *  3. defender strategy
			 */

			String key = getDefAttckrSeq(seq);
			//double[] defstrat = buildDefStrat(key, defstrategy, naction);



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



			double[] recentattstrat = new double[6];//computeAttackerQBR(key, defstrat, naction, lambda, rewrdsmap);

			//attstrategy.put(key, recentattstrat);


			/**
			 * compute the loglikelihoodvalue for attacker strategy
			 * 1. first get the sequence key 
			 */
			
			
			if(node.depth==0)
			{
				int x=1;
			}
			
			
			double[] defenderbrvalue = computeDefBRv2(key, rewrdsmap, attackfrequency, recentattstrat);
			
			
			
			double[] attrerdprevation = new double[6];
			double [] defbestresponsestrat = new double[6];
			
			if(defenderbrvalue[0] == -1)
			{
				
				defbestresponsestrat[0] = 1.0;
				
			}
			else
			{
				defbestresponsestrat[(int)defenderbrvalue[0]] = 1.0;
			}
			
			/*for(int i =0; i<6; i++)
			{
				System.out.println(" att action "+ i + ", prob "+ recentattstrat[i]);
			}*/
			
				
				
				
				

				
				/**
				 * now compute create an empty array and return the expected payoff of attacker for the prev action
				 */

				attrerdprevation = computeReturnReward(defbestresponsestrat, naction, rewrdsmap, recentattstrat, node);
				
				double s = 0;
				
				for(double dd: attrerdprevation)
				{ 
					s += dd;
				}
				
				/*if(s==0)
				{
					System.out.println("s "+ s);
				}
				*/

			
			
			
			
			if(node.depth==0)
			{
				double sum = 0;
				int defaction = 0;
				
				for(double ar: attrerdprevation)
				{
					//if(defbestresponsestrat[defaction]>0)
					{
						sum += (-ar);
					}
				}
				
				AdversaryModelExps.defbr += sum;
				System.out.println("def BR value "+ AdversaryModelExps.defbr);
				
				
				
			}
			
			


			/*System.out.println("Non Leafndoe, returning attacker reward for node******************** ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(attrerdprevation[i] + " ");
			}
			System.out.println("\n");*/

			return attrerdprevation;




			// return expected payoff of attacker if attacker plays the action that leads to this node

		}



		return null;

	}




	private static double[] computeDefBR(String key, HashMap<Integer, double[]> rewrdsmap,
			HashMap<String, int[]> attackfrequency, double[] recentattstrat) {
		
		
		double[] defresponse = new double[2];
		defresponse[0] = -1;
		defresponse[1] = Double.NEGATIVE_INFINITY;
		
		
		
		if(attackfrequency.containsKey(key))
		{
			
			
			double sumattackerfreq = 0;
			int attplay[] = attackfrequency.get(key);
			for(int t: attplay)
			{
				sumattackerfreq += t;
			}
			
			
			
			
			
			if(sumattackerfreq>0.0)
			{
				//System.out.println("Information set reached \n key: "+ key);
				
				//System.out.println("sum of attacker play "+ sumattackerfreq);
				//double [] attstrat = new double[6];
				//System.out.println("Attacker strat : ");
				for(int i=0; i<recentattstrat.length; i++)
				{
					recentattstrat[i] = attplay[i]/sumattackerfreq;
					//System.out.println("action "+ i + " : "+ recentattstrat[i]);
				}
				
				double defrewards [] = new double[6];
				
				/**
				 * Need to do this bcz def has negative rewards
				 */
				
				for(int i=0; i<6; i++)
				{
					defrewards[i] = Double.NEGATIVE_INFINITY;
				}
				
				
				//System.out.println("Computing def rewards");
				
				for(int defactino=0; defactino<6; defactino++)
				{
					/** compute reward
					 * 
					 */
					//System.out.println("def action "+ defactino);
					
					double[] attrwrds = rewrdsmap.get(defactino);
					
					for(int attaction=0; attaction<6; attaction++)
					{
						double tmpdefreward = recentattstrat[attaction]*(attrwrds[attaction]);
						
						if(recentattstrat[attaction]>0)
						{
							if(attrwrds[attaction] != 0)	
							{
								defrewards[defactino] = 0;
							}
							defrewards[defactino] += tmpdefreward;
							//System.out.println("Attaction "+attaction+", tmp def reward "+ tmpdefreward);
							//System.out.println("total def reward "+ defrewards[defactino]);
						}
					}
					
					
				}
				
				
				double maxdefrwrd = Double.NEGATIVE_INFINITY;
				int maxaction = 0;
				
				for(int i=0; i<6; i++)
				{
					//System.out.println("defaction "+i+" Def reward "+ defrewards[i]);
					if(defrewards[i]>maxdefrwrd)
					{
						maxdefrwrd = defrewards[i];
						maxaction = i;
					}
				}
				
				//System.out.println("Def best reward "+ maxdefrwrd + ", def best action "+ maxaction);
				
				defresponse[0] = maxaction;
				defresponse[1] = maxdefrwrd;
				
				return defresponse;
				
				
				
				
			}
			else 
			{
				//defresponse[0] = 1;
				return defresponse;
			}
			
		}
		
		
		return defresponse;
	}
	
	
	private static double[] computeDefBRv2(String key, HashMap<Integer, double[]> rewrdsmap,
			HashMap<String, int[]> attackfrequency, double[] recentattstrat) {
		
		
		double[] defresponse = new double[2];
		defresponse[0] = -1;
		defresponse[1] = Double.NEGATIVE_INFINITY;
		
		
		
		if(attackfrequency.containsKey(key))
		{
			
			
			double sumattackerfreq = 0;
			int attplay[] = attackfrequency.get(key);
			for(int t: attplay)
			{
				sumattackerfreq += t;
			}

			//System.out.println("Information set reached \n key: "+ key);

			//System.out.println("sum of attacker play "+ sumattackerfreq);

			if(sumattackerfreq==0)
			{
				recentattstrat[0] = 1;
			}
			else
			{
				//System.out.println("Attacker strat : ");
				for(int i=0; i<recentattstrat.length; i++)
				{
					recentattstrat[i] = attplay[i]/sumattackerfreq;
					//System.out.println("action "+ i + " : "+ recentattstrat[i]);
				}
			}




			//double [] attstrat = new double[6];


			double defrewards [] = new double[6];

			/**
			 * Need to do this bcz def has negative rewards
			 */

			for(int i=0; i<6; i++)
			{
				defrewards[i] = Double.NEGATIVE_INFINITY;
			}


			//System.out.println("Computing def rewards");

			for(int defactino=0; defactino<6; defactino++)
			{
				/** compute reward
				 * 
				 */
				//System.out.println("def action "+ defactino);

				double[] attrwrds = rewrdsmap.get(defactino);

				for(int attaction=0; attaction<6; attaction++)
				{
					double tmpdefreward = recentattstrat[attaction]*(attrwrds[attaction]);

					if(recentattstrat[attaction]>0)
					{
						if(attrwrds[attaction] != 0)	
						{
							defrewards[defactino] = 0;
						}
						defrewards[defactino] += tmpdefreward;
						//System.out.println("Attaction "+attaction+", tmp def reward "+ tmpdefreward);
						//System.out.println("total def reward "+ defrewards[defactino]);
					}
				}


			}


			double maxdefrwrd = Double.NEGATIVE_INFINITY;
			int maxaction = 0;

			for(int i=0; i<6; i++)
			{
				//System.out.println("defaction "+i+" Def reward "+ defrewards[i]);
				if(defrewards[i]>maxdefrwrd)
				{
					maxdefrwrd = defrewards[i];
					maxaction = i;
				}
			}

			//System.out.println("Def best reward "+ maxdefrwrd + ", def best action "+ maxaction);

			defresponse[0] = maxaction;
			defresponse[1] = maxdefrwrd;

			return defresponse;


		}
		
		
		return defresponse;
	}

	private static double[] genTreeRecurDouble(int depth, int naction, int DEPTH_LIMIT, DNode node, 
			HashMap<Integer,Integer[]> noderewards, String seq, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String,double[]> attstrategy, double lambda, 
			HashMap<String,double[]> attackfrequency) 
	{

		if(depth==DEPTH_LIMIT)
		{
			//System.out.println("leaf Node " + node.nodeid + ", seq "+ seq);
			int defreward = 99999;//computeDefenderReward(node, noderewards);
			int reward = 20+computeAttackerReward(seq, noderewards);
			//System.out.println();

			node.attacker_reward = reward;
			node.defender_reward = defreward;
			node.leaf = true;
			double[] rd = new double[naction];
			rd[node.prevaction] = node.attacker_reward;
			/*System.out.println("Leafndoe, returning attacker rewards ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(rd[i] + " ");
			}*/
			//System.out.println("\n");
			return rd;
		}



		if(node.player==1) // attacker
		{
			//System.out.println("player 1 node, returning all reward from the childs(defnodes) ");
			double[] rwrds = new double[naction];
			for(int action = 0; action<naction; action++)
			{
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecurDouble(depth+1, naction, DEPTH_LIMIT, child, noderewards, 
						tmpseq, defstrategy, attstrategy, lambda, attackfrequency);
				rwrds[action] = tmprwrd[action];

			}
			/*for(int i=0; i<naction; i++)
			{
				System.out.print(rwrds[i] + " ");
			}
			System.out.println("\n");*/
			return rwrds;
		}
		else if(node.player==0) // defender
		{

			//System.out.println("player 0 node, received rewards from attacker nodes ");

			HashMap<Integer, double[]> rewrdsmap = new HashMap<Integer, double[]>();
			for(int action = 0; action<naction; action++)
			{
				//System.out.println("def action "+ action);
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecurDouble(depth+1, naction, DEPTH_LIMIT, 
						child, noderewards, tmpseq, defstrategy, attstrategy, lambda, attackfrequency);
				/*for(int i=0; i<naction; i++)
				{
					System.out.println("attaction "+i+" : "+tmprwrd[i]);
				}
				System.out.println("\n");*/
				rewrdsmap.put(action, tmprwrd); // these are the rewards from attacker
			}
			/**
			 *  1. compute attacker Q-BR
			 *  2. Need sequence
			 *  3. defender strategy
			 */

			String key = getDefAttckrSeq(seq);
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



			double[] recentattstrat = computeAttackerQBR(key, defstrat, naction, lambda, rewrdsmap);

			attstrategy.put(key, recentattstrat);


			/**
			 * compute the loglikelihoodvalue for attacker strategy
			 * 1. first get the sequence key 
			 */

			double llvalsum = computeLLVDouble(attackfrequency, recentattstrat, key, naction);




			EquationGenerator.llval += llvalsum;
			//System.out.println("llval : "+ (-EquationGenerator.llval));

			/**
			 * now compute create an empty array and return the expected payoff of attacker for the prev action
			 */

			double[] attrerdprevation = computeReturnReward(defstrat, naction, rewrdsmap, recentattstrat, node);





			/*System.out.println("Non Leafndoe, returning attacker reward for node******************** ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(attrerdprevation[i] + " ");
			}
			System.out.println("\n");*/

			return attrerdprevation;




			// return expected payoff of attacker if attacker plays the action that leads to this node

		}



		return null;

	}


	public static double[][] genTreeRecurSUQR(int depth, int naction, int DEPTH_LIMIT, DNode node, 
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
				treenodecount++;
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
				treenodecount++;
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
			if(AdversaryModel.suqrw4==0)
			{
				//int sboost = 5;
				suqrpref = computeAttackSuccess(seq, naction, noderewards, boost); 
			}
			else if(AdversaryModel.suqrw4==1)
			{
				//int fboost = 5;
				suqrpref = computeAttackFailure(seq, naction, noderewards, boost); 
			}
			else if(AdversaryModel.suqrw4==2)
			{
				//int ipboost = 5;
				suqrpref = SUQRTreeGeneratorParallel.computeImmediatePointPercentage(seq, naction, noderewards, boost); 
			}
			else if(AdversaryModel.suqrw4==3)
			{
				//int tpboost = 5;
				suqrpref = SUQRTreeGeneratorParallel.computeTotalPointPercentage(seq, naction, noderewards, boost); 
			}
			else if(AdversaryModel.suqrw4==4)
			{
				//int ppboost = 5;
				suqrpref = SUQRTreeGeneratorParallel.computeTotalPenaltyPercentage(seq, naction, noderewards, boost); 
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
			double[] recentattstrat = computeAttackerSUQBR(key, defstrat, naction, lambda, rewrdsmap, penaltysmap, omega, suqrpref);

			attstrategy.put(key, recentattstrat);


			/**
			 * compute the loglikelihoodvalue for attacker strategy
			 * 1. first get the sequence key 
			 */

			double llvalsum = computeLLV(attackfrequency, recentattstrat, key, naction);




			EquationGenerator.llval += llvalsum;
			//System.out.println("llval : "+ (-EquationGenerator.llval));

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
	
	
	
	public static double[][] attackStrategySUQR(int depth, int naction, int DEPTH_LIMIT, DNode node, 
			HashMap<Integer,Integer[]> noderewards, String seq, HashMap<String,HashMap<String,Double>> defstrategy, 
			HashMap<String,double[]> attstrategy, double lambda, double[] omega) throws Exception 
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
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[][] tmprwrd = attackStrategySUQR(depth+1, naction, DEPTH_LIMIT, child, noderewards, 
						tmpseq, defstrategy, attstrategy, lambda, omega);
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
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[][] tmprwrd = attackStrategySUQR(depth+1, naction, DEPTH_LIMIT, 
						child, noderewards, tmpseq, defstrategy, attstrategy, lambda, omega);
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
			if(AdversaryModel.suqrw4==0)
			{
				//int sboost = 5;
				suqrpref = computeAttackSuccess(seq, naction, noderewards, boost); 
			}
			else if(AdversaryModel.suqrw4==1)
			{
				//int fboost = 5;
				suqrpref = computeAttackFailure(seq, naction, noderewards, boost); 
			}
			else if(AdversaryModel.suqrw4==2)
			{
				//int ipboost = 5;
				suqrpref = SUQRTreeGeneratorParallel.computeImmediatePointPercentage(seq, naction, noderewards, boost); 
			}
			else if(AdversaryModel.suqrw4==3)
			{
				//int tpboost = 5;
				suqrpref = SUQRTreeGeneratorParallel.computeTotalPointPercentage(seq, naction, noderewards, boost); 
			}
			else if(AdversaryModel.suqrw4==4)
			{
				//int ppboost = 5;
				suqrpref = SUQRTreeGeneratorParallel.computeTotalPenaltyPercentage(seq, naction, noderewards, boost); 
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
			double[] recentattstrat = computeAttackerSUQBR(key, defstrat, naction, lambda, rewrdsmap, penaltysmap, omega, suqrpref);

			attstrategy.put(key, recentattstrat);



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
	
	
	
	
	



	public static double[] computeAttackSuccess(String seq, int naction, HashMap<Integer, Integer[]> noderewards, int boost) {
		
		

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
		
		
	

/*
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

		//System.out.print("");

		for(int i= 0; i<seq.size(); i++)
		{
			System.out.print(seq.get(i) + ", ");
		}
		 
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

		return success;*/
	}


	private static double[] computeAttackFailure(String seq, int naction, HashMap<Integer, Integer[]> noderewards, int boost) {

		
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
	
	
	
	
		/*double[] failure = new double[naction];

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

		//System.out.print("");

		for(int i= 0; i<seq.size(); i++)
		{
			System.out.print(seq.get(i) + ", ");
		}
		 
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

		return failure;*/
	}


	private static double[] computeImmediatePointPercentage(String seq, int naction, HashMap<Integer, Integer[]> noderewards, int boost) {


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


	private static double[] computeTotalPointPercentage(String seq, int naction, HashMap<Integer, Integer[]> noderewards, int boost) {


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



	
	
	
	private static double[] computeReturnReward(double[] defstrat, int naction, HashMap<Integer, double[]> rewrdsmap, double[] recentattstrat, DNode node) {


		double[] attrerdprevation = new double[naction];

		double exppayoff = 0.0;

		for(int defaction = 0; defaction<naction; defaction++)
		{
			// use rewards map to get the payoffs of attacker
			double[] rwrd = rewrdsmap.get(defaction);

			double defprob = defstrat[defaction];
			double attexpsum = 0;

			for(int attaction=0; attaction<naction; attaction++)
			{
				double tmp = rwrd[attaction]*recentattstrat[attaction];
				attexpsum += tmp;
			}
			exppayoff += (defprob*attexpsum);

		}

		//System.out.println("att action  "+node.prevaction+" : "+exppayoff);

		if(node.prevaction >=0 )
		{
			attrerdprevation[node.prevaction] = exppayoff;
		}
		else
		{
			attrerdprevation[0] = exppayoff;
		}


		return attrerdprevation;

	}



	private static double[][] computeReturnRewardSUQR(double[] defstrat, int naction, HashMap<Integer, double[]> rewrdsmap, double[] recentattstrat, DNode node, HashMap<Integer,double[]> penaltysmap) {


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

	private static double computeLLV(HashMap<String, int[]> attackfrequency, double[] recentattstrat, String key, int naction) {

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


	private static double computeLLVDouble(HashMap<String, double[]> attackfrequency, double[] recentattstrat, String key, int naction) {

		double llvalsum = 0;


		//System.out.println("seq : "+ key + "\n");
		if(attackfrequency.containsKey(key))
		{
			double[] freq = attackfrequency.get(key);
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

	private static double[] computeAttackerQBR(String key, double[] defstrat, int naction, double lambda, HashMap<Integer, double[]> rewrdsmap) {


		HashMap<Integer, Double> attexp = new HashMap<Integer, Double>();


		double exponnentsum = 0.0;

		for(int attaction = 0; attaction<naction; attaction++)
		{
			double sum = 0.0;
			for(int defaction = 0; defaction<naction; defaction++)
			{
				double atttmprwrd[] = rewrdsmap.get(defaction); // get the rewards of attacker for dfenders  action
				// now get the reward for attacker;s action
				double attrwd = atttmprwrd[attaction];

				sum += (attrwd* defstrat[defaction]);

			}
			attexp.put(attaction, sum);
			exponnentsum += Math.exp(lambda*sum);
		}

		double [] recentattstrat = new double[naction];


		double sm = 0.0;

		//System.out.println("atatcker strategy: ");
		for(int attaction = 0; attaction<naction; attaction++)
		{
			double prob = Math.exp(lambda*attexp.get(attaction))/exponnentsum; 
			recentattstrat[attaction] = prob;
			sm += prob;

			//System.out.println("attaction "+attaction+" : "+recentattstrat[attaction]);

		}

		if(sm<(1-0.0001))
		{
			System.out.println("problem in attaacker strategy, sum(prob) != 1");
		}

		return recentattstrat;

	}


	private static double[] computeAttackerSUQBR(String key, double[] defstrat, int naction, double lambda, 
			HashMap<Integer, double[]> rewrdsmap, HashMap<Integer,double[]> penaltysmap, double[] omega, double[] attacksuccess) throws Exception {


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
				
				double maxreward = 131;
				double minreward = 0;

				try {

					attrwd = AdversaryModelExps.normalize(attrwd, minreward, maxreward, 0, 1);
				}
				catch(Exception ex)
				{
					throw new Exception("normalization in suqr reward");
				}


				double maxpen = 131;
				double minpen = 0;

				try {

					double tmpattpnlty = AdversaryModelExps.normalize(-attpnlty, minpen, maxpen, 0, 1);

					attpnlty = -tmpattpnlty;
				}
				catch(Exception ex)
				{
					throw new Exception("normalization in suqr pen");
				}



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

		//System.out.println("atatcker strategy: ");
		for(int attaction = 0; attaction<naction; attaction++)
		{
			double prob = Math.exp(lambda*attsu.get(attaction))/exponnentsum; 
			recentattstrat[attaction] = prob;
			sm += prob;

			//System.out.println("attaction "+attaction+" : "+recentattstrat[attaction]);

		}

		//System.out.println("sum : "+ sm);
		if(sm<(1-0.001))
		{

			System.out.println(sm+" problem! attaacker strategy, sum(prob) != 1");
			throw new Exception("problem with prob sum "+sm);
		}

		return recentattstrat;

	}

	private static double[] buildDefStrat(String key, HashMap<String, HashMap<String, Double>> defstrategy, int naction) {


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

	private static double[] genTreeRecurPT(int depth, int naction, int DEPTH_LIMIT, DNode node, 
			HashMap<Integer,Integer[]> noderewards, String seq, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String,double[]> attstrategy,
			double alpha, double beta, double theta, double gamma) throws Exception 
	{

		if(depth==DEPTH_LIMIT)
		{
			//System.out.println("leaf Node " + node.nodeid + ", seq "+ seq);
			int defreward = 99999;//computeDefenderReward(node, noderewards);
			int reward = 20+computeAttackerReward(seq, noderewards);
			//System.out.println();

			node.attacker_reward = reward;
			node.defender_reward = defreward;
			node.leaf = true;
			double[] rd = new double[naction];
			rd[node.prevaction] = node.attacker_reward;
			/*System.out.println("Leafndoe, returning attacker rewards ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(rd[i] + " ");
			}*/
			//System.out.println("\n");
			return rd;
		}



		if(node.player==1) // attacker
		{
			//System.out.println("player 1 node, returning all reward from the childs(defnodes) ");
			double[] rwrds = new double[naction];
			for(int action = 0; action<naction; action++)
			{
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecurPT(depth+1, naction, DEPTH_LIMIT, child, noderewards, 
						tmpseq, defstrategy, attstrategy, alpha, beta, theta, gamma);
				rwrds[action] = tmprwrd[action];

			}
			/*for(int i=0; i<naction; i++)
			{
				System.out.print(rwrds[i] + " ");
			}
			System.out.println("\n");*/
			return rwrds;
		}
		else if(node.player==0) // defender
		{

			//System.out.println("player 0 node, received rewards from attacker nodes ");

			HashMap<Integer, double[]> rewrdsmap = new HashMap<Integer, double[]>();
			for(int action = 0; action<naction; action++)
			{
				//System.out.println("def action "+ action);
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecurPT(depth+1, naction, DEPTH_LIMIT, 
						child, noderewards, tmpseq, defstrategy, attstrategy, alpha, beta, theta, gamma);
				/*for(int i=0; i<naction; i++)
				{
					System.out.println("attaction "+i+" : "+tmprwrd[i]);
				}
				System.out.println("\n");*/
				rewrdsmap.put(action, tmprwrd); // these are the rewards from attacker for every action for defender's every action
			}
			/**
			 *  1. compute attacker Q-BR
			 *  2. Need sequence
			 *  3. defender strategy
			 */

			String key = getDefAttckrSeq(seq);
			double[] defstrat = new double[naction];
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
			/*System.out.println("defender strategy : ");
			for(int i=0; i<naction; i++)
			{
				System.out.println("defaction "+i+" : "+defstrat[i]);
			}*/





			/**
			 * determined who are the controllers of the nodes
			 */


			//int[] controllers = getControllers(seq, noderewards);





			/**
			 * now compute PT
			 * 
			 * 1. for every action of attacker
			 * 		for every action of defender
			 * 			compute exp
			 */

			HashMap<Integer, Double> attprospects = new HashMap<Integer, Double>();


			//double exponnentsum = 0.0;

			for(int attaction = 0; attaction<naction; attaction++)
			{
				double sum = 0.0;
				for(int defaction = 0; defaction<naction; defaction++)
				{
					double atttmprwrd[] = rewrdsmap.get(defaction); // get the rewards of attacker for dfenders  action
					// now get the reward for attacker;s action
					double attrwd = atttmprwrd[attaction];


					double weightedreward = weightedR(attrwd, alpha, beta, theta);
					double weightedprob = weightedP(defstrat[defaction], gamma);

					sum += (weightedreward* weightedprob);

				}
				attprospects.put(attaction, sum);

			}

			double [] recentattstrat = new double[naction];


			double maxprospect = Double.NEGATIVE_INFINITY;
			int maxprospectaction = -1;
			double  sm =0;

			//System.out.println("atatcker strategy: ");
			for(int attaction = 0; attaction<naction; attaction++)
			{

				double pros = attprospects.get(attaction);

				//System.out.println("action : "+ attaction + ", pros "+ pros);

				if(maxprospect<pros)
				{
					maxprospect = pros;
					maxprospectaction = attaction;
				}
			}




			recentattstrat[maxprospectaction] = 1.0;


			for(int attaction = 0; attaction<naction; attaction++)
			{

				sm += recentattstrat[attaction];

			}

			if(sm<(1-0.0001))
			{
				System.out.println("problem in attaacker strategy, sum(prob) != 1");
				throw new Exception("att strat problem not ==1 ");

			}

			attstrategy.put(key, recentattstrat);


			/**
			 * compute the loglikelihoodvalue for attacker strategy
			 * 1. first get the sequence key 
			 */

			/*double ptval = 0.0;

			System.out.println("seq : "+ key + "\n");
			if(attackfrequency.containsKey(key))
			{
				int[] freq = attackfrequency.get(key);
				//double[] attstrtgy = attackstrategy.get(seq);
				for(int a=0; a<naction; a++)
				{
					if(freq[a]>0 && recentattstrat[a]>0)
					{
						double tmpptval = freq[a]* recentattstrat[a];
						//System.out.println("llval : "+ tmpllval);
						ptval += tmpptval;
					}

				}
				//System.out.println("llvalsum : "+ llvalsum);
			}
			else
			{
				System.out.println("DOes not have the sequence");
				//throw new Exception("DOes not have the sequence");
				int[] freq = attackfrequency.get(key);
				double[] attstrtgy = {1, 0, 0, 0, 0, 0};


				double tmpptval = freq[0]* attstrtgy[0];
				//System.out.println("llval : "+ tmpllval);
				ptval += tmpptval;



				//System.out.println("llvalsum : "+ llvalsum);
			}


			EquationGenerator.ptval += ptval;*/
			//System.out.println("llval : "+ (-EquationGenerator.llval));

			/**
			 * now compute create an empty array and return the expected payoff of attacker for the prev action
			 */

			double[] attrerdprevation = new double[naction];


			double exppayoff = 0.0;

			for(int defaction = 0; defaction<naction; defaction++)
			{
				// use rewards map to get the payoffs of attacker
				double[] rwrd = rewrdsmap.get(defaction);

				double defprob = defstrat[defaction];
				double attexpsum = 0;

				for(int attaction=0; attaction<naction; attaction++)
				{
					double tmp = rwrd[attaction]*recentattstrat[attaction];
					attexpsum += tmp;
				}
				exppayoff += (defprob*attexpsum);

			}

			//System.out.println("att action  "+node.prevaction+" : "+exppayoff);

			if(node.prevaction >=0 )
			{
				attrerdprevation[node.prevaction] = exppayoff;
			}
			else
			{
				attrerdprevation[0] = exppayoff;
			}



			/*System.out.println("Non Leafndoe, returning attacker reward for node******************** ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(attrerdprevation[i] + " ");
			}
			System.out.println("\n");*/

			return attrerdprevation;




			// return expected payoff of attacker if attacker plays the action that leads to this node

		}



		return null;

	}


	private static double[] genTreeRecurPT(int depth, int naction, int DEPTH_LIMIT, DNode node, 
			HashMap<Integer,Integer[]> noderewards, String seq, HashMap<String,HashMap<String,Double>> defstrategy, HashMap<String,double[]> attstrategy, HashMap<String,int[]> attackfrequency, double alpha, double beta, double theta, double gamma) throws Exception 
	{

		if(depth==DEPTH_LIMIT)
		{
			//System.out.println("leaf Node " + node.nodeid + ", seq "+ seq);
			int defreward = 99999;//computeDefenderReward(node, noderewards);
			int reward = 20+computeAttackerReward(seq, noderewards);
			//System.out.println();

			node.attacker_reward = reward;
			node.defender_reward = defreward;
			node.leaf = true;
			double[] rd = new double[naction];
			rd[node.prevaction] = node.attacker_reward;
			/*System.out.println("Leafndoe, returning attacker rewards ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(rd[i] + " ");
			}*/
			//System.out.println("\n");
			return rd;
		}



		if(node.player==1) // attacker
		{
			//System.out.println("player 1 node, returning all reward from the childs(defnodes) ");
			double[] rwrds = new double[naction];
			for(int action = 0; action<naction; action++)
			{
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecurPT(depth+1, naction, DEPTH_LIMIT, child, noderewards, 
						tmpseq, defstrategy, attstrategy, attackfrequency, alpha, beta, theta, gamma);
				rwrds[action] = tmprwrd[action];

			}
			/*for(int i=0; i<naction; i++)
			{
				System.out.print(rwrds[i] + " ");
			}
			System.out.println("\n");*/
			return rwrds;
		}
		else if(node.player==0) // defender
		{

			//System.out.println("player 0 node, received rewards from attacker nodes ");

			HashMap<Integer, double[]> rewrdsmap = new HashMap<Integer, double[]>();
			for(int action = 0; action<naction; action++)
			{
				//System.out.println("def action "+ action);
				DNode child = new DNode(treenodecount, depth+1, node.player^1);
				treenodecount++;
				child.parent = node;
				child.prevaction = action;
				String tmpseq = seq + "," + action;
				if(seq.equals(""))
				{
					tmpseq =  action +"";
				}
				double[] tmprwrd = genTreeRecurPT(depth+1, naction, DEPTH_LIMIT, 
						child, noderewards, tmpseq, defstrategy, attstrategy, attackfrequency, alpha, beta, theta, gamma);
				/*for(int i=0; i<naction; i++)
				{
					System.out.println("attaction "+i+" : "+tmprwrd[i]);
				}
				System.out.println("\n");*/
				rewrdsmap.put(action, tmprwrd); // these are the rewards from attacker for every action for defender's every action
			}
			/**
			 *  1. compute attacker Q-BR
			 *  2. Need sequence
			 *  3. defender strategy
			 */

			String key = getDefAttckrSeq(seq);
			double[] defstrat = new double[naction];
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
			/*System.out.println("defender strategy : ");
			for(int i=0; i<naction; i++)
			{
				System.out.println("defaction "+i+" : "+defstrat[i]);
			}*/





			/**
			 * determined who are the controllers of the nodes
			 */


			//int[] controllers = getControllers(seq, noderewards);





			/**
			 * now compute PT
			 * 
			 * 1. for every action of attacker
			 * 		for every action of defender
			 * 			compute exp
			 */

			HashMap<Integer, Double> attprospects = new HashMap<Integer, Double>();


			//double exponnentsum = 0.0;

			for(int attaction = 0; attaction<naction; attaction++)
			{
				double sum = 0.0;
				for(int defaction = 0; defaction<naction; defaction++)
				{
					double atttmprwrd[] = rewrdsmap.get(defaction); // get the rewards of attacker for dfenders  action
					// now get the reward for attacker;s action
					double attrwd = atttmprwrd[attaction];


					double weightedreward = weightedR(attrwd, alpha, beta, theta);
					double weightedprob = weightedP(defstrat[defaction], gamma);

					sum += (weightedreward* weightedprob);

				}
				attprospects.put(attaction, sum);

			}

			double [] recentattstrat = new double[naction];


			double maxprospect = Double.NEGATIVE_INFINITY;
			int maxprospectaction = -1;
			double  sm =0;

			//System.out.println("atatcker strategy: ");
			for(int attaction = 0; attaction<naction; attaction++)
			{

				double pros = attprospects.get(attaction);

				//System.out.println("action : "+ attaction + ", pros "+ pros);

				if(maxprospect<pros)
				{
					maxprospect = pros;
					maxprospectaction = attaction;
				}
			}




			recentattstrat[maxprospectaction] = 1.0;


			for(int attaction = 0; attaction<naction; attaction++)
			{

				sm += recentattstrat[attaction];

			}

			if(sm<(1-0.0001))
			{
				System.out.println("problem in attaacker strategy, sum(prob) != 1");
				throw new Exception("att strat problem not ==1 ");

			}

			attstrategy.put(key, recentattstrat);


			/**
			 * compute the loglikelihoodvalue for attacker strategy
			 * 1. first get the sequence key 
			 */

			double ptval = 0.0;

			//System.out.println("seq : "+ key + "\n");
			if(attackfrequency.containsKey(key))
			{
				int[] freq = attackfrequency.get(key);
				//double[] attstrtgy = attackstrategy.get(seq);
				for(int a=0; a<naction; a++)
				{
					if(freq[a]>0 && recentattstrat[a]>0)
					{
						double tmpptval = freq[a]* recentattstrat[a];
						//System.out.println("llval : "+ tmpllval);
						ptval += tmpptval;
					}

				}
				//System.out.println("llvalsum : "+ llvalsum);
			}
			else
			{
				/*//System.out.println("DOes not have the sequence");
				//throw new Exception("DOes not have the sequence");
				int[] freq = attackfrequency.get(key);
				double[] attstrtgy = {1, 0, 0, 0, 0, 0};


				double tmpptval = freq[0]* attstrtgy[0];
				//System.out.println("llval : "+ tmpllval);
				ptval += tmpptval;
				 */


				//System.out.println("llvalsum : "+ llvalsum);
			}


			EquationGenerator.ptval += ptval;
			//System.out.println("llval : "+ (-EquationGenerator.llval));

			/**
			 * now compute create an empty array and return the expected payoff of attacker for the prev action
			 */

			double[] attrerdprevation = new double[naction];


			double exppayoff = 0.0;

			for(int defaction = 0; defaction<naction; defaction++)
			{
				// use rewards map to get the payoffs of attacker
				double[] rwrd = rewrdsmap.get(defaction);

				double defprob = defstrat[defaction];
				double attexpsum = 0;

				for(int attaction=0; attaction<naction; attaction++)
				{
					double tmp = rwrd[attaction]*recentattstrat[attaction];
					attexpsum += tmp;
				}
				exppayoff += (defprob*attexpsum);

			}

			//System.out.println("att action  "+node.prevaction+" : "+exppayoff);

			if(node.prevaction >=0 )
			{
				attrerdprevation[node.prevaction] = exppayoff;
			}
			else
			{
				attrerdprevation[0] = exppayoff;
			}



			/*System.out.println("Non Leafndoe, returning attacker reward for node******************** ");
			for(int i=0; i<naction; i++)
			{
				System.out.print(attrerdprevation[i] + " ");
			}
			System.out.println("\n");*/

			return attrerdprevation;




			// return expected payoff of attacker if attacker plays the action that leads to this node

		}



		return null;

	}






	private static double weightedP(double x, double gamma) {


		double nom = Math.pow(x, gamma);

		double dnom = Math.pow(Math.pow(x, gamma) + Math.pow(1-x, gamma), (1/gamma));

		double wp = nom/dnom;


		return wp;
	}

	private static double weightedR(double c, double alpha, double beta, double theta) {


		if(c>=0)
		{
			return Math.pow(c, alpha);
		}
		else if(c<0)
		{
			return -theta*Math.pow(-c, beta);
		}


		return 999999;
	}

	private static double valueFunction(Integer integer, double alpha, double beta, double theta) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static int[] getControllers(String seq, HashMap<Integer, Integer[]> noderewards) {




		int[] controllers = new int[noderewards.size()];

		/*int attpoints = 0;
		int defpoints = 0;
		 */
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

			/*
			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action
			attpoints -= attcost;
			defpoints -= defcost;*/
			//reward for current action
			if(defaction != attaction)
			{
				//int attreward = noderewards.get(attaction)[0];
				//	attpoints += attreward;
				controllers[attaction] = 1;
			}
			// now reward for other controlled nodes
			/*for(int j=0; j<controllers.length; j++)
			{
				if((controllers[j] != controllers[attaction]) && (controllers[j]==1))
				{
					int attreward = noderewards.get(attaction)[0];
					attpoints += attreward;
				}
			}*/
		}
		//System.out.print( attpoints+", ");

		return controllers;


	}

	public static String getDefAttckrSeq(String seq) {


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

	private static int computeAttackerReward(DNode node, HashMap<Integer,Integer[]> noderewards) {

		DNode tempnode = node;


		ArrayList<Integer> seq = new ArrayList<Integer>();



		while(tempnode.parent != null)
		{

			seq.add(tempnode.prevaction);
			tempnode = tempnode.parent;

		}

		// reverse it. 


		ArrayList<Integer> revseq = new ArrayList<Integer>();

		for(int i =0; i<seq.size(); i++)
		{
			revseq.add(seq.get(seq.size()-i-1));
		}

		int reward = computeAttackerReward(revseq, noderewards);
		return reward;
	}

	private static int computeDefenderReward(DNode node, HashMap<Integer,Integer[]> noderewards) {

		DNode tempnode = node;


		ArrayList<Integer> seq = new ArrayList<Integer>();



		while(tempnode.parent != null)
		{

			seq.add(tempnode.prevaction);
			tempnode = tempnode.parent;

		}

		// reverse it. 


		ArrayList<Integer> revseq = new ArrayList<Integer>();

		for(int i =0; i<seq.size(); i++)
		{
			revseq.add(seq.get(seq.size()-i-1));
		}

		int reward = computeDefenderReward(revseq, noderewards);
		return reward;
	}

	public static int computeAttackerReward(String seq, HashMap<Integer, Integer[]> noderewards) {



		//seq = "0,1,1,0,5,3,1,3,5,0";

		int[] controllers = new int[noderewards.size()];

		int attpoints = 0;
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
			attpoints -= attcost;
			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];
				attpoints += attreward;
				controllers[attaction] = 1;
				controllers[defaction] = 0;
			}
			else if((defaction == attaction) && (controllers[attaction]==1)) // when def==att
			{
				int attreward = noderewards.get(attaction)[0];
				attpoints += attreward;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((j != attaction) && (controllers[j]==1))
				{
					int attreward = noderewards.get(j)[0];
					attpoints += attreward;
				}
			}
		}
		//System.out.print( attpoints+", ");

		return attpoints;
	}
	
	
	public static int computeDeffReward(String seq, HashMap<Integer, Integer[]> noderewards) {



		//seq = "0,1,1,0,5,3,1,3,5,0";

		int[] controllers = new int[noderewards.size()];

		int attpoints = 0;
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
			attpoints -= attcost;
			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];
				attpoints += attreward;
				controllers[attaction] = 1;
				controllers[defaction] = 0;
			}
			else if((defaction == attaction) && (controllers[attaction]==1)) // when def==att
			{
				int attreward = noderewards.get(attaction)[0];
				attpoints += attreward;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((j != attaction) && (controllers[j]==1))
				{
					int attreward = noderewards.get(j)[0];
					attpoints += attreward;
				}
			}
		}
		//System.out.print( attpoints+", ");

		return attpoints;
	}


	public static int[] computeAttackerUtilities(String seq, HashMap<Integer, Integer[]> noderewards) {


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



	public static int computeAttackerReward(HashMap<Integer, Integer[]> noderewards, HashMap<String, String> user_seq, HashMap<String, Integer> user_reward) {



		//seq = "0,1,1,0,5,3,1,3,5,0";
		int sumattackerpoints = 0;

		int defpoints = 0;

		for(String user: user_seq.keySet())
		{

			if(user.equals("\"$2y$10$4CyKTc5BlQCaUdr5Sqs2JeqJmHjCT2oE8XllF2mAp.Wyy9/Ace03u\""))
			{
				//int pp=1;
				System.out.println("hi");
			}

			int attpoints = 20;

			String seq = user_seq.get(user);

			int[] controllers = new int[noderewards.size()];




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

				int defaction = -1;//Integer.parseInt(splittedseq[2*i]);
				int attaction = -1;//Integer.parseInt(splittedseq[2*i+1]);

				if(splittedseq[2*i].equals(""))
				{
					defaction = 0;
				}
				else
				{
					defaction = Integer.parseInt(splittedseq[2*i]);
				}

				if(splittedseq[2*i+1].equals(""))
				{
					attaction = 5;;
				}
				else
				{
					attaction = Integer.parseInt(splittedseq[2*i+1]);
				}





				int attcost = noderewards.get(attaction)[1];
				int defcost = noderewards.get(defaction)[1];
				// cost for action
				attpoints -= attcost;
				defpoints -= defcost;
				//reward for current action
				if(defaction != attaction)
				{
					int attreward = noderewards.get(attaction)[0];
					attpoints += attreward;
					controllers[attaction] = 1;
					controllers[defaction] = 0;
				}
				else if((defaction == attaction) && (controllers[attaction]==1))
				{
					int attreward = noderewards.get(attaction)[0];
					attpoints += attreward;
				}




				// now reward for other controlled nodes
				for(int j=0; j<controllers.length; j++)
				{
					if((j != attaction) && (controllers[j]==1))
					{
						int attreward = noderewards.get(j)[0];
						attpoints += attreward;
					}
				}
			}

			user_reward.put(user, attpoints);

			sumattackerpoints += attpoints;
		}
		//System.out.print( attpoints+", ");

		return sumattackerpoints/user_seq.size();
	}



	private static int computeAttackerReward(ArrayList<Integer> seq, HashMap<Integer, Integer[]> noderewards) {





		int[] controllers = new int[noderewards.size()];

		int attpoints = 0;
		int defpoints = 0;

		/*//System.out.print("");

		for(int i= 0; i<seq.size(); i++)
		{
			System.out.print(seq.get(i) + ", ");
		}
		 */
		//System.out.println();
		for(int i= 0; i<(seq.size()/2); i++)
		{

			int defaction = seq.get(2*i);
			int attaction = seq.get(2*i+1);


			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action
			attpoints -= attcost;
			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];
				attpoints += attreward;
				controllers[attaction] = 1;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((controllers[j] != controllers[attaction]) && (controllers[j]==1))
				{
					int attreward = noderewards.get(attaction)[0];
					attpoints += attreward;
				}
			}
		}
		//System.out.print( attpoints+", ");

		return attpoints;
	}


	private static int computeDefenderReward(ArrayList<Integer> seq, HashMap<Integer, Integer[]> noderewards) {





		int[] controllers = new int[noderewards.size()];

		int attpoints = 0;
		int defpoints = 0;

		/*//System.out.print("");

		for(int i= 0; i<seq.size(); i++)
		{
			System.out.print(seq.get(i) + ", ");
		}
		 */
		//System.out.println();
		for(int i= 0; i<(seq.size()/2); i++)
		{

			int defaction = seq.get(2*i);
			int attaction = seq.get(2*i+1);


			int attcost = noderewards.get(attaction)[1];
			int defcost = noderewards.get(defaction)[1];
			// cost for action
			attpoints -= attcost;
			defpoints -= defcost;
			//reward for current action
			if(defaction != attaction)
			{
				int attreward = noderewards.get(attaction)[0];
				attpoints += attreward;
				controllers[attaction] = 1;
			}
			// now reward for other controlled nodes
			for(int j=0; j<controllers.length; j++)
			{
				if((controllers[j] != controllers[attaction]) && (controllers[j]==1))
				{
					int attreward = noderewards.get(attaction)[0];
					attpoints += attreward;
				}
			}
		}
		//System.out.print( defpoints+", ");

		return defpoints;
	}

	public static DNode buildGameTree(int DEPTH_LIMIT, int naction) {




		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		DNode root = createGameTree(DEPTH_LIMIT, naction, noderewards);
		System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		System.out.println();
		printTree(root, naction);


		return root;

	}


	public static DNode buildGameTreeBFS(int DEPTH_LIMIT, int naction) {




		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		DNode root = createGameTreeBFS(DEPTH_LIMIT, naction, noderewards);
		System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		System.out.println();
		printTree(root, naction);


		return root;

	}




	public static void computeAttackerBestResponse(HashMap<String, InfoSet> isets, HashMap<String, int[]> attackfrequency, int naction,
			HashMap<String,HashMap<String,Double>> defstrategy, DNode root, int depthlimit, HashMap<Integer,ArrayList<String>> depthinfoset, double lambda) throws Exception 
	{


		String[] keyset = new String[isets.size()];
		int indx=0;

		for(String is: isets.keySet())
		{
			keyset[indx++] = is;
		}
		Arrays.sort(keyset);




		/**
		 * 1. for all information set in a depth compute attacker best response
		 * 2. 
		 * 
		 */

		for(int depth=depthlimit-1; depth>0; depth -=2)
		{
			System.out.println("depth "+ depth);


			for(String isetname: depthinfoset.get(depth))
			{
				System.out.println("iset : "+ isetname);
				InfoSet iset = isets.get(isetname);

				/**
				 * now compute the sequence to reach the information set of defender
				 */

				/**
				 * find the information set of defender that led to iset
				 * 
				 * find a parent node pnode
				 * 
				 * find the infoset of the pnode 
				 */

				DNode parentnode = iset.nodes.get(0).parent;



				String parentisetname = parentnode.infoset;

				System.out.println("parent iset "+ parentisetname);


				InfoSet parentiset = isets.get(parentisetname);


				/**
				 * now find the sequence that led to parentiset
				 */

				//System.out.println(parentisetname+ " probs : ");
				for(Integer a: parentiset.qre_prob_val.keySet())
				{
					System.out.println(a+" : "+ parentiset.qre_prob_val.get(a));
				}


				/**
				 * now compute expected utiilty for attacker for playing every action using reward for the child nodes
				 */

				/**
				 * for every action of attacker in every node compute the expected utility
				 */

				HashMap<Integer, Double> attackerexputility = new HashMap<Integer, Double>();
				double sumexputility = 0; // sum of exponent of utility

				for(int action=0; action<naction; action++)
				{

					double sumexpval = 0;

					System.out.println("action "+ action);

					for(DNode infnode: iset.nodes)
					{

						/**
						 * get the defender prob
						 */

						System.out.println("Node  "+ infnode.nodeid);

						double defprob = parentiset.qre_prob_val.get(infnode.prevaction);

						System.out.println("def prob  "+ defprob);
						/**
						 * get attacker utility
						 */
						// get the child node for playing action

						DNode child = infnode.child.get(action);

						System.out.println("child node  "+ child.nodeid);


						double attutility = child.attacker_reward;


						System.out.println("attutility  "+ attutility);


						double tmpattackexputility = defprob*attutility;


						System.out.println("tmpattackexputility  "+ tmpattackexputility);

						sumexpval += tmpattackexputility;

						System.out.println("sumexpval  "+ sumexpval);


					}

					sumexputility += Math.exp(lambda*sumexpval);

					System.out.println("sumexputility  "+ sumexputility);

					attackerexputility.put(action, sumexpval);

				}


				/**
				 * now compute the Q-BR
				 */

				HashMap<Integer, Double> qbr = new HashMap<Integer, Double>();


				double sumqbr = 0.0;

				for(int action=0; action<naction; action++)
				{
					double tmpqbr = Math.exp(lambda*attackerexputility.get(action)) /sumexputility;
					sumqbr += tmpqbr;

					System.out.println("action  "+ action + " , qbr "+ tmpqbr);
					qbr.put(action, tmpqbr);
				}

				System.out.println("sum qbr  "+ Math.round(sumqbr * 100.0) / 100.0);


				/**
				 * update the attacker strategy
				 */

				for(int action=0; action<naction; action++)
				{
					iset.qre_prob_val.put(action, qbr.get(action));

				}



				/**
				 * now propagate the expected values to upwards
				 * 
				 * 
				 * 1. For every node of attacker compute the expected utility and update the node's atatcker utility
				 * 2. for defender information set compute the expected utlity for attacker
				 * 
				 */
				System.out.println("iset "+ iset.id);
				for(DNode node: iset.nodes)
				{
					double exp = 0;
					for(int action=0; action<naction; action++)
					{
						DNode child = node.child.get(action);

						double attut = iset.qre_prob_val.get(action)*child.attacker_reward;
						exp += attut;
					}
					System.out.println("node "+ node.nodeid + ", att_reward " + exp);
					node.attacker_reward = exp;
				}



				for(DNode node: parentiset.nodes)
				{
					double exp = 0;
					for(int action=0; action<naction; action++)
					{
						System.out.print("action "+ action + ", ");

						DNode child = node.child.get(action);

						System.out.println("child node "+ child.nodeid + ", att_reward " + child.attacker_reward);
					}
				}




				// now update the reward for the defender's infoset's node's attacker reward
				System.out.println("updating parent info set "+ parentiset.id+" node with attacker reward");
				for(DNode node: parentiset.nodes)
				{
					double exp = 0;
					for(int action=0; action<naction; action++)
					{
						System.out.println("action "+ action);

						DNode child = node.child.get(action);

						System.out.println("child node "+ child.nodeid + ", att_reward " + child.attacker_reward);


						System.out.println("parentiset.qre_prob_val "+ parentiset.qre_prob_val.get(action));

						double attut = parentiset.qre_prob_val.get(action)*child.attacker_reward;

						System.out.println("attut "+ attut);
						exp += attut;
						System.out.println("exp "+ exp);
					}
					System.out.println("node "+ node.nodeid + ", att_reward " + exp);
					node.attacker_reward = exp;
				}
				System.out.println();
				//int p=1;


			}

		}














		/*HashMap<String, String> container = new HashMap<String, String>();
		for(String isetname: keyset)
		{


			InfoSet iset = isets.get(isetname);

			if(iset.player==1 && iset.depth>0)
			{


			ArrayList<String> doneprobs = new ArrayList<String>();

				// find the history of any node

				DNode node = iset.nodes.get(0);
				DNode child = null;
				for(DNode ch: node.child.values())
				{
					child= ch;
					break;
				}

				String histp1 = "";
				String histp0 = "";

				DNode tmp = node.parent;

				int pl = 1;

				while(tmp != null && tmp.nodeid != 0)
				{

					if(pl==0)
					{
						histp0 += (tmp.prevaction+1);
					}
					else
					{
						histp1 += (tmp.prevaction+1);
					}
					tmp = tmp.parent;
					pl = pl^1;
				}

				System.out.println(isetname + " hist : "+ histp0 + " "+ histp1 );


				String key = histp0 +"0"+histp1;
				String value = iset.qre_var.get(child.nodeid);
				container.put(key, value);

				int z=1;
			}




			for(Integer nodeid: iset.qre_var.keySet())
			{


				String qre_prob = iset.qre_prob.get(nodeid);
				String qre_var = iset.qre_var.get(nodeid);

				if(!doneprobs.contains(qre_prob))
				{
					System.out.print("0.5;");
					doneprobs.add(qre_prob);
				}



			}
		}*/


	}

	public static void updateTreeWithDefStartegy(HashMap<String, InfoSet> isets, DNode root, HashMap<String, HashMap<String, Double>> strategy, int naction) 
	{

		String[] keyset = new String[isets.size()];
		int indx=0;

		for(String is: isets.keySet())
		{
			keyset[indx++] = is;
		}
		Arrays.sort(keyset);



		for(String isetname: keyset)
		{

			InfoSet iset = isets.get(isetname);
			// for every info set
			/**
			 * 1. find the history of attackere and defender
			 */
			if(iset.player==0)
			{


				// find the history of any node
				System.out.println("infoset "+ isetname);
				DNode node = iset.nodes.get(0);
				DNode child = null;
				for(DNode ch: node.child.values())
				{
					child= ch;
					break;
				}

				String histp1 = "";
				String histp0 = "";

				DNode tmp = node;

				int pl = 1; // player of previous node.parent

				while(tmp != null && tmp.nodeid != 0)
				{

					if(pl==0)
					{
						histp0  = (tmp.prevaction)+"," + histp0;
					}
					else
					{
						histp1 = (tmp.prevaction)+"," + histp1;
					}
					tmp = tmp.parent;
					pl = pl^1;
				}

				System.out.println(isetname + " hist : "+ histp0 + " "+ histp1 );





				String key = histp0 +" "+histp1;
				//String value = iset.qre_var.get(child.nodeid);

				if(key.equals(" "))
				{
					key = "EMPTY EMPTY";
				}
				else
				{
					histp0 = histp0.substring(0, histp0.length()-1);
					histp1 = histp1.substring(0, histp1.length()-1);
					key = histp0 +" "+histp1;

				}





				HashMap<String, Double> defstrat = new HashMap<String, Double>();

				if(strategy.containsKey(key))
				{
					defstrat = strategy.get(key);
				}
				else
				{
					for(int i=0; i<naction; i++)
					{
						if(i==0)
							defstrat.put(String.valueOf(i), 1.0);
						else
							defstrat.put(String.valueOf(i), 0.0);

					}
				}



				for(DNode ch : iset.nodes) // here we assume that defender information sets have only one node
				{


					for(int ac=0; ac<naction; ac++)
					{
						String action = ac+"";
						double prob = 0;
						if(defstrat.containsKey(action))
						{
							prob = defstrat.get(action);

						}
						iset.qre_prob_val.put(ac, prob);
						System.out.println("Setting prob "+ prob + " for action "+ action);

					}
					int z=1;
				}



			}


		}

	}

	public static HashMap<String, double[]> prepareAttackerStrategy(HashMap<Integer, ArrayList<String>> depthinfoset, HashMap<String, InfoSet> isets, int naction) throws Exception 
	{


		HashMap<String, double[]> attstrat = new HashMap<String, double[]>();


		for(ArrayList<String> depthisets: depthinfoset.values())
		{



			for(String isetname: depthisets)
			{

				double strat [] = new double[naction];
				InfoSet iset = isets.get(isetname);

				// get the history

				//System.out.println("infoset "+ isetname);
				DNode node = iset.nodes.get(0);
				DNode child = null;
				for(DNode ch: node.child.values())
				{
					child= ch;
					break;
				}

				String histp1 = "";
				String histp0 = "";

				DNode tmp = node.parent;

				int pl = 1;

				while(tmp != null && tmp.nodeid != 0)
				{

					if(pl==0)
					{
						histp0 = (tmp.prevaction)+"," + histp0;
					}
					else
					{
						histp1 = (tmp.prevaction)+"," + histp1;
					}
					tmp = tmp.parent;
					pl = pl^1;
				}

				//System.out.println(isetname + " hist : "+ histp0 + " "+ histp1 );





				String key = histp0 +" "+histp1;
				//String value = iset.qre_var.get(child.nodeid);

				if(key.equals(" ") || histp1.equals(""))
				{
					key = "EMPTY EMPTY";
				}
				else
				{
					histp0 = histp0.substring(0, histp0.length()-1);
					histp1 = histp1.substring(0, histp1.length()-1);
					key = histp0 +" "+histp1;

				}


				//System.out.println("key " + key );

				double sum = 0.0;



				System.out.println("infoset "+ iset.id + ", seq "+ key + ", att_strat : ");
				for(int a=0; a<naction; a++)
				{
					double prob = iset.qre_prob_val.get(a);
					strat[a] = prob;
					System.out.println("action " + a + ", prob " + prob);
					sum += prob;
				}


				//System.out.println("prob sum " + Math.round(sum * 100.0) / 100.0);

				attstrat.put(key, strat);




				if(sum<(1.0-0.0001))
				{
					throw new Exception("Attacker strategy prob sum "+ sum);
				}




			}
		}

		return attstrat;

	}

	public static DNode buildGameTreeRecur(int DEPTH_LIMIT, int naction, HashMap<String,HashMap<String,Double>> defstrategy,
			HashMap<String, double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency) {

		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		DNode root = createGameTreeRecur(DEPTH_LIMIT, naction, noderewards, defstrategy, attstrategy, lambda, attackfrequency);
		//System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		//System.out.println();
		printTree(root, naction);


		return root;
	}
	
	
	public static DNode buildGameTreeRecurDefBR(int DEPTH_LIMIT, int naction, HashMap<String,HashMap<String,Double>> defstrategy,
			HashMap<String, double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency) {

		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		
		
		
		DNode root = createGameTreeRecurDefBR(DEPTH_LIMIT, naction, noderewards, defstrategy, attstrategy, lambda, attackfrequency);
		//System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		//System.out.println();
		//printTree(root, naction);


		return root;
	}


	public static DNode buildGameTreeRecurDouble(int DEPTH_LIMIT, int naction, HashMap<String,HashMap<String,Double>> defstrategy,
			HashMap<String, double[]> attstrategy, double lambda, HashMap<String,double[]> attackfrequency) {

		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		DNode root = createGameTreeRecurDouble(DEPTH_LIMIT, naction, noderewards, defstrategy, attstrategy, lambda, attackfrequency);
		//System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		//System.out.println();
		printTree(root, naction);


		return root;
	}


	public  static DNode buildGameTreeRecurSUQR(int DEPTH_LIMIT, int naction, HashMap<String,HashMap<String,Double>> defstrategy,
			HashMap<String, double[]> attstrategy, double lambda, HashMap<String,int[]> attackfrequency, double[] omega) throws Exception {

		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);

		DNode root = createGameTreeRecurSUQR(DEPTH_LIMIT, naction, noderewards, defstrategy, attstrategy, lambda, attackfrequency, omega);
		//System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		//System.out.println();
		printTree(root, naction);


		return root;
	}



	/**
	 * prospect theory
	 * @param DEPTH_LIMIT
	 * @param naction
	 * @param defstrategy
	 * @param attstrategy
	 * @param lambda
	 * @param attackfrequency
	 * @param gamma 
	 * @param theta 
	 * @param beta 
	 * @param alpha 
	 * @return
	 * @throws Exception 
	 */
	public static DNode buildGameTreeRecurPT(int DEPTH_LIMIT, int naction, HashMap<String,HashMap<String,Double>> defstrategy,
			HashMap<String, double[]> attstrategy, HashMap<String,int[]> attackfrequency, double alpha, double beta, double theta, double gamma) throws Exception {

		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		DNode root = createGameTreeRecurPT(DEPTH_LIMIT, naction, noderewards, defstrategy, attstrategy, attackfrequency, alpha, beta, theta, gamma);
		//System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		//System.out.println();
		printTree(root, naction);


		return root;
	}


	public static void buildGameTreeRecurNE(int DEPTH_LIMIT, int naction, HashMap<String,HashMap<String,Double>> defstrategy,
			HashMap<String, double[]> attstrategy, double lambda) {

		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		createGameTreeRecurNE(DEPTH_LIMIT, naction, noderewards, defstrategy, attstrategy, lambda);
		//System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		//System.out.println();
		//sprintTree(root, naction);


		//return root;
	}


	public static void buildGameTreeRecurPT(int DEPTH_LIMIT, int naction, HashMap<String,HashMap<String,Double>> defstrategy,
			HashMap<String, double[]> attstrategy, double alpha, double beta, double theta, double gamma) throws Exception {

		HashMap<Integer, Integer[]> noderewards = createNodeRewards(naction);

		try {
			createGameTreeRecurPT(DEPTH_LIMIT, naction, noderewards, defstrategy, attstrategy, alpha, beta, theta, gamma);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Node id "+ root.nodeid + ", parent : "+ null + ", player "+ 0);
		//System.out.println();
		//sprintTree(root, naction);


		//return root;
	}




}


class InfoSet{

	ArrayList<DNode> nodes = new ArrayList<DNode>();


	HashMap<Integer, String> qre_prob = new HashMap<Integer, String>();
	HashMap<Integer, String> qre_var = new HashMap<Integer, String>();

	HashMap<Integer, Double> qre_prob_val = new HashMap<Integer, Double>();
	HashMap<Integer, Double> qre_var_val = new HashMap<Integer, Double>();




	int player;
	int depth;
	String id;
	public static int varcount = 1;

	public InfoSet(int player, int depth, String id) 
	{
		super();
		this.player = player;
		this.depth = depth;
		this.id = id;
	}






}


class DNode {
	public int nodeid;
	public int depth;
	public int player;
	public DNode parent;
	public boolean leaf;
	public int defender_reward;
	public double attacker_reward;
	public double attacker_penalty;
	int prevaction = -1;
	String infoset;
	int[] rewards;

	public HashMap<Integer, DNode> child = new HashMap<Integer, DNode>();


	public DNode() {
		super();

	}

	public DNode(int nodeid, int depth, int player) {
		super();
		this.nodeid = nodeid;
		this.depth = depth;
		this.player = player;
		this.attacker_reward = nodeid;
	}


	public DNode(int nodeid, int depth, int player, int naction) {
		super();
		this.nodeid = nodeid;
		this.depth = depth;
		this.player = player;
		this.attacker_reward = nodeid;
		this.rewards = new int[naction];
	}

	public DNode(DNode node) {
		super();
		this.nodeid = node.nodeid;
		this.depth = node.depth;
		this.player = node.player;
		this.parent = node.parent;

		for(Integer action: node.child.keySet())
		{
			this.child.put(action, node.child.get(action));
		}
	}





}
