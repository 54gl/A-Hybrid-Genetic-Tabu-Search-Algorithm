package dfjsp_ga_ts;

public class Tabu {

    private int machine;
    private int[] locationArr;
    private Operation[] operationArr;
    private int len;

    public Tabu(int machine, int[] locationArr, Operation[] operationArr, int len) {
        this.machine = machine;
        this.locationArr = locationArr;
        this.operationArr = operationArr;
        this.len = len;
    }

    public static Tabu createTabu(TabuSearch.Move moveType, Operation[] move, int len, Data data) {
        Operation v = null;
        Operation u = null;
        switch (moveType) {
            case vTou:
                v = move[0];
                u = move[1];
                break;
            case uTov:
                u = move[0];
                v = move[1];
                break;
            case innerTou:
                v = move[0];
                u = move[1];
                break;
            case innerTov:
                u = move[0];
                v = move[1];
                break;
        }
        Operation[] operationArr = {u, v};
        int[] locationArr = {u.getMachineLocation(), v.getMachineLocation()};
        return new Tabu(data.getProcessMachine()[u.getJob()][u.getOperationNum()][u.getMachineIndex()], locationArr, operationArr, len);
    }

    public int getMachine() {
        return machine;
    }

    public void setMachine(int machine) {
        this.machine = machine;
    }

    public int[] getLocationArr() {
        return locationArr;
    }

    public void setLocationArr(int[] locationArr) {
        this.locationArr = locationArr;
    }

    public Operation[] getOperationArr() {
        return operationArr;
    }

    public void setOperationArr(Operation[] operationArr) {
        this.operationArr = operationArr;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }
}
