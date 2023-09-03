package dfjsp_ga_ts;

import javax.rmi.CORBA.Util;
import java.util.*;

public class GA {

    // 禁忌搜索参数设置
    private static final int initialTabuLen = 10;
    private static final int bound = 6;
    static int TABU_LIMIT_MAX = 3000; //3000

    // 遗传算法参数设置
    private static final double CROSS_PROBABLY = 0.9;
    private static final double MUTATE_PROBABLY = 0.1;

    /**
     * 种群交叉操作
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
     * 轮盘赌根据 accumlateFitness 选择两个个体
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
     * 若执行交叉操作，返回 true
     *
     * @return
     */
    private static boolean isCrossed() {
        double crossProb = Utils.random.nextDouble();
        return crossProb < CROSS_PROBABLY;
    }

    /**
     * 交叉 XO, 即交叉,交叉之前需要clone，不然会影响原来的fsg
     * @param father
     * @param mother
     * @param data
     * @return
     */
    public static FSG cross(FSG father, FSG mother, Data data) {
        FSG fsgNew = null;
        // 两种交叉方案选择一种
        int choose = Utils.random.nextInt(3);
        //int choose = 0;
        switch (choose) {
            case 0:
                // 交叉工厂
                fsgNew = crossFactory(father, mother);
                break;
            case 1:
                // 交叉工件
                fsgNew = crossJob(father, mother, data);
                break;
            case 2:
                // 交叉机器
                fsgNew = corssMachine(father, mother);
                break;
        }
        return fsgNew;
    }

    // 方案0 ： 交叉工厂
    private static FSG crossFactory(FSG father, FSG mother) {
        FSG offspring = father.clone();
        int[] offspringFactory = offspring.getFactoryArr();
        int[] motherFactory = mother.getFactoryArr();
        // 选择2个交叉点
        int[] crossPoint = chooseTwoPoint(offspringFactory.length);
        // 选择完交叉点后交叉
        for (int i = crossPoint[0]; i < crossPoint[1] + 1; i++) {
            offspringFactory[i] = motherFactory[i];
        }
        return offspring;
    }

