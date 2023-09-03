package dfjsp_ga_ts;

import java.util.*;

public class TabuSearch {

    private int tabuLen;

    private Map<Operation, PathInfo> L_0Tov;
    private Map<Operation, PathInfo> L_uTon;
    private int pathFitness;
    private LinkedList<Operation> completeCriticalPath = new LinkedList<>();
    private Operation[] firstOperationOnMachine;
    private Operation[] lastOperationOnMachine;

    private int moveSize = -1;

    public TabuSearch(int tabuLen, Operation[][] operationArr, ArrayList<Integer> jobList, Data data) {
        this.tabuLen = tabuLen;
        L_0Tov(operationArr, jobList, data);
        L_uTon(operationArr, jobList, data);
    }

    /**
     * 获取起点 start 到各其他点的距离
     * @param operationArr
     * @return
     */
    private void L_0Tov(Operation[][] operationArr, ArrayList<Integer> jobList, Data data) {
        L_0Tov = new HashMap<>();
        // 工序上解码到了第几个位置
        int jobCount = jobList.size();
        int[] decodedOperationIndex = new int[data.getJobCount()];
        Arrays.fill(decodedOperationIndex, 0);
        int decodedCount = 0;
        this.firstOperationOnMachine = new Operation[data.getMachineCount()];
        int totalOperation = 0;
        for (Integer job : jobList) {
            totalOperation += data.getOperationCount()[job];
        }
        while (decodedCount < totalOperation) {
            for (int jobIndex = 0; jobIndex < jobCount; jobIndex++) {
                int job = jobList.get(jobIndex);
                if (decodedOperationIndex[job] >= data.getOperationCount()[job])  continue;
                Operation currentOperation = operationArr[job][decodedOperationIndex[job]];
                int currentMachine = data.getProcessMachine()[currentOperation.getJob()]
                        [currentOperation.getOperationNum()]
                        [currentOperation.getMachineIndex()];
                if (currentOperation.getMP() == null) {
                    Operation JP = currentOperation.getJP();
                    int value;
                    if (JP == null) {
                        value = 0;
                    } else {
                        value = L_0Tov.get(JP).getTotalValue() + data.getProcessTime()[JP.getJob()][JP.getOperationNum()][JP.getMachineIndex()];
                    }
                    PathInfo pathInfo = new PathInfo(value, null);
                    L_0Tov.put(currentOperation, pathInfo);
                    decodedOperationIndex[job]++;
                    decodedCount++;
                    if (this.firstOperationOnMachine[currentMachine] == null) {
                        this.firstOperationOnMachine[currentMachine] = currentOperation;
                    }
                    continue;
                }
                Operation MP = currentOperation.getMP();
                if (L_0Tov.get(MP) == null) continue;
                Operation JP = currentOperation.getJP();
                if (JP == null) {
                    PathInfo PathOfMP = L_0Tov.get(MP);
                    int value = PathOfMP.getTotalValue() +
                            data.getProcessTime()[MP.getJob()][MP.getOperationNum()][MP.getMachineIndex()];
                    PathInfo pathInfo = new PathInfo(value, null);
                    L_0Tov.put(currentOperation, pathInfo);
                } else {
                    PathInfo PathOfMP = L_0Tov.get(MP);
                    PathInfo PathOfJP = L_0Tov.get(JP);
                    int ValueOfMP = PathOfMP.getTotalValue() + data.getProcessTime()[MP.getJob()][MP.getOperationNum()][MP.getMachineIndex()];
                    int ValueOfJP = PathOfJP.getTotalValue() + data.getProcessTime()[JP.getJob()][JP.getOperationNum()][JP.getMachineIndex()];
                    if (ValueOfMP > ValueOfJP) {
                        PathInfo pathInfo = new PathInfo(ValueOfMP, null);
                        L_0Tov.put(currentOperation, pathInfo);
                    } else {
                        PathInfo pathInfo = new PathInfo(ValueOfJP, null);
                        L_0Tov.put(currentOperation, pathInfo);
                    }
                }
                decodedOperationIndex[job]++;
                decodedCount++;
                if (this.firstOperationOnMachine[currentMachine] == null) {
                    this.firstOperationOnMachine[currentMachine] = currentOperation;
                }
            }
        }
        int firstJob = jobList.get(0);
        pathFitness = L_0Tov.get(operationArr[firstJob][operationArr[firstJob].length - 1]).getTotalValue() +
                data.getProcessTime()
                        [operationArr[firstJob][operationArr[firstJob].length - 1].getJob()]
                        [operationArr[firstJob][operationArr[firstJob].length - 1].getOperationNum()]
                        [operationArr[firstJob][operationArr[firstJob].length - 1].getMachineIndex()];
        Operation lastOperation = operationArr[firstJob][operationArr[firstJob].length - 1];
        for (int j = 1; j < jobList.size(); j++) {
            int job = jobList.get(j);
            PathInfo currentPathInfo = L_0Tov.get(operationArr[job][data.getOperationCount()[job] - 1]);
            int currentValue = currentPathInfo.getTotalValue() +
                    data.getProcessTime()
                            [operationArr[job][data.getOperationCount()[job] -1].getJob()]
                            [operationArr[job][data.getOperationCount()[job] -1].getOperationNum()]
                            [operationArr[job][data.getOperationCount()[job] -1].getMachineIndex()];
            if (currentValue > pathFitness) {
                pathFitness = currentValue;
                lastOperation = operationArr[job][data.getOperationCount()[job] -1];
            }
        }
        Operation currentOp = lastOperation;
        completeCriticalPath.push(currentOp);
        while (L_0Tov.get(currentOp).getTotalValue() != 0) {
            Operation MP = currentOp.getMP();
            int operationStartTime = L_0Tov.get(currentOp).getTotalValue();
            int MPEndTime = MP == null ? -1 : L_0Tov.get(MP).getTotalValue() + data.getProcessTime()[MP.getJob()][MP.getOperationNum()][MP.getMachineIndex()];
            if (operationStartTime == MPEndTime) {
                completeCriticalPath.push(MP);
                currentOp = MP;
            } else {
                Operation JP = currentOp.getJP();
                completeCriticalPath.push(JP);
                currentOp = JP;
            }
        }
    }

