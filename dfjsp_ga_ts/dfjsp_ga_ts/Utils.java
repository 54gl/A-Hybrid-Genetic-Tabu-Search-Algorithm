package dfjsp_ga_ts;

import java.util.*;

public class Utils {

    public static Random random = new Random();

    // 打乱数组
    public static void shuffle(int[] code) {
        for (int i = code.length; i > 0; i--) {
            int randIndex = random.nextInt(i);
            exchange(code, randIndex, i-1);
        }
    }

    public static void exchange(int[] code, int i, int j) {
        int temp = code[i];
        code[i] = code[j];
        code[j] = temp;
    }

    public static int[][] copy2DimArr(int[][] arr) {
        int[][] res = arr.clone();
        for (int i = 0; i < res.length; i++) {
            res[i] = arr[i].clone();
        }
        return res;
    }

    /**
     * 返回最优的 move,若有多个最优的 move,则随机选择一个返回
     * @param childrenMoveMap
     * @return
     */
    public static GA.TupleOfMove bestMoveTuple(HashMap<GA.TupleOfMove, Integer> childrenMoveMap) {
        ArrayList<GA.TupleOfMove> bestList = new ArrayList<>();
        Iterator<GA.TupleOfMove> iterator = childrenMoveMap.keySet().iterator();
        GA.TupleOfMove chosenMove = iterator.next();
        int chosenValue = childrenMoveMap.get(chosenMove);
        bestList.add(chosenMove);
        while (iterator.hasNext()) {
            GA.TupleOfMove next = iterator.next();
            int nextValue = childrenMoveMap.get(next);
            if (nextValue < chosenValue) {
                bestList.clear();
                bestList.add(next);
                chosenValue = nextValue;
            } else if (nextValue == chosenValue) {
                bestList.add(next);
            }
        }
        int chosenIndex = Utils.random.nextInt(bestList.size());
        return bestList.get(chosenIndex);
    }

    /**
     * 判断 move 是否被禁忌， 若禁忌则返回为 true， 若没有被禁忌，则返回为 false
     * @param operationArr
     * @param moveType
     * @param move
     * @param tabuTable
     * @return
     */
    public static boolean isForbid(Operation[][] operationArr, TabuSearch.Move moveType, Operation[] move,
                                   LinkedList<Tabu> tabuTable, Data data) {
        move(moveType, move);
        out:
        for (Tabu tabu: tabuTable) {
            Operation[] operations= tabu.getOperationArr();
            int[] locations = tabu.getLocationArr();
            int tabuMachine = tabu.getMachine();
            for (int i = 0; i < operations.length; i++) {
                Operation tabuOperation = operations[i];
                int tabuLocation = locations[i];
                Operation operation = operationArr[tabuOperation.getJob()][tabuOperation.getOperationNum()];
                int machine = data.getProcessMachine()[operation.getJob()][operation.getOperationNum()][operation.getMachineIndex()];
                int location = operation.getMachineLocation();
                if (tabuMachine != machine || tabuLocation != location) continue out;
            }
            moveBack(moveType, move);
            return true;
        }
        moveBack(moveType, move);
        return false;
    }