    // 方案1 ： 交叉工件, 首先把工件分为2组,父代保留，母代里插
    private static FSG crossJob(FSG father, FSG mother, Data data) {
        FSG offspring = father.clone();
        int[] offJobArr = offspring.getJobArr();
        // 把工件分为2组, 0组为父亲，1组为母亲
        ArrayList<ArrayList<Integer>> group = assignJobTwoGroup(data.getJobCount());

        // 父亲保留，母亲插入
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

    // 方案2 ： 交叉机器数组
    private static FSG corssMachine(FSG father, FSG mother) {
        FSG offspring = father.clone();
        int[][] offMachineIndexArr = offspring.getMachineIndexArr();
        int[][] motherMachineIndexArr = mother.getMachineIndexArr();
        // 选择2个交叉点
        int[] crossPoint = chooseTwoPoint(offMachineIndexArr.length);
        for (int i = crossPoint[0]; i <= crossPoint[1]; i++) {
            if (offMachineIndexArr[i].length >= 0)
                System.arraycopy(motherMachineIndexArr[i], 0, offMachineIndexArr[i], 0, offMachineIndexArr[i].length);
        }
        return offspring;
    }

    // 把工件分为2组
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

    // 得到两个交叉点
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
     * 种群变异操作
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
     * 若执行变异操作，返回 true
     * @return
     */
    private static boolean isMutated() {
        double mutateProb = Utils.random.nextDouble();
        return mutateProb < MUTATE_PROBABLY;

    }

    // 变异之前需要clone，不然会影响原来的fsg
    public static void mutate(FSG fsg, Data data) {
        // 三种变异方案选择一种
        int choose = Utils.random.nextInt(3);
        //int choose = 0;
        switch (choose) {
            case 0:
                // 变异工厂
                mutateFactory(fsg, data);
                break;
            case 1:
                // 变异工件
                mutateJob(fsg, data);
                break;
            case 2:
                // 变异机器
                mutateMachine(fsg, data);
                break;
        }
    }

    // 方案0 ： 变异工厂, 选择一个零件变换工厂
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

    // 方案1 ： 变异工件, 选择同一工厂的两个不同工件互换位置
    private static void mutateJob(FSG fsg, Data data) {
        int[] jobArr = fsg.getJobArr();
        // 得到工件分配到的工厂情况
        ArrayList<ArrayList<Integer>> jobToFactoryList = jobToFactory(fsg, data);
        // 得到工件数目大于等于2的工厂，从这些工厂中选择2个零件交换位置
        ArrayList<Integer> jobBigerTwo = jobBig2Factory(jobToFactoryList);
        int factoryIndex = Utils.random.nextInt(jobBigerTwo.size());
        // 选中交换工件的工厂,得到工厂中的工件编号
        int chooseFactory = jobBigerTwo.get(factoryIndex);
        ArrayList<Integer> jobs = jobToFactoryList.get(chooseFactory);
        // 挑选2个工件
        int[] choosedTwoJob = chooseTwoJob(jobs);
        // 在jobvector中选择两个零件接着互换位置，分别各挑选一道工序
        changeJobOrder(jobArr, choosedTwoJob);
    }

    // 方案2 ： 变异机器, 每一个零件选择一道工序变换机器
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

    // 得到哪个零件在哪个工厂
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

    // 得到工件数目大于2的工厂
    private static ArrayList<Integer> jobBig2Factory(ArrayList<ArrayList<Integer>> jobToFactoryList) {
        ArrayList<Integer> jobBigerTwo = new ArrayList<>();
        for (int i = 0; i < jobToFactoryList.size(); i++) {
            if (jobToFactoryList.get(i).size() >= 2) {
                jobBigerTwo.add(i);
            }
        }
        return jobBigerTwo;
    }

    // 挑选2个工件
    private static int[] chooseTwoJob(ArrayList<Integer> jobs) {
        if (jobs.size() < 2) {
            System.out.println("零件数目小于2错误");
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

    // 	在jobvector中选择两个零件接着互换位置，分别各挑选一道工序
    private static void changeJobOrder(int[] jobArr, int[] choosedJob) {
        // 找到chooseJob[0] and [1] 所在的位置
        ArrayList<Integer> list_0 = new ArrayList<>();
        ArrayList<Integer> list_1 = new ArrayList<>();
        for (int i = 0; i < jobArr.length; i++) {
            if (jobArr[i] == choosedJob[0]) {
                list_0.add(i);
            } else if (jobArr[i] == choosedJob[1]) {
                list_1.add(i);
            }
        }
        // 交换的两零件序号
        int[] code = new int[2];
        code[0] = list_0.get(Utils.random.nextInt(list_0.size()));
        code[1] = list_1.get(Utils.random.nextInt(list_1.size()));
        // 交换的两零件位置
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

            // 合并工件排序
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

            // 合并机器分配
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

            // 合并各工厂适应度值
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

            // 选择可行移动
            TupleOfMove chosenMove = null;
            while (childrenMove.size() != 0) {
                TupleOfMove currentMove = childrenMove.poll();
                if (currentMove.estimateFitness < historyBest.getFitness()||
                        !Utils.isForbid(chrom.getOperationArr(), currentMove.moveType, currentMove.move, tabuTable, data)) {
                    chosenMove = currentMove;
                    break;
                }
            }

            // 评估变换机器
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

            // 保存机器移动
            Object[] childrenMoveMachineArr = childrenMoveMachine.toArray();

            // 选择机器移动
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

                // 删除
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
                // 删除

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

                        // todo 保险 可移动工序 == 0
                        if (chosenMove == null) {
                            int rand = Utils.random.nextInt(childrenMoveMachine.size());
                            chosenMachineMove = (TupleOfMoveMachine) childrenMoveMachine.toArray()[rand];
                            executeMachineMove(chrom, tabuTable, chosenMachineMove, tabuSearch, data);
                        }
                        // todo 保险 可移动工序 == 0
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

            // 删除禁忌长度为0的禁忌，添加新的禁忌
            Iterator<Tabu> tabuIterator = tabuTable.iterator();
            while (tabuIterator.hasNext()) {
                Tabu next = tabuIterator.next();
                next.setLen(next.getLen() - 1);
                if (next.getLen() == 0) tabuIterator.remove();
            }
        }

        return historyBest;
    }

    // 按适应度值更新种群
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
