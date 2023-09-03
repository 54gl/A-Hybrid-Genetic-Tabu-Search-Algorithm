package dfjsp_ga_ts;

public class FSG implements Cloneable{

    // ��������
    private int[] factoryArr;

    // ��������
    private int[] jobArr;

    // ������������
    private int[][] machineIndexArr;

    // ������������Ӧ��ֵ
    private double[] fitnessArr;


    public FSG(Data data) {
        this.factoryArr = assignFactory(data.getFactoryCount(), data.getJobCount());
        this.jobArr = assignJob(data);
        this.machineIndexArr = assignMachine(data);
    }

    // ���Ź�������0��ʼ���
    private int[] assignFactory(int sumFactory, int sumJob) {
        int[] factoryArr = new int[sumJob];
        for (int i = 0; i < factoryArr.length; i++) {
            factoryArr[i] = Utils.random.nextInt(sumFactory);
        }
        return factoryArr;
    }

    // ���乤����0��ʼ���
    private int[] assignJob(Data data) {
        int[] jobArr = new int[data.getTotalOperation()];
        int operationIndex = 0;
        for (int i = 0; i < data.getJobCount(); i++) {
            for (int j = 0; j < data.getOperationCount()[i]; j++) {
                jobArr[operationIndex++] = i;
            }
        }
        Utils.shuffle(jobArr);
        return jobArr;
    }

    // �����������0��ʼ���
    private int[][] assignMachine(Data data) {
        int[][] machineIndex = new int[data.getJobCount()][];
        for (int i = 0; i < data.getJobCount(); i++) {
            machineIndex[i] = new int[data.getOperationCount()[i]];
            for (int j = 0; j < machineIndex[i].length; j++) {
                int index = Utils.random.nextInt(data.getProcessMachine()[i][j].length);
                machineIndex[i][j] = index;
            }
        }
        return machineIndex;
    }

    @Override
    public FSG clone() {
        FSG clonedFSG = null;
        try {
            clonedFSG = (FSG) super.clone();
            clonedFSG.factoryArr = this.factoryArr.clone();
            clonedFSG.jobArr = this.jobArr.clone();
            clonedFSG.machineIndexArr = this.machineIndexArr.clone();
            for (int i = 0; i < clonedFSG.machineIndexArr.length; i++) {
                clonedFSG.machineIndexArr[i] = this.machineIndexArr[i].clone();
            }
            clonedFSG.fitnessArr = this.fitnessArr.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clonedFSG;
    }

    public int[] getFactoryArr() {
        return factoryArr;
    }

    public void setFactoryArr(int[] factoryArr) {
        this.factoryArr = factoryArr;
    }

    public int[] getJobArr() {
        return jobArr;
    }

    public void setJobArr(int[] jobArr) {
        this.jobArr = jobArr;
    }

    public int[][] getMachineIndexArr() {
        return machineIndexArr;
    }

    public void setMachineIndexArr(int[][] machineIndexArr) {
        this.machineIndexArr = machineIndexArr;
    }

    public double[] getFitnessArr() {
        return fitnessArr;
    }

    public void setFitnessArr(double[] fitnessArr) {
        this.fitnessArr = fitnessArr;
    }
}
