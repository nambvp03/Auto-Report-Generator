package edu.cpp.admissions.bean;

public class StatusType {

    int analystCount;
    int initialTCRCount;
    int finalTCRCount;
    int completedCount;
    int drReviewCount;
    int referDirCount;
    int grandTotal;

    public int getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }

    public int getAnalystCount() {
        return analystCount;
    }

    public void setAnalystCount(int analystCount) {
        this.analystCount = analystCount;
    }

    public int getInitialTCRCount() {
        return initialTCRCount;
    }

    public void setInitialTCRCount(int initialTCRCount) {
        this.initialTCRCount = initialTCRCount;
    }

    public int getFinalTCRCount() {
        return finalTCRCount;
    }

    public void setFinalTCRCount(int finalTCRCount) {
        this.finalTCRCount = finalTCRCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public int getDrReviewCount() {
        return drReviewCount;
    }

    public void setDrReviewCount(int drReviewCount) {
        this.drReviewCount = drReviewCount;
    }

    public int getReferDirCount() {
        return referDirCount;
    }

    public void setReferDirCount(int referDirCount) {
        this.referDirCount = referDirCount;
    }
}
