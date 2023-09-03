package dfjsp_ga_ts;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 */
public class Main {

    private static final int POP_SIZE = 40; //40
    private static final int TERMINATION_TIMES = 30; //30

    public static void main(String[] args) {

        String file = "C:\\Users\\Akira\\Desktop\\data2.txt";

        //String file = "C:\\Users\\Akira\\Desktop\\FJSP_DATA\\rdata\\la15.txt";
        Data data = new Data(file, 3);

        // 产生初始种群,并计算fitness
        FSG[] pop = Population.initializePop(POP_SIZE, data);

        // 保存全局最优解
        FSG globalBest = Population.getBest(pop).clone();

        System.out.println("globalBest: " + Arrays.toString(globalBest.getFitnessArr()));

//        // 打印结果
//        int[] jobArr1 = globalBest.getJobArr();
//        int[] factoryArr1 = globalBest.getFactoryArr();
//        int[][] machineIndexArr1 = globalBest.getMachineIndexArr();
//
//        ArrayList<ArrayList<Integer>> JobLisOfFactory1= new ArrayList<>();
//        for (int j = 0; j < data.getFactoryCount(); j++) {
//            JobLisOfFactory1.add(new ArrayList<>());
//        }
//        ArrayList<ArrayList<Integer>> FactoryToJob1= new ArrayList<>();
//        for (int j = 0; j < data.getFactoryCount(); j++) {
//            FactoryToJob1.add(new ArrayList<>());
//        }
//        for (int j = 0; j < factoryArr1.length; j++) {
//            FactoryToJob1.get(factoryArr1[j]).add(j);
//        }
//
//        for (int j = 0; j < jobArr1.length; j++) {
//            int job = jobArr1[j];
//            int factory = factoryArr1[job];
//            JobLisOfFactory1.get(factory).add(job);
//        }
//
//        for (int j = 0; j < data.getFactoryCount(); j++) {
//            ArrayList<Integer> list = JobLisOfFactory1.get(j);
//            int[] code = new int[list.size()];
//            for (int k = 0; k < code.length; k++) {
//                code[k] = list.get(k);
//            }
//            Decode.printOfSingleFactory(code, machineIndexArr1, data);
//        }
//


        int bestNotChange = 0;
        int gen = 0;

        //while (bestNotChange <= TERMINATION_TIMES && Arrays.stream(globalBest.getFitnessArr()).max().getAsDouble() > 500) {

        while (bestNotChange <= TERMINATION_TIMES) {
            // 染色体交叉
            FSG[] nextPop = GA.crossPop(pop, data);

            // 染色体变异
            GA.mutatePop(nextPop, data);

            // 染色体禁忌搜索
            GA.tabuSearchPop(pop, data);

            // 更新种群
            GA.updatePop(pop, nextPop);

            // 更新全局最优解
            FSG newBest = pop[0].clone();
            if (Arrays.stream(newBest.getFitnessArr()).max().getAsDouble() < Arrays.stream(globalBest.getFitnessArr()).max().getAsDouble()) {
                globalBest = newBest;
                bestNotChange = 0;
            } else {
                bestNotChange++;
            }
            gen++;
            System.out.println("gen" + gen + ": "+ Arrays.stream(globalBest.getFitnessArr()).max().getAsDouble());
        }
        System.out.println("bestFitness: " + Arrays.stream(globalBest.getFitnessArr()).max().getAsDouble());

        // 打印结果
        int[] jobArr = globalBest.getJobArr();
        int[] factoryArr = globalBest.getFactoryArr();
        int[][] machineIndexArr = globalBest.getMachineIndexArr();

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

        for (int j = 0; j < data.getFactoryCount(); j++) {
            ArrayList<Integer> list = JobLisOfFactory.get(j);
            int[] code = new int[list.size()];
            for (int k = 0; k < code.length; k++) {
                code[k] = list.get(k);
            }
            Decode.printOfSingleFactory(code, machineIndexArr, data);
        }
    }

}