package dfjsp_ga_ts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Decode {

    public static double fitnessOfSingleFactory(Chrom chrom, int[] code, int[][] machineIndex, Data data) {

        if (code == null || code.length == 0) return 0;

        // �����Ϲ����ӹ�˳��
        ArrayList<ArrayList<int[]>> ganttGraphSequence = initializeList(data.getMachineCount());

        // �����Ϲ����ӹ�ʼĩʱ��
        ArrayList<ArrayList<int[]>> ganttGraphTime = initializeList(data.getMachineCount());

        // ÿ������ÿ������Ŀ�ʼ������ʱ��
        int[][][] jobStartAndEndTime = new int[data.getJobCount()][][];
        for (int i = 0; i < data.getOperationCount().length; i++) {
            jobStartAndEndTime[i] = new int[data.getOperationCount()[i]][2];
        }

        // ��¼ÿ���������ֵĹ�����Ŀ��Ĭ�ϴ� 0 ��ʼ
        int[] operations = new int[data.getJobCount()];

        for (int i = 0; i < code.length; i++) {
            int job = code[i];
            int operation = operations[job]++;
            int machine = data.getProcessMachine()[job][operation][machineIndex[job][operation]];
            int processTime = data.getProcessTime()[job][operation][machineIndex[job][operation]];

            // �����Ϲ������У���ռ��
            ArrayList<int[]> sequence = ganttGraphSequence.get(machine);
            ArrayList<int[]> time = ganttGraphTime.get(machine);
            ArrayList<Integer> gap = getTimeGap(time);

            // ��һ������Ŀ�ʼ�����ʱ��
            int[] startAndEndTime = new int[2];

            // ����ǵ�һ������
            if (operation == 0) {
                startAndEndTime = dealFirstOperation(gap, processTime, sequence, time, job, operation);
                jobStartAndEndTime[job][operation][0] = startAndEndTime[0];
                jobStartAndEndTime[job][operation][1] = startAndEndTime[1];
                // ������ǵ�һ������
            } else {
                // ��һ�������깤ʱ��
                int lastOperationFinishTime = jobStartAndEndTime[job][operation - 1][1];
                int sequenceSize = sequence.size();
                // �����ϻ�δ�ӹ����
                if (sequenceSize == 0) {
                    startAndEndTime[0] = lastOperationFinishTime;
                    change(startAndEndTime, sequence, time, jobStartAndEndTime, job, operation, processTime);
                    continue;
                }
                // ��������������ڼӹ�
                for (int j = 0; j < sequenceSize; j++) {
                    if (time.get(j)[0] >= lastOperationFinishTime) {
                        if (j == 0) {
                            gap.set(0, time.get(0)[0] - lastOperationFinishTime);
                            startAndEndTime = insertGaphaslastOper(lastOperationFinishTime, processTime, sequence, time, gap, job, operation);
                            // ��һ���������ʱ������������֮��
                        } else if (time.get(j-1)[1] <= lastOperationFinishTime) {
                            for (int k = 0; k < j; k++) {
                                gap.set(k, 0);
                            }
                            gap.set(j, time.get(j)[0] - lastOperationFinishTime);
                            startAndEndTime = insertGapOuter(j, lastOperationFinishTime, processTime, sequence, time, gap, job, operation);
                            // ��һ���������ʱ����һ�������ڲ�
                        } else {
                            for (int k = 0; k < j; k++) {
                                gap.set(k, 0);
                            }
                            startAndEndTime = insertGapInner(processTime, sequence, time, gap, job, operation);
                        }
                        jobStartAndEndTime[job][operation][0] = startAndEndTime[0];
                        jobStartAndEndTime[job][operation][1] = startAndEndTime[1];
                        break;
                    }
                    // ֻ�ܲ������
                    if (j == time.size() - 1) {
                        int machineFinishTime = time.get(gap.size() - 1)[1];
                        if (lastOperationFinishTime >= machineFinishTime) {
                            startAndEndTime[0] = lastOperationFinishTime;
                        } else {
                            startAndEndTime[0] = machineFinishTime;
                        }
                        change(startAndEndTime, sequence, time, jobStartAndEndTime, job, operation, processTime);
                    }
                }
            }
        }
        // �����Ϲ����ӹ�˳��
        chrom.setGanttGraphSequence(ganttGraphSequence);
        chrom.transferGraph(data);
        return solveMakespan(jobStartAndEndTime);
    }

    private static <T> ArrayList<ArrayList<T>> initializeList(int machineCount) {
        ArrayList<ArrayList<T>> list = new ArrayList<>(machineCount);
        for (int i = 0; i < machineCount; i++) {
            list.add(new ArrayList<>());
        }
        return list;
    }

    private static ArrayList<Integer> getTimeGap(ArrayList<int[]> time) {
        ArrayList<Integer> gap = new ArrayList<>();
        for (int i = 0; i < time.size(); i++) {
            int gapTime;
            if (i == 0) {
                gapTime = time.get(i)[0];
            } else {
                gapTime = time.get(i)[0] - time.get(i-1)[1];
            }
            gap.add(gapTime);
        }
        return gap;
    }

    // �����һ������
    private static int[] dealFirstOperation(ArrayList<Integer> gap, int processTime,
                                            ArrayList<int[]> sequence, ArrayList<int[]> time,
                                            int job, int operation) {
        int[] startAndEndTime = new int[2];
        // ��������ϻ�û�мӹ����
        if (gap.size() == 0) {
            startAndEndTime[1] = processTime;
            sequence.add(new int[] {job, operation});
            time.add(new int[] {startAndEndTime[0], startAndEndTime[1]});
            // �������������������ӹ�
        } else {
            startAndEndTime = insertGapFirst(processTime, sequence, time, gap, job, operation);
        }
        return startAndEndTime;
    }

    private static int[] insertGapFirst(int processTime, ArrayList<int[]> sequence,
                                        ArrayList<int[]> time, ArrayList<Integer> gap,
                                        int job, int operation) {
        int[] startAndEndTime = {-1, -1};
        int startTime = startAndEndTime[0];
        int endTime = startAndEndTime[1];
        for (int j = 0; j < gap.size(); j++) {
            if (processTime <= gap.get(j)) {
                if (j == 0) {
                    startTime = 0;
                    endTime = processTime;
                } else {
                    startTime = time.get(j-1)[1];
                    endTime = startTime + processTime;
                }
                sequence.add(j, new int[] {job, operation});
                time.add(j, new int[] {startTime, endTime});
                break;
            }
            // �޿տɲ壬�ŵ����
            if (j == gap.size() - 1) {
                startTime = time.get(gap.size() - 1)[1];
                endTime = startTime + processTime;
                sequence.add(new int[] {job, operation});
                time.add(new int[] {startTime, endTime});
            }
        }
        startAndEndTime[0] = startTime;
        startAndEndTime[1] = endTime;
        return startAndEndTime;
    }

    private static void change(int[] startAndEndTime, ArrayList<int[]> sequence, ArrayList<int[]> time,
                               int[][][] jobStartAndEndTime,
                               int job, int operation, int processTime) {
        startAndEndTime[1] = startAndEndTime[0] + processTime;
        sequence.add(new int[] {job, operation});
        time.add(new int[] {startAndEndTime[0], startAndEndTime[1]});
        jobStartAndEndTime[job][operation][0] = startAndEndTime[0];
        jobStartAndEndTime[job][operation][1] = startAndEndTime[1];
    }

    // ������һ�����������£��ڵ�һ����λ�������ܲ��� gap
    private static int[] insertGaphaslastOper(int lastOperationFinishTime, int processTime,
                                              ArrayList<int[]> sequence, ArrayList<int[]> time,
                                              ArrayList<Integer> gap, int job, int operation) {
        int[] startAndEndTime = {-1, -1};
        int startTime = startAndEndTime[0];
        int endTime = startAndEndTime[1];
        for (int j = 0; j < gap.size(); j++) {
            if (processTime <= gap.get(j)) {
                if (j == 0) {
                    startTime = lastOperationFinishTime;
                    endTime = startTime + processTime;
                } else {
                    startTime = time.get(j-1)[1];
                    endTime = startTime + processTime;
                }
                sequence.add(j, new int[] {job, operation});
                time.add(j, new int[] {startTime, endTime});
                break;
            }
            // �޿տɲ壬�ŵ����
            if (j == gap.size() - 1) {
                startTime = time.get(gap.size() - 1)[1];
                endTime = startTime + processTime;
                sequence.add(new int[] {job, operation});
                time.add(new int[] {startTime, endTime});
            }
        }
        startAndEndTime[0] = startTime;
        startAndEndTime[1] = endTime;
        return startAndEndTime;
    }

    // ��һ���������ʱ����������֮��
    private static int[] insertGapOuter(int target, int lastOperationFinishTime,
                                        int processTime, ArrayList<int[]> sequence,
                                        ArrayList<int[]> time, ArrayList<Integer> gap,
                                        int job, int operation) {
        int[] startAndEndTime = {-1, -1};
        int startTime = startAndEndTime[0];
        int endTime = startAndEndTime[1];
        for (int j = 0; j < gap.size(); j++) {
            if (processTime <= gap.get(j)) {
                if (j == target) {
                    startTime = lastOperationFinishTime;
                    endTime = startTime + processTime;
                } else {
                    startTime = time.get(j-1)[1];
                    endTime = startTime + processTime;
                }
                sequence.add(j, new int[] {job, operation});
                time.add(j, new int[] {startTime, endTime});
                break;
            }
            // �޿տɲ壬�ŵ����
            if (j == gap.size() - 1) {
                startTime = time.get(gap.size() - 1)[1];
                endTime = startTime + processTime;
                sequence.add(new int[] {job, operation});
                time.add(new int[] {startTime, endTime});
            }
        }
        startAndEndTime[0] = startTime;
        startAndEndTime[1] = endTime;
        return startAndEndTime;
    }


    // ��һ���������ʱ����һ�������ڲ�
    private static int[] insertGapInner(int processTime, ArrayList<int[]> sequence,
                                        ArrayList<int[]> time, ArrayList<Integer> gap,
                                        int job, int operation) {
        int[] startAndEndTime = {-1, -1};
        int startTime = startAndEndTime[0];
        int endTime = startAndEndTime[1];
        for (int j = 0; j < gap.size(); j++) {
            if (processTime <= gap.get(j)) {
                startTime = time.get(j-1)[1];
                endTime = startTime + processTime;
                sequence.add(j, new int[] {job, operation});
                time.add(j, new int[] {startTime, endTime});
                break;
            }
            // �޿տɲ壬�ŵ����
            if (j == gap.size() - 1) {
                startTime = time.get(gap.size() - 1)[1];
                endTime = startTime + processTime;
                sequence.add(new int[] {job, operation});
                time.add(new int[] {startTime, endTime});
            }
        }
        startAndEndTime[0] = startTime;
        startAndEndTime[1] = endTime;
        return startAndEndTime;
    }

    // ���ݹ���ÿ������Ŀ�ʼ�����ʱ�䣬��� makespan
    private static int solveMakespan(int[][][] jobStartAndEndTime) {
        int makespan = -1;
        for (int i = 0; i < jobStartAndEndTime.length; i++) {
            int tempJobMakespan = jobStartAndEndTime[i][jobStartAndEndTime[i].length - 1][1];
            if (tempJobMakespan > makespan) {
                makespan = tempJobMakespan;
            }
        }
        return makespan;
    }

    public static void printOfSingleFactory(int[] code, int[][] machineIndex, Data data) {

        if (code == null || code.length == 0) return;

        // �����Ϲ����ӹ�˳��
        ArrayList<ArrayList<int[]>> ganttGraphSequence = initializeList(data.getMachineCount());

        // �����Ϲ����ӹ�ʼĩʱ��
        ArrayList<ArrayList<int[]>> ganttGraphTime = initializeList(data.getMachineCount());

        // ÿ������ÿ������Ŀ�ʼ������ʱ��
        int[][][] jobStartAndEndTime = new int[data.getJobCount()][][];
        for (int i = 0; i < data.getOperationCount().length; i++) {
            jobStartAndEndTime[i] = new int[data.getOperationCount()[i]][2];
        }

        // ��¼ÿ���������ֵĹ�����Ŀ��Ĭ�ϴ� 0 ��ʼ
        int[] operations = new int[data.getJobCount()];

        for (int i = 0; i < code.length; i++) {
            int job = code[i];
            int operation = operations[job]++;
            int machine = data.getProcessMachine()[job][operation][machineIndex[job][operation]];
            int processTime = data.getProcessTime()[job][operation][machineIndex[job][operation]];

            // �����Ϲ������У���ռ��
            ArrayList<int[]> sequence = ganttGraphSequence.get(machine);
            ArrayList<int[]> time = ganttGraphTime.get(machine);
            ArrayList<Integer> gap = getTimeGap(time);

            // ��һ������Ŀ�ʼ�����ʱ��
            int[] startAndEndTime = new int[2];

            // ����ǵ�һ������
            if (operation == 0) {
                startAndEndTime = dealFirstOperation(gap, processTime, sequence, time, job, operation);
                jobStartAndEndTime[job][operation][0] = startAndEndTime[0];
                jobStartAndEndTime[job][operation][1] = startAndEndTime[1];
                // ������ǵ�һ������
            } else {
                // ��һ�������깤ʱ��
                int lastOperationFinishTime = jobStartAndEndTime[job][operation - 1][1];
                int sequenceSize = sequence.size();
                // �����ϻ�δ�ӹ����
                if (sequenceSize == 0) {
                    startAndEndTime[0] = lastOperationFinishTime;
                    change(startAndEndTime, sequence, time, jobStartAndEndTime, job, operation, processTime);
                    continue;
                }
                // ��������������ڼӹ�
                for (int j = 0; j < sequenceSize; j++) {
                    if (time.get(j)[0] >= lastOperationFinishTime) {
                        if (j == 0) {
                            gap.set(0, time.get(0)[0] - lastOperationFinishTime);
                            startAndEndTime = insertGaphaslastOper(lastOperationFinishTime, processTime, sequence, time, gap, job, operation);
                            // ��һ���������ʱ������������֮��
                        } else if (time.get(j-1)[1] <= lastOperationFinishTime) {
                            for (int k = 0; k < j; k++) {
                                gap.set(k, 0);
                            }
                            gap.set(j, time.get(j)[0] - lastOperationFinishTime);
                            startAndEndTime = insertGapOuter(j, lastOperationFinishTime, processTime, sequence, time, gap, job, operation);
                            // ��һ���������ʱ����һ�������ڲ�
                        } else {
                            for (int k = 0; k < j; k++) {
                                gap.set(k, 0);
                            }
                            startAndEndTime = insertGapInner(processTime, sequence, time, gap, job, operation);
                        }
                        jobStartAndEndTime[job][operation][0] = startAndEndTime[0];
                        jobStartAndEndTime[job][operation][1] = startAndEndTime[1];
                        break;
                    }
                    // ֻ�ܲ������
                    if (j == time.size() - 1) {
                        int machineFinishTime = time.get(gap.size() - 1)[1];
                        if (lastOperationFinishTime >= machineFinishTime) {
                            startAndEndTime[0] = lastOperationFinishTime;
                        } else {
                            startAndEndTime[0] = machineFinishTime;
                        }
                        change(startAndEndTime, sequence, time, jobStartAndEndTime, job, operation, processTime);
                    }
                }
            }
        }

        HashSet<Integer> codeSet = new HashSet<>();
        for (int i = 0; i < code.length; i++) {
            codeSet.add(code[i]);
        }
        System.out.println(codeSet);
        for (int machine = 0; machine < ganttGraphSequence.size(); machine++) {
            ArrayList<int[]> list = ganttGraphSequence.get(machine);
            System.out.print(list.size() + " ");
            for (int[] ints: list) {
                System.out.print(Arrays.toString(new int[] {ints[0] + 1, ints[1] + 1}) + " ");
            }
            System.out.println();
        }
        System.out.println("=======================");

        for (int machine = 0; machine < ganttGraphTime.size(); machine++) {
            ArrayList<int[]> list = ganttGraphTime.get(machine);
            for (int[] ints: list) {
                System.out.print(Arrays.toString(ints) + " ");
            }
            System.out.println();
        }
        System.out.println("***********************");
    }

    public static int DecodeOfSingleFactory(int[] code, Data data) {

        if (code == null || code.length == 0) return -1;

        // �����Ϲ����ӹ�˳��
        ArrayList<ArrayList<int[]>> ganttGraphSequence = initializeList(data.getMachineCount());

        // �����Ϲ����ӹ�ʼĩʱ��
        ArrayList<ArrayList<int[]>> ganttGraphTime = initializeList(data.getMachineCount());

        // ÿ������ÿ������Ŀ�ʼ������ʱ��
        int[][][] jobStartAndEndTime = new int[data.getJobCount()][][];
        for (int i = 0; i < data.getOperationCount().length; i++) {
            jobStartAndEndTime[i] = new int[data.getOperationCount()[i]][2];
        }

        // ��¼ÿ���������ֵĹ�����Ŀ��Ĭ�ϴ� 0 ��ʼ
        int[] operations = new int[data.getJobCount()];

        for (int i = 0; i < code.length; i++) {
            int job = code[i];
            int operation = operations[job]++;

            if (job == 0 && operation == 4) {
                System.out.println(job);
            }

            int makespan = Integer.MAX_VALUE;
            int machineIndex = -1;
            ArrayList<Integer> machineIndexList = new ArrayList<>();

            for (int j = 0; j < data.getProcessTime()[job][operation].length; j++) {
                if (data.getProcessTime()[job][operation][j] < makespan) {
                    machineIndexList.clear();
                    machineIndexList.add(j);
                    makespan = data.getProcessTime()[job][operation][j];
                } else if (data.getProcessTime()[job][operation][j] == makespan) {
                    machineIndexList.add(j);
                }
            }
            machineIndex = machineIndexList.get(Utils.random.nextInt(machineIndexList.size()));

            int machine = data.getProcessMachine()[job][operation][machineIndex];
            int processTime = data.getProcessTime()[job][operation][machineIndex];

            // �����Ϲ������У���ռ��
            ArrayList<int[]> sequence = ganttGraphSequence.get(machine);
            ArrayList<int[]> time = ganttGraphTime.get(machine);
            ArrayList<Integer> gap = getTimeGap(time);

            // ��һ������Ŀ�ʼ�����ʱ��
            int[] startAndEndTime = new int[2];

            // ����ǵ�һ������
            if (operation == 0) {
                startAndEndTime = dealFirstOperation(gap, processTime, sequence, time, job, operation);
                jobStartAndEndTime[job][operation][0] = startAndEndTime[0];
                jobStartAndEndTime[job][operation][1] = startAndEndTime[1];
                // ������ǵ�һ������
            } else {
                // ��һ�������깤ʱ��
                int lastOperationFinishTime = jobStartAndEndTime[job][operation - 1][1];
                int sequenceSize = sequence.size();
                // �����ϻ�δ�ӹ����
                if (sequenceSize == 0) {
                    startAndEndTime[0] = lastOperationFinishTime;
                    change(startAndEndTime, sequence, time, jobStartAndEndTime, job, operation, processTime);
                    continue;
                }
                // ��������������ڼӹ�
                for (int j = 0; j < sequenceSize; j++) {
                    if (time.get(j)[0] >= lastOperationFinishTime) {
                        if (j == 0) {
                            gap.set(0, time.get(0)[0] - lastOperationFinishTime);
                            startAndEndTime = insertGaphaslastOper(lastOperationFinishTime, processTime, sequence, time, gap, job, operation);
                            // ��һ���������ʱ������������֮��
                        } else if (time.get(j - 1)[1] <= lastOperationFinishTime) {
                            for (int k = 0; k < j; k++) {
                                gap.set(k, 0);
                            }
                            gap.set(j, time.get(j)[0] - lastOperationFinishTime);
                            startAndEndTime = insertGapOuter(j, lastOperationFinishTime, processTime, sequence, time, gap, job, operation);
                            // ��һ���������ʱ����һ�������ڲ�
                        } else {
                            for (int k = 0; k < j; k++) {
                                gap.set(k, 0);
                            }
                            startAndEndTime = insertGapInner(processTime, sequence, time, gap, job, operation);
                        }
                        jobStartAndEndTime[job][operation][0] = startAndEndTime[0];
                        jobStartAndEndTime[job][operation][1] = startAndEndTime[1];
                        break;
                    }
                    // ֻ�ܲ������
                    if (j == time.size() - 1) {
                        int machineFinishTime = time.get(gap.size() - 1)[1];
                        if (lastOperationFinishTime >= machineFinishTime) {
                            startAndEndTime[0] = lastOperationFinishTime;
                        } else {
                            startAndEndTime[0] = machineFinishTime;
                        }
                        change(startAndEndTime, sequence, time, jobStartAndEndTime, job, operation, processTime);
                    }
                }
            }
        }

        System.out.println(Arrays.toString(code));
        for (int machine = 0; machine < ganttGraphSequence.size(); machine++) {
            ArrayList<int[]> list = ganttGraphSequence.get(machine);
            System.out.print(list.size() + " ");
            for (int[] ints : list) {
                System.out.print(Arrays.toString(new int[]{ints[0] + 1, ints[1] + 1}) + " ");
            }
            System.out.println();
        }
        System.out.println("=======================");

        for (int machine = 0; machine < ganttGraphTime.size(); machine++) {
            ArrayList<int[]> list = ganttGraphTime.get(machine);
            for (int[] ints : list) {
                System.out.print(Arrays.toString(ints) + " ");
            }
            System.out.println();
        }
        System.out.println("***********************");
        return solveMakespan(jobStartAndEndTime);
    }
}
