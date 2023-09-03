package dfjsp_ga_ts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Data {
    private int[][][] processTime;
    private int[][][] processMachine;
    private int jobCount;
    private int machineCount;
    private int[] operationCount;
    private int totalOperation;
    private int factoryCount;

    public Data(String fileName, int factoryCount) {
        readData(fileName);
        this.factoryCount = factoryCount;
    }

    private void readData(String fileName) {
        try (
                FileReader fr = new FileReader(fileName);
                BufferedReader br = new BufferedReader(fr)) {
            String[] jobAndMachineCount = br.readLine().split(" ");
            jobCount = Integer.valueOf(jobAndMachineCount[0]);
            machineCount = Integer.valueOf(jobAndMachineCount[1]);
            this.operationCount = new int[jobCount];
            processTime = new int[jobCount][][];
            processMachine = new int[jobCount][][];
            for (int i = 0; i < jobCount; i++) {
                String[] readLineString = br.readLine().split(" ");
                int[] readLine = stringToInt(readLineString);
                int operationCount = readLine[0];
                this.operationCount[i] = operationCount;
                this.totalOperation += operationCount;
                processMachine[i] = new int[operationCount][];
                processTime[i] = new int[operationCount][];
                int operationNum = 0;
                int j = 1;
                while (j < readLine.length) {
                    int machineCount = readLine[j];
                    processMachine[i][operationNum] = new int[machineCount];
                    processTime[i][operationNum] = new int[machineCount];
                    for (int k = 0; k < machineCount; k++) {
                        // 加工机器编号从 0 开始
                        processMachine[i][operationNum][k] = readLine[j + 2*k + 1] - 1;


                        //processMachine[i][operationNum][k] = readLine[j + 2*k + 1];

                        processTime[i][operationNum][k] = readLine[j + 2*k + 2];
                    }
                    operationNum++;
                    j += machineCount*2 + 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] stringToInt(String[] readLineString) {
        int[] data = new int[readLineString.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = Integer.valueOf(readLineString[i]);
        }
        return data;
    }

    public int[][][] getProcessTime() {
        return processTime;
    }

    public void setProcessTime(int[][][] processTime) {
        this.processTime = processTime;
    }

    public int[][][] getProcessMachine() {
        return processMachine;
    }

    public void setProcessMachine(int[][][] processMachine) {
        this.processMachine = processMachine;
    }

    public int getJobCount() {
        return jobCount;
    }

    public void setJobCount(int jobCount) {
        this.jobCount = jobCount;
    }

    public int[] getOperationCount() {
        return operationCount;
    }

    public void setOperationCount(int[] operationCount) {
        this.operationCount = operationCount;
    }

    public int getTotalOperation() {
        return totalOperation;
    }

    public void setTotalOperation(int totalOperation) {
        this.totalOperation = totalOperation;
    }

    public int getMachineCount() {
        return machineCount;
    }

    public void setMachineCount(int machineCount) {
        this.machineCount = machineCount;
    }

    public int getFactoryCount() {
        return factoryCount;
    }

    public void setFactoryCount(int factoryCount) {
        this.factoryCount = factoryCount;
    }
}