    /**
     * 获取终点 end 到各其他点的距离
     * @param operationArr
     * @return
     */
    private void L_uTon(Operation[][] operationArr, ArrayList<Integer> jobList, Data data) {
        L_uTon = new HashMap<>();
        // 工序上解码到了第几个位置
        int jobCount = jobList.size();
        int[] decodedOperationIndex = new int[data.getJobCount()];
        this.lastOperationOnMachine = new Operation[data.getMachineCount()];
        for (int i = 0; i < data.getOperationCount().length; i++) {
            decodedOperationIndex[i] = data.getOperationCount()[i] - 1;
        }
        int totalOperation = 0;
        for (Integer job : jobList) {
            totalOperation += data.getOperationCount()[job];
        }
        int decodedCount = 0;
        while (decodedCount < totalOperation) {
            for (int jobIndex = 0; jobIndex < jobCount; jobIndex++) {
                int job = jobList.get(jobIndex);
                if (decodedOperationIndex[job] < 0)  continue;
                Operation currentOperation = operationArr[job][decodedOperationIndex[job]];
                int currentMachine = data.getProcessMachine()[currentOperation.getJob()]
                        [currentOperation.getOperationNum()]
                        [currentOperation.getMachineIndex()];
                if (currentOperation.getMS() == null) {
                    Operation JS = currentOperation.getJS();
                    int value;
                    if (JS == null) {
                        value = data.getProcessTime()[currentOperation.getJob()][currentOperation.getOperationNum()][currentOperation.getMachineIndex()];
                    } else {
                        value = L_uTon.get(JS).getTotalValue() +
                                data.getProcessTime()[currentOperation.getJob()][currentOperation.getOperationNum()][currentOperation.getMachineIndex()];
                    }
                    PathInfo pathInfo = new PathInfo(value, null);
                    L_uTon.put(currentOperation, pathInfo);
                    decodedOperationIndex[job]--;
                    decodedCount++;
                    if (this.lastOperationOnMachine[currentMachine] == null) {
                        this.lastOperationOnMachine[currentMachine] = currentOperation;
                    }
                    continue;
                }
                Operation MS = currentOperation.getMS();
                if (L_uTon.get(MS) == null) continue;
                Operation JS = currentOperation.getJS();
                if (JS == null) {
                    PathInfo PathOfMS = L_uTon.get(MS);
                    int value = PathOfMS.getTotalValue() +
                            data.getProcessTime()[currentOperation.getJob()][currentOperation.getOperationNum()][currentOperation.getMachineIndex()];
                    PathInfo pathInfo = new PathInfo(value, null);
                    L_uTon.put(currentOperation, pathInfo);
                } else {
                    PathInfo PathOfMS = L_uTon.get(MS);
                    PathInfo PathOfJS = L_uTon.get(JS);
                    if (PathOfMS.getTotalValue() > PathOfJS.getTotalValue()) {
                        int value = PathOfMS.getTotalValue() +
                                data.getProcessTime()[currentOperation.getJob()][currentOperation.getOperationNum()][currentOperation.getMachineIndex()];
                        PathInfo pathInfo = new PathInfo(value, null);
                        L_uTon.put(currentOperation, pathInfo);
                    } else {
                        int value = PathOfJS.getTotalValue() +
                                data.getProcessTime()[currentOperation.getJob()][currentOperation.getOperationNum()][currentOperation.getMachineIndex()];
                        PathInfo pathInfo = new PathInfo(value, null);
                        L_uTon.put(currentOperation, pathInfo);
                    }
                }
                decodedOperationIndex[job]--;
                decodedCount++;
                if (this.lastOperationOnMachine[currentMachine] == null) {
                    this.lastOperationOnMachine[currentMachine] = currentOperation;
                }
            }
        }
//        pathFitness = L_uTon.get(operationArr[0][0]).getTotalValue();
//        for (int i = 1; i < operationArr.length; i++) {
//            PathInfo currentPathInfo = L_uTon.get(operationArr[i][0]);
//            int currentValue = currentPathInfo.getTotalValue();
//            if (currentValue > pathFitness) {
//                pathFitness = currentValue;
//            }
//        }
//
//        System.out.println(pathFitness);
    }

