package cyberpsycho;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

public class FlipItV12 {
	
	
	
	public static void  simulateAttacker() throws Exception
	{
		
		
		
		
		
		
	
		
		/** 
		 * 1.compute attacker strategy
		 * 2. Run simulation 500
		 * 
		 */
		AdversaryModel.suqrw4 = 4;
		int nexamples=100;
		int naction=6;
		int DEPTH_LIMIT = 10;
		double lambda=5;
		double[] omega = {0,5,-10,0};
		int numberofnodes = 6;
		int roundlimit = 5;
		
		HashMap<Integer, Integer[]> noderewards = EquationGenerator.createNodeRewards(naction);
		DNode root = new DNode(0, 0, 0);
		
		HashMap<String, HashMap<String, Double>> defstrategy = null;
		defstrategy = Data.readStrategy("g5d5_FI_v2.txt");
		HashMap<String, double[]> attstrategy = new HashMap<String, double[]>();
		
		EquationGenerator.attackStrategySUQR(0, naction, DEPTH_LIMIT, root, noderewards, "", defstrategy, attstrategy, lambda, omega);
		
	
		
		


		int[][] negameplay = new int[nexamples][DEPTH_LIMIT];
		int[][] negameplaydef = new int[nexamples][DEPTH_LIMIT];

		double points = AdversaryModelExps.playGame(defstrategy, attstrategy, naction, roundlimit, negameplay, nexamples, noderewards, negameplaydef);


		int attackcount[] = AdversaryModelExps.getAttackFrequency(negameplay, numberofnodes, roundlimit);
		int defcount[] = AdversaryModelExps.getDefFrequency(negameplay, numberofnodes, roundlimit);



		try
		{
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("brplaysuqr.csv"),true));

			//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");
			pw.append("player,#trial,w0,w1,w2,w3,points,nodeA(4/1),nodeB(5/3),NodeC(10/9),nodeD(8/8),NodeE(15/20),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");

			//pw.append("0,"+nexamples +","+ estimatedlambdanaive+","+points+","+0+","+0+","+0+",");
			pw.append(0+","+nexamples+","+ omega[0]+","+ omega[1]+","+ omega[2]+","+ omega[3]+","+points+",");
			
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
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("brplaysuqr.csv"),true));

			//pw.append("cluster,#users,lambda,score,mscore,nscore,pscore,nodeA(10/8),nodeB(10/2),NodeC(4/2),nodeD(4/8),NodeE(10/5),nodeF(PASS)"+ "\n");

			//pw.append(cluster+","+users_groups.size()+","+ estimatedlambda+","+sumscore+","+sum_mscore+","+sum_nscore+","+sum_pscore+"\n");

			pw.append(1+","+nexamples+","+ omega[0]+","+ omega[1]+","+ omega[2]+","+ omega[3]+","+0+",");

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

		
		

		
	}
	

}
