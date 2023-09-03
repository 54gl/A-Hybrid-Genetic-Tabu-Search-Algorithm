package dfjsp_ga_ts;

import javax.rmi.CORBA.Util;
import java.util.*;

public class GA {

    // ����������������
    private static final int initialTabuLen = 10;
    private static final int bound = 6;
    static int TABU_LIMIT_MAX = 3000; //3000

    // �Ŵ��㷨��������
    private static final double CROSS_PROBABLY = 0.9;
    private static final double MUTATE_PROBABLY = 0.1;

    /**
     * ��Ⱥ�������
     *
     * @param pop
     * @param data
     * @return
     */
    public static FSG[] crossPop(FSG[] pop, Data data) {
        FSG[] nextPop = new FSG[pop.length];
        double[] accumlateFitness = Calculation.getAccumlatedFitness(pop);
        for (int i = 0; i < nextPop.length; i++) {
            FSG[] parents = rouletteTwo(pop, accumlateFitness);
            if (isCrossed()) {
                nextPop[i] = cross(parents[0], parents[1], data);
            } else {
                nextPop[i] = parents[0].clone();
            }
        }
        return nextPop;
    }

    /**
     * ���̶ĸ��� accumlateFitness ѡ����������
     *
     * @param pop
     * @param accumlateFitness
     * @return
     */
    private static FSG[] rouletteTwo(FSG[] pop, double[] accumlateFitness) {
        FSG[] result = new FSG[2];
        double choosePop = Utils.random.nextDouble();
        for (int i = 0; i < accumlateFitness.length; i++) {
            if (choosePop < accumlateFitness[i]) {
                result[0] = pop[i];
                break;
            }
        }
        do {
            choosePop = Utils.random.nextDouble();
            for (int i = 0; i < accumlateFitness.length; i++) {
                if (choosePop < accumlateFitness[i]) {
                    result[1] = pop[i];
                    break;
                }
            }
        } while (result[1] == result[0]);
        return result;
    }

    /**
     * ��ִ�н������������ true
     *
     * @return
     */
    private static boolean isCrossed() {
        double crossProb = Utils.random.nextDouble();
        return crossProb < CROSS_PROBABLY;
    }

    /**
     * ���� XO, ������,����֮ǰ��Ҫclone����Ȼ��Ӱ��ԭ����fsg
     * @param father
     * @param mother
     * @param data
     * @return
     */
    public static FSG cross(FSG father, FSG mother, Data data) {
        FSG fsgNew = null;
        // ���ֽ��淽��ѡ��һ��
        int choose = Utils.random.nextInt(3);
        //int choose = 0;
        switch (choose) {
            case 0:
                // ���湤��
                fsgNew = crossFactory(father, mother);
                break;
            case 1:
                // ���湤��
                fsgNew = crossJob(father, mother, data);
                break;
            case 2:
                // �������
                fsgNew = corssMachine(father, mother);
                break;
        }
        return fsgNew;
    }

    // ����0 �� ���湤��
    private static FSG crossFactory(FSG father, FSG mother) {
        FSG offspring = father.clone();
        int[] offspringFactory = offspring.getFactoryArr();
        int[] motherFactory = mother.getFactoryArr();
        // ѡ��2�������
        int[] crossPoint = chooseTwoPoint(offspringFactory.length);
        // ѡ���꽻���󽻲�
        for (int i = crossPoint[0]; i < crossPoint[1] + 1; i++) {
            offspringFactory[i] = motherFactory[i];
        }
        return offspring;
    }