    /**
     * 获取关键路径
     * @param data  工件与机器数据
     */
    public LinkedList<LinkedList<Operation>> criticalPath(Data data) {
        LinkedList<LinkedList<Operation>> criticalPath = new LinkedList<>();
        Iterator<Operation> iterator = completeCriticalPath.iterator();
        Operation firstOperation = iterator.next();
        LinkedList<Operation> operationBlock = new LinkedList<>();
        operationBlock.add(firstOperation);
        criticalPath.add(operationBlock);
        int currentMachine = data.getProcessMachine()
                [firstOperation.getJob()][firstOperation.getOperationNum()][firstOperation.getMachineIndex()];
        while (iterator.hasNext()) {
            Operation operation = iterator.next();
            int machine = data.getProcessMachine()
                    [operation.getJob()][operation.getOperationNum()][operation.getMachineIndex()];
            if (currentMachine == machine) {
                operationBlock.add(operation);
            } else {
                currentMachine = machine;
                operationBlock = new LinkedList<>();
                operationBlock.add(operation);
                criticalPath.add(operationBlock);
            }
        }
        return criticalPath;
    }

    /**
     * 获取全部可行移动
     * @param data 工件与机器数据
     * @return
     */
    public HashMap<Move, LinkedList<Operation[]>> feasibleMove(Data data) {
        LinkedList<LinkedList<Operation>> criticalPath = criticalPath(data);
        HashMap<Move, LinkedList<Operation[]>> moveLinkedListHashMap = new HashMap<>();
        for (Move move : Move.values()) {
            moveLinkedListHashMap.put(move, new LinkedList<>());
        }
        moveSize = 0;
        for (LinkedList<Operation> operationList : criticalPath) {
            // 第一块不移动块首
            if (operationList == criticalPath.getFirst()) {
                if (operationList.size() == 1) {
                    continue;
                } else if (operationList.size() == 2) {
                    LinkedList<Operation[]> moveUtoV = moveLinkedListHashMap.get(Move.uTov);
                    moveUtoV.add(new Operation[]{operationList.getFirst(), operationList.getLast()});
                } else if (operationList.size() == 3) {
                    Operation u = operationList.getFirst();
                    Operation v = operationList.getLast();
                    LinkedList<Operation[]> moveUtoV = moveLinkedListHashMap.get(Move.uTov);
                    if (satisfyUtoV(u, v, data)) moveUtoV.add(new Operation[] {u, v});
                    LinkedList<Operation[]> moveVtoU = moveLinkedListHashMap.get(Move.vTou);
                    moveVtoU.add(new Operation[]{v, operationList.get(1)});
                    if (satisfyVtoU(v, u, data)) moveVtoU.add(new Operation[] {v, u});
                } else {
                    // uTov
                    Operation uTov_u = operationList.getFirst();
                    Operation uTov_v = operationList.getLast();
                    LinkedList<Operation[]> moveUtoV = moveLinkedListHashMap.get(Move.uTov);
                    if (satisfyUtoV(uTov_u, uTov_v, data)) moveUtoV.add(new Operation[] {uTov_u, uTov_v});

                    // vTou
                    Operation vTou_v = operationList.getLast();
                    Iterator<Operation> iterator_vTou = operationList.iterator();
                    LinkedList<Operation[]> moveVtoU = moveLinkedListHashMap.get(Move.vTou);
                    while (iterator_vTou.hasNext()) {
                        Operation vTou_u = iterator_vTou.next();
                        if (vTou_u == vTou_v.getMP()) {
                            moveVtoU.add(new Operation[] {vTou_v, vTou_u});
                            break;
                        }
                        if (satisfyVtoU(vTou_v, vTou_u, data)) {
                            moveVtoU.add(new Operation[] {vTou_v, vTou_u});
                        }
                    }

                    // innerTov
                    LinkedList<Operation[]> moveInnerToV = moveLinkedListHashMap.get(Move.innerTov);
                    for (int i = 1; i < operationList.size() - 2; i++) {
                        Operation inner = operationList.get(i);
                        Operation inner_v = operationList.getLast();
                        if (satisfyUtoV(inner, inner_v, data)) {
                            moveInnerToV.add(new Operation[] {inner, inner_v});
                        }
                    }
                }
                continue;
            }

            // 最后一块不移动块尾
            if (operationList == criticalPath.getLast()) {
                if (operationList.size() == 1) {
                    continue;
                } else if (operationList.size() == 2) {
                    LinkedList<Operation[]> moveUtoV = moveLinkedListHashMap.get(Move.uTov);
                    moveUtoV.add(new Operation[]{operationList.getFirst(), operationList.getLast()});
                } else if (operationList.size() == 3) {
                    Operation u = operationList.getFirst();
                    Operation v = operationList.getLast();
                    LinkedList<Operation[]> moveUtoV = moveLinkedListHashMap.get(Move.uTov);
                    moveUtoV.add(new Operation[]{u, operationList.get(1)});
                    if (satisfyUtoV(u, v, data)) moveUtoV.add(new Operation[] {u, v});
                    LinkedList<Operation[]> moveVtoU = moveLinkedListHashMap.get(Move.vTou);
                    if (satisfyVtoU(v, u, data)) moveVtoU.add(new Operation[] {v, u});
                } else {
                    // uTov
                    Iterator<Operation> iterator_uTov = operationList.iterator();
                    Operation uTov_u = iterator_uTov.next();
                    LinkedList<Operation[]> moveUtoV = moveLinkedListHashMap.get(Move.uTov);
                    Operation uTov_MSu = iterator_uTov.next();
                    moveUtoV.add(new Operation[] {uTov_u, uTov_MSu});
                    while (iterator_uTov.hasNext()) {
                        Operation uTov_v = iterator_uTov.next();
                        if (satisfyUtoV(uTov_u, uTov_v, data)) {
                            moveUtoV.add(new Operation[] {uTov_u, uTov_v});
                        }
                    }

                    // vTou
                    Operation vTou_v = operationList.getLast();
                    Operation vTou_u = operationList.getFirst();
                    LinkedList<Operation[]> moveVtoU = moveLinkedListHashMap.get(Move.vTou);
                    if (satisfyVtoU(vTou_v, vTou_u, data)) moveVtoU.add(new Operation[] {vTou_v, vTou_u});

                    // innerTou
                    LinkedList<Operation[]> moveInnerToU = moveLinkedListHashMap.get(Move.innerTou);
                    for (int i = 2; i < operationList.size() - 1; i++) {
                        Operation inner = operationList.get(i);
                        Operation inner_u = operationList.getFirst();
                        if (satisfyVtoU(inner, inner_u, data)) {
                            moveInnerToU.add(new Operation[] {inner, inner_u});
                        }
                    }
                }
                continue;
            }

            // 既不是块首也不是块尾
            if (operationList.size() == 1) {
                continue;
            } else if (operationList.size() == 2) {
                LinkedList<Operation[]> moveUtoV = moveLinkedListHashMap.get(Move.uTov);
                moveUtoV.add(new Operation[]{operationList.getFirst(), operationList.getLast()});
            } else if (operationList.size() == 3) {
                Operation u = operationList.getFirst();
                Operation v = operationList.getLast();
                LinkedList<Operation[]> moveUtoV = moveLinkedListHashMap.get(Move.uTov);
                moveUtoV.add(new Operation[]{u, operationList.get(1)});
                if (satisfyUtoV(u, v, data)) moveUtoV.add(new Operation[] {u, v});
                LinkedList<Operation[]> moveVtoU = moveLinkedListHashMap.get(Move.vTou);
                moveVtoU.add(new Operation[]{v, operationList.get(1)});
                if (satisfyVtoU(v, u, data)) moveVtoU.add(new Operation[] {v, u});
            } else {
                // uTov
                Iterator<Operation> iterator_uTov = operationList.iterator();
                Operation uTov_u = iterator_uTov.next();
                LinkedList<Operation[]> moveUtoV = moveLinkedListHashMap.get(Move.uTov);
                Operation uTov_MSu = iterator_uTov.next();
                moveUtoV.add(new Operation[] {uTov_u, uTov_MSu});
                while (iterator_uTov.hasNext()) {
                    Operation uTov_v = iterator_uTov.next();
                    if (satisfyUtoV(uTov_u, uTov_v, data)) {
                        moveUtoV.add(new Operation[] {uTov_u, uTov_v});
                    }
                }

                // vTou
                Operation vTou_v = operationList.getLast();
                Iterator<Operation> iterator_vTou = operationList.iterator();
                LinkedList<Operation[]> moveVtoU = moveLinkedListHashMap.get(Move.vTou);
                while (iterator_vTou.hasNext()) {
                    Operation vTou_u = iterator_vTou.next();
                    if (vTou_u == vTou_v.getMP()) {
                        moveVtoU.add(new Operation[] {vTou_v, vTou_u});
                        break;
                    }
                    if (satisfyVtoU(vTou_v, vTou_u, data)) {
                        moveVtoU.add(new Operation[] {vTou_v, vTou_u});
                    }
                }

                // innerTou && innerTov
                LinkedList<Operation[]> moveInnerToU= moveLinkedListHashMap.get(Move.innerTou);
                LinkedList<Operation[]> moveInnerToV = moveLinkedListHashMap.get(Move.innerTov);

                for (int i = 1; i < operationList.size() - 1; i++) {
                    Operation inner = operationList.get(i);
                    Operation inner_u = operationList.getFirst();
                    Operation inner_v = operationList.getLast();
                    if (i == 1) { //只考虑插入块尾
                        if (satisfyUtoV(inner, inner_v, data)) {
                            moveInnerToV.add(new Operation[] {inner, inner_v});
                        }
                    } else if (i == operationList.size() - 2) { //只考虑插入块首
                        if (satisfyVtoU(inner, inner_u, data)) {
                            moveInnerToU.add(new Operation[] {inner, inner_u});
                        }
                    } else { //既考虑插入块首，又考虑插入块尾
                        if (satisfyUtoV(inner, inner_v, data)) {
                            moveInnerToV.add(new Operation[] {inner, inner_v});
                        }
                        if (satisfyVtoU(inner, inner_u, data)) {
                            moveInnerToU.add(new Operation[] {inner, inner_u});
                        }
                    }
                }
            }
        }

//        HashMap<Move, LinkedList<Operation[]>> cloneMap = (HashMap<Move, LinkedList<Operation[]>>) moveLinkedListHashMap.clone();
//        for (Move move : Move.values()) {
//            LinkedList<Operation[]> operations = cloneMap.get(move);
//            cloneMap.put(move, (LinkedList<Operation[]>) operations.clone());
//        }


        for (Move move : Move.values()) {
            LinkedList<Operation[]> operations = moveLinkedListHashMap.get(move);
            operations.removeIf(next -> next[0].getJob() == next[1].getJob() &&
                    ((next[0].getMS() == next[1]) || (next[1].getMS() == next[0])));
            moveSize += operations.size();
        }


//        if (moveSize == 0) {
//            System.out.println("=========================");
//        }

        return moveLinkedListHashMap;
    }

