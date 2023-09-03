package dfjsp_ga_ts;

/**
 *  each operation 变换到的机器及所在的位置
 */
public class ChangedMachine {

    private int[] machines;
    private Operation[][] locations; // 插入的【u，v】工序

    public ChangedMachine(int[] machines, Operation[][] locations) {
        this.machines = machines;
        this.locations = locations;
    }

    public int[] getMachines() {
        return machines;
    }

    public void setMachines(int[] machines) {
        this.machines = machines;
    }

    public Operation[][] getLocations() {
        return locations;
    }

    public void setLocations(Operation[][] locations) {
        this.locations = locations;
    }
}
