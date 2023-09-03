package dfjsp_ga_ts;

public class GraphFactory {
    public static Operation[][] createOperationArr(Data data) {
        int jobCount = data.getJobCount(); // 工件数量
        int[] operationCount = data.getOperationCount(); // 工序数量
        Operation[][] allOperation = new Operation[jobCount][];
        for (int i = 0; i < jobCount; i++) {
            allOperation[i] = new Operation[operationCount[i]];
        }
        for (int i = 0; i < jobCount; i++) {
            for (int j = 0; j < operationCount[i]; j++) {
                allOperation[i][j] = new Operation(i, j);
            }
        }
        for (int i = 0; i < jobCount; i++) {
            for (int j = 0; j < operationCount[i]; j++) {
                if (j == 0) {
                    allOperation[i][j].setJP(null);
                    allOperation[i][j].setJS(allOperation[i][j + 1]);
                } else if (j == operationCount[i] - 1) {
                    allOperation[i][j].setJP(allOperation[i][j - 1]);
                    allOperation[i][j].setJS(null);
                } else {
                    allOperation[i][j].setJP(allOperation[i][j - 1]);
                    allOperation[i][j].setJS(allOperation[i][j + 1]);
                }
            }
        }
        return allOperation;
    }
}