    /**
     * 根据 move 的类型，移动 move
     * @param moveType move 类型
     * @param move    即将执行的 move
     */
    public static void move (TabuSearch.Move moveType, Operation[] move){
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

    private static void moveInnerToV (Operation inner, Operation v){
        // 变工序所在机器的位置
        inner.setMachineLocation(v.getMachineLocation());
        Operation currentOperation = inner.getMS();
        while (currentOperation != v.getMS()) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() - 1);
            currentOperation = currentOperation.getMS();
        }
    }

    private static void moveUtoV (Operation u, Operation v){
        // 变工序所在机器的位置
        u.setMachineLocation(v.getMachineLocation());
        Operation currentOperation = u.getMS();
        while (currentOperation != v.getMS()) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() - 1);
            currentOperation = currentOperation.getMS();
        }
    }

    private static void moveVtoU (Operation v, Operation u){
        // 变工序所在机器的位置
        v.setMachineLocation(u.getMachineLocation());
        Operation currentOperation = u;
        while (currentOperation != v) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() + 1);
            currentOperation = currentOperation.getMS();
        }
    }

    private static void moveInnerToU (Operation inner, Operation u){
        // 变工序所在机器的位置
        inner.setMachineLocation(u.getMachineLocation());
        Operation currentOperation = u;
        while (currentOperation != inner) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() + 1);
            currentOperation = currentOperation.getMS();
        }
    }

    /**
     * 根据 move 的类型，返回move前的operationArr
     * @param moveType move 类型
     * @param move    已经执行的 move，需返回move前的形态
     */
    public static void moveBack (TabuSearch.Move moveType, Operation[] move){
        switch (moveType) {
            case innerTov:
                moveBackInnerToV(move[0], move[1]);
                break;
            case innerTou:
                moveBackInnerToU(move[0], move[1]);
                break;
            case uTov:
                moveBackUtoV(move[0], move[1]);
                break;
            case vTou:
                moveBackVtoU(move[0], move[1]);
                break;
        }
    }

    private static void moveBackInnerToV(Operation inner, Operation v) {
        inner.setMachineLocation(inner.getMS().getMachineLocation());
        Operation currentOperation = inner.getMS();
        while (currentOperation != v.getMS()) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() + 1);
            currentOperation = currentOperation.getMS();
        }
    }

    private static void moveBackInnerToU(Operation inner, Operation u) {
        inner.setMachineLocation(inner.getMP().getMachineLocation());
        Operation currentOperation = u;
        while (currentOperation != inner) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() - 1);
            currentOperation = currentOperation.getMS();
        }
    }

    private static void moveBackUtoV(Operation u, Operation v) {
        u.setMachineLocation(u.getMS().getMachineLocation());
        Operation currentOperation = u.getMS();
        while (currentOperation != v.getMS()) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() + 1);
            currentOperation = currentOperation.getMS();
        }
    }

    private static void moveBackVtoU(Operation v, Operation u) {
        v.setMachineLocation(v.getMP().getMachineLocation());
        Operation currentOperation = u;
        while (currentOperation != v) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() - 1);
            currentOperation = currentOperation.getMS();
        }
    }


    public static boolean isForbidMachine(Operation insertOp, int insertMachine, Operation u, Operation v,
                                          LinkedList<Tabu> tabuTable, Operation[][] operationArr, Data data) {
        int oldMachineIndex = insertOp.getMachineIndex();
        moveMachine(insertOp, insertMachine, u, v, data);
        out:
        for (Tabu tabu: tabuTable) {
            Operation[] operations= tabu.getOperationArr();
            int[] locations = tabu.getLocationArr();
            int tabuMachine = tabu.getMachine();
            for (int i = 0; i < operations.length; i++) {
                Operation tabuOperation = operations[i];
                int tabuLocation = locations[i];
                Operation operation = operationArr[tabuOperation.getJob()][tabuOperation.getOperationNum()];
                int machine = data.getProcessMachine()[operation.getJob()][operation.getOperationNum()][operation.getMachineIndex()];
                int location = operation.getMachineLocation();
                if (tabuMachine != machine || tabuLocation != location) continue out;
            }
            moveMachineBack(insertOp, v, oldMachineIndex, insertOp.getMP(), insertOp.getMS());
            return true;
        }
        moveMachineBack(insertOp, v, oldMachineIndex, insertOp.getMP(), insertOp.getMS());
        return false;
    }

    private static void moveMachineBack(Operation insertOp, Operation v, int oldMachineIndex, Operation mp, Operation ms) {

        if (mp != null) {
            insertOp.setMachineLocation(mp.getMachineLocation() + 1);
        } else {
            insertOp.setMachineLocation(0);
        }

        if (ms != null) {
            Operation cur = ms;
            while (cur != null) {
                cur.setMachineLocation(cur.getMachineLocation() + 1);
                cur = cur.getMS();
            }
        }

        if (v != null) {
            Operation cur = v;
            while (cur != null) {
                cur.setMachineLocation(cur.getMachineLocation() - 1);
                cur = cur.getMS();
            }
        }

        insertOp.setMachineIndex(oldMachineIndex);
    }

    private static void moveMachine(Operation insertOp, int insertMachine, Operation u, Operation v, Data data) {
        Operation MS_OP = insertOp.getMS();

        if (u != null) {
            insertOp.setMachineLocation(u.getMachineLocation() + 1);
        } else {
            insertOp.setMachineLocation(0);
        }

        if (v != null) {
            Operation cur = v;
            while (cur != null) {
                cur.setMachineLocation(cur.getMachineLocation() + 1);
                cur = cur.getMS();
            }
        }

        if (MS_OP != null) {
            Operation cur = MS_OP;
            while (cur != null) {
                cur.setMachineLocation(cur.getMachineLocation() - 1);
                cur = cur.getMS();
            }
        }

        // 最后换Op机器索引
        for (int i = 0; i < data.getProcessMachine()[insertOp.getJob()][insertOp.getOperationNum()].length; i++) {
            if (insertMachine == data.getProcessMachine()[insertOp.getJob()][insertOp.getOperationNum()][i]) {
                insertOp.setMachineIndex(i);
                return;
            }
        }

        insertOp.setMachineIndex(-1);

    }
}
