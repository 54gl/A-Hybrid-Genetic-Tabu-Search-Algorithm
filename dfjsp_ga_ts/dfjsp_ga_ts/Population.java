package dfjsp_ga_ts;

import java.util.ArrayList;
import java.util.Arrays;

public class Population {

    // 初始化种群
    public static FSG[] initializePop(int popSize, Data data) {
        FSG[] pop = new FSG[popSize];
        for (int i = 0; i < popSize; i++) {
            FSG fsg = new FSG(data);
            double[] fitnessFsg = new double[data.getFactoryCount()];
            pop[i] = fsg;
            int[] factoryArr = fsg.getFactoryArr();
            int[] jobArr = fsg.getJobArr();
            int[][] machineArr = fsg.getMachineIndexArr();

            // 初始化工厂分配
            ArrayList<ArrayList<Integer>> JobLisOfFactory= new ArrayList<>();
            for (int j = 0; j < data.getFactoryCount(); j++) {
                JobLisOfFactory.add(new ArrayList<>());
            }
            ArrayList<ArrayList<Integer>> FactoryToJob= new ArrayList<>();
            for (int j = 0; j < data.getFactoryCount(); j++) {
                FactoryToJob.add(new ArrayList<>());
            }
            for (int j = 0; j < factoryArr.length; j++) {
                FactoryToJob.get(factoryArr[j]).add(j);
            }
            for (int j = 0; j < jobArr.length; j++) {
                int job = jobArr[j];
                int factory = factoryArr[job];
                JobLisOfFactory.get(factory).add(job);
            }

            Chrom[] chroms = new Chrom[data.getFactoryCount()];
            for (int j = 0; j < chroms.length; j++) {
                ArrayList<Integer> list = JobLisOfFactory.get(j);
                int[] code = new int[list.size()];
                for (int k = 0; k < code.length; k++) {
                    code[k] = list.get(k);
                }
                chroms[j] = new Chrom(code, machineArr, data);
                fitnessFsg[j] = Decode.fitnessOfSingleFactory(chroms[j], code, machineArr, data);
            }
            fsg.setFitnessArr(fitnessFsg);
            pop[i] = fsg;
        }
        return pop;
    }

    // 获得种群中最优解
    public static FSG getBest(FSG[] pop) {
        FSG best = pop[0];
        for (int i = 1; i < pop.length; i++) {
            if (Arrays.stream(pop[i].getFitnessArr()).max().getAsDouble() < Arrays.stream(best.getFitnessArr()).max().getAsDouble()) {
                best = pop[i];
            }
        }
        return best;
    }
}