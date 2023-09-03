package dfjsp_ga_ts;

import java.util.Objects;

/**
 * 工序类
 */
public class Operation {

    private int job;
    private int operationNum;
    private int machineIndex;
    private int machineLocation;

    private Operation JS;
    private Operation JP;
    private Operation MS;
    private Operation MP;

    public Operation(int job, int operationNum) {
        this.job = job;
        this.operationNum = operationNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return job == operation.job &&
                operationNum == operation.operationNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(job, operationNum);
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public int getOperationNum() {
        return operationNum;
    }

    public void setOperationNum(int operationNum) {
        this.operationNum = operationNum;
    }

    public Operation getJS() {
        return JS;
    }

    public void setJS(Operation JS) {
        this.JS = JS;
    }

    public Operation getJP() {
        return JP;
    }

    public void setJP(Operation JP) {
        this.JP = JP;
    }

    public Operation getMS() {
        return MS;
    }

    public void setMS(Operation MS) {
        this.MS = MS;
    }

    public Operation getMP() {
        return MP;
    }

    public void setMP(Operation MP) {
        this.MP = MP;
    }

    public int getMachineLocation() {
        return machineLocation;
    }

    public void setMachineLocation(int machineLocation) {
        this.machineLocation = machineLocation;
    }

    public int getMachineIndex() {
        return machineIndex;
    }

    public void setMachineIndex(int machineIndex) {
        this.machineIndex = machineIndex;
    }

    @Override
    public String toString() {
        return "Operation{" +
                job + ", " +
                operationNum +
                '}';
    }

/*    @Override
    public String toString() {
        int JP_Job = -1;int JP_Op = -1;
        int JS_Job = -1;int JS_Op = -1;
        int MP_Job = -1;int MP_Op = -1;
        int MS_Job = -1;int MS_Op = -1;

        if (JP != null) {
            JP_Job = JP.getJob();
            JP_Op = JP.getOperationNum();
        }
        if (JS != null) {
            JS_Job = JS.getJob();
            JS_Op = JS.getOperationNum();
        }
        if (MP != null) {
            MP_Job = MP.getJob();
            MP_Op = MP.getOperationNum();
        }
        if (MS != null) {
            MS_Job = MS.getJob();
            MS_Op = MS.getOperationNum();
        }


        return "Operation{" +
                "job=" + this.job +
                ", operationNum=" + operationNum +
                ", machineLocation=" + machineLocation +
                ", JS=" + JS_Job + " " + JS_Op +
                ", JP=" + JP_Job + " " + JP_Op +
                ", MS=" + MS_Job + " " + MS_Op +
                ", MP=" + MP_Job + " " + MP_Op +
                '}';
    }*/
}
