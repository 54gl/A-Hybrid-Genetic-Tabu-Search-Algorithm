package dfjsp_ga_ts;

import java.util.ArrayList;

public class Chrom implements Cloneable {

    private int[] code;
    private int[] OperationCountOnMachine;
    private int[][] machineIndexArr;
    private ArrayList<ArrayList<int[]>> ganttGraphSequence;
    private Operation[][] operationArr;
    private double fitness;

    /**
     * 随机初始化 chrom
     * @param data
     */
    public Chrom(int[] code, int[][] machineIndexArr, Data data) {
        this.operationArr = GraphFactory.createOperationArr(data);
        this.code = code;
        this.machineIndexArr = machineIndexArr;
        this.fitness = Decode.fitnessOfSingleFactory(this, code, machineIndexArr, data);
        this.OperationCountOnMachine = new int[data.getMachineCount()];
//        if (fitness != 0) {
//            for (int i = 0; i < OperationCountOnMachine.length; i++) {
//                this.OperationCountOnMachine[i] = this.getGanttGraphSequence().get(i).size();
//            }
//        }
    }

    public Chrom() {}

    /**
     * 甘特图转化为析取图
     * @return
     */
    public void transferGraph(Data data) {
        //graph = GraphFactory.createBasedGraph(operationArr, data);
        // 添加机器上工序之间的加工顺序
        for (int machine = 0; machine < ganttGraphSequence.size(); machine++) {
            ArrayList<int[]> operationList = ganttGraphSequence.get(machine);
            for (int i = 0; i < operationList.size(); i++) {
                Operation operation = operationArr[operationList.get(i)[0]][operationList.get(i)[1]];

                for (int j = 0; j < data.getProcessMachine()[operation.getJob()][operation.getOperationNum()].length; j++) {
                    int selectedMachine = data.getProcessMachine()[operation.getJob()][operation.getOperationNum()][j];
                    if (machine == selectedMachine) {
                        operation.setMachineIndex(j);
                        break;
                    }
                }

                if (i == 0) {
                    operation.setMP(null);
                    if (operationList.size() != 1) {
                        Operation MS = operationArr[operationList.get(i + 1)[0]][operationList.get(i + 1)[1]];
                        operation.setMS(MS);
                    } else {
                        operation.setMS(null);
                    }
                } else if (i == operationList.size() - 1) {
                    operation.setMP(operationArr[operationList.get(i - 1)[0]][operationList.get(i - 1)[1]]);
                    operation.setMS(null);
                } else {
                    Operation MP = operationArr[operationList.get(i - 1)[0]][operationList.get(i - 1)[1]];
                    Operation MS = operationArr[operationList.get(i + 1)[0]][operationList.get(i + 1)[1]];
                    operation.setMP(MP);
                    operation.setMS(MS);
                }

                operation.setMachineLocation(i);
            }
        }
    }

    /**
     * 从当前的operationArr转换为code
     * @param data
     */
    public void transferCode(ArrayList<Integer> jobList, Data data) {
        // 工序上解码到了第几个位置
        int[] decodedOperationIndex = new int[data.getJobCount()];
        int operationCount = 0;
        for (Integer job : jobList) {
            operationCount += data.getOperationCount()[job];
        }
        int decodedCount = 0;
        while (decodedCount < operationCount) {
            for (Integer job : jobList) {
                if (decodedOperationIndex[job] >= data.getOperationCount()[job]) continue;
                Operation currentOperation = operationArr[job][decodedOperationIndex[job]];
                if (currentOperation.getMP() == null) {
                    code[decodedCount] = currentOperation.getJob();
                    decodedOperationIndex[job]++;
                    decodedCount++;
                    continue;
                }
                Operation MP = currentOperation.getMP();
                if (decodedOperationIndex[MP.getJob()] > MP.getOperationNum()) {
                    code[decodedCount] = currentOperation.getJob();
                    decodedOperationIndex[job]++;
                    decodedCount++;
                }
            }
        }
    }


    public ArrayList<ArrayList<int[]>> getGanttGraphSequence() {
        return ganttGraphSequence;
    }

    public void setGanttGraphSequence(ArrayList<ArrayList<int[]>> ganttGraphSequence) {
        this.ganttGraphSequence = ganttGraphSequence;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Operation[][] getOperationArr() {
        return operationArr;
    }

    public void setOperationArr(Operation[][] operationArr) {
        this.operationArr = operationArr;
    }

    public int[] getOperationCountOnMachine() {
        return OperationCountOnMachine;
    }

    public void setOperationCountOnMachine(int[] operationCountOnMachine) {
        OperationCountOnMachine = operationCountOnMachine;
    }

    public int[] getCode() {
        return code;
    }

    public void setCode(int[] code) {
        this.code = code;
    }

    public int[][] getMachineIndexArr() {
        return machineIndexArr;
    }

    public void setMachineIndexArr(int[][] machineIndexArr) {
        this.machineIndexArr = machineIndexArr;
    }

    @Override
    public Chrom clone() {
        Chrom res = null;
        try {
            res = (Chrom) super.clone();
            res.code = this.code.clone();
            res.machineIndexArr = Utils.copy2DimArr(this.machineIndexArr);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return res;
    }
}