    // ����1 �� ���湤��, ���Ȱѹ�����Ϊ2��,����������ĸ�����
    private static FSG crossJob(FSG father, FSG mother, Data data) {
        FSG offspring = father.clone();
        int[] offJobArr = offspring.getJobArr();
        // �ѹ�����Ϊ2��, 0��Ϊ���ף�1��Ϊĸ��
        ArrayList<ArrayList<Integer>> group = assignJobTwoGroup(data.getJobCount());

        // ���ױ�����ĸ�ײ���
        int[] motherJobArr = mother.getJobArr();
        LinkedList<Integer> remainMotherStack = new LinkedList<>();
        for (int i = 0; i < motherJobArr.length; i++) {
            if (group.get(1).contains(motherJobArr[i])) {
                remainMotherStack.add(motherJobArr[i]);
            }
        }
        for (int i = 0; i < offJobArr.length; i++) {
            if (group.get(1).contains(offJobArr[i])) {
                offJobArr[i] = remainMotherStack.pop();
            }
        }
        return offspring;
    }

    // ����2 �� �����������
    private static FSG corssMachine(FSG father, FSG mother) {
        FSG offspring = father.clone();
        int[][] offMachineIndexArr = offspring.getMachineIndexArr();
        int[][] motherMachineIndexArr = mother.getMachineIndexArr();
        // ѡ��2�������
        int[] crossPoint = chooseTwoPoint(offMachineIndexArr.length);
        for (int i = crossPoint[0]; i <= crossPoint[1]; i++) {
            if (offMachineIndexArr[i].length >= 0)
                System.arraycopy(motherMachineIndexArr[i], 0, offMachineIndexArr[i], 0, offMachineIndexArr[i].length);
        }
        return offspring;
    }

