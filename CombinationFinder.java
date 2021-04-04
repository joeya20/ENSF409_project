import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.List;

/**
 * Class used to solve satifiably problem of if an item can be made, and for
 * what price.
 */
class CombinationFinder {

    /*
     * Saved selections. Used to returning best selection and culling bad branches
     */
    private int number;

    private int bestPrice = -1;
    private InventoryEntity[] bestSelection;

    /* selection stacks for solver */
    private ArrayDeque<InventoryEntity> cSelection = new ArrayDeque<InventoryEntity>();
    private ArrayDeque<InventoryEntity> rSelection = new ArrayDeque<InventoryEntity>();

    private InventoryEntity[] inventory;

    public CombinationFinder(List<InventoryEntity> items, String itemType, int number) {

        /*
         * Filter item types and then sort by best choices per dollar, finally store as
         * array
         */
        this.inventory = items.stream().filter(s -> s.getType().equals(itemType))
                .sorted(Comparator.comparingInt(s -> s.getPrice() / s.getProperties().length))
                .toArray(InventoryEntity[]::new);
        this.number = number;
    }

    public int getBestPrice() {
        return this.bestPrice;
    }

    public InventoryEntity[] getRemovedItems() {
        return this.bestSelection;
    }

    public void solve() {

        int[] cs = new int[inventory.length];
        this.solve(cs, 0, 0, this.number);
    }

    public void solve(int[] constraintSum, int cPrice, int n, int number) {
        // System.out.println("n: " + n + " price: " + cPrice + " bestPrice: " +
        // this.bestPrice + " solution:" + isSolution(constraintSum, number));

        /* cull nodes that we know are bad */
        if (cPrice > bestPrice && bestPrice != -1) {
            return;
        }

        /* end of tree leaf: found a soultion */
        if (isSolution(constraintSum, number)) {
            this.bestSelection = this.cSelection.stream().toArray(InventoryEntity[]::new);
            this.bestPrice = cPrice;
            return;
        }

        /* edge case: end if tree no more items are left to check */
        if (n == inventory.length) {
            return;
        }

        int[] constraintSumCopy = constraintSum.clone();
        boolean[] nElementConstraints = this.inventory[n].getProperties();
        for (int i = 0; i < constraintSumCopy.length; i++) {
            if (nElementConstraints[i]) {
                constraintSumCopy[i] += 1;
            }
        }

        if (!constraintSumCopy.equals(constraintSum)) {
            cSelection.push(inventory[n]);
            solve(constraintSumCopy, cPrice + this.inventory[n].getPrice(), n + 1, number);
            cSelection.pop();
        }

        rSelection.push(inventory[n]);
        solve(constraintSum, cPrice, n + 1, number);
        rSelection.pop();

        return;

    }

    private boolean isSolution(int[] constraints, int value) {
        for (var c : constraints) {
            if (c < value) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] s) {
        var iarr = new ArrayList<InventoryEntity>();
        iarr.add(new InventoryEntity("1", 75, new String[] { "Y", "N", "N", "N" }));
        iarr.add(new InventoryEntity("2", 50, new String[] { "N", "Y", "N", "Y" }));
        iarr.add(new InventoryEntity("3", 75, new String[] { "N", "N", "Y", "N" }));
        iarr.add(new InventoryEntity("4", 100, new String[] { "Y", "N", "Y", "Y" }));

        CombinationFinder c = new CombinationFinder(iarr, "", 1);
        int[] cs = new int[iarr.get(0).getProperties().length];
        c.solve(cs, 0, 0, 1);
        System.out.println("ANS: " + c.getBestPrice());
        for (var item : c.getRemovedItems()) {
            System.out.println(item.getId());
        }
        // c.solve(cs, 0);

    }

}
