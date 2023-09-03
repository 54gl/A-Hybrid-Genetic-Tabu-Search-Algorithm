package dfjsp_ga_ts;

import java.util.*;

public class Utils {

    public static Random random = new Random();

    // ��������
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
     * �������ŵ� move,���ж�����ŵ� move,�����ѡ��һ������
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
     * �ж� move �Ƿ񱻽��ɣ� �������򷵻�Ϊ true�� ��û�б����ɣ��򷵻�Ϊ false
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
     * ���� move �����ͣ��ƶ� move
     * @param moveType move ����
     * @param move    ����ִ�е� move
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
        // �乤�����ڻ�����λ��
        inner.setMachineLocation(v.getMachineLocation());
        Operation currentOperation = inner.getMS();
        while (currentOperation != v.getMS()) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() - 1);
            currentOperation = currentOperation.getMS();
        }
    }

    private static void moveUtoV (Operation u, Operation v){
        // �乤�����ڻ�����λ��
        u.setMachineLocation(v.getMachineLocation());
        Operation currentOperation = u.getMS();
        while (currentOperation != v.getMS()) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() - 1);
            currentOperation = currentOperation.getMS();
        }
    }

    private static void moveVtoU (Operation v, Operation u){
        // �乤�����ڻ�����λ��
        v.setMachineLocation(u.getMachineLocation());
        Operation currentOperation = u;
        while (currentOperation != v) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() + 1);
            currentOperation = currentOperation.getMS();
        }
    }

    private static void moveInnerToU (Operation inner, Operation u){
        // �乤�����ڻ�����λ��
        inner.setMachineLocation(u.getMachineLocation());
        Operation currentOperation = u;
        while (currentOperation != inner) {
            currentOperation.setMachineLocation(currentOperation.getMachineLocation() + 1);
            currentOperation = currentOperation.getMS();
        }
    }

    /**
     * ���� move �����ͣ�����moveǰ��operationArr
     * @param moveType move ����
     * @param move    �Ѿ�ִ�е� move���践��moveǰ����̬
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

        // ���Op��������
        for (int i = 0; i < data.getProcessMachine()[insertOp.getJob()][insertOp.getOperationNum()].length; i++) {
            if (insertMachine == data.getProcessMachine()[insertOp.getJob()][insertOp.getOperationNum()][i]) {
                insertOp.setMachineIndex(i);
                return;
            }
        }

        insertOp.setMachineIndex(-1);

    }
}
