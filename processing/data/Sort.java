package processing.data;

public abstract class Sort implements Runnable {
    public Sort() {
    }

    public void run() {
        int c = this.size();
        if (c > 1) {
            this.sort(0, c - 1);
        }

    }

    protected void sort(int i, int j) {
        int pivotIndex = (i + j) / 2;
        this.swap(pivotIndex, j);
        int k = this.partition(i - 1, j);
        this.swap(k, j);
        if (k - i > 1) {
            this.sort(i, k - 1);
        }

        if (j - k > 1) {
            this.sort(k + 1, j);
        }

    }

    protected int partition(int left, int right) {
        int pivot = right;

        do {
            do {
                ++left;
            } while(this.compare(left, pivot) < 0);

            while(right != 0) {
                --right;
                if (this.compare(right, pivot) <= 0) {
                    break;
                }
            }

            this.swap(left, right);
        } while(left < right);

        this.swap(left, right);
        return left;
    }

    public abstract int size();

    public abstract int compare(int var1, int var2);

    public abstract void swap(int var1, int var2);
}
