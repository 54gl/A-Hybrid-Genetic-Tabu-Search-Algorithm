package dfjsp_ga_ts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Calculation {

    public static int estimate(TabuSearch.Move moveType, Map<Operation, PathInfo> L_0Tov,
                               Map<Operation, PathInfo> L_uTon, Operation[] operations, Data data) {
            int estimateValue = Integer.MIN_VALUE;
            switch (moveType) {
                case uTov:
                    HashMap<Operation, Integer> lambda_utoV_0W = lambda_UtoV_0W(L_0Tov, operations[0], operations[1], data);
                    HashMap<Operation, Integer> lambda_utoV_WN = lambda_UtoV_WN(L_uTon, operations[0], operations[1], data);
                    for (Operation operation : lambda_utoV_0W.keySet()) {
                        int L_OW = lambda_utoV_0W.get(operation);
                        int L_WN = lambda_utoV_WN.get(operation);
                        int newEstimateValue = L_OW + L_WN;
                        if (newEstimateValue > estimateValue) {
                            estimateValue = newEstimateValue;
                        }
                    }
                    break;
                case vTou:
                    HashMap<Operation, Integer> lambda_VtoU_0W = lambda_VtoU_0W(L_0Tov, operations[0], operations[1], data);
                    HashMap<Operation, Integer> lambda_VtoU_WN = lambda_VtoU_WN(L_uTon, operations[0], operations[1], data);
                    for (Operation operation : lambda_VtoU_0W.keySet()) {
                        int L_OW = lambda_VtoU_0W.get(operation);
                        int L_WN = lambda_VtoU_WN.get(operation);
                        int newEstimateValue = L_OW + L_WN;
                        if (newEstimateValue > estimateValue) {
                            estimateValue = newEstimateValue;
                        }
                    }
                    break;
                case innerTou:
                    HashMap<Operation, Integer> lambda_innerToU_OW = lambda_VtoU_0W(L_0Tov, operations[0], operations[1], data);
                    HashMap<Operation, Integer> lambda_innerToU_WN = lambda_VtoU_WN(L_uTon, operations[0], operations[1], data);
                    for (Operation operation : lambda_innerToU_OW.keySet()) {
                        int L_OW = lambda_innerToU_OW.get(operation);
                        int L_WN = lambda_innerToU_WN.get(operation);
                        int newEstimateValue = L_OW + L_WN;
                        if (newEstimateValue > estimateValue) {
                            estimateValue = newEstimateValue;
                        }
                    }
                    break;
                case innerTov:
                    HashMap<Operation, Integer> lambda_innerToV_OW = lambda_UtoV_0W(L_0Tov, operations[0], operations[1], data);
                    HashMap<Operation, Integer> lambda_innerToV_WN = lambda_UtoV_WN(L_uTon, operations[0], operations[1], data);
                    for (Operation operation : lambda_innerToV_OW.keySet()) {
                        int L_OW = lambda_innerToV_OW.get(operation);
                        int L_WN = lambda_innerToV_WN.get(operation);
                        int newEstimateValue = L_OW + L_WN;
                        if (newEstimateValue > estimateValue) {
                            estimateValue = newEstimateValue;
                        }
                    }
                    break;
            }
            return estimateValue;
    }


    public static HashMap<Operation, Integer> lambda_UtoV_0W(Map<Operation, PathInfo> L_0Tov,
                                                             Operation u, Operation v, Data data) {
        HashMap<Operation, Integer> resultMap = new HashMap<>();
        // l1即是v, 只有u,v两道工序的情况
        if (u.getMS() == v) {
            int lambda_UtoV_0V;
            if (u.getMachineLocation() == 0) {
                Operation JP_v = v.getJP();
                if (JP_v == null) {
                    lambda_UtoV_0V = 0;
                } else {
                    lambda_UtoV_0V = L_0Tov.get(JP_v).getTotalValue() +
                            data.getProcessTime()[JP_v.getJob()][JP_v.getOperationNum()][JP_v.getMachineIndex()];
                }
            } else {
                int comparedValue_1;
                Operation JP_v = v.getJP();
                if (JP_v == null) {
                    comparedValue_1 = 0;
                } else {
                    comparedValue_1 = L_0Tov.get(JP_v).getTotalValue() +
                            data.getProcessTime()[JP_v.getJob()][JP_v.getOperationNum()][JP_v.getMachineIndex()];
                }
                Operation MP_u = u.getMP();
                int comparedValue_2 = L_0Tov.get(MP_u).getTotalValue() +
                        data.getProcessTime()[MP_u.getJob()][MP_u.getOperationNum()][MP_u.getMachineIndex()];
                lambda_UtoV_0V = Math.max(comparedValue_1, comparedValue_2);
            }
            resultMap.put(v, lambda_UtoV_0V);
        } else {
            // l1不是v
            Operation l1 = u.getMS();
            int lambda_UtoV_0l1;
            if (u.getMachineLocation() == 0) {
                Operation JP_l1 = l1.getJP();
                if (JP_l1 == null) {
                    lambda_UtoV_0l1 = 0;
                } else {
                    lambda_UtoV_0l1 = L_0Tov.get(JP_l1).getTotalValue() +
                            data.getProcessTime()[JP_l1.getJob()][JP_l1.getOperationNum()][JP_l1.getMachineIndex()];
                }
            } else {
                int comparedValue_1;
                Operation JP_l1 = l1.getJP();
                if (JP_l1 == null) {
                    comparedValue_1 = 0;
                } else {
                    comparedValue_1 = L_0Tov.get(JP_l1).getTotalValue() +
                            data.getProcessTime()[JP_l1.getJob()][JP_l1.getOperationNum()][JP_l1.getMachineIndex()];
                }
                Operation MP_u = u.getMP();
                int comparedValue_2 = L_0Tov.get(MP_u).getTotalValue() +
                        data.getProcessTime()[MP_u.getJob()][MP_u.getOperationNum()][MP_u.getMachineIndex()];
                lambda_UtoV_0l1 = Math.max(comparedValue_1, comparedValue_2);
            }
            resultMap.put(l1, lambda_UtoV_0l1);

            Operation w = l1.getMS();
            while (w != v.getMS()) {
                int lambda_UtoV_0W;
                if (w.getJP() == null) {
                    lambda_UtoV_0W = resultMap.get(w.getMP()) +
                            data.getProcessTime()[w.getMP().getJob()][w.getMP().getOperationNum()][w.getMP().getMachineIndex()];
                } else {
                    Operation JP_w = w.getJP();
                    int comparedValue_1 = L_0Tov.get(JP_w).getTotalValue() +
                            data.getProcessTime()[JP_w.getJob()][JP_w.getOperationNum()][JP_w.getMachineIndex()];
                    int comparedValue_2 = resultMap.get(w.getMP()) +
                            data.getProcessTime()[w.getMP().getJob()][w.getMP().getOperationNum()][w.getMP().getMachineIndex()];
                    lambda_UtoV_0W = Math.max(comparedValue_1, comparedValue_2);
                }
                resultMap.put(w, lambda_UtoV_0W);
                w = w.getMS();
            }
        }

        int lambda_UtoV_0U;
        if (u.getJP() == null) {
            lambda_UtoV_0U = resultMap.get(v) +
                    data.getProcessTime()[v.getJob()][v.getOperationNum()][v.getMachineIndex()];
        } else {
            Operation JP_u = u.getJP();
            int comparedValue_1 = L_0Tov.get(JP_u).getTotalValue() +
                    data.getProcessTime()[JP_u.getJob()][JP_u.getOperationNum()][JP_u.getMachineIndex()];
            int comparedValue_2 = resultMap.get(v) +
                    data.getProcessTime()[v.getJob()][v.getOperationNum()][v.getMachineIndex()];
            lambda_UtoV_0U = Math.max(comparedValue_1, comparedValue_2);
        }
        resultMap.put(u, lambda_UtoV_0U);

        return resultMap;
    }

    public static HashMap<Operation, Integer> lambda_UtoV_WN(Map<Operation, PathInfo> L_uTon,
                                                             Operation u, Operation v, Data data) {
        HashMap<Operation, Integer> resultMap = new HashMap<>();

        // 计算lambda(u, n)
        int lambda_UtoV_UN;
        if (v.getMS() == null) {
            if (u.getJS() == null) {
                lambda_UtoV_UN = data.getProcessTime()[u.getJob()][u.getOperationNum()][u.getMachineIndex()];
            } else {
                lambda_UtoV_UN = L_uTon.get(u.getJS()).getTotalValue() +
                        data.getProcessTime()[u.getJob()][u.getOperationNum()][u.getMachineIndex()];
            }
        } else {
            int comparedValue_1;
            if (u.getJS() == null) {
                comparedValue_1 = 0;
            } else {
                comparedValue_1 = L_uTon.get(u.getJS()).getTotalValue();
            }
            int comparedValue_2 = L_uTon.get(v.getMS()).getTotalValue();
            lambda_UtoV_UN = data.getProcessTime()[u.getJob()][u.getOperationNum()][u.getMachineIndex()] +
                    Math.max(comparedValue_1, comparedValue_2);
        }
        resultMap.put(u, lambda_UtoV_UN);

        // 计算lambda(v, n)
        int lambda_UtoV_VN;
        if (v.getJS() == null) {
            lambda_UtoV_VN = data.getProcessTime()[v.getJob()][v.getOperationNum()][v.getMachineIndex()] +
                    resultMap.get(u);
        } else {
            int comparedValue_1 = L_uTon.get(v.getJS()).getTotalValue();
            int comparedValue_2 = resultMap.get(u);
            lambda_UtoV_VN = data.getProcessTime()[v.getJob()][v.getOperationNum()][v.getMachineIndex()] +
                    Math.max(comparedValue_1, comparedValue_2);
        }
        resultMap.put(v, lambda_UtoV_VN);

        // 计算lambda(w, n), w in (l1,...,lk)
        int lambda_UtoV_WN;
        Operation w = v.getMP();
        while (w != u) {
            if (w.getJS() == null) {
                lambda_UtoV_WN = data.getProcessTime()[w.getJob()][w.getOperationNum()][w.getMachineIndex()] +
                        resultMap.get(w.getMS());
            } else {
                int comparedValue_1 = L_uTon.get(w.getJS()).getTotalValue();
                int comparedValue_2 = resultMap.get(w.getMS());
                lambda_UtoV_WN = data.getProcessTime()[w.getJob()][w.getOperationNum()][w.getMachineIndex()] +
                        Math.max(comparedValue_1, comparedValue_2);
            }
            resultMap.put(w, lambda_UtoV_WN);
            w = w.getMP();
        }
        return resultMap;
    }

    public static HashMap<Operation, Integer> lambda_VtoU_0W(Map<Operation, PathInfo> L_0Tov,
                                                             Operation v, Operation u, Data data) {
        HashMap<Operation, Integer> resultMap = new HashMap<>();

        int lambda_VtoU_0V;
        if (u.getMP() == null) {
            if (v.getJP() == null) {
                lambda_VtoU_0V = 0;
            } else {
                Operation JP_v = v.getJP();
                lambda_VtoU_0V = L_0Tov.get(JP_v).getTotalValue() +
                        data.getProcessTime()[JP_v.getJob()][JP_v.getOperationNum()][JP_v.getMachineIndex()];
            }
        } else {
            int comparedValue_1;
            int comparedValue_2;
            if (v.getJP() == null) {
                comparedValue_1 = 0;
            } else {
                Operation JP_v = v.getJP();
                comparedValue_1 = L_0Tov.get(JP_v).getTotalValue() +
                        data.getProcessTime()[JP_v.getJob()][JP_v.getOperationNum()][JP_v.getMachineIndex()];
            }
            Operation MP_u = u.getMP();
            comparedValue_2 = L_0Tov.get(MP_u).getTotalValue() +
                        data.getProcessTime()[MP_u.getJob()][MP_u.getOperationNum()][MP_u.getMachineIndex()];
            lambda_VtoU_0V = Math.max(comparedValue_1, comparedValue_2);
        }
        resultMap.put(v, lambda_VtoU_0V);

        int lambda_VtoU_0U;
        if (u.getJP() == null) {
            lambda_VtoU_0U = resultMap.get(v) +
                    data.getProcessTime()[v.getJob()][v.getOperationNum()][v.getMachineIndex()];
        } else {
            Operation JP_u = u.getJP();
            int comparedValue_1 = L_0Tov.get(JP_u).getTotalValue() +
                    data.getProcessTime()[JP_u.getJob()][JP_u.getOperationNum()][JP_u.getMachineIndex()];
            int comparedValue_2 = resultMap.get(v) +
                    data.getProcessTime()[v.getJob()][v.getOperationNum()][v.getMachineIndex()];
            lambda_VtoU_0U = Math.max(comparedValue_1, comparedValue_2);
        }
        resultMap.put(u, lambda_VtoU_0U);

        // 计算lambda(0, w), w in (l1,...,lk)
        int lambda_VtoU_0W;
        Operation w = u.getMS();
        while (w != v) {
            if (w.getJP() == null) {
                Operation MP_w = w.getMP();
                lambda_VtoU_0W = resultMap.get(MP_w) +
                        data.getProcessTime()[MP_w.getJob()][MP_w.getOperationNum()][MP_w.getMachineIndex()];
            } else {
                Operation JP_w = w.getJP();
                Operation MP_w = w.getMP();
                int comparedValue_1 = L_0Tov.get(JP_w).getTotalValue() +
                        data.getProcessTime()[JP_w.getJob()][JP_w.getOperationNum()][JP_w.getMachineIndex()];
                int comparedValue_2 = resultMap.get(MP_w) +
                        data.getProcessTime()[MP_w.getJob()][MP_w.getOperationNum()][MP_w.getMachineIndex()];
                lambda_VtoU_0W = Math.max(comparedValue_1, comparedValue_2);
            }
            resultMap.put(w, lambda_VtoU_0W);
            w = w.getMS();
        }

        return resultMap;
    }

    public static HashMap<Operation, Integer> lambda_VtoU_WN(Map<Operation, PathInfo> L_uTon,
                                                             Operation v, Operation u, Data data) {
        HashMap<Operation, Integer> resultMap = new HashMap<>();
        if (u.getMS() == v) { // lk 即是 u
            int lambda_VtoU_UN;
            if (v.getMS() == null) {
                if (u.getJS() == null) {
                    lambda_VtoU_UN = data.getProcessTime()[u.getJob()][u.getOperationNum()][u.getMachineIndex()];
                } else {
                    lambda_VtoU_UN = data.getProcessTime()[u.getJob()][u.getOperationNum()][u.getMachineIndex()] +
                            L_uTon.get(u.getJS()).getTotalValue();
                }
            } else {
                if (u.getJS() == null) {
                    lambda_VtoU_UN = data.getProcessTime()[u.getJob()][u.getOperationNum()][u.getMachineIndex()] +
                            L_uTon.get(v.getMS()).getTotalValue();
                } else {
                    int comparedValue_1 = L_uTon.get(u.getJS()).getTotalValue();
                    int comparedValue_2 = L_uTon.get(v.getMS()).getTotalValue();
                    lambda_VtoU_UN = data.getProcessTime()[u.getJob()][u.getOperationNum()][u.getMachineIndex()] +
                            Math.max(comparedValue_1, comparedValue_2);
                }
            }
            resultMap.put(u, lambda_VtoU_UN);
        } else {
            int lambda_VtoU_lkN;
            Operation lk = v.getMP();
            if (v.getMS() == null) {
                if (lk.getJS() == null) {
                    lambda_VtoU_lkN = data.getProcessTime()[lk.getJob()][lk.getOperationNum()][lk.getMachineIndex()];
                } else {
                    lambda_VtoU_lkN = data.getProcessTime()[lk.getJob()][lk.getOperationNum()][lk.getMachineIndex()] +
                            L_uTon.get(lk.getJS()).getTotalValue();
                }
            } else {
                if (lk.getJS() == null) {
                    lambda_VtoU_lkN = data.getProcessTime()[lk.getJob()][lk.getOperationNum()][lk.getMachineIndex()] +
                            L_uTon.get(v.getMS()).getTotalValue();
                } else {
                    int comparedValue_1 = L_uTon.get(lk.getJS()).getTotalValue();
                    int comparedValue_2 = L_uTon.get(v.getMS()).getTotalValue();
                    lambda_VtoU_lkN = data.getProcessTime()[lk.getJob()][lk.getOperationNum()][lk.getMachineIndex()] +
                            Math.max(comparedValue_1, comparedValue_2);
                }
            }
            resultMap.put(lk, lambda_VtoU_lkN);

            // 计算lambda(w, n), w in (u, l1,...,lk-1)
            int lambda_VtoU_WN;
            Operation w = lk.getMP();
            while (w != u.getMP()) {
                if (w.getJS() == null) {
                    lambda_VtoU_WN = data.getProcessTime()[w.getJob()][w.getOperationNum()][w.getMachineIndex()] +
                            resultMap.get(w.getMS());
                } else {
                    int comparedValue_1 = L_uTon.get(w.getJS()).getTotalValue();
                    int comparedValue_2 = resultMap.get(w.getMS());
                    lambda_VtoU_WN = data.getProcessTime()[w.getJob()][w.getOperationNum()][w.getMachineIndex()] +
                            Math.max(comparedValue_1, comparedValue_2);
                }
                resultMap.put(w, lambda_VtoU_WN);
                w = w.getMP();
            }
        }

        int lambda_VtoU_VN;
        if (v.getJS() == null) {
            lambda_VtoU_VN = data.getProcessTime()[v.getJob()][v.getOperationNum()][v.getMachineIndex()] +
                    resultMap.get(u);
        } else {
            int comparedValue_1 = L_uTon.get(v.getJS()).getTotalValue();
            int comparedValue_2 =  resultMap.get(u);
            lambda_VtoU_VN = data.getProcessTime()[v.getJob()][v.getOperationNum()][v.getMachineIndex()] +
                    Math.max(comparedValue_1, comparedValue_2);
        }
        resultMap.put(v, lambda_VtoU_VN);

        return resultMap;
    }

    public static int estimateChangeMacine(Operation x, int machine, Operation u, Operation v,
                                           Map<Operation, PathInfo> L_0Tov,
                                           Map<Operation, PathInfo> L_uTon, Data data) {
        Operation JP_x = x.getJP();
        int r_x = 0;
        if (JP_x != null) {
            r_x = L_0Tov.get(JP_x).getTotalValue() +
                    data.getProcessTime()[JP_x.getJob()][JP_x.getOperationNum()][JP_x.getMachineIndex()];
        }
        if (u != null) {
            int temp = L_0Tov.get(u).getTotalValue() +
                    data.getProcessTime()[u.getJob()][u.getOperationNum()][u.getMachineIndex()];
            if (temp > r_x) {
                r_x = temp;
            }
        }

        Operation JS_x = x.getJS();
        int q_x = 0;
        if (JS_x != null) {
            q_x = L_uTon.get(JS_x).getTotalValue();
        }
        if (v != null) {
            int temp = L_uTon.get(v).getTotalValue();
            if (temp > q_x) {
                q_x = temp;
            }
        }

        int p_x = 0;
        for (int i = 0; i < data.getProcessMachine()[x.getJob()][x.getOperationNum()].length; i++) {
            if (data.getProcessMachine()[x.getJob()][x.getOperationNum()][i] == machine) {
                p_x = data.getProcessTime()[x.getJob()][x.getOperationNum()][i];
                break;
            }
        }

        return r_x + p_x + q_x;
    }

    // 计算整个种群的累积 fitness
    public static double[] getAccumlatedFitness(FSG[] pop) {
        double[] accumFitness = new double[pop.length];
        accumFitness[0] = 1 / Arrays.stream(pop[0].getFitnessArr()).max().getAsDouble();
        for (int i = 1; i < accumFitness.length; i++) {
            accumFitness[i] += accumFitness[i - 1] + 1 / Arrays.stream(pop[i].getFitnessArr()).max().getAsDouble();
        }
        for (int i = 0; i < accumFitness.length; i++) {
            accumFitness[i] /= accumFitness[accumFitness.length - 1];
        }
        return accumFitness;
    }
}