    /**
     * 获取全部换机器移动
     * @param data
     * @return
     */
    public HashMap<Operation, ChangedMachine> feasibleMachineMove(Data data) {
        HashMap<Operation, ChangedMachine> machineMoveMap = new HashMap<>();
        for (Operation operation: this.completeCriticalPath) {
            int[] machineList = data.getProcessMachine()[operation.getJob()][operation.getOperationNum()];
            if (machineList.length == 1) {
                continue;
            }
            int[] changedMachines = new int[machineList.length - 1];
            Operation[][] changedLocations = new Operation[changedMachines.length][];
            int currentMachineIndex = 0;
            for (int machine: machineList) {
                if (machine == data.getProcessMachine()
                                [operation.getJob()][operation.getOperationNum()][operation.getMachineIndex()]) {
                    continue;
                }
                // 计算Rk
                ArrayList<Operation> Rk = new ArrayList<>();
                Operation JP_v = operation.getJP();
                int Sv_ = 0;
                if (JP_v != null) {
                    Sv_ = this.L_0Tov.get(JP_v).getTotalValue() + data.getProcessTime()
                            [JP_v.getJob()][JP_v.getOperationNum()][JP_v.getMachineIndex()];
                }
                Operation currentOperation = this.getFirstOperationOnMachine()[machine];
                int Sx = 0;
                int Px = 0;
                while (currentOperation != null) {
                    Sx = this.L_0Tov.get(currentOperation).getTotalValue();
                    Px = data.getProcessTime()
                            [currentOperation.getJob()][currentOperation.getOperationNum()][currentOperation.getMachineIndex()];
                    if (Sx + Px > Sv_) {
                        break;
                    }
                    currentOperation = currentOperation.getMS();
                }
                while (currentOperation != null) {
                    Rk.add(currentOperation);
                    currentOperation = currentOperation.getMS();
                }

                // 计算Lk
                ArrayList<Operation> Lk = new ArrayList<>();
                Operation JS_v = operation.getJS();
                int Tv_ = 0;
                if (JS_v != null) {
                    Tv_ = this.L_uTon.get(JS_v).getTotalValue();
                }
                currentOperation = this.getLastOperationOnMachine()[machine];
                int TxPlusPx = 0;
                while (currentOperation != null) {
                    TxPlusPx = this.L_uTon.get(currentOperation).getTotalValue();
                    if (TxPlusPx > Tv_) {
                        break;
                    }
                    currentOperation = currentOperation.getMP();
                }
                while (currentOperation != null) {
                    Lk.add(currentOperation);
                    currentOperation = currentOperation.getMP();
                }
                Collections.reverse(Lk);

                // Lk, Rk 交集
                ArrayList<Operation> insertOperationList = new ArrayList<>();
                ArrayList<Operation> intersectionList = (ArrayList<Operation>) Lk.clone();
                intersectionList.retainAll(Rk);
                if (intersectionList.isEmpty()) {
                    Operation currOp = this.firstOperationOnMachine[machine];
                    if (!Lk.isEmpty()) {
                        currOp = Lk.get(Lk.size() - 1).getMS();
                    }
                    if (Rk.isEmpty()) {
                        while (currOp != null) {
                            insertOperationList.add(currOp);
                            currOp = currOp.getMS();
                        }
                    } else {
                        while (currOp != Rk.get(0)) {
                            insertOperationList.add(currOp);


                            if (currOp == null) {
                                System.out.println(this.firstOperationOnMachine[machine]);
                                System.out.println(currOp);

                            }

                            currOp = currOp.getMS();
                        }
                    }
                } else {
                    insertOperationList = intersectionList;
                }

                changedMachines[currentMachineIndex] = machine;
                if (insertOperationList.isEmpty()) {
                    changedLocations[currentMachineIndex] = null;
                } else {
                    changedLocations[currentMachineIndex] = new Operation[]
                            {insertOperationList.get(0), insertOperationList.get(insertOperationList.size() - 1)};
                }
                currentMachineIndex++;
            }
            machineMoveMap.put(operation, new ChangedMachine(changedMachines, changedLocations));
        }
        return machineMoveMap;
    }


