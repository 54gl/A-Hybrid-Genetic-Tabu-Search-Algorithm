package dfjsp_ga_ts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Test2 {

    public static void main(String[] args) {
        String file = "C:\\Users\\Akira\\Desktop\\data2.txt";
        Data data = new Data(file, 3);

        FSG fsg = new FSG(data);
        double[] fitnessFsg = new double[data.getFactoryCount()];
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


            HashSet<Integer> codeSet = new HashSet<>();
            for (int i = 0; i < code.length; i++) {
                codeSet.add(code[i] + 1);
            }
            System.out.println(codeSet);

            fitnessFsg[j] = Decode.DecodeOfSingleFactory(code, data);
        }
        fsg.setFitnessArr(fitnessFsg);
        System.out.println(Arrays.toString(fitnessFsg));
    }

}
