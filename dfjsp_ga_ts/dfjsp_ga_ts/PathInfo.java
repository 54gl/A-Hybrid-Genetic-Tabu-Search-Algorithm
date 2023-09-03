package dfjsp_ga_ts;

import java.util.LinkedList;

/**
 * 析取图类
 */
public class PathInfo {
    private int totalValue;
    private LinkedList<Operation> edgeInfos;

    public PathInfo(int totalValue, LinkedList<Operation> edgeInfos) {
        this.totalValue = totalValue;
        this.edgeInfos = edgeInfos;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }

    public LinkedList<Operation> getEdgeInfos() {
        return edgeInfos;
    }

    public void setEdgeInfos(LinkedList<Operation> edgeInfos) {
        this.edgeInfos = edgeInfos;
    }

    @Override
    public String toString() {
        return "PathInfo{" +
                "totalValue=" + totalValue +
                ", edgeInfos=" + edgeInfos +
                '}';
    }
}