    // �ѹ�����Ϊ2��
    private static ArrayList<ArrayList<Integer>> assignJobTwoGroup(int totalJob) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>(2);
        result.add(new ArrayList<>());
        result.add(new ArrayList<>());
        for (int i = 0; i < totalJob; i++) {
            int p = Utils.random.nextInt(2);
            ArrayList<Integer> list = result.get(p);
            list.add(i);
        }
        return result;
    }

    // �õ����������
    private static int[] chooseTwoPoint(int num) {
        int[] crossPoint = new int[2];
        crossPoint[0] = Utils.random.nextInt(num);
        do {
            crossPoint[1] = Utils.random.nextInt(num);
        } while (crossPoint[1] == crossPoint[0]);
        if (crossPoint[0] > crossPoint[1]) Utils.exchange(crossPoint, 0, 1);
        return crossPoint;
    }

    /**
     * ��Ⱥ�������
     */
    public static void mutatePop(FSG[] pop, Data data) {
        for (int i = 0; i < pop.length; i++) {
            FSG fsg = pop[i];
            if (isMutated()) {
                mutate(fsg, data);
            }
        }
    }

    /**
     * ��ִ�б������������ true
     * @return
     */
    private static boolean isMutated() {
        double mutateProb = Utils.random.nextDouble();
        return mutateProb < MUTATE_PROBABLY;

    }

    // ����֮ǰ��Ҫclone����Ȼ��Ӱ��ԭ����fsg
    public static void mutate(FSG fsg, Data data) {
        // ���ֱ��췽��ѡ��һ��
        int choose = Utils.random.nextInt(3);
        //int choose = 0;
        switch (choose) {
            case 0:
                // ���칤��
                mutateFactory(fsg, data);
                break;
            case 1:
                // ���칤��
                mutateJob(fsg, data);
                break;
            case 2:
                // �������
                mutateMachine(fsg, data);
                break;
        }
    }

    // ����0 �� ���칤��, ѡ��һ������任����
    private static void mutateFactory(FSG fsg, Data data) {
        int[] factoryArr = fsg.getFactoryArr();
        int chooseJob = Utils.random.nextInt(factoryArr.length);
        int initialFactory = factoryArr[chooseJob];
        int newFactory;
        do {
            newFactory = Utils.random.nextInt(data.getFactoryCount());
        } while (newFactory == initialFactory);
        factoryArr[chooseJob] = newFactory;
    }

    // ����1 �� ���칤��, ѡ��ͬһ������������ͬ��������λ��
    private static void mutateJob(FSG fsg, Data data) {
        int[] jobArr = fsg.getJobArr();
        // �õ��������䵽�Ĺ������
        ArrayList<ArrayList<Integer>> jobToFactoryList = jobToFactory(fsg, data);
        // �õ�������Ŀ���ڵ���2�Ĺ���������Щ������ѡ��2���������λ��
        ArrayList<Integer> jobBigerTwo = jobBig2Factory(jobToFactoryList);
        int factoryIndex = Utils.random.nextInt(jobBigerTwo.size());
        // ѡ�н��������Ĺ���,�õ������еĹ������
        int chooseFactory = jobBigerTwo.get(factoryIndex);
        ArrayList<Integer> jobs = jobToFactoryList.get(chooseFactory);
        // ��ѡ2������
        int[] choosedTwoJob = chooseTwoJob(jobs);
        // ��jobvector��ѡ������������Ż���λ�ã��ֱ����ѡһ������
        changeJobOrder(jobArr, choosedTwoJob);
    }

    // ����2 �� �������, ÿһ�����ѡ��һ������任����
    private static void mutateMachine(FSG fsg, Data data) {
        int[][] machineIndexArr = fsg.getMachineIndexArr();
        for (int job = 0; job < machineIndexArr.length; job++) {
            int op = Utils.random.nextInt(machineIndexArr[job].length);
            if (data.getProcessMachine()[job][op].length != 1) {
                int newIndex = Utils.random.nextInt(data.getProcessMachine()[job][op].length);
                while(newIndex == machineIndexArr[job][op]) {
                    newIndex = Utils.random.nextInt(data.getProcessMachine()[job][op].length);
                }
                machineIndexArr[job][op] = newIndex;
            }
        }
    }

    // �õ��ĸ�������ĸ�����
    private static ArrayList<ArrayList<Integer>> jobToFactory(FSG fsg, Data data) {
        ArrayList<ArrayList<Integer>> jobToFactoryList = new ArrayList<>();
        for (int i = 0; i < data.getFactoryCount(); i++) {
            jobToFactoryList.add(new ArrayList<>());
        }
        int[] factoryArr = fsg.getFactoryArr();
        for (int i = 0; i < factoryArr.length; i++) {
            int factoryNum = factoryArr[i];
            ArrayList<Integer> jobList = jobToFactoryList.get(factoryNum);
            jobList.add(i);
        }
        return jobToFactoryList;
    }

    // �õ�������Ŀ����2�Ĺ���
    private static ArrayList<Integer> jobBig2Factory(ArrayList<ArrayList<Integer>> jobToFactoryList) {
        ArrayList<Integer> jobBigerTwo = new ArrayList<>();
        for (int i = 0; i < jobToFactoryList.size(); i++) {
            if (jobToFactoryList.get(i).size() >= 2) {
                jobBigerTwo.add(i);
            }
        }
        return jobBigerTwo;
    }

    // ��ѡ2������
    private static int[] chooseTwoJob(ArrayList<Integer> jobs) {
        if (jobs.size() < 2) {
            System.out.println("�����ĿС��2����");
            return null;
        }
        int[] result = new int[2];
        int[] chooseNum = new int[2];
        int size = jobs.size();
        chooseNum[0] = Utils.random.nextInt(size);
        do {
            chooseNum[1] = Utils.random.nextInt(size);
        } while (chooseNum[1] == chooseNum[0]);
        for (int i = 0; i < result.length; i++) {
            result[i] = jobs.get(chooseNum[i]);
        }
        return result;
    }

    // 	��jobvector��ѡ������������Ż���λ�ã��ֱ����ѡһ������
    private static void changeJobOrder(int[] jobArr, int[] choosedJob) {
        // �ҵ�chooseJob[0] and [1] ���ڵ�λ��
        ArrayList<Integer> list_0 = new ArrayList<>();
        ArrayList<Integer> list_1 = new ArrayList<>();
        for (int i = 0; i < jobArr.length; i++) {
            if (jobArr[i] == choosedJob[0]) {
                list_0.add(i);
            } else if (jobArr[i] == choosedJob[1]) {
                list_1.add(i);
            }
        }
        // ��������������
        int[] code = new int[2];
        code[0] = list_0.get(Utils.random.nextInt(list_0.size()));
        code[1] = list_1.get(Utils.random.nextInt(list_1.size()));
        // �����������λ��
        Utils.exchange(jobArr, code[0], code[1]);
    }

    public static void tabuSearchPop(FSG[] pop, Data data) {
        for (int i = 0; i < pop.length; i++) {
            FSG fsg = pop[i];
            int[] factoryArr = fsg.getFactoryArr();
            int[] jobArr = fsg.getJobArr();
            int[][] machineIndexArr = fsg.getMachineIndexArr();

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
                int[][] clonedMachineIndexArr = Utils.copy2DimArr(machineIndexArr);
                chroms[j] = tabuSearch(new Chrom(code, clonedMachineIndexArr, data), data);
            }

            // �ϲ���������
            int[] newJobArr = new int[data.getTotalOperation()];
            int start = 0;
            for (int j = 0; j < chroms.length; j++) {
                Chrom chrom = chroms[j];
                int[] code = chrom.getCode();
                for (int k = 0; k < code.length; k++) {
                    newJobArr[start++] = code[k];
                }
            }
            fsg.setJobArr(newJobArr);

            // �ϲ���������
            int[][] newMachineIndexArr = Utils.copy2DimArr(machineIndexArr);
            for (int j = 0; j < chroms.length; j++) {
                Chrom chrom = chroms[j];
                int[] code = chrom.getCode();
                HashSet<Integer> jobSet = new HashSet<>();
                for (int ele : code) {
                    jobSet.add(ele);
                }
                for (Integer job : jobSet) {
                    for (int k = 0; k < data.getOperationCount()[job]; k++) {
                        newMachineIndexArr[job][k] = chrom.getMachineIndexArr()[job][k];
                    }
                }
            }
            fsg.setMachineIndexArr(newMachineIndexArr);

            // �ϲ���������Ӧ��ֵ
            double[] newFitnessArr = new double[chroms.length];
            for (int j = 0; j < chroms.length; j++) {
                newFitnessArr[j] = chroms[j].getFitness();
            }
            fsg.setFitnessArr(newFitnessArr);
        }
    }

    private static Chrom tabuSearch(Chrom chrom, Data data) {
        Chrom historyBest = chrom.clone();
        if (chrom.getCode() == null || chrom.getCode().length == 0 ||
                chrom.getCode().length == data.getOperationCount()[chrom.getCode()[0]]) {
            return historyBest;
        }
//        chrom.setOperationArr(GraphFactory.createOperationArr(data));
//        chrom.transferGraph(data);
        int tabuLen = initialTabuLen + Utils.random.nextInt(bound);
        int[] code = chrom.getCode();
        HashSet<Integer> codeSet = new HashSet<>();
        for (int i = 0; i < code.length; i++) {
            codeSet.add(code[i]);
        }
        ArrayList<Integer> jobList = new ArrayList<>(codeSet);
        TabuSearch tabuSearch = new TabuSearch(tabuLen, chrom.getOperationArr(), jobList, data);
        chrom.setFitness(tabuSearch.getPathFitness());
        LinkedList<Tabu> tabuTable = new LinkedList<>();

        int smallNotChange = 0;
        int BigChangeTimes = 0;

        while (BigChangeTimes <= TABU_LIMIT_MAX) {

            Map<Operation, PathInfo> L_0Tov = tabuSearch.getL_0Tov();
            Map<Operation, PathInfo> L_uTon = tabuSearch.getL_uTon();

            HashMap<TabuSearch.Move, LinkedList<Operation[]>> moveMap = tabuSearch.feasibleMove(data);
            PriorityQueue<TupleOfMove> childrenMove = new PriorityQueue<>(100);

            int currentChosenIndex = 0;
            TupleOfMove randChosenTupleOfMove = null;
            int randChosenIndex = -1;
            if (tabuSearch.getMoveSize() != 0) {
                randChosenIndex = Utils.random.nextInt(tabuSearch.getMoveSize());
            }

            for (TabuSearch.Move moveType : moveMap.keySet()) {
                LinkedList<Operation[]> moveList = moveMap.get(moveType);
                for (Operation[] move : moveList) {
                    int estimateFitness = Calculation.estimate(moveType, L_0Tov, L_uTon, move, data);
                    TupleOfMove tupleOfMove = new TupleOfMove(moveType, move, estimateFitness);
                    childrenMove.offer(tupleOfMove);
                    if (currentChosenIndex == randChosenIndex) randChosenTupleOfMove = tupleOfMove;
                    currentChosenIndex++;
                }
            }

            // ѡ������ƶ�
            TupleOfMove chosenMove = null;
            while (childrenMove.size() != 0) {
                TupleOfMove currentMove = childrenMove.poll();
                if (currentMove.estimateFitness < historyBest.getFitness()||
                        !Utils.isForbid(chrom.getOperationArr(), currentMove.moveType, currentMove.move, tabuTable, data)) {
                    chosenMove = currentMove;
                    break;
                }
            }

            // �����任����
            HashMap<Operation, ChangedMachine> changedMachineMap= tabuSearch.feasibleMachineMove(data);
            PriorityQueue<TupleOfMoveMachine> childrenMoveMachine = new PriorityQueue<>(500);
            for (Map.Entry<Operation, ChangedMachine> entry : changedMachineMap.entrySet()) {
                Operation operation = entry.getKey();
                ChangedMachine value = entry.getValue();
                int[] machines = value.getMachines();
                Operation[][] locations = value.getLocations();
                for (int i = 0; i < machines.length; i++) {
                    int machine = machines[i];
                    Operation[] location = locations[i];
                    if (location == null) continue;
                    Operation cur = location[0];
                    while (cur != location[1].getMS()) {
                        Operation u = cur.getMP();
                        Operation v = cur;
                        int res = Calculation.estimateChangeMacine(operation, machine, u, v, tabuSearch.getL_0Tov(), tabuSearch.getL_uTon(), data);
                        childrenMoveMachine.offer(new TupleOfMoveMachine(operation, u, v, machine, res));
                        cur = cur.getMS();
                    };
                    Operation u = location[1];
                    Operation v = cur;
                    int res = Calculation.estimateChangeMacine(operation, machine, u, v, tabuSearch.getL_0Tov(), tabuSearch.getL_uTon(), data);
                    childrenMoveMachine.offer(new TupleOfMoveMachine(operation, u, v, machine, res));
                }
            }

            // ��������ƶ�
            Object[] childrenMoveMachineArr = childrenMoveMachine.toArray();

            // ѡ������ƶ�
            TupleOfMoveMachine chosenMachineMove = null;
            while (childrenMoveMachine.size() != 0) {
                TupleOfMoveMachine currentMachineMove = childrenMoveMachine.poll();
                if (currentMachineMove.estimateFitness < historyBest.getFitness()||
                        !Utils.isForbidMachine(currentMachineMove.operation, currentMachineMove.machine,
                                currentMachineMove.u, currentMachineMove.v, tabuTable, chrom.getOperationArr(), data)) {
                    chosenMachineMove = currentMachineMove;
                    break;
                }
            }

            if (smallNotChange == 200) {

                // ɾ��
                if (tabuSearch.getMoveSize() + childrenMoveMachineArr.length == 0) {
                    double currentFitness = tabuSearch.getPathFitness();
                    if (currentFitness < historyBest.getFitness()) {
                        chrom.transferCode(jobList, data);
                        for (Integer job : jobList) {
                            for (int i = 0; i < data.getOperationCount()[job]; i++) {
                                int[][] machineIndexArr = chrom.getMachineIndexArr();
                                machineIndexArr[job][i] = chrom.getOperationArr()[job][i].getMachineIndex();
                            }
                        }
                        chrom.setFitness(currentFitness);
                        historyBest = chrom.clone();
                    }
                    break;
                }
                // ɾ��

                int randIndex = Utils.random.nextInt(tabuSearch.getMoveSize() + childrenMoveMachineArr.length);
                if (randIndex >= tabuSearch.getMoveSize()) {
                    chosenMachineMove = (TupleOfMoveMachine) childrenMoveMachineArr[randIndex - tabuSearch.getMoveSize()];
                    executeMachineMove(chrom, tabuTable, chosenMachineMove, tabuSearch, data);
                } else {
                    chosenMove = randChosenTupleOfMove;
                    executeOperationMove(tabuTable, chosenMove, tabuSearch, data);
                }
                smallNotChange = -1;
            } else {
                if (chosenMove == null && chosenMachineMove != null) {
                    executeMachineMove(chrom, tabuTable, chosenMachineMove, tabuSearch, data);
                } else if (chosenMove != null && chosenMachineMove == null) {
                    executeOperationMove(tabuTable, chosenMove, tabuSearch, data);
                } else if (chosenMove != null) {
                    if (chosenMove.estimateFitness <= chosenMachineMove.estimateFitness) {
                        executeOperationMove(tabuTable, chosenMove, tabuSearch, data);
                    } else {
                        executeMachineMove(chrom, tabuTable, chosenMachineMove, tabuSearch, data);
                    }
                } else {
                    if (tabuSearch.getMoveSize() + childrenMoveMachineArr.length == 0) {
                        double currentFitness = tabuSearch.getPathFitness();
                        if (currentFitness < historyBest.getFitness()) {
                            chrom.transferCode(jobList, data);
                            for (Integer job : jobList) {
                                for (int i = 0; i < data.getOperationCount()[job]; i++) {
                                    int[][] machineIndexArr = chrom.getMachineIndexArr();
                                    machineIndexArr[job][i] = chrom.getOperationArr()[job][i].getMachineIndex();
                                }
                            }
                            chrom.setFitness(currentFitness);
                            historyBest = chrom.clone();
                        }
                        break;
                    }

                    int randIndex = Utils.random.nextInt(tabuSearch.getMoveSize() + childrenMoveMachineArr.length);
                    if (randIndex >= tabuSearch.getMoveSize()) {
                        chosenMachineMove = (TupleOfMoveMachine) childrenMoveMachineArr[randIndex - tabuSearch.getMoveSize()];
                        executeMachineMove(chrom, tabuTable, chosenMachineMove, tabuSearch, data);
                    } else {
                        chosenMove = randChosenTupleOfMove;

                        // todo ���� ���ƶ����� == 0
                        if (chosenMove == null) {
                            int rand = Utils.random.nextInt(childrenMoveMachine.size());
                            chosenMachineMove = (TupleOfMoveMachine) childrenMoveMachine.toArray()[rand];
                            executeMachineMove(chrom, tabuTable, chosenMachineMove, tabuSearch, data);
                        }
                        // todo ���� ���ƶ����� == 0
                        else {
                            executeOperationMove(tabuTable, chosenMove, tabuSearch, data);
                        }

                    }
                    smallNotChange = -1;
                }
            }

            tabuSearch = new TabuSearch(tabuLen, chrom.getOperationArr(), jobList, data);
            double currentFitness = tabuSearch.getPathFitness();
            if (currentFitness < historyBest.getFitness()) {
                chrom.transferCode(jobList, data);
                for (Integer job : jobList) {
                    for (int i = 0; i < data.getOperationCount()[job]; i++) {
                        int[][] machineIndexArr = chrom.getMachineIndexArr();
                        machineIndexArr[job][i] = chrom.getOperationArr()[job][i].getMachineIndex();
                    }
                }
                chrom.setFitness(currentFitness);
                historyBest = chrom.clone();
                BigChangeTimes = 0;
                smallNotChange = 0;
            } else {
                BigChangeTimes++;
                smallNotChange++;
            }

            // ɾ�����ɳ���Ϊ0�Ľ��ɣ�����µĽ���
            Iterator<Tabu> tabuIterator = tabuTable.iterator();
            while (tabuIterator.hasNext()) {
                Tabu next = tabuIterator.next();
                next.setLen(next.getLen() - 1);
                if (next.getLen() == 0) tabuIterator.remove();
            }
        }

        return historyBest;
    }

    // ����Ӧ��ֵ������Ⱥ
    public static void updatePop(FSG[] pop, FSG[] nextPop) {
        FSG[] combinedPop = new FSG[pop.length + nextPop.length];
        int start = 0;
        for (int i = 0; i < pop.length; i++) {
            combinedPop[start] = pop[i];
            start++;
        }
        for (int i = 0; i < nextPop.length; i++) {
            combinedPop[start] = nextPop[i];
            start++;
        }
        Arrays.sort(combinedPop, (f1, f2) -> {
            if (Arrays.stream(f1.getFitnessArr()).max().getAsDouble() < Arrays.stream(f2.getFitnessArr()).max().getAsDouble()) {
                return -1;
            } else if (Arrays.stream(f1.getFitnessArr()).max().getAsDouble() > Arrays.stream(f2.getFitnessArr()).max().getAsDouble()) {
                return +1;
            } else {
                return 0;
            }
        });
        System.arraycopy(combinedPop, 0, pop, 0, pop.length);
    }

    static class TupleOfMove implements Comparable<TupleOfMove> {
        TabuSearch.Move moveType;
        Operation[] move;
        int estimateFitness;

        public TupleOfMove(TabuSearch.Move moveType, Operation[] move, int estimateFitness) {
            this.moveType = moveType;
            this.move = move;
            this.estimateFitness = estimateFitness;
        }

        @Override
        public int compareTo(TupleOfMove o) {
            return estimateFitness - o.estimateFitness;
        }
    }

    static class TupleOfMoveMachine implements Comparable<TupleOfMoveMachine> {
        Operation operation;
        Operation u;
        Operation v;
        int machine;
        int estimateFitness;

        public TupleOfMoveMachine(Operation operation, Operation u, Operation v, int machine, int estimateFitness) {
            this.operation = operation;
            this.u = u;
            this.v = v;
            this.machine = machine;
            this.estimateFitness = estimateFitness;
        }

        @Override
        public int compareTo(TupleOfMoveMachine o) {
            return estimateFitness - o.estimateFitness;
        }
    }

    private static void executeOperationMove(LinkedList<Tabu> tabuTable, TupleOfMove chosenMove, TabuSearch tabuSearch, Data data) {
        int tabuLen = initialTabuLen + Utils.random.nextInt(bound);
        tabuTable.add(Tabu.createTabu(chosenMove.moveType, chosenMove.move, tabuLen + 1, data));
        tabuSearch.move(chosenMove.moveType, chosenMove.move);
        if (chosenMove.move[0].getJob() == chosenMove.move[1].getJob()) {
            tabuSearch.move(chosenMove.moveType, new Operation[] {chosenMove.move[1], chosenMove.move[0]});
        }
    }

    private static void executeMachineMove(Chrom chrom, LinkedList<Tabu> tabuTable, TupleOfMoveMachine chosenMachineMove, TabuSearch tabuSearch, Data data) {
        int originMachine = data.getProcessMachine()[chosenMachineMove.operation.getJob()][chosenMachineMove.operation.getOperationNum()][chosenMachineMove.operation.getMachineIndex()];
        int newMachine = chosenMachineMove.machine;
        int[] operationCountOnMachine = chrom.getOperationCountOnMachine();
        operationCountOnMachine[originMachine] -= 1;
        operationCountOnMachine[newMachine] += 1;
        int tabuLen = initialTabuLen + Utils.random.nextInt(bound);
        tabuTable.add(new Tabu(originMachine, new int[] {chosenMachineMove.operation.getMachineLocation()}, new Operation[] {chosenMachineMove.operation}, tabuLen + 1));
        tabuSearch.moveMachine(chosenMachineMove.operation, chosenMachineMove.machine, chosenMachineMove.u, chosenMachineMove.v, data);
    }
}