    /**
     * 根据 move 的类型，移动 move
     * @param moveType move 类型
     * @param move    即将执行的 move
     */
    public void move(TabuSearch.Move moveType, Operation[] move) {
        switch (moveType) {
            case innerTov:
                moveInnerToV(move[0], move[1]);
                break;
            case innerTou:
                moveInnerToU(move[0], move[1]);
                break;
            case uTov:
                moveUtoV(move[0], move[1]);
                break;
            case vTou:
                moveVtoU(move[0], move[1]);
                break;
        }
    }

    public void moveUtoV(Operation u, Operation v) {
        // 变工序所在机器的位置
        u.setMachineLocation(v.getMachineLocation());
        Operation currentOperation = u.getMS();
        while (currentOperation != v.getMS()) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() - 1);
            currentOperation = currentOperation.getMS();
        }

        // 变工序的MP与MS
        if (u.getMP() != null) {
            u.getMP().setMS(u.getMS());
        }
        u.getMS().setMP(u.getMP());
        u.setMS(v.getMS());
        if (v.getMS() != null) {
            v.getMS().setMP(u);
        }
        v.setMS(u);
        u.setMP(v);
    }

    public void moveVtoU(Operation v, Operation u) {
        // 变工序所在机器的位置
        v.setMachineLocation(u.getMachineLocation());
        Operation currentOperation = u;
        while (currentOperation != v) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() + 1);
            currentOperation = currentOperation.getMS();
        }

        // 变工序的MP与MS
        v.getMP().setMS(v.getMS());
        if (v.getMS() != null) {
            v.getMS().setMP(v.getMP());
        }
        if (u.getMP() != null) {
            u.getMP().setMS(v);
        }
        v.setMP(u.getMP());
        u.setMP(v);
        v.setMS(u);
    }

    public void moveInnerToU(Operation inner, Operation u) {
        // 变工序所在机器的位置
        inner.setMachineLocation(u.getMachineLocation());
        Operation currentOperation = u;
        while (currentOperation != inner) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() + 1);
            currentOperation = currentOperation.getMS();
        }

        // 变工序的MP与MS
        inner.getMP().setMS(inner.getMS());
        if (inner.getMS() != null) {
            inner.getMS().setMP(inner.getMP());
        }
        if (u.getMP() != null) {
            u.getMP().setMS(inner);
        }
        inner.setMP(u.getMP());
        u.setMP(inner);
        inner.setMS(u);
    }

    public void moveInnerToV(Operation inner, Operation v) {
        // 变工序所在机器的位置
        inner.setMachineLocation(v.getMachineLocation());
        Operation currentOperation = inner.getMS();
        while (currentOperation != v.getMS()) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() - 1);
            currentOperation = currentOperation.getMS();
        }

        // 变工序的MP与MS
        if (inner.getMP() != null) {
            inner.getMP().setMS(inner.getMS());
        }
        inner.getMS().setMP(inner.getMP());
        inner.setMS(v.getMS());
        if (v.getMS() != null) {
            v.getMS().setMP(inner);
        }
        v.setMS(inner);
        inner.setMP(v);
    }

    public int getTabuLen() {
        return tabuLen;
    }

    public void setTabuLen(int tabuLen) {
        this.tabuLen = tabuLen;
    }

    public Map<Operation, PathInfo> getL_0Tov() {
        return L_0Tov;
    }

    public void setL_0Tov(Map<Operation, PathInfo> l_0Tov) {
        L_0Tov = l_0Tov;
    }

    public Map<Operation, PathInfo> getL_uTon() {
        return L_uTon;
    }

    public void setL_uTon(Map<Operation, PathInfo> l_uTon) {
        L_uTon = l_uTon;
    }

    public int getPathFitness() {
        return pathFitness;
    }

    public void setPathFitness(int pathFitness) {
        this.pathFitness = pathFitness;
    }

    public LinkedList<Operation> getCompleteCriticalPath() {
        return completeCriticalPath;
    }

    public void setCompleteCriticalPath(LinkedList<Operation> completeCriticalPath) {
        this.completeCriticalPath = completeCriticalPath;
    }

    public int getMoveSize() {
        return moveSize;
    }

    public void setMoveSize(int moveSize) {
        this.moveSize = moveSize;
    }

    public Operation[] getFirstOperationOnMachine() {
        return firstOperationOnMachine;
    }

    public void setFirstOperationOnMachine(Operation[] firstOperationOnMachine) {
        this.firstOperationOnMachine = firstOperationOnMachine;
    }

    public Operation[] getLastOperationOnMachine() {
        return lastOperationOnMachine;
    }

    public void setLastOperationOnMachine(Operation[] lastOperationOnMachine) {
        this.lastOperationOnMachine = lastOperationOnMachine;
    }

    /**
     * 枚举表示向前移动或者向后移动
     */
    public enum Move {
        uTov, vTou, innerTou, innerTov
    }

    private boolean satisfyUtoV(Operation u, Operation v, Data data) {
     /*   int L_vTon = L_uTon.get(v).getTotalValue();
        int L_jsuTon = u.getJS() == null ? Integer.MIN_VALUE : L_uTon.get(u.getJS()).getTotalValue();
        return L_vTon >= L_jsuTon;*/

        int L_vTon = L_uTon.get(v).getTotalValue();
        int L_jsuTon = u.getJS() == null ? Integer.MIN_VALUE : L_uTon.get(u.getJS()).getTotalValue() -
                data.getProcessTime()[u.getJS().getJob()][u.getJS().getOperationNum()][u.getJS().getMachineIndex()];
        return L_vTon > L_jsuTon;
    }

    private boolean satisfyVtoU(Operation v, Operation u, Data data) {
/*        int L_0Tou = L_0Tov.get(u).getTotalValue();
        Operation JP_v = v.getJP();
        int comparedValue = JP_v == null ? Integer.MIN_VALUE : L_0Tov.get(JP_v).getTotalValue()
                + data.getProcessTime()[JP_v.getJob()][JP_v.getOperationNum()];
        return L_0Tou + data.getProcessTime()[u.getJob()][u.getOperationNum()] >= comparedValue;*/

        int L_0Tou = L_0Tov.get(u).getTotalValue();
        Operation JP_v = v.getJP();
        int comparedValue = JP_v == null ? Integer.MIN_VALUE : L_0Tov.get(JP_v).getTotalValue();
        return L_0Tou + data.getProcessTime()[u.getJob()][u.getOperationNum()][u.getMachineIndex()] > comparedValue;
    }


    public void moveMachine(Operation insertOp, int machine, Operation u, Operation v, Data data) {
        Operation MP_OP = insertOp.getMP();
        Operation MS_OP = insertOp.getMS();

        if (u != null) {
            u.setMS(insertOp);
            insertOp.setMP(u);
            insertOp.setMachineLocation(u.getMachineLocation() + 1);
        } else {
            insertOp.setMP(null);
            insertOp.setMachineLocation(0);
        }
        if (v != null) {
            v.setMP(insertOp);
            insertOp.setMS(v);
            Operation cur = v;
            while (cur != null) {
                cur.setMachineLocation(cur.getMachineLocation() + 1);
                cur = cur.getMS();
            }
        } else {
            insertOp.setMS(null);
        }

        if (MP_OP != null) {
            MP_OP.setMS(MS_OP);
        }
        if (MS_OP != null) {
            MS_OP.setMP(MP_OP);
            Operation cur = MS_OP;
            while (cur != null) {
                cur.setMachineLocation(cur.getMachineLocation() - 1);
                cur = cur.getMS();
            }
        }

        // 最后换Op机器索引
        for (int i = 0; i < data.getProcessMachine()[insertOp.getJob()][insertOp.getOperationNum()].length; i++) {
            if (machine == data.getProcessMachine()[insertOp.getJob()][insertOp.getOperationNum()][i]) {
                insertOp.setMachineIndex(i);
                return;
            }
        }

        insertOp.setMachineIndex(-1);
    }

}
