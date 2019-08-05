public class FixedCapacityStack<Item>
{
    private Item[] a;
    private int N;

    public FixedCapacityStack(int cap)
    {
        a = (Item[]) new Object[cap];
    }

    public boolean isEmpty()
    {
        return N ==0;
    }

    public int size()
    {
        return N;
    }

    public void push(Item item)
    {
        a[N++] = item;
    }

    public Item pop()
    {
        return a[--N];
    }
}